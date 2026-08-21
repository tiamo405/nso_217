#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}

shopt -s nullglob
worker_dirs=("$WORKERS_DIR"/worker-*)
running=0
stopped=0

printf '%-11s %-8s %-8s %-7s %-10s %s\n' WORKER PID STATE RSS_MB ELAPSED LAST_AUTO_LOG
for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    pid='-'
    state='STOPPED'
    rss='-'
    elapsed='-'

    if [[ -f "$worker_dir/home/worker.done" ]]; then
        state='DONE'
        stopped=$((stopped + 1))
    elif [[ -f "$pid_file" ]]; then
        candidate=$(<"$pid_file")
        if [[ "$candidate" =~ ^[0-9]+$ ]] && kill -0 "$candidate" 2>/dev/null; then
            pid=$candidate
            state='RUNNING'
            rss_kb=$(ps -o rss= -p "$pid" | tr -d ' ')
            rss=$(awk -v kb="${rss_kb:-0}" 'BEGIN { printf "%.1f", kb / 1024 }')
            elapsed=$(ps -o etime= -p "$pid" | tr -d ' ')
            running=$((running + 1))
        else
            stopped=$((stopped + 1))
        fi
    else
        stopped=$((stopped + 1))
    fi

    last_log='-'
    if [[ -f "$worker_dir/stdout.log" ]]; then
        last_log=$(grep 'AUTO NVHN' "$worker_dir/stdout.log" | tail -n 1 || true)
        [[ -n "$last_log" ]] || last_log='-'
    fi
    printf '%-11s %-8s %-8s %-7s %-10s %s\n' "$worker_name" "$pid" "$state" "$rss" "$elapsed" "$last_log"
done

echo "Tổng: running=$running, stopped=$stopped"
