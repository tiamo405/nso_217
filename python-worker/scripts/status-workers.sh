#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"

if [[ ! -d "$DIST_DIR" ]]; then
    echo "Thư mục $DIST_DIR chưa tồn tại. Vui lòng build-workers trước."
    exit 0
fi

printf "%-12s %-8s %-12s %-8s %-8s %-30s\n" "WORKER" "PID" "STATUS" "%CPU" "%MEM" "LATEST LOG"
printf "%s\n" "-----------------------------------------------------------------------------------------"

for WDIR in "$DIST_DIR"/worker-*; do
    [[ -d "$WDIR" ]] || continue
    WNAME="$(basename "$WDIR")"
    PID_FILE="$WDIR/bot.pid"
    LOG_FILE="$WDIR/stdout.log"
    MARKER_FILE="$WDIR/completed.marker"

    PID="-"
    STATUS="STOPPED"
    CPU="0.0"
    MEM="0.0"

    if [[ -f "$PID_FILE" ]]; then
        P="$(cat "$PID_FILE" 2>/dev/null || echo "")"
        if [[ -n "$P" ]] && kill -0 "$P" 2>/dev/null; then
            PID="$P"
            STATUS="RUNNING"
            # Get CPU and MEM stats if ps available
            if command -v ps &>/dev/null; then
                STATS=$(ps -p "$PID" -o %cpu,%mem --no-headers 2>/dev/null || echo "0.0 0.0")
                CPU=$(echo "$STATS" | awk '{print $1}')
                MEM=$(echo "$STATS" | awk '{print $2}')
            fi
        fi
    fi

    if [[ "$STATUS" != "RUNNING" ]] && [[ -f "$MARKER_FILE" ]]; then
        STATUS="COMPLETED"
    fi

    LAST_LOG=""
    if [[ -f "$LOG_FILE" ]]; then
        LAST_LOG=$(tail -n 1 "$LOG_FILE" | cut -c 1-35)
    fi

    printf "%-12s %-8s %-12s %-8s %-8s %-30s\n" "$WNAME" "$PID" "$STATUS" "$CPU" "$MEM" "$LAST_LOG"
done

