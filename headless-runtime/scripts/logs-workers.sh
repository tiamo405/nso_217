#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}
FILTER=${LOG_FILTER:-AUTO NVHN}

shopt -s nullglob
if [[ $# -gt 0 ]]; then
    number=${1#worker-}
    if [[ "$number" =~ ^[0-9]+$ ]]; then
        worker_name=$(printf 'worker-%02d' "$((10#$number))")
    else
        echo "Worker không hợp lệ: $1" >&2
        exit 1
    fi
    log_files=("$WORKERS_DIR/$worker_name/stdout.log")
else
    log_files=("$WORKERS_DIR"/worker-*/stdout.log)
fi

if (( ${#log_files[@]} == 0 )) || [[ ! -f "${log_files[0]}" ]]; then
    echo "Chưa có log headless worker." >&2
    exit 1
fi

echo "Đang theo dõi ${#log_files[@]} log; Ctrl+C để thoát. Bộ lọc: $FILTER"
tail -n 20 -F "${log_files[@]}" | grep --line-buffered "$FILTER"
