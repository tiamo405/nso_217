#!/usr/bin/env bash
# Kiểm tra patches còn áp được lên src/ hiện tại (dry-run, không ghi file nào)
# và không đụng vào file mà optimized-runtime override.
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

TMP=$(mktemp -d)
trap 'rm -rf -- "$TMP"' EXIT
cp -R "$REPO_DIR/src"/. "$TMP"/
cp -R "$REPO_DIR/optimized-runtime/src"/. "$TMP"/
cp -R "$REPO_DIR/optimized-runtime/overrides"/. "$TMP"/

status=0
shopt -s nullglob
for patch_file in "$RUNTIME_DIR/patches"/*.patch; do
    name=$(basename -- "$patch_file")
    while read -r target; do
        if [[ -f "$REPO_DIR/optimized-runtime/overrides/$target" ]]; then
            echo "CẢNH BÁO $name: $target bị optimized-runtime override, diff phải tính trên bản override"
        fi
    done < <(sed -n 's#^+++ b/\([^[:space:]]*\).*#\1#p' "$patch_file")
    if patch --batch --forward --quiet -d "$TMP" -p1 <"$patch_file" >/dev/null; then
        echo "OK   $name"
    else
        echo "LỖI  $name"
        status=1
    fi
done
exit $status
