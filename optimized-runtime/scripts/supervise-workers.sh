#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
START_DELAY=${START_DELAY:-30}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # supervise all workers (default delay 3s between starts)
  $(basename "$0") --delay 5       # supervise all workers, wait 5s between worker starts
  $(basename "$0") 3               # supervise only worker-03
  $(basename "$0") 1 2 3           # supervise worker-01, worker-02, worker-03
EOF
}

worker_args=()
start_args=()
while (( $# > 0 )); do
    case "$1" in
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
trap cleanup EXIT INT TERM

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

    sleep "$CHECK_INTERVAL"
done
