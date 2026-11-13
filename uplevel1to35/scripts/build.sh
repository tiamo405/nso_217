#!/usr/bin/env bash
# Build runtime uplevel1to35 (level 1 -> 35: As20 nhiệm vụ 0..17, rồi As35 nhiệm vụ 18..23).
#
# Thứ tự ghép nguồn trong thư mục tạm (src/, optimized-runtime/, uplevel/, uplevel35/ KHÔNG bị sửa):
#   1. src/ + optimized-runtime/src + optimized-runtime/overrides   nền headless tối ưu
#   2. uplevel/scripts/apply-transforms.sh                          uplevel/overrides + vá As20/As10/Controller...
#   3. uplevel35/src/**                                             As35, Uplevel35, tasks/... (class mới)
#   4. uplevel1to35/src/**                                          Uplevel1to35 (điều phối giai đoạn)
#   5. uplevel1to35/transforms/apply.py                             AccountAutoManager/Controller/As35
# Patch của uplevel35/patches KHÔNG dùng: 001/004 nhắm src/AccountAutoManager (uplevel thay cả file),
# 002/003 trùng chỗ uplevel đã vá -> viết lại trong transforms/apply.py.
set -euo pipefail
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"

UPLEVEL_DIR="$REPO_DIR/uplevel"
UPLEVEL35_DIR="$REPO_DIR/uplevel35"
OPTIMIZED_DIR="$REPO_DIR/optimized-runtime"
ACCOUNT_CSV=$SOURCE_CSV

if [[ ! -f "$ACCOUNT_CSV" ]]; then
    echo "Không tìm thấy account CSV: $ACCOUNT_CSV" >&2
    exit 1
fi
if [[ "$BUILD_DIR" == / || "$BUILD_DIR" == "$RUNTIME_DIR" || "$BUILD_DIR" == "$REPO_DIR" ]]; then
    echo "UPLEVEL1TO35_BUILD_DIR không an toàn: $BUILD_DIR" >&2
    exit 1
fi

BUILD_PARENT=$(dirname -- "$BUILD_DIR")
BUILD_NAME=$(basename -- "$BUILD_DIR")
mkdir -p "$BUILD_PARENT"
STAGING_DIR=$(mktemp -d "$BUILD_PARENT/.${BUILD_NAME}.build.XXXXXX")
WORK_SRC_DIR="$STAGING_DIR/src"
STAGE_CLASSES_DIR="$STAGING_DIR/classes"
SOURCES_FILE="$STAGING_DIR/sources.txt"
BACKUP_DIR="$BUILD_PARENT/.${BUILD_NAME}.old.$$"

cleanup() {
    if [[ "${UPLEVEL1TO35_KEEP_STAGING:-0}" == 1 && -n "${STAGING_DIR:-}" ]]; then
        echo "Giữ thư mục staging: $STAGING_DIR" >&2
        rm -rf -- "$BACKUP_DIR"
        return
    fi
    rm -rf -- "${STAGING_DIR:-}" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$WORK_SRC_DIR" "$STAGE_CLASSES_DIR"

echo "[1/6] Copy src/ + nền optimized-runtime"
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$OPTIMIZED_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$OPTIMIZED_DIR/overrides"/. "$WORK_SRC_DIR"/

echo "[2/6] Lớp uplevel (As20, nhiệm vụ 0..17)"
"$UPLEVEL_DIR/scripts/apply-transforms.sh" "$WORK_SRC_DIR"

echo "[3/6] Class uplevel35 + uplevel1to35"
copy_new_classes() {
    local from=$1 label=$2 java_file name
    while IFS= read -r -d '' java_file; do
        name=$(basename -- "$java_file")
        if [[ -e "$WORK_SRC_DIR/$name" ]]; then
            echo "Build lỗi: $label/$name trùng file có sẵn sau khi ghép uplevel." >&2
            exit 1
        fi
        cp -- "$java_file" "$WORK_SRC_DIR/$name"
    done < <(find "$from" -name '*.java' -print0)
}
copy_new_classes "$UPLEVEL35_DIR/src" "uplevel35/src"
copy_new_classes "$RUNTIME_DIR/src" "uplevel1to35/src"

echo "[4/6] Vá AccountAutoManager / Controller / As35"
python3 "$RUNTIME_DIR/transforms/apply.py" "$WORK_SRC_DIR"
for hook in 'new As20(mode)' 'new As35()' 'Uplevel1to35.phase(me)' 'relaunchForUplevel35(me)' 'onUplevel35Finished'; do
    grep -Fq -- "$hook" "$WORK_SRC_DIR/AccountAutoManager.java" || { echo "Build lỗi: thiếu hook '$hook'" >&2; exit 1; }
done
grep -Fq 'Uplevel35.onNpcMessage(' "$WORK_SRC_DIR/Controller.java" || { echo "Build lỗi: thiếu hook NPC trong Controller" >&2; exit 1; }
grep -Fq 'AccountAutoManager.onAs20Finished()' "$WORK_SRC_DIR/As10.java" || { echo "Build lỗi: As10 thiếu onAs20Finished" >&2; exit 1; }

echo "[5/6] javac"
find "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"
if ! javac -encoding UTF-8 -source 8 -target 8 -Xlint:none -nowarn -d "$STAGE_CLASSES_DIR" @"$SOURCES_FILE" \
    2>"$STAGING_DIR/javac.log"; then
    cat "$STAGING_DIR/javac.log" >&2
    echo "Build lỗi: javac thất bại" >&2
    exit 1
fi
for cls in OptimizedMain AccountAutoManager As20 As35 Uplevel35 Uplevel1to35 TaskMatsurugi35 UplevelStatus; do
    [[ -f "$STAGE_CLASSES_DIR/$cls.class" ]] || { echo "Build lỗi: thiếu $cls.class" >&2; exit 1; }
done

echo "[6/6] Tài nguyên (map, account.csv)"
cp -R "$WORK_SRC_DIR"/. "$STAGE_CLASSES_DIR"/
find "$STAGE_CLASSES_DIR" -name '*.java' -delete
mkdir -p "$STAGE_CLASSES_DIR/map"
for map_id in $(seq 0 159); do
    if [[ -f "$REPO_DIR/src/map/$map_id" ]]; then
        cp "$REPO_DIR/src/map/$map_id" "$STAGE_CLASSES_DIR/map/$map_id"
    elif [[ ! -f "$STAGE_CLASSES_DIR/map/$map_id" ]]; then
        : >"$STAGE_CLASSES_DIR/map/$map_id"
    fi
done
cp -- "$ACCOUNT_CSV" "$STAGE_CLASSES_DIR/account.csv"
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
    cp -- "$REPO_DIR/delllllllllll.txt" "$STAGE_CLASSES_DIR/delllllllllll.txt"
fi
if [[ "${UPLEVEL1TO35_KEEP_STAGING:-0}" != 1 ]]; then
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
echo "Build uplevel1to35 thành công: $CLASSES_DIR"
