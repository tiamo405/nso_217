#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}

if [[ ${1:-} == "--json" ]]; then
    if (( $# != 1 )); then
        echo "Usage: $(basename "$0") [--json]" >&2
        exit 2
    fi
    exec python3 "$SCRIPT_DIR/status_workers_json.py" "$WORKERS_DIR"
fi

if (( $# != 0 )); then
    echo "Usage: $(basename "$0") [--json]" >&2
    exit 2
fi

shopt -s nullglob
worker_dirs=("$WORKERS_DIR"/worker-*)
running=0
stopped=0

printf '%-11s %-8s %-8s %-7s %-7s %-10s %-9s %s\n' WORKER PID STATE CPU RSS_MB ELAPSED ACCOUNTS LAST_AUTO_LOG
for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    pid='-'
    state='STOPPED'
    cpu='-'
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
            cpu=$(ps -o pcpu= -p "$pid" | tr -d ' ')
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

    accounts='-'
    if [[ -f "$worker_dir/account.csv" ]]; then
        accounts=$(awk 'NR > 1 && $0 !~ /^[[:space:]]*$/ { count++ } END { print count + 0 }' "$worker_dir/account.csv")
    fi

    last_log='-'
    if [[ -f "$worker_dir/stdout.log" ]]; then
        last_log=$(grep 'AUTO NVHN' "$worker_dir/stdout.log" | tail -n 1 || true)
        [[ -n "$last_log" ]] || last_log='-'
    fi

    printf '%-11s %-8s %-8s %-7s %-7s %-10s %-9s %s\n' "$worker_name" "$pid" "$state" "$cpu" "$rss" "$elapsed" "$accounts" "$last_log"
done

echo "Tổng: running=$running, stopped=$stopped"
