#!/usr/bin/env bash
# Build runtime uplevel35 (level 25 -> 35).
#
# Thứ tự ghép nguồn trong thư mục tạm (src/, optimized-runtime/, uplevel/ KHÔNG bị sửa):
#   1. src/                              mã game gốc
#   2. optimized-runtime/src + overrides nền headless tối ưu (chỉ đọc)
#   3. uplevel35/patches/*.patch         patch -p1, áp theo thứ tự tên file
#   4. uplevel35/src/**                  class mới (As35, tasks/...)
# Patch chỉ được sửa file mà optimized-runtime KHÔNG override, để diff luôn
# tính trên src/ (kiểm tra bằng scripts/check-patches.sh).
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
RUNTIME_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$RUNTIME_DIR/.." && pwd)
OPTIMIZED_DIR="$REPO_DIR/optimized-runtime"
BUILD_DIR=$(realpath -m -- "${UPLEVEL35_BUILD_DIR:-"$RUNTIME_DIR/build"}")
ACCOUNT_CSV=${UPLEVEL35_ACCOUNT_CSV:-"$REPO_DIR/account-35.csv"}

if [[ ! -f "$ACCOUNT_CSV" ]]; then
    echo "Không tìm thấy account CSV: $ACCOUNT_CSV" >&2
    exit 1
fi
if [[ "$BUILD_DIR" == / || "$BUILD_DIR" == "$RUNTIME_DIR" || "$BUILD_DIR" == "$REPO_DIR" ]]; then
    echo "UPLEVEL35_BUILD_DIR không an toàn: $BUILD_DIR" >&2
    exit 1
fi

BUILD_PARENT=$(dirname -- "$BUILD_DIR")
BUILD_NAME=$(basename -- "$BUILD_DIR")
mkdir -p "$BUILD_PARENT"
STAGING_DIR=$(mktemp -d "$BUILD_PARENT/.${BUILD_NAME}.build.XXXXXX")
WORK_SRC_DIR="$STAGING_DIR/src"
CLASSES_DIR="$STAGING_DIR/classes"
SOURCES_FILE="$STAGING_DIR/sources.txt"
BACKUP_DIR="$BUILD_PARENT/.${BUILD_NAME}.old.$$"

cleanup() {
    if [[ "${UPLEVEL35_KEEP_STAGING:-0}" == 1 && -n "${STAGING_DIR:-}" ]]; then
        echo "Giữ thư mục staging: $STAGING_DIR" >&2
        rm -rf -- "$BACKUP_DIR"
        return
    fi
    rm -rf -- "${STAGING_DIR:-}" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$WORK_SRC_DIR" "$CLASSES_DIR"

echo "[1/5] Copy src/ + nền optimized-runtime"
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$OPTIMIZED_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$OPTIMIZED_DIR/overrides"/. "$WORK_SRC_DIR"/

echo "[2/5] Áp patches"
shopt -s nullglob
for patch_file in "$RUNTIME_DIR/patches"/*.patch; do
    echo "  - $(basename -- "$patch_file")"
    if ! patch --batch --forward --quiet -d "$WORK_SRC_DIR" -p1 <"$patch_file"; then
        echo "Build lỗi: không áp được $(basename -- "$patch_file") (src/ đã đổi? chạy scripts/check-patches.sh)" >&2
        exit 1
    fi
done
shopt -u nullglob
find "$WORK_SRC_DIR" \( -name '*.orig' -o -name '*.rej' \) -delete

echo "[3/5] Thêm class uplevel35"
while IFS= read -r -d '' java_file; do
    name=$(basename -- "$java_file")
    if [[ -e "$WORK_SRC_DIR/$name" ]]; then
        echo "Build lỗi: uplevel35/src/$name trùng file có sẵn; hãy sửa bằng patch." >&2
        exit 1
    fi
    cp -- "$java_file" "$WORK_SRC_DIR/$name"
done < <(find "$RUNTIME_DIR/src" -name '*.java' -print0)

# JVM desktop cần lookup resource theo class (giống optimized-runtime/build-optimized.sh).
sed -i 's#"".getClass().getResourceAsStream("/map/" + var1)#TileMap.class.getResourceAsStream("/map/" + var1)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream("/map/" + mapID)#TileMap.class.getResourceAsStream("/map/" + mapID)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#RMS.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/RMS.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#Res.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/Res.java"

for hook in 'new As35()' 'onUplevel35Finished'; do
    grep -Fq -- "$hook" "$WORK_SRC_DIR/AccountAutoManager.java" || { echo "Build lỗi: thiếu hook '$hook'" >&2; exit 1; }
done
grep -Fq 'Uplevel35.onNpcMessage(' "$WORK_SRC_DIR/Controller.java" || { echo "Build lỗi: thiếu hook NPC trong Controller" >&2; exit 1; }

echo "[4/5] javac"
find "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"
if ! javac -encoding UTF-8 -source 8 -target 8 -Xlint:none -nowarn -d "$CLASSES_DIR" @"$SOURCES_FILE" \
    2>"$STAGING_DIR/javac.log"; then
    cat "$STAGING_DIR/javac.log" >&2
    echo "Build lỗi: javac thất bại" >&2
    exit 1
fi
for cls in OptimizedMain As35 Uplevel35 TaskBaRei26; do
    [[ -f "$CLASSES_DIR/$cls.class" ]] || { echo "Build lỗi: thiếu $cls.class" >&2; exit 1; }
done

echo "[5/5] Tài nguyên (map, account.csv)"
cp -R "$WORK_SRC_DIR"/. "$CLASSES_DIR"/
find "$CLASSES_DIR" -name '*.java' -delete
mkdir -p "$CLASSES_DIR/map"
for map_id in $(seq 0 159); do
    if [[ -f "$REPO_DIR/src/map/$map_id" ]]; then
        cp "$REPO_DIR/src/map/$map_id" "$CLASSES_DIR/map/$map_id"
    elif [[ ! -f "$CLASSES_DIR/map/$map_id" ]]; then
        : >"$CLASSES_DIR/map/$map_id"
    fi
done
cp -- "$ACCOUNT_CSV" "$CLASSES_DIR/account.csv"
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
    cp -- "$REPO_DIR/delllllllllll.txt" "$CLASSES_DIR/delllllllllll.txt"
fi
if [[ "${UPLEVEL35_KEEP_STAGING:-0}" != 1 ]]; then
    rm -rf -- "$WORK_SRC_DIR" "$SOURCES_FILE"
fi

if [[ -d "$BUILD_DIR" ]]; then
    mv -- "$BUILD_DIR" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$BUILD_DIR"; then
    [[ -d "$BACKUP_DIR" ]] && mv -- "$BACKUP_DIR" "$BUILD_DIR"
    exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT
echo "Build uplevel35 thành công: $BUILD_DIR/classes"
