#!/usr/bin/env bash
set -euo pipefail

REPO_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
TA_THU_DIR="$REPO_DIR/ta-thu-runtime"

if rg -n 'AutoTaThuOrders|ORDER_ITEM_ID|Service\.gI\(\)\.buyItem\(' "$TA_THU_DIR/src"; then
    echo "Tà Thú vẫn còn logic mua Tà Thú Lệnh" >&2
    exit 1
fi

if rg -n 'new AutoFlipNvhn' "$TA_THU_DIR/src" "$TA_THU_DIR/overrides"; then
    echo "Tà Thú vẫn còn khởi tạo logic lật hình" >&2
    exit 1
fi

rg -q 'bỏ qua lật hình' "$TA_THU_DIR/src/TaThuAccountManager.java"
echo "PASS: Tà Thú không mua lệnh và không lật hình"
