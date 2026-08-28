#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}
CHECK_INTERVAL=${CHECK_INTERVAL:-20}
REPEATED_STATUS_LIMIT=${REPEATED_STATUS_LIMIT:-5}
STALE_LOG_SECONDS=${STALE_LOG_SECONDS:-300}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # supervise all workers
  $(basename "$0") 3               # supervise only worker-03
  $(basename "$0") 3 8 10          # supervise worker-03, worker-08, worker-10
  $(basename "$0") --delay 10      # wait 10s between worker starts
  CHECK_INTERVAL=30 $(basename "$0")
  REPEATED_STATUS_LIMIT=10 $(basename "$0")
  STALE_LOG_SECONDS=300 $(basename "$0")  # restart nếu stdout im lặng 5 phút
EOF
}

worker_args=()
start_args=()
while (( $# > 0 )); do
    case "$1" in
        --delay|-d)
            if (( $# < 2 )) || ! [[ "$2" =~ ^[0-9]+$ ]]; then
                echo "Delay phải là số giây không âm." >&2
                usage
                exit 1
            fi
            start_args+=("$1" "$2")
            shift 2
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            number=${1#worker-}
            if ! [[ "$number" =~ ^[0-9]+$ ]]; then
                echo "Worker không hợp lệ: $1" >&2
                usage
                exit 1
            fi
            worker_args+=("$number")
            start_args+=("$1")
            shift
            ;;
    esac
done

if ! [[ "$CHECK_INTERVAL" =~ ^[1-9][0-9]*$ ]]; then
    echo "CHECK_INTERVAL phải là số giây dương." >&2
    exit 1
fi
if ! [[ "$REPEATED_STATUS_LIMIT" =~ ^[0-9]+$ ]] || (( REPEATED_STATUS_LIMIT < 1 )); then
    echo "REPEATED_STATUS_LIMIT phải là số nguyên dương." >&2
    exit 1
fi
if ! [[ "$STALE_LOG_SECONDS" =~ ^[0-9]+$ ]]; then
    echo "STALE_LOG_SECONDS phải là số giây không âm." >&2
    exit 1
fi

mkdir -p "$WORKERS_DIR"
if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
    old_pid=$(<"$SUPERVISOR_PID_FILE")
    if [[ "$old_pid" =~ ^[0-9]+$ ]] && kill -0 "$old_pid" 2>/dev/null; then
        echo "Headless supervisor đã chạy (PID $old_pid)." >&2
        exit 1
    fi
fi
printf '%s\n' "$$" >"$SUPERVISOR_PID_FILE"

stop_worker_dir() {
    local worker_dir=$1
    local worker_name
    local pid_file
    local pid
    local cmdline

    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    if [[ ! -f "$pid_file" ]]; then
        return
    fi

    pid=$(<"$pid_file")
    if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        return
    fi

    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    if [[ "$cmdline" != *"HeadlessMain"* || "$cmdline" != *"$worker_dir"* ]]; then
        echo "Bỏ qua $worker_name: PID $pid không thuộc headless worker này." >&2
        return
    fi

    kill "$pid"
    for ((attempt = 0; attempt < 50; attempt++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            break
        fi
        sleep 0.1
    done
    if kill -0 "$pid" 2>/dev/null; then
        kill -9 "$pid"
    fi
    rm -f -- "$pid_file"
    echo "Đã dừng $worker_name (PID $pid)"
}

stop_selected() {
    local number
    local worker_name
    local worker_dir

    trap - INT TERM
    rm -f -- "$SUPERVISOR_PID_FILE"
    if (( ${#worker_args[@]} == 0 )); then
        "$SCRIPT_DIR/stop-workers.sh"
    else
        for number in "${worker_args[@]}"; do
            worker_name=$(printf 'worker-%02d' "$((10#$number))")
            worker_dir="$WORKERS_DIR/$worker_name"
            if [[ -d "$worker_dir" ]]; then
                stop_worker_dir "$worker_dir"
            fi
        done
    fi
    exit 0
}
trap stop_selected INT TERM

find_repeated_status() {
    local log_file=$1

    [[ -f "$log_file" ]] || return 1
    awk -v limit="$REPEATED_STATUS_LIMIT" '
        /^===== START / {
            last_key = ""
            repeated = 0
            next
        }
        index($0, "AUTO NVHN PREP: đang tới Okaza") > 0 {
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

find_stale_log() {
    local log_file=$1
    local modified_at now age

    (( STALE_LOG_SECONDS > 0 )) || return 1
    if [[ ! -f "$log_file" ]]; then
        echo "chưa có stdout.log"
        return 0
    fi
    modified_at=$(stat -c %Y -- "$log_file" 2>/dev/null) || return 1
    now=$(date +%s)
    age=$((now - modified_at))
    if (( age >= STALE_LOG_SECONDS )); then
        echo "stdout.log không đổi ${age}s (ngưỡng ${STALE_LOG_SECONDS}s)"
        return 0
    fi
    return 1
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
    if [[ "$cmdline" != *"HeadlessMain"* || "$cmdline" != *"$worker_dir"* ]]; then
        echo "Bỏ qua restart $worker_name: PID $pid không thuộc headless worker này." >&2
        return 1
    fi

    if ! kill "$pid" 2>/dev/null; then
        rm -f -- "$pid_file"
        "$SCRIPT_DIR/start-workers.sh" --delay 0 "$worker_name"
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

    "$SCRIPT_DIR/start-workers.sh" --delay 0 "$worker_name"
}

if (( ${#worker_args[@]} == 0 )); then
    echo "Headless supervisor đang chạy tất cả worker; Ctrl+C để dừng."
else
    echo "Headless supervisor đang chạy worker: ${worker_args[*]}; Ctrl+C để dừng."
fi

while true; do
    shopt -s nullglob
    worker_dirs=()
    if (( ${#worker_args[@]} == 0 )); then
        worker_dirs=("$WORKERS_DIR"/worker-*)
    else
        for number in "${worker_args[@]}"; do
            worker_name=$(printf 'worker-%02d' "$((10#$number))")
            worker_dir="$WORKERS_DIR/$worker_name"
            if [[ -d "$worker_dir" ]]; then
                worker_dirs+=("$worker_dir")
            fi
        done
    fi

    # Java creates worker.done after it has processed every account. The first
    # completion is archived atomically so start-workers can launch one audit
    # pass. A worker.done created while this archive exists is the final result.
    for worker_dir in "${worker_dirs[@]}"; do
        done_marker="$worker_dir/home/worker.done"
        first_pass_marker="$worker_dir/home/worker.first-pass.done"
        if [[ -f "$done_marker" && ! -f "$first_pass_marker" ]]; then
            mv -- "$done_marker" "$first_pass_marker"
            worker_name=$(basename -- "$worker_dir")
            echo "[$(date '+%F %T')] $worker_name đã xong lượt 1/2; chuẩn bị chạy kiểm tra lượt 2/2."
        fi
    done

    "$SCRIPT_DIR/start-workers.sh" "${start_args[@]}" || true

    for worker_dir in "${worker_dirs[@]}"; do
        [[ -f "$worker_dir/home/worker.done" ]] && continue
        pid_file="$worker_dir/bot.pid"
        [[ -f "$pid_file" ]] || continue
        pid=$(<"$pid_file")
        if ! [[ "$pid" =~ ^[0-9]+$ ]] || ! kill -0 "$pid" 2>/dev/null; then
            continue
        fi

        worker_name=$(basename -- "$worker_dir")
        if stale_reason=$(find_stale_log "$worker_dir/stdout.log"); then
            echo "[$(date '+%F %T')] $worker_name không có log mới; đang restart."
            echo "Lý do: $stale_reason"
            restart_worker "$worker_dir" || true
            continue
        fi

        if repeated_status=$(find_repeated_status "$worker_dir/stdout.log"); then
            echo "[$(date '+%F %T')] $worker_name có trạng thái AUTO NVHN bị lặp từ $REPEATED_STATUS_LIMIT lần liên tiếp; đang restart."
            echo "Trạng thái bị lặp: $repeated_status"
            restart_worker "$worker_dir" || true
        fi
    done

    sleep "$CHECK_INTERVAL"
done
