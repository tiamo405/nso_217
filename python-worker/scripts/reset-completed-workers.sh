#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"

if [[ ! -d "$DIST_DIR" ]]; then
    echo "Thư mục $DIST_DIR chưa tồn tại."
    exit 0
fi

shopt -s nullglob
markers=("$DIST_DIR"/worker-*/completed.marker "$DIST_DIR"/worker-*/home/worker.done)
count=0
for marker in "${markers[@]}"; do
    worker_dir=$(dirname -- "$marker")
    if [[ "$(basename -- "$worker_dir")" == "home" ]]; then
        worker_dir=$(dirname -- "$worker_dir")
    fi
    rm -f -- "$marker"
    echo "Đã reset $(basename -- "$worker_dir")"
    count=$((count + 1))
done

echo "=== Đã xóa $count marker hoàn tất trong $DIST_DIR ==="
