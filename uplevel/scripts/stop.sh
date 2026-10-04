#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
RUN_DIR=${UPLEVEL_RUN_DIR:-"$UPLEVEL_DIR/run"}
BUILD_DIR=$(realpath -m -- "${UPLEVEL_BUILD_DIR:-"$UPLEVEL_DIR/build"}/classes")
PID_FILE="$RUN_DIR/as20.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "Uplevel chưa chạy."
  exit 0
fi

pid=$(<"$PID_FILE")
if ! [[ "$pid" =~ ^[0-9]+$ ]] || [[ ! -r "/proc/$pid/cmdline" ]]; then
  rm -f -- "$PID_FILE"
  echo "Đã xóa PID cũ."
  exit 0
fi

cmdline=$(tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null || true)
if [[ "$cmdline" != *"OptimizedMain"* || "$cmdline" != *"$BUILD_DIR"* ]]; then
  echo "PID $pid không thuộc uplevel; giữ nguyên PID file." >&2
  exit 1
fi

kill -TERM "$pid"
for _ in {1..10}; do
  if ! kill -0 "$pid" 2>/dev/null; then
    rm -f -- "$PID_FILE"
    echo "Đã dừng uplevel."
    exit 0
  fi
  sleep 1
done
echo "Uplevel chưa thoát; PID $pid vẫn chạy." >&2
exit 1
