#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
CLASSES_DIR=${OPTIMIZED_CLASSES_DIR:-"$RUNTIME_DIR/build/classes"}
START_DELAY=${START_DELAY:-3}
WORKER_NICE=${WORKER_NICE:-}
WORKER_TASKSET=${WORKER_TASKSET:-}

source "$SCRIPT_DIR/tuning-options.sh"
SERVER_NAME=${NSO_SERVER:-tk}

normalize_server() {
    case "${1,,}" in
        ninjamobile|ninja) SERVER_NAME=ninjamobile ;;
        ninjamobilesv4|"ninja mobile sv4"|ninja-sv4|nsm4.ninjasm.net) SERVER_NAME=ninjamobileSV4 ;;
        tk|truyenky|truyen-ky) SERVER_NAME=tk ;;
        *)
            echo "Server không hợp lệ: $1 (chọn ninjamobile, ninjamobileSV4 hoặc tk)." >&2
            exit 1
            ;;
    esac
}

normalize_server "$SERVER_NAME"

is_optimized_worker_pid() {
    local pid=$1
    local worker_dir=$2
    local cmdline

    [[ "$pid" =~ ^[0-9]+$ ]] || return 1
    [[ -r "/proc/$pid/cmdline" ]] || return 1
    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    [[ "$cmdline" == *"OptimizedMain"* && "$cmdline" == *"$worker_dir"* ]]
}

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--server ninjamobile|ninjamobileSV4|tk] [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # start all workers
  $(basename "$0") 3               # start only worker-03
  $(basename "$0") 3 8 10          # start worker-03, worker-08, worker-10
  $(basename "$0") --delay 5       # start all, wait 5s between workers
  $(basename "$0") --server tk     # chạy bằng server Truyền Kỳ
  $(basename "$0") --server ninjamobileSV4 # chạy bằng NinjaMobile SV4
EOF
}

worker_args=()
while (( $# > 0 )); do
    case "$1" in
        --server)
            if (( $# < 2 )); then
                echo "Thiếu tên server sau --server." >&2
                usage
                exit 1
            fi
            normalize_server "$2"
            shift 2
            ;;
        --server=*)
            normalize_server "${1#--server=}"
            shift
            ;;
        --delay|-d)
            if (( $# < 2 )) || ! [[ "$2" =~ ^[0-9]+$ ]]; then
                echo "Delay phải là số giây không âm." >&2
                usage
                exit 1
            fi
            START_DELAY=$2
            shift 2
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            worker_args+=("$1")
            shift
            ;;
    esac
done

if [[ ! -d "$CLASSES_DIR" ]]; then
    echo "Chưa có classes. Chạy ./optimized-runtime/build-optimized.sh trước." >&2
    exit 1
fi

shopt -s nullglob
worker_dirs=()
if (( ${#worker_args[@]} > 0 )); then
    for number in "${worker_args[@]}"; do
        number=${number#worker-}
        if ! [[ "$number" =~ ^[0-9]+$ ]]; then
            echo "Worker không hợp lệ: $number" >&2
            usage
            exit 1
        fi
        worker_name=$(printf 'worker-%02d' "$((10#$number))")
        worker_dir="$WORKERS_DIR/$worker_name"
        if [[ ! -d "$worker_dir" ]]; then
            echo "Không tìm thấy $worker_name tại $WORKERS_DIR" >&2
            exit 1
        fi
        worker_dirs+=("$worker_dir")
    done
else
    worker_dirs=("$WORKERS_DIR"/worker-*)
    if (( ${#worker_dirs[@]} == 0 )); then
        echo "Chưa có worker nào. Chạy ./optimized-runtime/scripts/build-workers.sh trước." >&2
        exit 1
    fi
fi

started=0
running=0
failed=0
completed=0

for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"

    if [[ -f "$worker_dir/.paused" ]]; then
        echo "$worker_name đang tạm dừng, không khởi động"
        continue
    fi

    # Tương thích worker cũ: bản Supervisor trước đây đổi worker.done thành
    # worker.first-pass.done để chạy lượt 2. Chính sách mới coi marker cũ là
    # đã hoàn thành, không khởi động thêm một lượt ngoài lịch định kỳ.
    legacy_done_marker="$worker_dir/home/worker.first-pass.done"
    if [[ -f "$legacy_done_marker" && ! -f "$worker_dir/home/worker.done" ]]; then
        mv -- "$legacy_done_marker" "$worker_dir/home/worker.done"
        echo "$worker_name đã hoàn tất theo marker cũ, không chạy lại ngoài lịch định kỳ"
    fi

    if [[ -f "$worker_dir/home/worker.done" ]]; then
        echo "$worker_name đã hoàn tất toàn bộ account, không khởi động lại"
        completed=$((completed + 1))
        continue
    fi

    if [[ -f "$pid_file" ]]; then
        pid=$(<"$pid_file")
        if is_optimized_worker_pid "$pid" "$worker_dir"; then
            echo "$worker_name đang chạy (PID $pid)"
            running=$((running + 1))
            continue
        fi
        rm -f -- "$pid_file"
    fi

    mkdir -p "$worker_dir/home"
    printf '\n===== START OPTIMIZED %s =====\n' "$(date '+%F %T')" >>"$worker_dir/stdout.log"
    printf '\n===== START OPTIMIZED %s =====\n' "$(date '+%F %T')" >>"$worker_dir/java-errors.log"

    read -r -a java_opts_array <<< "$JAVA_OPTS"
    command_prefix=()
    if [[ -n "$WORKER_NICE" ]]; then
        command_prefix+=(nice -n "$WORKER_NICE")
    fi
    if [[ -n "$WORKER_TASKSET" ]]; then
        command_prefix+=(taskset -c "$WORKER_TASKSET")
    fi

    nohup "${command_prefix[@]}" "$JAVA_BIN" \
        "-Xms$JAVA_XMS" \
        "-Xmx$JAVA_XMX" \
        "${java_opts_array[@]}" \
        "${OPTIMIZED_SYSTEM_PROPS[@]}" \
        "-Dnso.server=$SERVER_NAME" \
        "-Dnso.worker.name=$worker_name" \
        "-Dnso.nvhn.error.dir=$RUNTIME_DIR/run/nvhn-errors" \
        "-Duser.home=$worker_dir/home" \
        -cp "$worker_dir:$CLASSES_DIR" \
        OptimizedMain \
        >>"$worker_dir/stdout.log" \
        2>>"$worker_dir/java-errors.log" &

    pid=$!
    printf '%s\n' "$pid" >"$pid_file"

    sleep 0.3

    if kill -0 "$pid" 2>/dev/null; then
        echo "Đã chạy $worker_name (PID $pid)"
        started=$((started + 1))
    else
        echo "$worker_name khởi động lỗi; xem $worker_dir/java-errors.log" >&2
        rm -f -- "$pid_file"
        failed=$((failed + 1))
    fi

    if (( START_DELAY > 0 )); then
        sleep "$START_DELAY"
    fi
done

echo "Kết quả: mới chạy=$started, đã chạy=$running, hoàn tất=$completed, lỗi=$failed"
(( failed == 0 ))
