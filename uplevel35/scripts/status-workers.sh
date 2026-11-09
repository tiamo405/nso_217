#!/usr/bin/env bash
# Trạng thái worker: PID, RAM, dòng UPLEVEL35 STATE mới nhất.
#   ./uplevel35/scripts/status-workers.sh [số_worker...]
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

select_workers "$@"
(( ${#WORKER_DIRS[@]} > 0 )) || { echo "Chưa có worker." >&2; exit 1; }
for dir in "${WORKER_DIRS[@]}"; do
    name=$(basename -- "$dir")
    if [[ -f "$dir/home/worker.done" ]]; then
        status="DONE"
    elif pid=$(worker_pid "$dir"); then
        rss=$(awk '/VmRSS/ {printf "%dMB", $2/1024}' "/proc/$pid/status" 2>/dev/null || echo "?")
        status="RUN pid=$pid rss=$rss"
    elif pid=$(loop_pid "$dir"); then
        status="RUN loop=$pid (đổi JVM)"
    else
        status="STOP"
    fi
    last=$(grep -a 'UPLEVEL35 STATE' "$dir/stdout.log" 2>/dev/null | tail -n 1 | sed 's/^.*UPLEVEL35 STATE //' || true)
    printf '%-10s %-28s %s\n' "$name" "$status" "${last:--}"
done
