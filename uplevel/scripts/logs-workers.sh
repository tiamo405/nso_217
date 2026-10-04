#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${UPLEVEL_WORKERS_DIR:-"$UPLEVEL_DIR/workers"}

if (( $# > 0 )); then
    number=${1#worker-}
    [[ "$number" =~ ^[0-9]+$ ]] || { echo "Worker khong hop le: $number" >&2; exit 2; }
    log_file="$WORKERS_DIR/$(printf 'worker-%02d' "$((10#$number))")/stdout.log"
    [[ -f "$log_file" ]] || { echo "Khong tim thay log: $log_file" >&2; exit 1; }
    exec tail -f "$log_file"
fi

shopt -s nullglob
logs=("$WORKERS_DIR"/worker-*/stdout.log)
(( ${#logs[@]} > 0 )) || { echo "Chua co log worker." >&2; exit 1; }
exec tail -f "${logs[@]}"
