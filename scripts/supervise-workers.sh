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
            last_key = ""
            repeated = 0
            next
        }
        index($0, "AUTO NVHN PREP: đang tới Okaza để mua thức ăn và lật hình") > 0 {
            prep = substr($0, index($0, "AUTO NVHN PREP:"))
            key = "prep " prep
            if (key == last_key) {
                repeated++
            } else {
                last_key = key
                repeated = 1
            }
            last_status = prep
            next
        }
        /^AUTO NVHN STATUS:/ {
            nvhn = ""
            progress = ""
            if (match($0, /nvhn=[0-9]+\/20/)) {
                nvhn = substr($0, RSTART, RLENGTH)
            }
            if (match($0, /progress=[0-9]+\/[0-9]+/)) {
                progress = substr($0, RSTART, RLENGTH)
            }
            if (nvhn == "" || progress == "") {
                last_key = ""
                repeated = 0
                next
            }

            key = nvhn " " progress
            if (key == last_key) {
                repeated++
            } else {
                last_key = key
                repeated = 1
            }
            last_status = $0
        }
        END {
            if (repeated >= limit) {
                print last_key " | " last_status
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
            echo "$worker_name có trạng thái AUTO NVHN bị lặp từ $REPEATED_STATUS_LIMIT lần liên tiếp; đang restart."
            echo "Trạng thái bị lặp: $repeated_status"
            restart_worker "$worker_dir" || true
        fi
    done

    sleep "$CHECK_INTERVAL"
done
