#!/usr/bin/env bash
# Web monitor (chỉ xem) cho worker uplevel/ và uplevel35/.
#   ./uplevel_monitor/run.sh            -> http://127.0.0.1:8090
#   UPLEVEL_MONITOR_HOST=0.0.0.0 UPLEVEL_MONITOR_PORT=8091 ./uplevel_monitor/run.sh
set -euo pipefail
REPO_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
cd -- "$REPO_DIR"
exec python3 -m uplevel_monitor.server
