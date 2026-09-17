#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
START_DELAY=${START_DELAY:-15}
REPEATED_STATUS_LIMIT=${REPEATED_STATUS_LIMIT:-5}
STALE_LOG_SECONDS=${STALE_LOG_SECONDS:-300}
PERIODIC_RESTART_SECONDS=${PERIODIC_RESTART_SECONDS:-10800}
REPEATED_STATUS_WINDOW_LINES=${REPEATED_STATUS_WINDOW_LINES:-20}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"
SERVER_NAME=${NSO_SERVER:-tk}

normalize_server() {
    case "${1,,}" in
        ninjamobile|ninja) SERVER_NAME=ninjamobile ;;
        tk|truyenky|truyen-ky) SERVER_NAME=tk ;;
        *)
            echo "Server không hợp lệ: $1 (chọn ninjamobile hoặc tk)." >&2
            exit 1
            ;;
    esac
}

normalize_server "$SERVER_NAME"

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--server ninjamobile|tk] [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # supervise all workers (default delay 3s between starts)
  $(basename "$0") --delay 5       # supervise all workers, wait 5s between worker starts
  $(basename "$0") 3               # supervise only worker-03
  $(basename "$0") 1 2 3           # supervise worker-01, worker-02, worker-03
  REPEATED_STATUS_LIMIT=5 $(basename "$0") # restart nếu trạng thái tiến độ lặp 5 lần
  STALE_LOG_SECONDS=300 $(basename "$0") # restart nếu stdout.log im lặng 300s
  PERIODIC_RESTART_SECONDS=10800 $(basename "$0") # restart worker sau 3 giờ
  REPEATED_STATUS_WINDOW_LINES=20 $(basename "$0") # cửa sổ phát hiện NPC25
  $(basename "$0") --server ninjamobile # chạy bằng server NinjaMobile
EOF
}

