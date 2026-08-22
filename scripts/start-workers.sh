#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}
MICROEMULATOR_JAR=${MICROEMULATOR_JAR:-"/home/namtp/Downloads/game-teamobi/Microemulator.jar"}
JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-16m}
JAVA_XMX=${JAVA_XMX:-96m}
JAVA_OPTS=${JAVA_OPTS:-"-XX:+UseSerialGC -XX:MinHeapFreeRatio=5 -XX:MaxHeapFreeRatio=10 -Djava.awt.headless=true"}
RMS_TEMPLATE_DIR=${RMS_TEMPLATE_DIR:-"/home/namtp/.microemulator/suite-NSO_217"}
START_DELAY=${START_DELAY:-3}
WORKER_NICE=${WORKER_NICE:-}
WORKER_TASKSET=${WORKER_TASKSET:-}

if [[ ! -f "$MICROEMULATOR_JAR" ]]; then
    echo "Không tìm thấy MicroEmulator: $MICROEMULATOR_JAR" >&2
    exit 1
fi

shopt -s nullglob
worker_dirs=()
if (( $# > 0 )); then
    for number in "$@"; do
        number=${number#worker-}
        if ! [[ "$number" =~ ^[0-9]+$ ]]; then
            echo "Worker không hợp lệ: $number" >&2
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
        echo "Chưa có worker. Chạy scripts/build-workers.sh trước." >&2
        exit 1
    fi
fi

started=0
running=0
failed=0
completed=0

for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"

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
    rms_dir="$worker_dir/home/.microemulator/nso-$worker_name/suite-NSO_217"
    mkdir -p "$rms_dir"
    if [[ -d "$RMS_TEMPLATE_DIR" ]]; then
        cp -a -n "$RMS_TEMPLATE_DIR"/. "$rms_dir"/
        # Tài khoản đăng nhập luôn do AccountAutoManager lấy từ CSV trong JAR.
        rm -f -- "$rms_dir/vjacc.rs" "$rms_dir/vjpass.rs" "$rms_dir/vjabcdip.rs"
    fi
    printf '\n===== START %s =====\n' "$(date '+%F %T')" >>"$worker_dir/stdout.log"
    printf '\n===== START %s =====\n' "$(date '+%F %T')" >>"$worker_dir/java-errors.log"

    read -r -a java_opts_array <<< "$JAVA_OPTS"
    command_prefix=()
    if [[ -n "$WORKER_NICE" ]]; then
        command_prefix+=(nice -n "$WORKER_NICE")
    fi
    if [[ -n "$WORKER_TASKSET" ]]; then
        command_prefix+=(taskset -c "$WORKER_TASKSET")
    fi

    launcher=()
    if command -v setsid >/dev/null 2>&1; then
        launcher+=(setsid)
    else
        launcher+=(nohup)
    fi

    "${launcher[@]}" "${command_prefix[@]}" "$JAVA_BIN" \
        "-Xms$JAVA_XMS" \
        "-Xmx$JAVA_XMX" \
        "${java_opts_array[@]}" \
        "-Duser.home=$worker_dir/home" \
        -cp "$MICROEMULATOR_JAR" \
        org.microemu.app.Headless \
        --id "nso-$worker_name" \
        --rms file \
        "$worker_dir/client_217.jar" \
        >>"$worker_dir/stdout.log" \
        2>>"$worker_dir/java-errors.log" &

    pid=$!
    printf '%s\n' "$pid" >"$pid_file"
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

echo "Kết quả: mới chạy=$started, đã chạy=$running, hoàn tất=$completed, lỗi=$failed"
(( failed == 0 ))
