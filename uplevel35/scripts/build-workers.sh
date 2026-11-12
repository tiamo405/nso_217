#!/usr/bin/env bash
# Chia account-35.csv thành N worker (workers/worker-XX/account.csv) và build runtime.
#   ./uplevel35/scripts/build-workers.sh [số_worker=1]
# UPLEVEL35_SKIP_BUILD=1 để chỉ chia lại account, không build.
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

WORKER_COUNT=${1:-1}
if (( $# > 1 )) || ! [[ "$WORKER_COUNT" =~ ^[1-9][0-9]*$ ]]; then
    echo "Usage: $(basename "$0") [worker_count]" >&2
    exit 2
fi
[[ -f "$SOURCE_CSV" ]] || { echo "Không tìm thấy account CSV: $SOURCE_CSV" >&2; exit 1; }

select_workers
for dir in "${WORKER_DIRS[@]}"; do
    if pid=$(loop_pid "$dir") || pid=$(worker_pid "$dir"); then
        echo "$(basename -- "$dir") đang chạy (PID $pid); hãy stop trước khi build lại." >&2
        exit 1
    fi
done

if [[ "${UPLEVEL35_SKIP_BUILD:-0}" != 1 ]]; then
    UPLEVEL35_ACCOUNT_CSV="$SOURCE_CSV" "$SCRIPT_DIR/build.sh"
fi

HEADER=$(head -n 1 "$SOURCE_CSV" | tr -d '\r')
mapfile -t ACCOUNTS < <(awk 'NR > 1 && $0 !~ /^[[:space:]]*$/ { sub(/\r$/, ""); print }' "$SOURCE_CSV")
TOTAL=${#ACCOUNTS[@]}
(( TOTAL > 0 )) || { echo "account CSV không có tài khoản." >&2; exit 1; }
(( WORKER_COUNT <= TOTAL )) || { echo "Có $TOTAL tài khoản nhưng yêu cầu $WORKER_COUNT worker." >&2; exit 1; }

STAGING_DIR=$(mktemp -d "$RUNTIME_DIR/.workers-build.XXXXXX")
trap 'rm -rf -- "$STAGING_DIR"' EXIT

# Chia xen kẽ (round-robin): acc thứ i vào worker ((i-1) % N) + 1.
# VD 12 acc / 6 worker: worker-01 = 1,7; worker-02 = 2,8; ...
# 14 acc / 6 worker: worker-01 = 1,7,13; worker-02 = 2,8,14; còn lại 2 acc.
for ((index = 1; index <= WORKER_COUNT; index++)); do
    name=$(printf 'worker-%02d' "$index")
    mkdir -p "$STAGING_DIR/$name/home"
    printf '%s\n' "$HEADER" >"$STAGING_DIR/$name/account.csv"
done
for ((row = 0; row < TOTAL; row++)); do
    name=$(printf 'worker-%02d' $((row % WORKER_COUNT + 1)))
    printf '%s\n' "${ACCOUNTS[row]}" >>"$STAGING_DIR/$name/account.csv"
done
for ((index = 1; index <= WORKER_COUNT; index++)); do
    count=$(( (TOTAL - index) / WORKER_COUNT + 1 ))
    echo "Đã tạo $(printf 'worker-%02d' "$index"): $count tài khoản"
done

if [[ -d "$WORKERS_DIR" ]]; then
    backup_dir="$RUNTIME_DIR/.workers-old.$$"
    mv -- "$WORKERS_DIR" "$backup_dir"
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
    rm -rf -- "$backup_dir"
else
    mv -- "$STAGING_DIR" "$WORKERS_DIR"
fi
trap - EXIT
echo "Hoàn tất: $TOTAL tài khoản / $WORKER_COUNT worker tại $WORKERS_DIR"
# Chia đều proxy theo khối worker liên tiếp; start-workers sẽ probe lại trước khi chạy.
python3 "$PROXY_ASSIGN" --workers-dir "$WORKERS_DIR" --proxy-file "$PROXY_FILE" --no-check || true
