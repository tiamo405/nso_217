#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"

if [[ ! -d "$DIST_DIR" ]]; then
    echo "Thư mục $DIST_DIR không tồn tại."
    exit 0
fi

echo "=== Đang dừng các NSO Python Workers ==="

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

STOPPED=0
for WDIR in "${TARGET_WORKERS[@]}"; do
    [[ -d "$WDIR" ]] || continue
    WNAME="$(basename "$WDIR")"
    PID_FILE="$WDIR/bot.pid"

    if [[ -f "$PID_FILE" ]]; then
        PID="$(cat "$PID_FILE" 2>/dev/null || echo "")"
        if [[ -n "$PID" ]]; then
            if kill -0 "$PID" 2>/dev/null; then
                echo "  [STOPPING] $WNAME (PID $PID)..."
                kill "$PID" 2>/dev/null || true
                for i in {1..10}; do
                    if ! kill -0 "$PID" 2>/dev/null; then
                        break
                    fi
                    sleep 0.5
                done
                if kill -0 "$PID" 2>/dev/null; then
                    kill -9 "$PID" 2>/dev/null || true
                fi
            else
                echo "  [STOPPED] $WNAME (PID $PID) not running"
            fi
        fi
        rm -f "$PID_FILE"
        STOPPED=$((STOPPED + 1))
    fi
done

echo "=== Đã dừng $STOPPED workers ==="



