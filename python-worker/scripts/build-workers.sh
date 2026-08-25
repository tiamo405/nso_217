#!/usr/bin/env bash
set -euo pipefail

# Determine directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_WORKER_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
REPO_ROOT="$(cd "$PYTHON_WORKER_DIR/.." && pwd)"

# Arguments
CSV_FILE="${1:-}"
NUM_WORKERS="${2:-5}"
# Validate number of workers
if ! [[ "$NUM_WORKERS" =~ ^[0-9]+$ ]] || [[ "$NUM_WORKERS" -le 0 ]]; then
    echo "Lỗi: Số worker phải là số nguyên dương." >&2
    exit 1
fi

# Resolve CSV file if not provided
if [[ -z "$CSV_FILE" ]]; then
    if [[ -f "$PYTHON_WORKER_DIR/accounts.csv" ]]; then
        CSV_FILE="$PYTHON_WORKER_DIR/accounts.csv"
    elif [[ -f "$REPO_ROOT/account.csv" ]]; then
        CSV_FILE="$REPO_ROOT/account.csv"
    else
        echo "Lỗi: Không tìm thấy file account.csv! Vui lòng chỉ định đường dẫn: ./build-workers.sh <path/to/account.csv> [num_workers]" >&2
        exit 1
    fi
fi

# Output directory for workers
DIST_DIR="$PYTHON_WORKER_DIR/dist_py/workers"
mkdir -p "$DIST_DIR"

echo "=== Đang đọc tài khoản từ $CSV_FILE ==="

# Read accounts (skip header, empty lines, and possible Windows CR)
mapfile -t ALL_ACCOUNTS < <(sed 's/\\r$//' "$CSV_FILE" | awk 'NF' | grep -v -i -E '^(username|user|acc|tai_khoan),')

TOTAL_ACCOUNTS=${#ALL_ACCOUNTS[@]}
if [[ "$TOTAL_ACCOUNTS" -eq 0 ]]; then
    echo "Lỗi: File CSV không có tài khoản hợp lệ nào." >&2
    exit 1
fi

echo "Tổng số tài khoản: $TOTAL_ACCOUNTS"
echo "Số worker yêu cầu: $NUM_WORKERS"

# Compute distribution
BASE_COUNT=$(( TOTAL_ACCOUNTS / NUM_WORKERS ))   # accounts per worker (floor)
REMAINDER=$(( TOTAL_ACCOUNTS % NUM_WORKERS ))   # first REMAINDER workers get +1

# Xóa toàn bộ nội dung dist_py (cẩn thận!)
rm -rf "${PYTHON_WORKER_DIR}/dist_py"
mkdir -p "${DIST_DIR}"   # DIST_DIR = "${PYTHON_WORKER_DIR}/dist_py/workers"

# Create workers and allocate accounts
CUR_IDX=0
for ((i = 0; i < NUM_WORKERS; i++)); do
    WORKER_ID=$(printf "%02d" $((i + 1)))
    WORKER_DIR="${DIST_DIR}/worker-${WORKER_ID}"
    mkdir -p "$WORKER_DIR"

    # Determine how many accounts this worker receives
    if (( i < REMAINDER )); then
        COUNT=$(( BASE_COUNT + 1 ))
    else
        COUNT=$BASE_COUNT
    fi
    START_IDX=$CUR_IDX
    END_IDX=$(( CUR_IDX + COUNT ))
    CUR_IDX=$END_IDX

    # Write accounts.csv for this worker
    echo "username,password" > "$WORKER_DIR/accounts.csv"
    for ((j = START_IDX; j < END_IDX; j++)); do
        echo "${ALL_ACCOUNTS[j]}" >> "$WORKER_DIR/accounts.csv"
    done

    echo "  [OK] Tạo $WORKER_DIR ($COUNT tài khoản)"
done

# Summary
echo "Số tài khoản trung bình mỗi worker: $BASE_COUNT (có $REMAINDER worker nhận thêm 1 tài khoản)"
echo "=== Hoàn tất build $NUM_WORKERS workers trong $DIST_DIR ==="
