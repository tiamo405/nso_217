#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
BUILD_DIR="$SCRIPT_DIR/build/classes"
WORK_SRC_DIR="$SCRIPT_DIR/build/src-repo"
SOURCES_FILE="$SCRIPT_DIR/build/sources.txt"
HEADLESS_ACCOUNT_CSV=${HEADLESS_ACCOUNT_CSV:-"$REPO_DIR/account.csv"}

rm -rf -- "$SCRIPT_DIR/build"
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

echo "Headless classes built at $BUILD_DIR"
