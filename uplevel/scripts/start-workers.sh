#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
BUILD_DIR=$(realpath -m -- "${UPLEVEL_BUILD_DIR:-"$UPLEVEL_DIR/build"}")
CLASSES_DIR="$BUILD_DIR/classes"
RUN_DIR=${UPLEVEL_RUN_DIR:-"$UPLEVEL_DIR/run"}
WORKERS_DIR=${UPLEVEL_WORKERS_DIR:-"$UPLEVEL_DIR/workers"}
JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-8m}
JAVA_XMX=${JAVA_XMX:-36m}
START_DELAY=${UPLEVEL_START_DELAY:-3}
AS20_MODE=${UPLEVEL_MODE:-0}
SERVER_NAME=${UPLEVEL_SERVER:-ninjamobile}
JAVA_OPTS=${JAVA_OPTS:-}

if [[ "$AS20_MODE" == 0 ]]; then
    AS20_MODE=1
fi

usage() {
    echo "Usage: $(basename "$0") [--mode 1..6] [--delay seconds] [worker_number...]" >&2
}

worker_args=()
while (( $# > 0 )); do
    case "$1" in
        --mode)
            (( $# >= 2 )) || { usage; exit 2; }
            AS20_MODE=$2
            shift 2
            ;;
        --mode=*)
            AS20_MODE=${1#--mode=}
            shift
            ;;
        --delay)
            (( $# >= 2 )) || { usage; exit 2; }
            START_DELAY=$2
            shift 2
            ;;
        --delay=*)
            START_DELAY=${1#--delay=}
            shift
            ;;
        --help|-h)
            usage
            exit 0
            ;;
        *)
            number=${1#worker-}
            [[ "$number" =~ ^[0-9]+$ ]] || { usage; exit 2; }
            worker_args+=("$number")
            shift
            ;;
    esac
done

if [[ "$AS20_MODE" == 0 ]]; then
    AS20_MODE=1
fi
if ! [[ "$AS20_MODE" =~ ^[1-6]$ ]]; then
    echo "Mode phai nam trong 1..6." >&2
    exit 1
fi
if ! [[ "$START_DELAY" =~ ^[0-9]+$ ]]; then
    echo "Delay phai la so nguyen khong am." >&2
    exit 1
fi
if [[ ! -d "$CLASSES_DIR" ]]; then
    echo "Chua co classes; chay build-workers.sh hoac build.sh truoc." >&2
    exit 1
fi

shopt -s nullglob
worker_dirs=()
if (( ${#worker_args[@]} > 0 )); then
    for number in "${worker_args[@]}"; do
        worker_name=$(printf 'worker-%02d' "$((10#$number))")
        worker_dir="$WORKERS_DIR/$worker_name"
        [[ -d "$worker_dir" ]] || { echo "Khong tim thay $worker_name." >&2; exit 1; }
        worker_dirs+=("$worker_dir")
    done
else
    worker_dirs=("$WORKERS_DIR"/worker-*)
    (( ${#worker_dirs[@]} > 0 )) || { echo "Chua co worker; chay build-workers.sh truoc." >&2; exit 1; }
fi

read -r -a JAVA_OPTS_ARRAY <<< "$JAVA_OPTS"
mkdir -p "$RUN_DIR/nvhn-errors"
started=0
running=0
completed=0

is_worker_pid() {
    local pid=$1
    local worker_dir=$2
    local cmdline
    [[ "$pid" =~ ^[0-9]+$ && -r "/proc/$pid/cmdline" ]] || return 1
    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    [[ "$cmdline" == *"OptimizedMain"* && "$cmdline" == *"$worker_dir"* ]]
}

for worker_dir in "${worker_dirs[@]}"; do
    worker_name=$(basename -- "$worker_dir")
    pid_file="$worker_dir/bot.pid"
    if [[ -f "$worker_dir/home/worker.done" ]]; then
        echo "$worker_name da hoan tat"
        completed=$((completed + 1))
        continue
    fi
    if [[ -f "$pid_file" ]]; then
        pid=$(<"$pid_file")
        if is_worker_pid "$pid" "$worker_dir"; then
            echo "$worker_name dang chay (PID $pid)"
            running=$((running + 1))
            continue
        fi
        rm -f -- "$pid_file"
    fi

    mkdir -p "$worker_dir/home"
    if [[ "${UPLEVEL_LOG_APPEND:-0}" != 1 ]]; then
        : >"$worker_dir/stdout.log"
        : >"$worker_dir/java-errors.log"
    fi
    printf '\n===== START UPLEVEL %s mode=%s =====\n' "$(date '+%F %T')" "$AS20_MODE" >>"$worker_dir/stdout.log"
    printf '\n===== START UPLEVEL %s mode=%s =====\n' "$(date '+%F %T')" "$AS20_MODE" >>"$worker_dir/java-errors.log"
    nohup "$JAVA_BIN" "-Xms$JAVA_XMS" "-Xmx$JAVA_XMX" \
        "${JAVA_OPTS_ARRAY[@]}" \
        -Dnso.server="$SERVER_NAME" \
        -Dnso.as20.mode="$AS20_MODE" \
        -Dnso.worker.name="$worker_name" \
        -Dnso.nvhn.error.dir="$RUN_DIR/nvhn-errors" \
        -Dnso.uplevel.crystal="${UPLEVEL_CRYSTAL_ID:--1}" \
        -Dnso.uplevel.all.crystals="${UPLEVEL_ALL_CRYSTALS:-false}" \
        -Dnso.uplevel.duplicate="${UPLEVEL_UPGRADE_DUPLICATE:-false}" \
        -Dnso.uplevel.duplicate.count="${UPLEVEL_UPGRADE_DUPLICATE_COUNT:-5}" \
        -Dnso.uplevel.no.split="${UPLEVEL_NO_SPLIT:-false}" \
        -Dnso.uplevel.allow.stacks="${UPLEVEL_ALLOW_STACKS:-false}" \
        -Dnso.uplevel.upgrade.safe="${UPLEVEL_UPGRADE_SAFE:-false}" \
        -Duser.home="$worker_dir/home" \
        -cp "$worker_dir:$CLASSES_DIR" \
        OptimizedMain \
        >>"$worker_dir/stdout.log" 2>>"$worker_dir/java-errors.log" &

    pid=$!
    printf '%s\n' "$pid" >"$pid_file"
    sleep 0.3
    if is_worker_pid "$pid" "$worker_dir"; then
        echo "Da chay $worker_name (PID $pid)"
        started=$((started + 1))
    else
        echo "$worker_name khoi dong loi; xem $worker_dir/java-errors.log" >&2
        rm -f -- "$pid_file"
    fi
    (( START_DELAY == 0 )) || sleep "$START_DELAY"
done

echo "Ket qua: moi=$started, dang chay=$running, hoan tat=$completed"
