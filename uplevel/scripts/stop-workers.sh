#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${UPLEVEL_WORKERS_DIR:-"$UPLEVEL_DIR/workers"}

is_worker_pid() {
    local pid=$1
    local worker_dir=$2
    local cmdline
    [[ "$pid" =~ ^[0-9]+$ && -r "/proc/$pid/cmdline" ]] || return 1
    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    [[ "$cmdline" == *"OptimizedMain"* && "$cmdline" == *"$worker_dir"* ]]
}

worker_dirs=()
if (( $# > 0 )); then
    for number in "$@"; do
        number=${number#worker-}
        [[ "$number" =~ ^[0-9]+$ ]] || { echo "Worker khong hop le: $number" >&2; exit 2; }
        worker_dirs+=("$WORKERS_DIR/$(printf 'worker-%02d' "$((10#$number))")")
    done
else
    shopt -s nullglob
    worker_dirs=("$WORKERS_DIR"/worker-*)
fi

stopped=0
for worker_dir in "${worker_dirs[@]}"; do
    pid_file="$worker_dir/bot.pid"
    [[ -f "$pid_file" ]] || continue
    pid=$(<"$pid_file")
    if ! is_worker_pid "$pid" "$worker_dir"; then
        rm -f -- "$pid_file"
        continue
    fi
    echo "Dung $(basename -- "$worker_dir") (PID $pid)"
    kill -TERM "$pid" 2>/dev/null || true
    for _ in {1..20}; do
        kill -0 "$pid" 2>/dev/null || break
        sleep 0.2
    done
    if kill -0 "$pid" 2>/dev/null; then
        kill -KILL "$pid" 2>/dev/null || true
    fi
    rm -f -- "$pid_file"
    stopped=$((stopped + 1))
done
echo "Da dung $stopped worker."
