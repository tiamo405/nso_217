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

BASE_SIZE=$((TOTAL / WORKER_COUNT))
EXTRA=$((TOTAL % WORKER_COUNT))
OFFSET=0
for ((index = 1; index <= WORKER_COUNT; index++)); do
    worker_name=$(printf 'worker-%02d' "$index")
    worker_dir="$STAGING_DIR/$worker_name"
    mkdir -p "$worker_dir/home"

    count=$BASE_SIZE
    if (( index <= EXTRA )); then
        count=$((count + 1))
    fi
    printf '%s\n' "$HEADER" >"$worker_dir/account.csv"
    for ((row = 0; row < count; row++)); do
        printf '%s\n' "${ACCOUNTS[OFFSET + row]}" >>"$worker_dir/account.csv"
    done
    OFFSET=$((OFFSET + count))
    echo "Da tao $worker_name: $count tai khoan"
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
