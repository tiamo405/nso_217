#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$UPLEVEL_DIR/.." && pwd)

WORKER_COUNT=${1:-10}
SOURCE_CSV=${UPLEVEL_ACCOUNT_CSV:-"$REPO_DIR/account-as20.csv"}
WORKERS_DIR=${UPLEVEL_WORKERS_DIR:-"$UPLEVEL_DIR/workers"}
BUILD_UPLEVEL=${UPLEVEL_BUILD_UPLEVEL:-1}

if (( $# > 1 )) || ! [[ "$WORKER_COUNT" =~ ^[1-9][0-9]*$ ]]; then
    echo "Usage: $(basename "$0") [worker_count]" >&2
    exit 2
fi
if [[ ! -f "$SOURCE_CSV" ]]; then
    echo "Khong tim thay account CSV: $SOURCE_CSV" >&2
    exit 1
fi

if [[ -d "$WORKERS_DIR" ]]; then
    while IFS= read -r pid_file; do
        pid=$(<"$pid_file")
        if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
            echo "Worker PID $pid van dang chay; hay stop truoc khi build lai." >&2
            exit 1
        fi
    done < <(find "$WORKERS_DIR" -mindepth 2 -maxdepth 2 -name bot.pid -type f 2>/dev/null || true)
fi

if [[ "$BUILD_UPLEVEL" != 0 ]]; then
    UPLEVEL_ACCOUNT_CSV="$SOURCE_CSV" "$SCRIPT_DIR/build.sh"
fi

HEADER=$(head -n 1 "$SOURCE_CSV" | tr -d '\r')
mapfile -t ACCOUNTS < <(awk 'NR > 1 && $0 !~ /^[[:space:]]*$/ { sub(/\r$/, ""); print }' "$SOURCE_CSV")
TOTAL=${#ACCOUNTS[@]}
if (( TOTAL == 0 )); then
    echo "account CSV khong co tai khoan." >&2
    exit 1
fi
if (( WORKER_COUNT > TOTAL )); then
    echo "Co $TOTAL tai khoan nhung yeu cau $WORKER_COUNT worker." >&2
    exit 1
fi

STAGING_DIR=$(mktemp -d "$UPLEVEL_DIR/.workers-build.XXXXXX")
cleanup() {
    rm -rf -- "$STAGING_DIR"
}
trap cleanup EXIT

# Chia xen ke (round-robin): acc thu i vao worker ((i-1) % N) + 1.
# VD 12 acc / 6 worker: worker-01 = 1,7; worker-02 = 2,8; ...
# 14 acc / 6 worker: worker-01 = 1,7,13; worker-02 = 2,8,14; con lai 2 acc.
for ((index = 1; index <= WORKER_COUNT; index++)); do
    worker_dir="$STAGING_DIR/$(printf 'worker-%02d' "$index")"
    mkdir -p "$worker_dir/home"
    printf '%s\n' "$HEADER" >"$worker_dir/account.csv"
done
for ((row = 0; row < TOTAL; row++)); do
    worker_dir="$STAGING_DIR/$(printf 'worker-%02d' $((row % WORKER_COUNT + 1)))"
    printf '%s\n' "${ACCOUNTS[row]}" >>"$worker_dir/account.csv"
done
for ((index = 1; index <= WORKER_COUNT; index++)); do
    count=$(( (TOTAL - index) / WORKER_COUNT + 1 ))
    echo "Da tao $(printf 'worker-%02d' "$index"): $count tai khoan"
done

if [[ -d "$WORKERS_DIR" ]]; then
    backup_dir="$UPLEVEL_DIR/.workers-old.$$"
    mv -- "$WORKERS_DIR" "$backup_dir"
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
    rm -rf -- "$backup_dir"
else
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
fi
trap - EXIT

echo "Hoan tat: $TOTAL tai khoan / $WORKER_COUNT worker tai $WORKERS_DIR"
