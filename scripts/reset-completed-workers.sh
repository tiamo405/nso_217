#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}

shopt -s nullglob
markers=("$WORKERS_DIR"/worker-*/home/worker.done)
for marker in "${markers[@]}"; do
    worker_dir=$(dirname -- "$(dirname -- "$marker")")
    rm -f -- "$marker"
    echo "Đã reset $(basename -- "$worker_dir")"
done
echo "Đã xóa ${#markers[@]} marker hoàn tất."
