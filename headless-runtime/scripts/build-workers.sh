#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$HEADLESS_DIR/.." && pwd)

WORKER_COUNT=${1:-10}
SOURCE_CSV=${ACCOUNT_CSV:-"$REPO_DIR/account.csv"}
WORKERS_DIR=${HEADLESS_WORKERS_DIR:-"$HEADLESS_DIR/workers"}
BUILD_HEADLESS=${BUILD_HEADLESS:-1}

if ! [[ "$WORKER_COUNT" =~ ^[1-9][0-9]*$ ]]; then
    echo "Số worker phải là số nguyên dương." >&2
    exit 1
fi

if [[ ! -f "$SOURCE_CSV" ]]; then
    echo "Không tìm thấy account CSV: $SOURCE_CSV" >&2
    exit 1
fi

if [[ -d "$WORKERS_DIR" ]]; then
    while IFS= read -r pid_file; do
        pid=$(<"$pid_file")
        if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
            echo "Headless worker PID $pid vẫn đang chạy. Hãy stop trước khi build lại." >&2
            exit 1
        fi
    done < <(find "$WORKERS_DIR" -mindepth 2 -maxdepth 2 -name bot.pid -type f)
fi

if [[ "$BUILD_HEADLESS" != "0" ]]; then
    echo "Đang build headless classes..."
    HEADLESS_ACCOUNT_CSV="$SOURCE_CSV" "$HEADLESS_DIR/build-headless.sh"
fi

mapfile -t ACCOUNTS < <(awk 'NR > 1 && $0 !~ /^[[:space:]]*$/ { sub(/\r$/, ""); print }' "$SOURCE_CSV")
TOTAL=${#ACCOUNTS[@]}

if (( TOTAL == 0 )); then
    echo "account.csv không có tài khoản." >&2
    exit 1
fi

if (( WORKER_COUNT > TOTAL )); then
    echo "Có $TOTAL tài khoản nhưng yêu cầu $WORKER_COUNT worker." >&2
    exit 1
fi

HEADER=$(head -n 1 "$SOURCE_CSV" | tr -d '\r')
STAGING_DIR=$(mktemp -d "$HEADLESS_DIR/.workers-build.XXXXXX")
trap 'rm -rf -- "$STAGING_DIR"' EXIT

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

    echo "Đã tạo $worker_name: $count tài khoản"
done

if [[ -d "$WORKERS_DIR" ]]; then
    backup_dir="$HEADLESS_DIR/.workers-old.$$"
    mv -- "$WORKERS_DIR" "$backup_dir"
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
    rm -rf -- "$backup_dir"
else
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
fi
trap - EXIT

echo "Hoàn tất: $TOTAL tài khoản / $WORKER_COUNT headless worker tại $WORKERS_DIR"
