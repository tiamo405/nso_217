#!/usr/bin/env bash
# Xem log worker uplevel1to35.
#   ./uplevel1to35/scripts/logs-workers.sh            tail -f tất cả worker
#   ./uplevel1to35/scripts/logs-workers.sh 1          tail -f worker-01
#   ./uplevel1to35/scripts/logs-workers.sh 1 --grep   chỉ dòng UPLEVEL* / AUTO (bỏ log game)
#   ./uplevel1to35/scripts/logs-workers.sh --errors   tail java-errors.log
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
    tail -n 200 -f "${logs[@]}" | grep --line-buffered -E '==>|=====|UPLEVEL|AUTO NVHN|AUTO LOGIN|AUTO AS20|Exception'
else
    exec tail -n 200 -f "${logs[@]}"
fi
