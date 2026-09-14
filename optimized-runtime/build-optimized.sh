#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
OPTIMIZED_BUILD_DIR=$(realpath -m -- "${OPTIMIZED_BUILD_DIR:-"$SCRIPT_DIR/build"}")
ACCOUNT_CSV=${OPTIMIZED_ACCOUNT_CSV:-"$REPO_DIR/account.csv"}

if [[ "$OPTIMIZED_BUILD_DIR" == / || "$OPTIMIZED_BUILD_DIR" == "$SCRIPT_DIR" || "$OPTIMIZED_BUILD_DIR" == "$REPO_DIR" ]]; then
  echo "OPTIMIZED_BUILD_DIR không an toàn: $OPTIMIZED_BUILD_DIR" >&2
  exit 1
fi

build_parent=$(dirname -- "$OPTIMIZED_BUILD_DIR")
build_name=$(basename -- "$OPTIMIZED_BUILD_DIR")
mkdir -p "$build_parent"
STAGING_DIR=$(mktemp -d "$build_parent/.${build_name}.build.XXXXXX")
BUILD_DIR="$STAGING_DIR/classes"
WORK_SRC_DIR="$STAGING_DIR/src-repo"
SOURCES_FILE="$STAGING_DIR/sources.txt"
BACKUP_DIR="$build_parent/.${build_name}.old.$$"

cleanup() {
  rm -rf -- "$STAGING_DIR" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$BUILD_DIR" "$WORK_SRC_DIR"

echo "[1/4] Copy mã nguồn gốc và áp dụng overrides..."
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$SCRIPT_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$SCRIPT_DIR/overrides"/. "$WORK_SRC_DIR"/

# Thay thế ClassLoader getResourceAsStream an toàn
sed -i 's#"".getClass().getResourceAsStream("/map/" + var1)#TileMap.class.getResourceAsStream("/map/" + var1)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream("/map/" + mapID)#TileMap.class.getResourceAsStream("/map/" + mapID)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#RMS.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/RMS.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#Res.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/Res.java"

echo "[2/4] Tìm danh sách các file Java..."
find "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"

echo "[3/4] Biên dịch với javac (Java 8 bytecode)..."
javac \
  -encoding UTF-8 \
  -source 8 \
  -target 8 \
  -Xlint:none \
  -d "$BUILD_DIR" \
  @"$SOURCES_FILE"

echo "[4/4] Đóng gói tài nguyên tĩnh (maps, account, configs)..."
cp -R "$WORK_SRC_DIR"/. "$BUILD_DIR"/
find "$BUILD_DIR" -name '*.java' -delete
mkdir -p "$BUILD_DIR/map"
for map_id in $(seq 0 159); do
  if [[ -f "$REPO_DIR/src/map/$map_id" ]]; then
    cp "$REPO_DIR/src/map/$map_id" "$BUILD_DIR/map/$map_id"
  elif [[ ! -f "$BUILD_DIR/map/$map_id" ]]; then
    : >"$BUILD_DIR/map/$map_id"
  fi
done

if [[ -f "$ACCOUNT_CSV" ]]; then
  cp -- "$ACCOUNT_CSV" "$BUILD_DIR/account.csv"
fi
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
  cp -- "$REPO_DIR/delllllllllll.txt" "$BUILD_DIR/delllllllllll.txt"
fi

if [[ ! -f "$BUILD_DIR/OptimizedMain.class" ]]; then
  echo "Build lỗi: không tìm thấy OptimizedMain.class" >&2
  exit 1
fi

if [[ -d "$OPTIMIZED_BUILD_DIR" ]]; then
  mv -- "$OPTIMIZED_BUILD_DIR" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$OPTIMIZED_BUILD_DIR"; then
  if [[ -d "$BACKUP_DIR" ]]; then
    mv -- "$BACKUP_DIR" "$OPTIMIZED_BUILD_DIR"
  fi
  exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT

echo "=========================================================="
echo " BUILD THÀNH CÔNG: $OPTIMIZED_BUILD_DIR/classes"
echo " Entry point: OptimizedMain"
echo "=========================================================="
