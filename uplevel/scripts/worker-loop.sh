#!/usr/bin/env bash
# Chạy một worker theo kiểu "mỗi nhân vật một JVM".
#   worker-loop.sh <worker_dir> <java> [java_args...]
# Java (AccountAutoManager, -Dnso.process.per.character=true) thoát mã 3 khi còn nhân vật/tài khoản
# tiếp theo; tiến độ nằm trong <worker_dir>/home/uplevel-progress.properties. Mã khác = dừng hẳn
# (hết tài khoản, lỗi, hoặc bị stop). PID java hiện tại ghi vào <worker_dir>/bot.pid.
set -uo pipefail

HANDOFF_EXIT_CODE=3
worker_dir=$1
shift
pid_file="$worker_dir/bot.pid"
java_pid=""

on_stop() {
    if [[ -n "$java_pid" ]]; then
        kill -TERM "$java_pid" 2>/dev/null || true
        wait "$java_pid" 2>/dev/null || true
    fi
    rm -f -- "$pid_file"
    exit 143
}
trap on_stop TERM INT HUP

run=1
while :; do
    "$@" &
    java_pid=$!
    printf '%s\n' "$java_pid" >"$pid_file"
    wait "$java_pid"
    rc=$?
    java_pid=""
    if (( rc == HANDOFF_EXIT_CODE )) && [[ ! -f "$worker_dir/home/worker.done" ]]; then
        run=$((run + 1))
        printf '\n===== NEXT PROCESS #%d %s =====\n' "$run" "$(date '+%F %T')"
        sleep 2
        continue
    fi
    printf '\n===== WORKER LOOP END rc=%d %s =====\n' "$rc" "$(date '+%F %T')"
    break
done
rm -f -- "$pid_file"
