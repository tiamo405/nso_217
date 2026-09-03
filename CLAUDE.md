# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Tổng quan dự án

**NSO Client 217** là một game client J2ME (Java Micro Edition) cho game Ninja School Online (NSO). Repo bao gồm:
1. **Java game client** (`src/`) – MIDlet J2ME, được compile thành `dist/client_217.jar`
2. **Headless worker system** (`headless-runtime/`) – chạy client qua MicroEmulator không giao diện, quản lý nhiều tài khoản song song
3. **Web dashboard** (`web_control/`) – FastAPI để điều khiển worker từ trình duyệt
4. **Python scripts** (`*.py`) – công cụ phụ trợ (đăng nhập hàng loạt, gửi mail, bán item, nhận exp...)

---

## Các lệnh thường dùng

### Build Java client

```bash
# Build toàn bộ (xóa dist/ và tạo lại dist/client_217.jar)
ant -f build-docker.xml all

# Chỉ build JAR mà không xóa dist/workers/
ant -f build-docker.xml docker-jar

# Build qua Docker (không cần cài JDK local)
docker compose build && docker compose run --rm nso-builder
```

### Quản lý headless workers (runtime chính)

```bash
# Chia account.csv thành N worker và build classes headless
./headless-runtime/scripts/build-workers.sh 10

# Chạy với supervisor (khuyến nghị – tự restart khi worker chết)
JAVA_XMS=8m JAVA_XMX=48m ./headless-runtime/scripts/supervise-workers.sh

# Xem trạng thái tất cả worker
./headless-runtime/scripts/status-workers.sh
./headless-runtime/scripts/status-workers.sh --json

# Xem live log
./headless-runtime/scripts/logs-workers.sh
./headless-runtime/scripts/logs-workers.sh 3   # chỉ worker 3

# Dừng supervisor và toàn bộ worker
./headless-runtime/scripts/stop-workers.sh

# Reset marker DONE để chạy lại mà không build lại JAR
./headless-runtime/scripts/reset-completed-workers.sh
```

### Web dashboard

```bash
# Chạy thủ công (phát triển)
./headless-runtime/scripts/run-web-control.sh
# Mở http://127.0.0.1:8080

# Bootstrap toàn bộ VPS (cài deps, tạo service systemd, cài Tailscale)
sudo ./headless-runtime/scripts/bootstrap-vps.sh

# Quản lý service
sudo systemctl restart nso-headless-web
sudo journalctl -u nso-headless-web -f
```

### Kiểm thử

```bash
# Chạy unit test web control
python3 -m unittest -v tests.test_web_control
python3 -m unittest -v tests.test_supervisor_watchdog

# Syntax check bash scripts
bash -n headless-runtime/build-headless.sh headless-runtime/scripts/*.sh scripts/*.sh

# Kiểm tra lỗi Python
python3 -m compileall -q web_control tests
```

### MicroEmulator workers cũ (scripts/ gốc)

```bash
# Build JAR worker dùng MicroEmulator (không phải headless)
./scripts/build-workers.sh 10
MICROEMULATOR_JAR=/path/to/Microemulator.jar ./scripts/supervise-workers.sh
./scripts/status-workers.sh
./scripts/stop-workers.sh
```

---

## Kiến trúc hệ thống

### Java game client (`src/`)

Entry point: `GameMidlet.java` (MIDlet) → `MotherCanvas.java` (game loop thread) → `GameCanvas.java` (rendering + input).

**Network**: `Session_ME.java` quản lý TCP socket (`socket://HOST:PORT`). Sau handshake, mọi message được XOR-encrypt với key từ server. `Service.java` là layer duy nhất gửi request đến server (login, attack, chat, shop...). `Message.java` là unit truyền nhận.

**Screens**: `GameCanvas.currentScreen` (kiểu `mScreen`) giữ màn hình hiện tại. Các screen chính: `LoginScr`, `SelectServerScr`, `SelectCharScr`, `GameScr`, `MapScr`.

