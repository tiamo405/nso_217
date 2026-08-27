#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$HEADLESS_DIR/.." && pwd)
ENV_FILE=${NSO_ENV_FILE:-"$REPO_DIR/.env"}
VENV_DIR=${NSO_VENV_DIR:-"$REPO_DIR/.venv"}

if [[ -f "$ENV_FILE" ]]; then
    set -a
    # shellcheck disable=SC1090
    source "$ENV_FILE"
    set +a
fi

port=${NSO_WEB_PORT:-8080}
if ! [[ "$port" =~ ^[0-9]+$ ]] || (( port < 1 || port > 65535 )); then
    echo "NSO_WEB_PORT không hợp lệ: $port" >&2
    exit 1
fi

cd -- "$REPO_DIR"
if [[ -x "$VENV_DIR/bin/python" ]] \
        && "$VENV_DIR/bin/python" -c 'import fastapi, uvicorn' >/dev/null 2>&1; then
    PYTHON_BIN="$VENV_DIR/bin/python"
elif python3 -c 'import fastapi, uvicorn' >/dev/null 2>&1; then
    PYTHON_BIN=python3
else
    echo "Không tìm thấy FastAPI/Uvicorn. Chạy headless-runtime/scripts/bootstrap-vps.sh hoặc pip install -r web_control/requirements.txt." >&2
    exit 1
fi

exec "$PYTHON_BIN" -m uvicorn web_control.app:app \
    --host 127.0.0.1 \
    --port "$port" \
    --workers 1
