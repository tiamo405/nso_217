#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}

if (( $# > 0 )); then
    number=${1#worker-}
    worker_name=$(printf 'worker-%02d' "$((10#$number))")
    log_file="$WORKERS_DIR/$worker_name/stdout.log"
    if [[ ! -f "$log_file" ]]; then
        echo "Không tìm thấy log tại $log_file" >&2
        exit 1
    fi
    echo "=== Đang theo dõi log của $worker_name (Ctrl+C để thoát) ==="
    tail -f "$log_file"
else
    shopt -s nullglob
    log_files=("$WORKERS_DIR"/worker-*/stdout.log)
    if (( ${#log_files[@]} == 0 )); then
        echo "Chưa có log file nào tại $WORKERS_DIR" >&2
        exit 1
    fi
    echo "=== Đang theo dõi tất cả workers (Ctrl+C để thoát) ==="
    tail -f "${log_files[@]}"
fi
