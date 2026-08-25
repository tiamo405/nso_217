#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"

if [[ ! -d "$DIST_DIR" ]]; then
    echo "Lỗi: Thư mục $DIST_DIR chưa tồn tại. Vui lòng chạy build-workers.sh trước!"
    exit 1
fi

echo "=== Khởi động các NSO Python Workers ==="

# Filter worker targets if arguments are passed
TARGET_WORKERS=()
if (( $# > 0 )); then
    for arg in "$@"; do
        num=${arg#worker-}
        if [[ "$num" =~ ^[0-9]+$ ]]; then
            wname=$(printf 'worker-%02d' "$((10#$num))")
            TARGET_WORKERS+=("$DIST_DIR/$wname")
        fi
    done
else
    TARGET_WORKERS=("$DIST_DIR"/worker-*)
fi

STARTED=0
for WDIR in "${TARGET_WORKERS[@]}"; do
    [[ -d "$WDIR" ]] || continue
    WNAME="$(basename "$WDIR")"
    PID_FILE="$WDIR/bot.pid"
    LOG_FILE="$WDIR/stdout.log"

    if [[ -f "$PID_FILE" ]] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
        echo "  [SKIP] $WNAME đang chạy (PID $(cat "$PID_FILE"))"
        continue
    fi

    # Remove completion marker if restarting worker manually
    rm -f "$WDIR/completed.marker"

    # Start worker process in background, ensure PID file is written
    printf '\n===== START %s =====\n' "$(date '+%F %T')" >> "$LOG_FILE"
    PYTHONPATH="$PYTHON_WORKER_DIR" nohup python3 -u "$PYTHON_WORKER_DIR/worker/worker_main.py" --worker-dir "$WDIR" >> "$LOG_FILE" 2>&1 &
    PID=$!
    # Wait a moment to confirm the process is alive
    sleep 0.5
    if kill -0 $PID 2>/dev/null; then
        echo "$PID" > "$PID_FILE"
        echo "  [STARTED] $WNAME (PID $PID) -> Log: $LOG_FILE"
        STARTED=$((STARTED + 1))
    else
        echo "  [FAILED] $WNAME could not start"
    fi
done

disown -a 2>/dev/null || true

echo "=== Đã khởi động $STARTED workers ==="

