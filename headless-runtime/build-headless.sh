#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
BUILD_DIR="$SCRIPT_DIR/build/classes"
SOURCES_FILE="$SCRIPT_DIR/build/sources.txt"

rm -rf -- "$SCRIPT_DIR/build"
mkdir -p "$BUILD_DIR"

find "$SCRIPT_DIR/src" "$REPO_DIR/src" -name '*.java' | sort >"$SOURCES_FILE"

javac \
  -encoding UTF-8 \
  -source 8 \
  -target 8 \
  -Xlint:none \
  -d "$BUILD_DIR" \
  @"$SOURCES_FILE"

cp -R "$REPO_DIR/src"/. "$BUILD_DIR"/
find "$BUILD_DIR" -name '*.java' -delete
for resource in account.csv delllllllllll.txt; do
  if [[ -f "$REPO_DIR/$resource" ]]; then
    cp -- "$REPO_DIR/$resource" "$BUILD_DIR/$resource"
  fi
done

echo "Headless classes built at $BUILD_DIR"
