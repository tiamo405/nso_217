#!/usr/bin/env bash
# Dừng worker uplevel35: TERM, sau 4 giây còn sống thì KILL.
#   ./uplevel35/scripts/stop-workers.sh [số_worker...]
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

select_workers "$@"
stopped=0
for worker_dir in "${WORKER_DIRS[@]}"; do
    if ! pid=$(worker_pid "$worker_dir"); then
        rm -f -- "$worker_dir/bot.pid"
        continue
    fi
    echo "Dừng $(basename -- "$worker_dir") (PID $pid)"
    kill -TERM "$pid" 2>/dev/null || true
    for _ in {1..20}; do
        kill -0 "$pid" 2>/dev/null || break
        sleep 0.2
    done
    kill -0 "$pid" 2>/dev/null && kill -KILL "$pid" 2>/dev/null || true
    rm -f -- "$worker_dir/bot.pid"
    stopped=$((stopped + 1))
done
echo "Đã dừng $stopped worker."
