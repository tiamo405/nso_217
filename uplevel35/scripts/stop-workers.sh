#!/usr/bin/env bash
# Dừng worker uplevel35: TERM, sau 4 giây còn sống thì KILL.
#   ./uplevel35/scripts/stop-workers.sh [số_worker...]
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

select_workers "$@"
stopped=0
stop_pid() {
    local pid=$1
    kill -TERM "$pid" 2>/dev/null || true
    for _ in {1..20}; do
        kill -0 "$pid" 2>/dev/null || return 0
        sleep 0.2
    done
    kill -KILL "$pid" 2>/dev/null || true
}

for worker_dir in "${WORKER_DIRS[@]}"; do
    found=0
    # Dừng worker-loop.sh trước để nó không chạy JVM mới, sau đó dừng java còn sót.
    if pid=$(loop_pid "$worker_dir"); then
        echo "Dừng $(basename -- "$worker_dir") (loop PID $pid)"
        stop_pid "$pid"
        found=1
    fi
    if pid=$(worker_pid "$worker_dir"); then
        echo "Dừng $(basename -- "$worker_dir") (java PID $pid)"
        stop_pid "$pid"
        found=1
    fi
    rm -f -- "$worker_dir/bot.pid" "$worker_dir/loop.pid"
    (( found == 0 )) || stopped=$((stopped + 1))
done
echo "Đã dừng $stopped worker."
