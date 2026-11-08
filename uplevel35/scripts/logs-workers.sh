#!/usr/bin/env bash
# Xem log worker uplevel35.
#   ./uplevel35/scripts/logs-workers.sh            tail -f tất cả worker
#   ./uplevel35/scripts/logs-workers.sh 1          tail -f worker-01
#   ./uplevel35/scripts/logs-workers.sh 1 --grep   chỉ dòng UPLEVEL35 (bỏ log game)
#   ./uplevel35/scripts/logs-workers.sh --errors   tail java-errors.log
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

file=stdout.log
only_uplevel=0
args=()
for arg in "$@"; do
    case "$arg" in
        --errors) file=java-errors.log ;;
        --grep) only_uplevel=1 ;;
        *) args+=("$arg") ;;
    esac
done

select_workers "${args[@]+"${args[@]}"}"
logs=()
for dir in "${WORKER_DIRS[@]}"; do
    [[ -f "$dir/$file" ]] && logs+=("$dir/$file")
done
(( ${#logs[@]} > 0 )) || { echo "Chưa có log worker." >&2; exit 1; }

if (( only_uplevel )); then
    tail -n 200 -f "${logs[@]}" | grep --line-buffered -E '==>|UPLEVEL35|AUTO NVHN|AUTO LOGIN|Exception'
else
    exec tail -n 200 -f "${logs[@]}"
fi
