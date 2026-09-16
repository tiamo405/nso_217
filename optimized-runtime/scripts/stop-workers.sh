#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
WORKERS_DIR=${OPTIMIZED_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
SUPERVISOR_PID_FILE="$WORKERS_DIR/supervisor.pid"

shopt -s nullglob

is_optimized_script_pid() {
    local pid=$1
    local expected_script=$2
    local expected_name=${expected_script##*/}
    local cwd token candidate

    [[ "$pid" =~ ^[0-9]+$ ]] || return 1
    [[ -r "/proc/$pid/cmdline" && -r "/proc/$pid/cwd" ]] || return 1
    cwd=$(readlink -f -- "/proc/$pid/cwd" 2>/dev/null) || return 1

    while IFS= read -r token; do
        [[ "$token" == */"$expected_name" || "$token" == "$expected_name" ]] || continue
        if [[ "$token" == /* ]]; then
            candidate=$(readlink -f -- "$token" 2>/dev/null) || continue
        else
            candidate=$(readlink -f -- "$cwd/$token" 2>/dev/null) || continue
        fi
        [[ "$candidate" == "$expected_script" ]] && return 0
    done < <(tr '\0' '\n' <"/proc/$pid/cmdline" 2>/dev/null)
    return 1
}

is_optimized_supervisor_pid() {
    is_optimized_script_pid "$1" "$SCRIPT_DIR/supervise-workers.sh"
}

is_optimized_worker_pid() {
    local pid=$1
    local worker_dir=$2
    local cmdline

    [[ "$pid" =~ ^[0-9]+$ ]] || return 1
    [[ -r "/proc/$pid/cmdline" ]] || return 1
    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    [[ "$cmdline" == *"OptimizedMain"* && "$cmdline" == *"$worker_dir"* ]]
}

stop_process_pid() {
    local pid=$1
    local pgid
    [[ "$pid" =~ ^[0-9]+$ ]] || return
    [[ "$pid" != "$$" ]] || return
    if ! kill -0 "$pid" 2>/dev/null; then
        return
    fi

    echo "Dừng process (PID $pid)..."
    # Web starts the supervisor in its own session. Kill that process group
    # too, otherwise a foreground start-workers.sh child may create a new
    # worker after this script has already scanned bot.pid files.
    pgid=$(ps -o pgid= -p "$pid" 2>/dev/null | tr -d ' ' || true)
    if [[ "$pgid" == "$pid" ]]; then
        kill -TERM -- "-$pgid" 2>/dev/null || true
    else
        kill "$pid" 2>/dev/null || true
    fi
    for _ in {1..10}; do
        if ! kill -0 "$pid" 2>/dev/null; then
            break
        fi
        sleep 0.2
    done
    if kill -0 "$pid" 2>/dev/null; then
        echo "Supervisor chưa thoát, gửi SIGKILL (PID $pid)..."
        if [[ "$pgid" == "$pid" ]]; then
            kill -KILL -- "-$pgid" 2>/dev/null || true
        else
            kill -9 "$pid" 2>/dev/null || true
        fi
    fi
}

pid_files=()
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
        pid_files+=("$worker_dir/bot.pid")
    done
else
    if [[ -f "$SUPERVISOR_PID_FILE" ]]; then
        sup_pid=$(<"$SUPERVISOR_PID_FILE")
        if is_optimized_supervisor_pid "$sup_pid"; then
            stop_process_pid "$sup_pid"
        elif [[ "$sup_pid" =~ ^[0-9]+$ ]] && kill -0 "$sup_pid" 2>/dev/null; then
            echo "Bỏ qua supervisor.pid cũ: PID $sup_pid không phải Optimized Supervisor." >&2
        fi
        rm -f -- "$SUPERVISOR_PID_FILE"
    fi

    # Supervisor cũ có thể đã xóa supervisor.pid nhưng vẫn tiếp tục chạy.
    # Quét theo đường dẫn script để Stop tất cả không bỏ sót tiến trình mồ côi.
    for proc_dir in /proc/[0-9]*; do
        proc_pid=${proc_dir##*/}
        if is_optimized_supervisor_pid "$proc_pid"; then
            stop_process_pid "$proc_pid"
        fi
    done

    # Nếu Supervisor đời cũ đã bị kill nhưng còn đang chờ start-workers.sh,
    # dừng luôn launcher đó để nó không sinh worker mới sau khi Stop xong.
    for proc_dir in /proc/[0-9]*; do
        proc_pid=${proc_dir##*/}
        if is_optimized_script_pid "$proc_pid" "$SCRIPT_DIR/start-workers.sh"; then
            stop_process_pid "$proc_pid"
        fi
    done
    pid_files=("$WORKERS_DIR"/worker-*/bot.pid)
fi

stopped=0
for pid_file in "${pid_files[@]}"; do
    [[ -f "$pid_file" ]] || continue
    pid=$(<"$pid_file")
    worker_dir=$(dirname -- "$pid_file")
    worker_name=$(basename -- "$worker_dir")

    if is_optimized_worker_pid "$pid" "$worker_dir"; then
        echo "Dừng $worker_name (PID $pid)..."
        kill "$pid" 2>/dev/null || true
        for _ in {1..10}; do
            if ! kill -0 "$pid" 2>/dev/null; then
                break
            fi
            sleep 0.2
        done
        if kill -0 "$pid" 2>/dev/null; then
            kill -9 "$pid" 2>/dev/null || true
        fi
        stopped=$((stopped + 1))
    elif [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
        echo "Bỏ qua $worker_name: PID $pid không thuộc worker này." >&2
    fi
    rm -f -- "$pid_file"
done

echo "Đã dừng $stopped worker."
