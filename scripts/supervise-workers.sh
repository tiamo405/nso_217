#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
REPEATED_STATUS_LIMIT=${REPEATED_STATUS_LIMIT:-5}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

if ! [[ "$REPEATED_STATUS_LIMIT" =~ ^[0-9]+$ ]] || (( REPEATED_STATUS_LIMIT < 1 )); then
    echo "REPEATED_STATUS_LIMIT phải là số nguyên dương." >&2
    exit 1
fi

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

find_repeated_status() {
    local log_file=$1

    [[ -f "$log_file" ]] || return 1
    awk -v limit="$REPEATED_STATUS_LIMIT" '
        /^===== START / {
            last_status = ""
            repeated = 0
            next
        }
        /^AUTO NVHN STATUS:/ {
            if ($0 == last_status) {
                repeated++
            } else {
                last_status = $0
                repeated = 1
            }
        }
        END {
            if (repeated > limit) {
                print last_status
                exit 0
            }
            exit 1
        }
    ' "$log_file"
}

restart_worker() {
    local worker_dir=$1
    local worker_name pid_file pid cmdline

    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    [[ -f "$pid_file" ]] || return 0

    pid=$(<"$pid_file")
    if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        return 0
    fi

    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    if [[ "$cmdline" != *"$worker_dir/client_217.jar"* ]]; then
        echo "Bỏ qua restart $worker_name: PID $pid không thuộc worker này." >&2
        return 1
    fi

    if ! kill "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        "$SCRIPT_DIR/start-workers.sh" "$worker_name"
        return
    fi
    for ((attempt = 0; attempt < 50; attempt++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            break
        fi
        sleep 0.1
    done
    if kill -0 "$pid" 2>/dev/null; then
        kill -9 "$pid" 2>/dev/null || true
    fi
    rm -f -- "$pid_file"

    "$SCRIPT_DIR/start-workers.sh" "$worker_name"
}

echo "Supervisor đang chạy; Ctrl+C để dừng toàn bộ worker."
while true; do
    "$SCRIPT_DIR/start-workers.sh" || true

    shopt -s nullglob
    worker_dirs=("$WORKERS_DIR"/worker-*)
    for worker_dir in "${worker_dirs[@]}"; do
        pid_file="$worker_dir/bot.pid"
        [[ -f "$pid_file" ]] || continue
        pid=$(<"$pid_file")
        if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
            continue
        fi

        if repeated_status=$(find_repeated_status "$worker_dir/stdout.log"); then
            worker_name=$(basename -- "$worker_dir")
            echo "$worker_name có AUTO NVHN STATUS giống nhau quá $REPEATED_STATUS_LIMIT lần; đang restart."
            echo "Trạng thái bị lặp: $repeated_status"
            restart_worker "$worker_dir" || true
        fi
    done

    sleep "$CHECK_INTERVAL"
done
