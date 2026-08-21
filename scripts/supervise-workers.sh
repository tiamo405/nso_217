#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
    old_pid=$(<"$SUPERVISOR_PID_FILE")
    if [[ "$old_pid" =~ ^[0-9]+$ ]] && kill -0 "$old_pid" 2>/dev/null; then
        echo "Supervisor đã chạy (PID $old_pid)." >&2
        exit 1
    fi
fi
printf '%s\n' "$$" >"$SUPERVISOR_PID_FILE"

stop_all() {
    trap - INT TERM
    rm -f -- "$SUPERVISOR_PID_FILE"
    "$SCRIPT_DIR/stop-workers.sh"
    exit 0
}
trap stop_all INT TERM

echo "Supervisor đang chạy; Ctrl+C để dừng toàn bộ worker."
while true; do
    "$SCRIPT_DIR/start-workers.sh" || true
    sleep "$CHECK_INTERVAL"
done
