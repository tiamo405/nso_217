#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"

WORKER_ID="${1:-worker-01}"

# Handle numeric shortcut e.g. "1" -> "worker-01"
if [[ "$WORKER_ID" =~ ^[0-9]+$ ]]; then
    WORKER_ID=$(printf "worker-%02d" "$WORKER_ID")
fi

LOG_FILE="$DIST_DIR/$WORKER_ID/stdout.log"

if [[ ! -f "$LOG_FILE" ]]; then
    echo "Lỗi: Không tìm thấy log file tại $LOG_FILE"
    echo "Danh sách worker có sẵn:"
    ls -d "$DIST_DIR"/worker-* 2>/dev/null | xargs -n 1 basename || true
    exit 1
fi

echo "=== Đang xem log của $WORKER_ID ($LOG_FILE) (Nhấn Ctrl+C để thoát) ==="
tail -f -n 50 "$LOG_FILE"

