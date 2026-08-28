#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}
CLASSES_DIR=${HEADLESS_CLASSES_DIR:-"$HEADLESS_DIR/build/classes"}
JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-8m}
JAVA_XMX=${JAVA_XMX:-48m}
JAVA_OPTS=${JAVA_OPTS:-"-XX:+UseSerialGC -XX:MinHeapFreeRatio=5 -XX:MaxHeapFreeRatio=10 -Djava.awt.headless=true"}
START_DELAY=${START_DELAY:-10}
WORKER_NICE=${WORKER_NICE:-}
WORKER_TASKSET=${WORKER_TASKSET:-}

usage() {
    cat >&2 <<EOF
Usage: $(basename "$0") [--delay seconds] [worker_number...]

Examples:
  $(basename "$0")                 # start all workers
  $(basename "$0") 3               # start only worker-03
  $(basename "$0") 3 8 10          # start worker-03, worker-08, worker-10
  $(basename "$0") --delay 10      # start all, wait 10s between workers
  START_DELAY=10 $(basename "$0") 3
EOF
}

worker_args=()
while (( $# > 0 )); do
    case "$1" in
        --delay|-d)
            if (( $# < 2 )) || ! [[ "$2" =~ ^[0-9]+$ ]]; then
                echo "Delay phải là số giây không âm." >&2
                usage
                exit 1
            fi
            START_DELAY=$2
            shift 2
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            worker_args+=("$1")
            shift
            ;;
    esac
done

if ! [[ "$START_DELAY" =~ ^[0-9]+$ ]]; then
    echo "START_DELAY phải là số giây không âm." >&2
    exit 1
fi

if [[ ! -d "$CLASSES_DIR" ]]; then
    echo "Chưa có headless classes. Chạy headless-runtime/scripts/build-workers.sh trước." >&2
    exit 1
fi

shopt -s nullglob
worker_dirs=()
if (( ${#worker_args[@]} > 0 )); then
    for number in "${worker_args[@]}"; do
        number=${number#worker-}
        if ! [[ "$number" =~ ^[0-9]+$ ]]; then
            echo "Worker không hợp lệ: $number" >&2
            usage
            exit 1
        fi
        worker_name=$(printf 'worker-%02d' "$((10#$number))")
        worker_dir="$WORKERS_DIR/$worker_name"
        if [[ ! -d "$worker_dir" ]]; then
            echo "Không tìm thấy $worker_name tại $WORKERS_DIR" >&2
            exit 1
        fi
        worker_dirs+=("$worker_dir")
    done
else
    worker_dirs=("$WORKERS_DIR"/worker-*)
    if (( ${#worker_dirs[@]} == 0 )); then
        echo "Chưa có headless worker. Chạy headless-runtime/scripts/build-workers.sh trước." >&2
        exit 1
    fi
fi

started=0
running=0
failed=0
completed=0
paused=0

for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"

    if [[ -f "$worker_dir/.paused" ]]; then
        echo "$worker_name đang tạm dừng, không khởi động"
        paused=$((paused + 1))
        continue
    fi
    worker_pass=1

    if [[ -f "$worker_dir/home/worker.first-pass.done" ]]; then
        worker_pass=2
    fi

    if [[ -f "$worker_dir/home/worker.done" ]]; then
        echo "$worker_name đã hoàn tất toàn bộ account, không khởi động lại"
        completed=$((completed + 1))
        continue
    fi

    if [[ -f "$pid_file" ]]; then
        pid=$(<"$pid_file")
        if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
            echo "$worker_name đang chạy (PID $pid)"
            running=$((running + 1))
            continue
        fi
        rm -f -- "$pid_file"
    fi

    mkdir -p "$worker_dir/home"
    printf '\n===== START %s PASS %s/2 =====\n' "$(date '+%F %T')" "$worker_pass" >>"$worker_dir/stdout.log"
    printf '\n===== START %s PASS %s/2 =====\n' "$(date '+%F %T')" "$worker_pass" >>"$worker_dir/java-errors.log"

    read -r -a java_opts_array <<< "$JAVA_OPTS"
    command_prefix=()
    if [[ -n "$WORKER_NICE" ]]; then
        command_prefix+=(nice -n "$WORKER_NICE")
    fi
    if [[ -n "$WORKER_TASKSET" ]]; then
        command_prefix+=(taskset -c "$WORKER_TASKSET")
    fi

    # Recheck immediately before launch so a web Stop racing this loop wins.
    if [[ -f "$worker_dir/.paused" ]]; then
        echo "$worker_name vừa được tạm dừng, không khởi động"
        paused=$((paused + 1))
        continue
    fi

    nohup "${command_prefix[@]}" "$JAVA_BIN" \
        "-Xms$JAVA_XMS" \
        "-Xmx$JAVA_XMX" \
        "${java_opts_array[@]}" \
        "-Duser.home=$worker_dir/home" \
        -cp "$worker_dir:$CLASSES_DIR" \
        HeadlessMain \
        >>"$worker_dir/stdout.log" \
        2>>"$worker_dir/java-errors.log" &

    pid=$!
    printf '%s\n' "$pid" >"$pid_file"

    # If Stop arrived between the last check and PID creation, terminate the
    # new process now instead of leaving a paused worker running unmanaged.
    if [[ -f "$worker_dir/.paused" ]]; then
        "$SCRIPT_DIR/stop-workers.sh" "$worker_name"
        paused=$((paused + 1))
        continue
    fi

    sleep 0.3

    if kill -0 "$pid" 2>/dev/null; then
        echo "Đã chạy $worker_name (PID $pid)"
        started=$((started + 1))
    else
        echo "$worker_name khởi động lỗi; xem $worker_dir/java-errors.log" >&2
        rm -f -- "$pid_file"
        failed=$((failed + 1))
    fi

    if (( START_DELAY > 0 )); then
        sleep "$START_DELAY"
    fi
done

echo "Kết quả: mới chạy=$started, đã chạy=$running, tạm dừng=$paused, hoàn tất=$completed, lỗi=$failed"
(( failed == 0 ))
