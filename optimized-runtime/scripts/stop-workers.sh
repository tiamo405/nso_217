#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

shopt -s nullglob
pid_files=()
if (( $# > 0 )); then
    for number in "$@"; do
        number=${number#worker-}
        if ! [[ "$number" =~ ^[0-9]+$ ]]; then
            echo "Worker không hợp lệ: $number" >&2
            exit 1
        fi
        worker_name=$(printf 'worker-%02d' "$((10#$number))")
        worker_dir="$WORKERS_DIR/$worker_name"
        if [[ ! -d "$worker_dir" ]]; then
            echo "Không tìm thấy $worker_name tại $WORKERS_DIR" >&2
            exit 1
        fi
        pid_files+=("$worker_dir/bot.pid")
    done
else
    if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
        sup_pid=$(<"$SUPERVISOR_PID_FILE")
        if [[ "$sup_pid" =~ ^[0-9]+$ ]] && kill -0 "$sup_pid" 2>/dev/null; then
            echo "Dừng supervisor (PID $sup_pid)..."
            kill "$sup_pid" 2>/dev/null || true
        fi
        rm -f -- "$SUPERVISOR_PID_FILE"
    fi
    pid_files=("$WORKERS_DIR"/worker-*/bot.pid)
fi

stopped=0
for pid_file in "${pid_files[@]}"; do
    [[ -f "$pid_file" ]] || continue
    pid=$(<"$pid_file")
    worker_dir=$(dirname -- "$pid_file")
    worker_name=$(basename -- "$worker_dir")

    if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
        echo "Dừng $worker_name (PID $pid)..."
        kill "$pid" 2>/dev/null || true
        for _ in {1..10}; do
            if ! kill -0 "$pid" 2>/dev/null; then
                break
            fi
            sleep 0.2
        done
        if kill -0 "$pid" 2>/dev/null; then
            kill -9 "$pid" 2>/dev/null || true
        fi
        stopped=$((stopped + 1))
    fi
    rm -f -- "$pid_file"
done

echo "Đã dừng $stopped worker."
