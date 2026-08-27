#!/usr/bin/env bash
set -euo pipefail

if (( EUID != 0 )); then
    echo "Hãy chạy bằng sudo: sudo ./headless-runtime/scripts/bootstrap-vps.sh" >&2
    exit 1
fi

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
HEADLESS_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$HEADLESS_DIR/.." && pwd)
APP_USER=${SUDO_USER:-root}
APP_GROUP=$(id -gn "$APP_USER")
VENV_DIR="$REPO_DIR/.venv"
ENV_FILE="$REPO_DIR/.env"
SERVICE_FILE=/etc/systemd/system/nso-headless-web.service
WEB_PORT=${NSO_WEB_PORT:-8080}

if ! [[ "$WEB_PORT" =~ ^[0-9]+$ ]] || (( WEB_PORT < 1 || WEB_PORT > 65535 )); then
    echo "NSO_WEB_PORT không hợp lệ: $WEB_PORT" >&2
    exit 1
fi

if ! command -v apt-get >/dev/null 2>&1; then
    echo "Bootstrap hiện hỗ trợ Ubuntu/Debian có apt-get." >&2
    exit 1
fi

run_as_app() {
    if [[ "$APP_USER" == root ]]; then
        "$@"
    else
        sudo -u "$APP_USER" -- "$@"
    fi
}

missing_packages=()
for package in openjdk-17-jdk-headless python3 python3-venv python3-pip ca-certificates curl; do
    if ! dpkg-query -W -f='${Status}' "$package" 2>/dev/null | grep -q 'install ok installed'; then
        missing_packages+=("$package")
    fi
done
if (( ${#missing_packages[@]} > 0 )); then
    echo "Đang cài dependency: ${missing_packages[*]}"
    apt-get update
    DEBIAN_FRONTEND=noninteractive apt-get install -y "${missing_packages[@]}"
fi

if ! python3 -c 'import sys; raise SystemExit(0 if sys.version_info >= (3, 10) else 1)'; then
    echo "FastAPI dashboard yêu cầu Python 3.10 trở lên." >&2
    exit 1
fi

chmod +x \
    "$HEADLESS_DIR"/*.sh \
    "$SCRIPT_DIR"/*.sh

if [[ ! -d "$VENV_DIR" ]]; then
    echo "Đang tạo Python virtual environment..."
    run_as_app python3 -m venv "$VENV_DIR"
fi
echo "Đang cài FastAPI/Uvicorn..."
run_as_app "$VENV_DIR/bin/python" -m pip install --upgrade pip
run_as_app "$VENV_DIR/bin/python" -m pip install -r "$REPO_DIR/web_control/requirements.txt"

if [[ ! -f "$ENV_FILE" ]]; then
    umask 077
    printf 'NSO_WEB_PORT=%s\n' "$WEB_PORT" >"$ENV_FILE"
    chown "$APP_USER:$APP_GROUP" "$ENV_FILE"
    echo "Đã tạo $ENV_FILE với quyền 600."
else
    chmod 600 "$ENV_FILE"
    chown "$APP_USER:$APP_GROUP" "$ENV_FILE"
    echo "Giữ nguyên cấu hình hiện có tại $ENV_FILE."
fi

env_value() {
    local key=$1
    sed -n "s/^${key}=//p" "$ENV_FILE" | tail -n 1
}

configured_port=$(env_value NSO_WEB_PORT)
if [[ -n "$configured_port" ]]; then
    WEB_PORT=$configured_port
fi
if ! [[ "$WEB_PORT" =~ ^[0-9]+$ ]] || (( WEB_PORT < 1 || WEB_PORT > 65535 )); then
    echo "NSO_WEB_PORT trong $ENV_FILE không hợp lệ: $WEB_PORT" >&2
    exit 1
fi

cat >"$SERVICE_FILE" <<EOF
[Unit]
Description=NSO Headless FastAPI Control
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=$APP_USER
Group=$APP_GROUP
WorkingDirectory=$REPO_DIR
EnvironmentFile=-$ENV_FILE
ExecStart=$SCRIPT_DIR/run-web-control.sh
Restart=on-failure
RestartSec=3
TimeoutStopSec=15
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=full

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable --now nso-headless-web.service

echo
echo "FastAPI đang listen tại http://127.0.0.1:$WEB_PORT"

if ! command -v tailscale >/dev/null 2>&1 && [[ ${INSTALL_TAILSCALE:-1} != 0 ]]; then
    echo "Đang cài Tailscale bằng installer chính thức..."
    tailscale_installer=$(mktemp /tmp/tailscale-install.XXXXXX.sh)
    if curl -fsSL https://tailscale.com/install.sh -o "$tailscale_installer"; then
        sh "$tailscale_installer"
    else
        rm -f -- "$tailscale_installer"
        echo "Không tải được installer Tailscale." >&2
        exit 1
    fi
    rm -f -- "$tailscale_installer"
fi

if command -v tailscale >/dev/null 2>&1; then
    if ! tailscale status >/dev/null 2>&1 && [[ -n ${TS_AUTHKEY:-} ]]; then
        echo "Đang kết nối VPS vào tailnet bằng TS_AUTHKEY..."
        tailscale up --auth-key="$TS_AUTHKEY"
    fi
    if tailscale status >/dev/null 2>&1; then
        if tailscale serve --bg "http://127.0.0.1:$WEB_PORT"; then
            echo "Đã cấu hình Tailscale Serve."
            tailscale serve status || true
        else
            echo "Không tự cấu hình được Tailscale Serve. Chạy thủ công:" >&2
            echo "  sudo tailscale serve --bg http://127.0.0.1:$WEB_PORT" >&2
        fi
    else
        echo "Tailscale đã cài nhưng chưa đăng nhập. Chạy:" >&2
        echo "  sudo tailscale up" >&2
        echo "  sudo tailscale serve --bg http://127.0.0.1:$WEB_PORT" >&2
    fi
else
    echo "Chưa tìm thấy Tailscale (INSTALL_TAILSCALE=0). Cài sau đó chạy:" >&2
    echo "  sudo tailscale up" >&2
    echo "  sudo tailscale serve --bg http://127.0.0.1:$WEB_PORT" >&2
fi

echo
echo "Kiểm tra service: systemctl status nso-headless-web --no-pager"
echo "Xem log: journalctl -u nso-headless-web -f"
