#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$UPLEVEL_DIR/.." && pwd)
BUILD_DIR=$(realpath -m -- "${UPLEVEL_BUILD_DIR:-"$UPLEVEL_DIR/build"}")
ACCOUNT_CSV=${UPLEVEL_ACCOUNT_CSV:-"$REPO_DIR/account-as20.csv"}

if [[ ! -f "$ACCOUNT_CSV" ]]; then
  echo "Không tìm thấy account CSV: $ACCOUNT_CSV" >&2
  exit 1
fi
if [[ "$BUILD_DIR" == / || "$BUILD_DIR" == "$UPLEVEL_DIR" || "$BUILD_DIR" == "$REPO_DIR" ]]; then
  echo "UPLEVEL_BUILD_DIR không an toàn: $BUILD_DIR" >&2
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
  rm -rf -- "$STAGING_DIR" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$WORK_SRC_DIR" "$CLASSES_DIR"
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$REPO_DIR/optimized-runtime/src"/. "$WORK_SRC_DIR"/
cp -R "$REPO_DIR/optimized-runtime/overrides"/. "$WORK_SRC_DIR"/
"$SCRIPT_DIR/apply-transforms.sh" "$WORK_SRC_DIR"
find "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"
javac -encoding UTF-8 -source 8 -target 8 -Xlint:none -d "$CLASSES_DIR" @"$SOURCES_FILE"
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
rm -rf -- "$WORK_SRC_DIR" "$SOURCES_FILE"

if [[ -d "$BUILD_DIR" ]]; then
  mv -- "$BUILD_DIR" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$BUILD_DIR"; then
  if [[ -d "$BACKUP_DIR" ]]; then
    mv -- "$BACKUP_DIR" "$BUILD_DIR"
  fi
  exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT

echo "Build uplevel thành công: $BUILD_DIR/classes"
