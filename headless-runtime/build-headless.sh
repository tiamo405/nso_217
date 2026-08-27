#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
HEADLESS_BUILD_DIR=$(realpath -m -- "${HEADLESS_BUILD_DIR:-"$SCRIPT_DIR/build"}")
HEADLESS_ACCOUNT_CSV=${HEADLESS_ACCOUNT_CSV:-"$REPO_DIR/account.csv"}

if [[ "$HEADLESS_BUILD_DIR" == / || "$HEADLESS_BUILD_DIR" == "$SCRIPT_DIR" || "$HEADLESS_BUILD_DIR" == "$REPO_DIR" ]]; then
  echo "HEADLESS_BUILD_DIR không an toàn: $HEADLESS_BUILD_DIR" >&2
  exit 1
fi

build_parent=$(dirname -- "$HEADLESS_BUILD_DIR")
build_name=$(basename -- "$HEADLESS_BUILD_DIR")
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

cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/

sed -i 's#"".getClass().getResourceAsStream("/map/" + var1)#TileMap.class.getResourceAsStream("/map/" + var1)#' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#RMS.class.getResourceAsStream(var0)#' "$WORK_SRC_DIR/RMS.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#Res.class.getResourceAsStream(var0)#' "$WORK_SRC_DIR/Res.java"

find "$SCRIPT_DIR/src" "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"

javac \
  -encoding UTF-8 \
  -source 8 \
  -target 8 \
  -Xlint:none \
  -d "$BUILD_DIR" \
  @"$SOURCES_FILE"

cp -R "$WORK_SRC_DIR"/. "$BUILD_DIR"/
find "$BUILD_DIR" -name '*.java' -delete
mkdir -p "$BUILD_DIR/map"
for map_id in $(seq 0 159); do
  if [[ ! -f "$BUILD_DIR/map/$map_id" ]]; then
    : >"$BUILD_DIR/map/$map_id"
  fi
done
if [[ -f "$HEADLESS_ACCOUNT_CSV" ]]; then
  cp -- "$HEADLESS_ACCOUNT_CSV" "$BUILD_DIR/account.csv"
fi
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
  cp -- "$REPO_DIR/delllllllllll.txt" "$BUILD_DIR/delllllllllll.txt"
fi

if [[ ! -f "$BUILD_DIR/HeadlessMain.class" ]]; then
  echo "Build lỗi: không tìm thấy HeadlessMain.class" >&2
  exit 1
fi

if [[ -d "$HEADLESS_BUILD_DIR" ]]; then
  mv -- "$HEADLESS_BUILD_DIR" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$HEADLESS_BUILD_DIR"; then
  if [[ -d "$BACKUP_DIR" ]]; then
    mv -- "$BACKUP_DIR" "$HEADLESS_BUILD_DIR"
  fi
  exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT

echo "Headless classes built at $HEADLESS_BUILD_DIR/classes"