**Auto/bot**: Các class `Auto*.java` (`AutoNvhn`, `AutoEnterCave`, `AutoNpc`...) là logic bot tích hợp sẵn trong client, không phải plugin ngoài.

**Resources**: `src/x1/` chứa toàn bộ asset (ảnh, hiệu ứng, background). `src/map/` chứa dữ liệu map (file nhị phân, đánh số từ 0–159). `src/font/` chứa bitmap font.

### Headless worker system (`headless-runtime/`)

Mục tiêu: chạy `GameMidlet` trực tiếp trên JVM thường (không J2ME) bằng cách cung cấp stub API `javax.microedition.*`.

- **`headless-runtime/src/`** – stub implementation của J2ME API + `HeadlessMain.java` là entry point thay cho MIDlet container.
- **`build-headless.sh`** – compile source game cùng stub, tạo `headless-runtime/build/classes/`.
- **`scripts/build-workers.sh`** – chia `account.csv` thành N nhóm, tạo thư mục `headless-runtime/workers/worker-XX/`.
- **`scripts/supervise-workers.sh`** – giám sát và restart worker theo PID file `bot.pid`. Worker tự thoát và tạo `worker.done` khi xử lý hết tất cả tài khoản.
- **`scripts/status-workers.sh`** – đọc PID + log, hỗ trợ `--json` cho web dashboard.

### Web dashboard (`web_control/`)

FastAPI app (`app.py`) gọi vào `HeadlessManager` (`manager.py`) để chạy các script bash. Dashboard listen `127.0.0.1:8080`, chỉ nên truy cập qua Tailscale Serve hoặc SSH tunnel – không expose ra Internet.

API routes: upload `account.csv` → chọn số worker → Build & Run. Hỗ trợ Start/Stop/Restart từng worker và live log streaming.

---

## Cấu hình và biến môi trường

File `.env` (copy từ `.env.example`):

| Biến | Mặc định | Ý nghĩa |
|------|----------|---------|
| `NSO_WEB_PORT` | `8080` | Port web dashboard |
| `ACCOUNT_CSV` | `account.csv` | Đường dẫn file tài khoản |
| `HEADLESS_WORKERS_DIR` | `headless-runtime/workers` | Thư mục chứa worker |
| `JAVA_XMS` / `JAVA_XMX` | `8m` / `48m` | Heap Java mỗi worker |
| `START_DELAY` | `3` | Giây giữa mỗi worker khi khởi động |
| `CHECK_INTERVAL` | `20` | Giây giữa mỗi lần supervisor kiểm tra |
| `STALE_LOG_SECONDS` | `300` | Restart worker nếu log im lặng quá N giây |

---

## Định dạng commit

Viết commit bằng **tiếng Việt có dấu** theo mẫu:

```
<type>(<scope>): <icon> <mô tả ngắn gọn>
```

Type: `feat` ✨, `fix` 🐛, `docs` 📝, `refactor` ♻️, `perf` ⚡, `test` ✅, `build` 🏗️, `chore` 🔧. Chỉ thêm body khi cần giải thích lý do.

---

## Lưu ý quan trọng

- **MicroEmulator bắt buộc** cho `scripts/` cũ. Path mặc định: `/home/namtp/Downloads/game-teamobi/Microemulator.jar`. Đổi bằng biến `MICROEMULATOR_JAR`.
- `headless-runtime/` là hướng thay thế MicroEmulator (đang phát triển). Hiện tại có thể dùng thay thế hoàn toàn – xem `headless-runtime/README.md`.
- `delllllllllll.txt` chứa danh sách item ID cần xóa (cách nhau bằng `;`). File này được đóng gói vào JAR khi build – phải build lại sau khi sửa.
- `account.csv` phải có header `username,password`, không có dấu cách thừa.
- Sau khi sửa source Java, luôn chạy lại cả compile lẫn build-workers trước khi chạy supervisor.
- Web dashboard không có password; không thay `127.0.0.1` thành `0.0.0.0` nếu máy đang mở thẳng ra Internet.
