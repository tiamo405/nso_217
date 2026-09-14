#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}

shopt -s nullglob
markers=("$WORKERS_DIR"/worker-*/home/worker.done "$WORKERS_DIR"/worker-*/home/worker.first-pass.done)
for marker in "${markers[@]}"; do
    worker_dir=$(dirname -- "$(dirname -- "$marker")")
    rm -f -- "$marker"
    echo "Đã reset $(basename -- "$worker_dir"): $(basename -- "$marker")"
done
echo "Đã xóa ${#markers[@]} marker hoàn tất."
