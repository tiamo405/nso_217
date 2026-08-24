#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
SUPERVISOR_PID_FILE="$DIST_DIR/supervisor.pid"

if [[ ! -d "$DIST_DIR" ]]; then
    echo "Lỗi: Thư mục $DIST_DIR chưa tồn tại. Vui lòng chạy build-workers.sh trước!" >&2
    exit 1
fi

if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
    old_pid=$(<"$SUPERVISOR_PID_FILE")
    if [[ "$old_pid" =~ ^[0-9]+$ ]] && kill -0 "$old_pid" 2>/dev/null; then
        echo "Supervisor đã chạy (PID $old_pid)." >&2
        exit 1
    fi
fi
printf '%s\n' "$$" >"$SUPERVISOR_PID_FILE"

stop_all() {
    trap - INT TERM
    rm -f -- "$SUPERVISOR_PID_FILE"
    "$SCRIPT_DIR/stop-workers.sh"
    exit 0
}
trap stop_all INT TERM

echo "=== Supervisor NSO Python Worker đang chạy (Ctrl+C để dừng) ==="

while true; do
    shopt -s nullglob
    worker_dirs=("$DIST_DIR"/worker-*)
    for worker_dir in "${worker_dirs[@]}"; do
        [[ -d "$worker_dir" ]] || continue
        worker_name=$(basename -- "$worker_dir")
        pid_file="$worker_dir/bot.pid"
        marker_file="$worker_dir/completed.marker"

        # Skip if worker completed all accounts
        if [[ -f "$marker_file" ]]; then
            continue
        fi

        # If process is dead, start it
        if [[ ! -f "$pid_file" ]] || ! kill -0 "$(<"$pid_file")" 2>/dev/null; then
            echo "[SUPERVISOR] $worker_name bị dừng, tự động khởi động lại..."
            "$SCRIPT_DIR/start-workers.sh" "$worker_name" || true
        fi
    done

    sleep "$CHECK_INTERVAL"
done
