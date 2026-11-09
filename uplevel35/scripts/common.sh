# shellcheck shell=bash
# Biến và hàm dùng chung cho các script uplevel35 (source, không chạy trực tiếp).

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$RUNTIME_DIR/.." && pwd)
BUILD_DIR=$(realpath -m -- "${UPLEVEL35_BUILD_DIR:-"$RUNTIME_DIR/build"}")
CLASSES_DIR="$BUILD_DIR/classes"
WORKERS_DIR=${UPLEVEL35_WORKERS_DIR:-"$RUNTIME_DIR/workers"}
SOURCE_CSV=${UPLEVEL35_ACCOUNT_CSV:-"$REPO_DIR/account-35.csv"}

# worker_dir_for 3 -> .../workers/worker-03
worker_dir_for() {
    local number=${1#worker-}
    [[ "$number" =~ ^[0-9]+$ ]] || { echo "Worker không hợp lệ: $1" >&2; return 2; }
    printf '%s/worker-%02d' "$WORKERS_DIR" "$((10#$number))"
}

# Danh sách worker theo tham số (rỗng = tất cả), ghi vào mảng WORKER_DIRS.
select_workers() {
    WORKER_DIRS=()
    local dir
    if (( $# > 0 )); then
        for arg in "$@"; do
            dir=$(worker_dir_for "$arg") || exit 2
            [[ -d "$dir" ]] || { echo "Không tìm thấy $(basename -- "$dir")." >&2; exit 1; }
            WORKER_DIRS+=("$dir")
        done
    else
        shopt -s nullglob
        WORKER_DIRS=("$WORKERS_DIR"/worker-*)
        shopt -u nullglob
    fi
}

# PID còn sống và đúng là java uplevel35 của worker này.
is_worker_pid() {
    local pid=$1 worker_dir=$2 cmdline
    [[ "$pid" =~ ^[0-9]+$ && -r "/proc/$pid/cmdline" ]] || return 1
    cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
    [[ "$cmdline" == *"-Dnso.runtime=uplevel35"* && "$cmdline" == *"$worker_dir/home"* ]]
}

worker_pid() {
    local worker_dir=$1 pid
    [[ -f "$worker_dir/bot.pid" ]] || return 1
    pid=$(<"$worker_dir/bot.pid")
    is_worker_pid "$pid" "$worker_dir" || return 1
    printf '%s' "$pid"
}

# PID của worker-loop.sh (chạy lại JVM cho từng nhân vật); cmdline của nó chứa luôn tham số java.
loop_pid() {
    local worker_dir=$1 pid
    [[ -f "$worker_dir/loop.pid" ]] || return 1
    pid=$(<"$worker_dir/loop.pid")
    is_worker_pid "$pid" "$worker_dir" || return 1
    printf '%s' "$pid"
}
