#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$UPLEVEL_DIR/.." && pwd)
BUILD_DIR=$(realpath -m -- "${UPLEVEL_BUILD_DIR:-"$UPLEVEL_DIR/build"}")
CLASSES_DIR="$BUILD_DIR/classes"
RUN_DIR=${UPLEVEL_RUN_DIR:-"$UPLEVEL_DIR/run"}
ACCOUNT_CSV=${UPLEVEL_ACCOUNT_CSV:-"$REPO_DIR/account-as20.csv"}
AS20_MODE=${1:-${UPLEVEL_MODE:-0}}
PID_FILE="$RUN_DIR/as20.pid"

if (( $# > 1 )) || ! [[ "$AS20_MODE" =~ ^[0-6]$ ]]; then
  echo "Dùng: $0 [mode 0..6]" >&2
  exit 1
fi
if [[ "$AS20_MODE" == 0 ]]; then
  AS20_MODE=1
fi
if [[ ! -f "$ACCOUNT_CSV" ]]; then
  echo "Không tìm thấy account CSV: $ACCOUNT_CSV" >&2
  exit 1
fi

if [[ -f "$PID_FILE" ]]; then
  old_pid=$(<"$PID_FILE")
  if [[ "$old_pid" =~ ^[0-9]+$ && -r "/proc/$old_pid/cmdline" ]]; then
    old_cmdline=$(tr '\0' ' ' <"/proc/$old_pid/cmdline" 2>/dev/null || true)
    if [[ "$old_cmdline" == *"OptimizedMain"* && "$old_cmdline" == *"$CLASSES_DIR"* ]]; then
      echo "Uplevel đang chạy (PID $old_pid). Dùng ./uplevel/scripts/stop.sh trước." >&2
      exit 1
    fi
  fi
fi

"$SCRIPT_DIR/build.sh"
mkdir -p "$RUN_DIR/home"

JAVA_BIN=${JAVA_BIN:-java}
LOG_FILE="$RUN_DIR/as20.log"
if [[ "${UPLEVEL_LOG_APPEND:-0}" != 1 ]]; then
  : >"$LOG_FILE"
fi
JAVA_CMD=("$JAVA_BIN" -Xms8m -Xmx36m \
  -Dnso.server=ninjamobile \
  -Dnso.as20.mode="$AS20_MODE" \
  -Dnso.uplevel.crystal="${UPLEVEL_CRYSTAL_ID:--1}" \
  -Dnso.uplevel.all.crystals="${UPLEVEL_ALL_CRYSTALS:-false}" \
  -Dnso.uplevel.duplicate="${UPLEVEL_UPGRADE_DUPLICATE:-false}" \
  -Dnso.uplevel.duplicate.count="${UPLEVEL_UPGRADE_DUPLICATE_COUNT:-5}" \
  -Dnso.uplevel.no.split="${UPLEVEL_NO_SPLIT:-false}" \
  -Dnso.uplevel.allow.stacks="${UPLEVEL_ALLOW_STACKS:-false}" \
  -Dnso.uplevel.upgrade.safe="${UPLEVEL_UPGRADE_SAFE:-false}" \
  -Duser.home="$RUN_DIR/home" \
  -cp "$CLASSES_DIR" \
  OptimizedMain)
if [[ "${UPLEVEL_FOREGROUND:-0}" == 1 ]]; then
  exec "${JAVA_CMD[@]}" >>"$LOG_FILE" 2>&1
fi

nohup "${JAVA_CMD[@]}" >>"$LOG_FILE" 2>&1 </dev/null &
pid=$!
printf '%s\n' "$pid" >"$PID_FILE"
sleep 1
if ! kill -0 "$pid" 2>/dev/null; then
  rm -f -- "$PID_FILE"
  echo "Uplevel khởi động lỗi; xem $LOG_FILE" >&2
  exit 1
fi
echo "Đã chạy uplevel (PID $pid, mode $AS20_MODE). Log: $LOG_FILE"
