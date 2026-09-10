#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
CLASSES_DIR=${HEADLESS_CLASSES_DIR:-"$SCRIPT_DIR/build/classes"}
HOME_DIR=${HEADLESS_HOME:-"$SCRIPT_DIR/run/home"}
JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-8m}
JAVA_XMX=${JAVA_XMX:-48m}
JAVA_OPTS=${JAVA_OPTS:-"-XX:+UseSerialGC -Djava.awt.headless=true"}
source "$SCRIPT_DIR/scripts/tuning-options.sh"

if [[ ! -d "$CLASSES_DIR" ]]; then
  if [[ -n "${HEADLESS_CLASSES_DIR:-}" ]]; then
    echo "Không tìm thấy HEADLESS_CLASSES_DIR: $CLASSES_DIR" >&2
    exit 1
  fi
  "$SCRIPT_DIR/build-headless.sh"
fi

mkdir -p "$HOME_DIR"
read -r -a java_opts_array <<< "$JAVA_OPTS"

exec "$JAVA_BIN" \
  "-Xms$JAVA_XMS" \
  "-Xmx$JAVA_XMX" \
  "${java_opts_array[@]}" \
  "${headless_tuning_args[@]}" \
  "-Duser.home=$HOME_DIR" \
  -cp "$CLASSES_DIR" \
  HeadlessMain
