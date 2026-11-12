#!/usr/bin/env bash
# Chạy worker uplevel35 (nền, nohup).
#   ./uplevel35/scripts/start-workers.sh [--delay giây] [--debug] [--no-proxy] [số_worker...]
# Proxy: có proxy trong proxy-tk.txt (UPLEVEL35_PROXY_FILE) thì worker chia đều theo khối liên tiếp;
#        file trống thì chạy mạng máy; --no-proxy / UPLEVEL35_PROXY=0 để ép tắt.
# Biến môi trường: UPLEVEL35_SERVER, UPLEVEL35_MIN, UPLEVEL35_TARGET, UPLEVEL35_LOG_APPEND=1,
#                  JAVA_BIN, JAVA_XMS, JAVA_XMX, JAVA_OPTS (thêm -D tùy ý, vd. -Dnso.uplevel35.bottle.id=...).
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-8m}
JAVA_XMX=${JAVA_XMX:-36m}
START_DELAY=${UPLEVEL35_START_DELAY:-3}
SERVER_NAME=${UPLEVEL35_SERVER:-180.93.114.122:14444}
DEBUG=${UPLEVEL35_DEBUG:-false}
JAVA_OPTS=${JAVA_OPTS:-}
# true: mỗi nhân vật một JVM (worker-loop.sh), tránh static của nhân vật trước dính sang nhân vật sau.
PROCESS_PER_CHARACTER=${UPLEVEL35_PROCESS_PER_CHARACTER:-true}
USE_PROXY=${UPLEVEL35_PROXY:-1}

args=()
while (( $# > 0 )); do
    case "$1" in
        --delay) START_DELAY=$2; shift 2 ;;
        --delay=*) START_DELAY=${1#--delay=}; shift ;;
        --debug) DEBUG=true; shift ;;
        --no-proxy) USE_PROXY=0; shift ;;
        --proxy) USE_PROXY=1; shift ;;
        -h|--help) echo "Usage: $(basename "$0") [--delay seconds] [--debug] [--no-proxy] [worker_number...]"; exit 0 ;;
        *) args+=("$1"); shift ;;
    esac
done
[[ "$START_DELAY" =~ ^[0-9]+$ ]] || { echo "Delay phải là số nguyên không âm." >&2; exit 1; }
[[ -d "$CLASSES_DIR" ]] || { echo "Chưa có classes; chạy build-workers.sh trước." >&2; exit 1; }

select_workers "${args[@]+"${args[@]}"}"
(( ${#WORKER_DIRS[@]} > 0 )) || { echo "Chưa có worker; chạy build-workers.sh trước." >&2; exit 1; }

if [[ "$USE_PROXY" == 1 ]]; then
    python3 "$PROXY_ASSIGN" --workers-dir "$WORKERS_DIR" --proxy-file "$PROXY_FILE" \
        --server "$SERVER_NAME" || exit 1
else
    python3 "$PROXY_ASSIGN" --workers-dir "$WORKERS_DIR" --proxy-file "$PROXY_FILE" --clear
fi

read -r -a JAVA_OPTS_ARRAY <<<"$JAVA_OPTS"
started=0 running=0 completed=0
for worker_dir in "${WORKER_DIRS[@]}"; do
    name=$(basename -- "$worker_dir")
    if [[ -f "$worker_dir/home/worker.done" ]]; then
        echo "$name đã hoàn tất (xóa home/worker.done để chạy lại)"
        completed=$((completed + 1))
        continue
    fi
    if pid=$(loop_pid "$worker_dir") || pid=$(worker_pid "$worker_dir"); then
        echo "$name đang chạy (PID $pid)"
        running=$((running + 1))
        continue
    fi
    rm -f -- "$worker_dir/bot.pid" "$worker_dir/loop.pid"
    mkdir -p "$worker_dir/home"
    if [[ "${UPLEVEL35_LOG_APPEND:-0}" != 1 ]]; then
        : >"$worker_dir/stdout.log"
        : >"$worker_dir/java-errors.log"
    fi
    banner="===== START UPLEVEL35 $(date '+%F %T') ====="
    printf '\n%s\n' "$banner" >>"$worker_dir/stdout.log"
    printf '\n%s\n' "$banner" >>"$worker_dir/java-errors.log"
    # NSO_SOCKS5_PROXY được Connector.java đọc; NSO_PROXY_REQUIRED=1 => không fallback IP máy.
    proxy_env=(env -u NSO_SOCKS5_PROXY NSO_PROXY_REQUIRED=0)
    proxy_label="IP máy"
    if [[ -s "$worker_dir/.proxy" ]]; then
        proxy_value=$(<"$worker_dir/.proxy")
        proxy_env=(env NSO_PROXY_REQUIRED=1 "NSO_SOCKS5_PROXY=$proxy_value")
        proxy_label="proxy ${proxy_value%%:*}"
    fi
    printf 'network: %s\n' "$proxy_label" >>"$worker_dir/stdout.log"
    nohup "${proxy_env[@]}" "$SCRIPT_DIR/worker-loop.sh" "$worker_dir" \
        "$JAVA_BIN" "-Xms$JAVA_XMS" "-Xmx$JAVA_XMX" \
        ${JAVA_OPTS_ARRAY[@]+"${JAVA_OPTS_ARRAY[@]}"} \
        -Dnso.runtime=uplevel35 \
        -Dnso.server="$SERVER_NAME" \
        -Dnso.worker.name="$name" \
        -Dnso.nvhn.error.dir="$RUNTIME_DIR/run/errors" \
        -Dnso.uplevel35.min="${UPLEVEL35_MIN:-25}" \
        -Dnso.uplevel35.target="${UPLEVEL35_TARGET:-35}" \
        -Dnso.uplevel35.debug="$DEBUG" \
        -Dnso.process.per.character="$PROCESS_PER_CHARACTER" \
        -Duser.home="$worker_dir/home" \
        -cp "$worker_dir:$CLASSES_DIR" \
        OptimizedMain \
        >>"$worker_dir/stdout.log" 2>>"$worker_dir/java-errors.log" &
    pid=$!
    printf '%s\n' "$pid" >"$worker_dir/loop.pid"
    sleep 0.5
    if is_worker_pid "$pid" "$worker_dir"; then
        echo "Đã chạy $name (loop PID $pid, java PID $(cat "$worker_dir/bot.pid" 2>/dev/null || echo ?), $proxy_label)"
        started=$((started + 1))
    else
        echo "$name khởi động lỗi; xem $worker_dir/java-errors.log" >&2
        rm -f -- "$worker_dir/bot.pid" "$worker_dir/loop.pid"
    fi
    (( START_DELAY == 0 )) || sleep "$START_DELAY"
done
echo "Kết quả: mới=$started, đang chạy=$running, hoàn tất=$completed"
