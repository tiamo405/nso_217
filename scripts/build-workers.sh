#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)

WORKER_COUNT=${1:-10}
SOURCE_CSV=${ACCOUNT_CSV:-"$REPO_DIR/account.csv"}
SOURCE_JAR=${BASE_JAR:-"$REPO_DIR/dist/client_217.jar"}
WORKERS_DIR=${WORKERS_DIR:-"$REPO_DIR/dist/workers"}

if ! [[ "$WORKER_COUNT" =~ ^[1-9][0-9]*$ ]]; then
    echo "Số worker phải là số nguyên dương." >&2
    exit 1
fi

for required_file in "$SOURCE_CSV" "$SOURCE_JAR"; do
    if [[ ! -f "$required_file" ]]; then
        echo "Không tìm thấy: $required_file" >&2
        exit 1
    fi
done

if ! command -v jar >/dev/null 2>&1; then
    echo "Không tìm thấy lệnh jar trong PATH." >&2
    exit 1
fi

if [[ -d "$WORKERS_DIR" ]]; then
    while IFS= read -r pid_file; do
        pid=$(<"$pid_file")
        if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
            echo "Worker PID $pid vẫn đang chạy. Hãy chạy stop-workers.sh trước khi build lại." >&2
            exit 1
        fi
    done < <(find "$WORKERS_DIR" -mindepth 2 -maxdepth 2 -name bot.pid -type f)
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
STAGING_DIR=$(mktemp -d "$REPO_DIR/dist/.workers-build.XXXXXX")
trap 'rm -rf -- "$STAGING_DIR"' EXIT
BASE_JAR_SNAPSHOT="$STAGING_DIR/.base-client.jar"

# `docker compose up -d` returns before the one-shot builder finishes. Wait until
# the source JAR is unchanged for a short period, then work from one verified
# snapshot so `jar uf` never reads a half-written ZIP file.
JAR_STABLE_SECONDS=${JAR_STABLE_SECONDS:-3}
if ! [[ "$JAR_STABLE_SECONDS" =~ ^[1-9][0-9]*$ ]]; then
    echo "JAR_STABLE_SECONDS phải là số nguyên dương." >&2
    exit 1
fi

snapshot_ready=false
for ((check = 1; check <= 20; check++)); do
    checksum_before=$(sha256sum "$SOURCE_JAR" 2>/dev/null | awk '{print $1}')
    sleep "$JAR_STABLE_SECONDS"
    checksum_after=$(sha256sum "$SOURCE_JAR" 2>/dev/null | awk '{print $1}')

    if [[ -n "$checksum_before" && "$checksum_before" == "$checksum_after" ]] \
            && jar tf "$SOURCE_JAR" >/dev/null 2>&1; then
        cp -- "$SOURCE_JAR" "$BASE_JAR_SNAPSHOT"
        snapshot_checksum=$(sha256sum "$BASE_JAR_SNAPSHOT" | awk '{print $1}')
        current_checksum=$(sha256sum "$SOURCE_JAR" | awk '{print $1}')
        if [[ "$snapshot_checksum" == "$current_checksum" ]] \
                && jar tf "$BASE_JAR_SNAPSHOT" >/dev/null 2>&1; then
            snapshot_ready=true
            break
        fi
    fi

    echo "JAR gốc đang được build hoặc chưa hoàn chỉnh, chờ kiểm tra lại ($check/20)..."
done

if [[ "$snapshot_ready" != true ]]; then
    echo "JAR gốc chưa ổn định sau 20 lần kiểm tra: $SOURCE_JAR" >&2
    echo "Hãy kiểm tra log builder rồi chạy lại." >&2
    exit 1
fi

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

    cp -- "$BASE_JAR_SNAPSHOT" "$worker_dir/client_217.jar"
    jar uf "$worker_dir/client_217.jar" -C "$worker_dir" account.csv

    embedded_count=$(unzip -p "$worker_dir/client_217.jar" account.csv | awk 'NR > 1 && $0 !~ /^[[:space:]]*$/ { count++ } END { print count + 0 }')
    if (( embedded_count != count )); then
        echo "Lỗi kiểm tra $worker_name: JAR có $embedded_count/$count tài khoản." >&2
        exit 1
    fi

    echo "Đã tạo $worker_name: $count tài khoản"
done

rm -f -- "$BASE_JAR_SNAPSHOT"

if [[ -d "$WORKERS_DIR" ]]; then
    backup_dir="$REPO_DIR/dist/.workers-old.$$"
    mv -- "$WORKERS_DIR" "$backup_dir"
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
    rm -rf -- "$backup_dir"
else
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
fi
trap - EXIT

echo "Hoàn tất: $TOTAL tài khoản / $WORKER_COUNT worker tại $WORKERS_DIR"
