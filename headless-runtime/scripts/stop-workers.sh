#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}

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
    pid_files=("$WORKERS_DIR"/worker-*/bot.pid)
fi
stopped=0

for pid_file in "${pid_files[@]}"; do
    if [[ ! -f "$pid_file" ]]; then
        worker_name=$(basename -- "$(dirname -- "$pid_file")")
        echo "$worker_name không chạy"
        continue
    fi
    worker_dir=$(dirname -- "$pid_file")
    worker_name=$(basename -- "$worker_dir")
    pid=$(<"$pid_file")

    if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
        echo "$worker_name không chạy"
        rm -f -- "$pid_file"
        continue
    fi

    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    if [[ "$cmdline" != *"HeadlessMain"* || "$cmdline" != *"$worker_dir"* ]]; then
        echo "Bỏ qua $worker_name: PID $pid không thuộc headless worker này." >&2
        continue
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
    stopped=$((stopped + 1))
done

echo "Đã dừng $stopped headless worker."
