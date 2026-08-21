#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
"$SCRIPT_DIR/stop-workers.sh"
"$SCRIPT_DIR/start-workers.sh"
