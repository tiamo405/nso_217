#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # supervise all workers
  $(basename "$0") 3               # supervise only worker-03
  $(basename "$0") 3 8 10          # supervise worker-03, worker-08, worker-10
  $(basename "$0") --delay 10      # wait 10s between worker starts
  CHECK_INTERVAL=30 $(basename "$0")
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
            start_args+=("$1" "$2")
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
            start_args+=("$1")
            shift
            ;;
    esac
done

if ! [[ "$CHECK_INTERVAL" =~ ^[1-9][0-9]*$ ]]; then
    echo "CHECK_INTERVAL phải là số giây dương." >&2
    exit 1
fi

mkdir -p "$WORKERS_DIR"
if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
    old_pid=$(<"$SUPERVISOR_PID_FILE")
    if [[ "$old_pid" =~ ^[0-9]+$ ]] && kill -0 "$old_pid" 2>/dev/null; then
        echo "Headless supervisor đã chạy (PID $old_pid)." >&2
        exit 1
    fi
fi
printf '%s\n' "$$" >"$SUPERVISOR_PID_FILE"

stop_worker_dir() {
    local worker_dir=$1
    local worker_name
    local pid_file
    local pid
    local cmdline

    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    if [[ ! -f "$pid_file" ]]; then
        return
    fi

    pid=$(<"$pid_file")
    if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        return
    fi

    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    if [[ "$cmdline" != *"HeadlessMain"* || "$cmdline" != *"$worker_dir"* ]]; then
        echo "Bỏ qua $worker_name: PID $pid không thuộc headless worker này." >&2
        return
    fi

    kill "$pid"
    for ((attempt = 0; attempt < 50; attempt++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            break
        fi
        sleep 0.1
    done
    if kill -0 "$pid" 2>/dev/null; then
        kill -9 "$pid"
    fi
    rm -f -- "$pid_file"
    echo "Đã dừng $worker_name (PID $pid)"
}

stop_selected() {
    local number
    local worker_name
    local worker_dir

    trap - INT TERM
    rm -f -- "$SUPERVISOR_PID_FILE"
    if (( ${#worker_args[@]} == 0 )); then
        "$SCRIPT_DIR/stop-workers.sh"
    else
        for number in "${worker_args[@]}"; do
            worker_name=$(printf 'worker-%02d' "$((10#$number))")
            worker_dir="$WORKERS_DIR/$worker_name"
            if [[ -d "$worker_dir" ]]; then
                stop_worker_dir "$worker_dir"
            fi
        done
    fi
    exit 0
}
trap stop_selected INT TERM

if (( ${#worker_args[@]} == 0 )); then
    echo "Headless supervisor đang chạy tất cả worker; Ctrl+C để dừng."
else
    echo "Headless supervisor đang chạy worker: ${worker_args[*]}; Ctrl+C để dừng."
fi

while true; do
    "$SCRIPT_DIR/start-workers.sh" "${start_args[@]}" || true
    sleep "$CHECK_INTERVAL"
done
