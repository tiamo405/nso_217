#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}

if [[ ${1:-} == "--json" ]]; then
    if (( $# != 1 )); then
        echo "Usage: $(basename "$0") [--json]" >&2
        exit 2
    fi
    exec python3 "$SCRIPT_DIR/status_workers_json.py" "$WORKERS_DIR"
fi

printf '%-12s %-8s %-8s %-10s %-20s\n' "WORKER" "PID" "%CPU" "RSS (MB)" "STATUS"
printf '%-12s %-8s %-8s %-10s %-20s\n' "------" "---" "----" "--------" "------"

for worker_dir in "$WORKERS_DIR"/worker-*; do
    [[ -d "$worker_dir" ]] || continue
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"

    status="STOPPED"
    pid="-"
    cpu="-"
    rss="-"

    if [[ -f "$worker_dir/home/worker.done" ]]; then
        status="DONE"
    elif [[ -f "$pid_file" ]]; then
        current_pid=$(<"$pid_file")
        if [[ "$current_pid" =~ ^[0-9]+$ ]] && kill -0 "$current_pid" 2>/dev/null; then
            status="RUNNING"
            pid="$current_pid"
            stats=$(ps -p "$pid" -o %cpu,rss --no-headers 2>/dev/null || echo "0 0")
            cpu=$(echo "$stats" | awk '{print $1}')
            rss_kb=$(echo "$stats" | awk '{print $2}')
            rss=$(awk -v kb="$rss_kb" 'BEGIN {printf "%.1f", kb/1024}')
        fi
    fi

    printf '%-12s %-8s %-8s %-10s %-20s\n' "$worker_name" "$pid" "$cpu" "$rss" "$status"
done
