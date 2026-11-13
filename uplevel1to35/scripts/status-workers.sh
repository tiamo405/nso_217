#!/usr/bin/env bash
# Trạng thái worker: PID, RAM, dòng STATE mới nhất (UPLEVEL STATE của As20 hoặc UPLEVEL35 STATE của As35).
#   ./uplevel1to35/scripts/status-workers.sh [số_worker...]
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
    last=$(grep -a -E 'UPLEVEL(35)? STATE ' "$dir/stdout.log" 2>/dev/null | tail -n 1 \
        | sed -E 's/^.*UPLEVEL(35)? STATE /\1 /; s/^35 /[as35] /; s/^ /[as20] /' || true)
    printf '%-10s %-28s %s\n' "$name" "$status" "${last:--}"
done