worker_args=()
start_args=()
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
            number=${1#worker-}
            if ! [[ "$number" =~ ^[0-9]+$ ]]; then
                echo "Worker không hợp lệ: $1" >&2
                usage
                exit 1
            fi
            worker_args+=("$number")
            shift
            ;;
    esac
done

export NSO_SERVER="$SERVER_NAME"

start_args=("--delay" "$START_DELAY")
if (( ${#worker_args[@]} > 0 )); then
    for w in "${worker_args[@]}"; do
        start_args+=("$w")
    done
fi

if [[ ! -d "$WORKERS_DIR" ]]; then
    echo "Chưa có thư mục workers. Chạy build-workers.sh trước." >&2
    exit 1
fi
if ! [[ "$STALE_LOG_SECONDS" =~ ^[0-9]+$ ]]; then
    echo "STALE_LOG_SECONDS phải là số nguyên không âm." >&2
    exit 1
fi
if ! [[ "$REPEATED_STATUS_LIMIT" =~ ^[0-9]+$ ]]; then
    echo "REPEATED_STATUS_LIMIT phải là số nguyên không âm." >&2
    exit 1
fi
if ! [[ "$PERIODIC_RESTART_SECONDS" =~ ^[0-9]+$ ]]; then
    echo "PERIODIC_RESTART_SECONDS phải là số giây không âm." >&2
    exit 1
fi
if ! [[ "$REPEATED_STATUS_WINDOW_LINES" =~ ^[1-9][0-9]*$ ]]; then
    echo "REPEATED_STATUS_WINDOW_LINES phải là số nguyên dương." >&2
    exit 1
fi

mkdir -p "$WORKERS_DIR"
if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
    old_pid=$(<"$SUPERVISOR_PID_FILE")
    if [[ "$old_pid" =~ ^[0-9]+$ ]] && kill -0 "$old_pid" 2>/dev/null; then
        echo "Supervisor đã chạy từ trước (PID $old_pid)." >&2
        exit 1
    fi
fi
printf '%s\n' "$$" >"$SUPERVISOR_PID_FILE"

cleanup() {
    rm -f -- "$SUPERVISOR_PID_FILE"
}
trap cleanup EXIT
trap 'exit 0' INT TERM

find_repeated_status() {
    local log_file=$1

    (( REPEATED_STATUS_LIMIT > 0 )) || return 1
    [[ -f "$log_file" ]] || return 1
    # Only scan the recent segment so a large append-only log does not make
    # every supervisor cycle read the complete file.
    tail -c 4M -- "$log_file" | awk \
        -v limit="$REPEATED_STATUS_LIMIT" \
        -v window="$REPEATED_STATUS_WINDOW_LINES" \
        -v npc25_message="AUTO NVHN NPC25: [Hãy nhận nhiệm vụ mỗi ngày từ ta rồi mới sử dụng tính năng này.]" '
        {
            line_number++
            expired = line_number - window
            if (npc25_lines[expired]) {
                npc25_count--
                delete npc25_lines[expired]
            }
            if (index($0, npc25_message) > 0) {
                npc25_lines[line_number] = 1
                npc25_count++
                npc25_last_status = $0
            }
        }
        /^===== START / {
            last_key = ""
            repeated = 0
            line_number = 0
            npc25_count = 0
            npc25_last_status = ""
            for (idx in npc25_lines) {
                delete npc25_lines[idx]
            }
            next
        }
        index($0, "AUTO NVHN PREP: đang tới Okaza") > 0 {
            prep = substr($0, index($0, "AUTO NVHN PREP:"))
            key = "prep " prep
            if (key == last_key) {
                repeated++
            } else {
                last_key = key
                repeated = 1
            }
            last_status = prep
            next
        }
        /^AUTO NVHN STATUS:/ {
            nvhn = ""
            progress = ""
            if (match($0, /nvhn=[0-9]+\/20/)) {
                nvhn = substr($0, RSTART, RLENGTH)
            }
            if (match($0, /progress=[0-9]+\/[0-9]+/)) {
                progress = substr($0, RSTART, RLENGTH)
            }
            if (nvhn == "" || progress == "") {
                last_key = ""
                repeated = 0
                next
            }

            # Compare the progress segment, not the whole line. Values such as
            # HP, currency, or timestamps can change while the task progress
            # remains stuck.
            key = nvhn " " progress
            if (key == last_key) {
                repeated++
            } else {
                last_key = key
                repeated = 1
            }
            last_status = $0
            next
        }
        /^AUTO NVHN / {
            # Catch other exact AUTO NVHN event lines as well. This is more
            # conservative than comparing every log line: five identical
            # task events in the recent segment indicate a likely retry loop.
            key = "event " $0
            if (key == last_key) {
                repeated++
            } else {
                last_key = key
                repeated = 1
            }
            last_status = $0
        }
        END {
            if (limit > 0 && npc25_count >= limit) {
                print "NPC25 lặp " npc25_count "/" limit " lần trong " window " dòng gần nhất: " npc25_last_status
                exit 0
            }
            if (repeated >= limit) {
                print last_key " | " last_status
                exit 0
            }
            exit 1
        }
    ' "$log_file"
}

find_stale_log() {
    local log_file=$1
    local modified_at now age

    (( STALE_LOG_SECONDS > 0 )) || return 1
    if [[ ! -f "$log_file" ]]; then
        echo "chưa có stdout.log"
        return 0
    fi

    modified_at=$(stat -c %Y -- "$log_file" 2>/dev/null) || return 1
    now=$(date +%s)
    age=$((now - modified_at))
    if (( age >= STALE_LOG_SECONDS )); then
        echo "stdout.log không đổi ${age}s (ngưỡng ${STALE_LOG_SECONDS}s)"
        return 0
    fi
    return 1
}

find_periodic_restart() {
    local pid_file=$1
    local modified_at now age

    (( PERIODIC_RESTART_SECONDS > 0 )) || return 1
    [[ -f "$pid_file" ]] || return 1

    modified_at=$(stat -c %Y -- "$pid_file" 2>/dev/null) || return 1
    now=$(date +%s)
    age=$((now - modified_at))
    if (( age >= PERIODIC_RESTART_SECONDS )); then
        echo "worker đã chạy ${age}s (ngưỡng ${PERIODIC_RESTART_SECONDS}s)"
        return 0
    fi
    return 1
}

restart_worker() {
    local worker_dir=$1
    local worker_name pid_file pid cmdline

    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    [[ -f "$pid_file" ]] || return 0

    pid=$(<"$pid_file")
    if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        return 0
    fi

    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    if [[ "$cmdline" != *"OptimizedMain"* || "$cmdline" != *"$worker_dir"* ]]; then
        echo "Bỏ qua restart $worker_name: PID $pid không thuộc optimized worker này." >&2
        return 1
    fi

    if ! kill "$pid" 2>/dev/null; then
        echo "Không dừng được $worker_name (PID $pid), bỏ qua restart." >&2
        return 1
    fi
    for ((attempt = 0; attempt < 50; attempt++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            break
        fi
        sleep 0.1
    done
    if kill -0 "$pid" 2>/dev/null; then
        kill -9 "$pid" 2>/dev/null || true
    fi
    rm -f -- "$pid_file"

    "$SCRIPT_DIR/start-workers.sh" --delay 0 "$worker_name"
}

echo "Optimized Supervisor đang chạy (PID $$)... Bấm Ctrl+C để dừng."
echo "Cấu hình giãn cách khởi động: START_DELAY=${START_DELAY}s giữa các worker"

while true; do
    shopt -s nullglob
    worker_dirs=()
    if (( ${#worker_args[@]} == 0 )); then
        worker_dirs=("$WORKERS_DIR"/worker-*)
    else
        for number in "${worker_args[@]}"; do
            worker_name=$(printf 'worker-%02d' "$((10#$number))")
            worker_dir="$WORKERS_DIR/$worker_name"
            if [[ -d "$worker_dir" ]]; then
                worker_dirs+=("$worker_dir")
            fi
        done
    fi

    # Khi worker hoàn thành lượt 1 (tạo worker.done), supervisor tự động đổi tên thành
    # worker.first-pass.done để start-workers.sh kích hoạt lượt kiểm tra thứ 2.
    # Chỉ khi worker.done được tạo ở lượt thứ 2 (khi đã có first-pass.done) mới là hoàn tất 2/2.
    for worker_dir in "${worker_dirs[@]}"; do
        [[ -f "$worker_dir/.paused" ]] && continue
        done_marker="$worker_dir/home/worker.done"
        first_pass_marker="$worker_dir/home/worker.first-pass.done"
        if [[ -f "$done_marker" && ! -f "$first_pass_marker" ]]; then
            mv -- "$done_marker" "$first_pass_marker"
            worker_name=$(basename -- "$worker_dir")
            echo "[$(date '+%F %T')] $worker_name đã xong lượt 1/2; chuẩn bị chạy kiểm tra lượt 2/2."
        fi
    done

    all_done=true
    for worker_dir in "${worker_dirs[@]}"; do
        if [[ ! -f "$worker_dir/home/worker.done" ]]; then
            all_done=false
            break
        fi
    done

    if [[ "$all_done" == "true" && ${#worker_dirs[@]} -gt 0 ]]; then
        echo "Tất cả worker đã hoàn tất nhiệm vụ. Supervisor kết thúc."
        break
    fi

    # Gọi start-workers.sh với đúng tham số delay để lần lượt khởi động so le
    "$SCRIPT_DIR/start-workers.sh" "${start_args[@]}" || true

    for worker_dir in "${worker_dirs[@]}"; do
        [[ -f "$worker_dir/.paused" ]] && continue
        [[ -f "$worker_dir/home/worker.done" ]] && continue

        pid_file="$worker_dir/bot.pid"
        [[ -f "$pid_file" ]] || continue
        pid=$(<"$pid_file")
        if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
            continue
        fi

        worker_name=$(basename -- "$worker_dir")
        if periodic_reason=$(find_periodic_restart "$pid_file"); then
            echo "[$(date '+%F %T')] $worker_name đã đến chu kỳ restart định kỳ; đang restart."
            echo "Lý do: $periodic_reason"
            restart_worker "$worker_dir" || true
            continue
        fi

        if stale_reason=$(find_stale_log "$worker_dir/stdout.log"); then
            echo "[$(date '+%F %T')] $worker_name log im lặng đủ ${STALE_LOG_SECONDS}s; đang restart."
            echo "Lý do: $stale_reason"
            restart_worker "$worker_dir" || true
            continue
        fi

        if repeated_status=$(find_repeated_status "$worker_dir/stdout.log"); then
            echo "[$(date '+%F %T')] $worker_name có trạng thái AUTO NVHN bị lặp từ $REPEATED_STATUS_LIMIT lần liên tiếp; đang restart."
            echo "Trạng thái bị lặp: $repeated_status"
            restart_worker "$worker_dir" || true
        fi
    done

    sleep "$CHECK_INTERVAL"
done
