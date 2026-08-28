# Headless worker scripts

Bộ script này chạy `HeadlessMain` trực tiếp, không dùng MicroEmulator và không cần copy JAR cho từng worker.

Mỗi worker chỉ có:

```text
headless-runtime/workers/worker-01/
├── account.csv
├── home/
├── stdout.log
├── java-errors.log
└── bot.pid
```

Khi start, classpath là:

```text
worker_dir:headless-runtime/build/classes
```

Vì vậy `AccountAutoManager` sẽ đọc `account.csv` riêng của từng worker trước.

## Build và chia worker

Chia `account.csv` gốc thành 10 worker:

```bash
headless-runtime/scripts/build-workers.sh 10
```

Chia từ file CSV khác:

```bash
ACCOUNT_CSV=account-test.csv headless-runtime/scripts/build-workers.sh 1
```

Không build lại classes, chỉ chia CSV:

```bash
BUILD_HEADLESS=0 headless-runtime/scripts/build-workers.sh 10
```

## Chạy

```bash
headless-runtime/scripts/start-workers.sh
```

Mặc định script start tất cả worker và cách nhau `START_DELAY=3` giây để tránh mở nhiều kết nối cùng lúc.

Đổi thời gian chờ giữa mỗi worker:

```bash
START_DELAY=10 headless-runtime/scripts/start-workers.sh
```

Hoặc dùng tham số:

```bash
headless-runtime/scripts/start-workers.sh --delay 10
```

Chạy riêng một worker:

```bash
headless-runtime/scripts/start-workers.sh 3
```

Chạy riêng nhiều worker:

```bash
headless-runtime/scripts/start-workers.sh 3 8 10
```

Cấu hình RAM/CPU:

```bash
JAVA_XMS=8m \
JAVA_XMX=48m \
START_DELAY=5 \
WORKER_NICE=10 \
headless-runtime/scripts/start-workers.sh
```

Pin CPU nếu muốn:

```bash
WORKER_TASKSET=0-3 headless-runtime/scripts/start-workers.sh
```

## Supervisor

Chạy supervisor để worker nào thoát thì tự bật lại:

```bash
headless-runtime/scripts/supervise-workers.sh
```

Mỗi worker được supervisor chạy đủ 2 lượt. Khi lượt đầu hoàn tất, marker
`home/worker.done` được đổi thành `home/worker.first-pass.done` và worker tự
chạy lượt kiểm tra thứ hai. Chỉ `home/worker.done` được tạo ở lượt thứ hai mới
là trạng thái `DONE` cuối cùng. Trong lượt kiểm tra thứ hai, nhân vật được NPC
báo đã hết NVHN ngay sẽ bỏ qua lật thẻ để không mua/lật trùng. Tài khoản từng
lỗi ở lượt một nhưng sang lượt hai vẫn còn và làm được NVHN thì vẫn lật thẻ sau
khi hoàn thành.

Đổi thời gian kiểm tra và thời gian chờ giữa mỗi lần start worker:

```bash
CHECK_INTERVAL=30 \
START_DELAY=10 \
headless-runtime/scripts/supervise-workers.sh
```

Supervisor mặc định restart worker đang chạy nếu `stdout.log` không có dữ liệu mới trong 5 phút:

```bash
STALE_LOG_SECONDS=300 headless-runtime/scripts/supervise-workers.sh
```

Đổi sang 10 phút bằng `STALE_LOG_SECONDS=600`. Đặt `STALE_LOG_SECONDS=0` để tắt riêng kiểm tra log im lặng; kiểm tra trạng thái AUTO NVHN bị lặp vẫn hoạt động.

Chỉ supervise một worker:

```bash
headless-runtime/scripts/supervise-workers.sh 3
```

Chỉ supervise nhiều worker:

```bash
headless-runtime/scripts/supervise-workers.sh 3 8 10
```

Giữ terminal supervisor mở. Nhấn `Ctrl+C` để dừng supervisor và các worker mà supervisor đang quản lý.

## Theo dõi

```bash
headless-runtime/scripts/status-workers.sh
headless-runtime/scripts/status-workers.sh --json
headless-runtime/scripts/logs-workers.sh
headless-runtime/scripts/logs-workers.sh 3
```

`--json` dành cho FastAPI dashboard và công cụ tự động; output dạng bảng mặc định không thay đổi.

Đổi bộ lọc log:

```bash
LOG_FILTER='AUTO NVHN HANG' headless-runtime/scripts/logs-workers.sh
```

## Dừng / restart

```bash
headless-runtime/scripts/stop-workers.sh
headless-runtime/scripts/restart-workers.sh
headless-runtime/scripts/stop-workers.sh 37
headless-runtime/scripts/restart-workers.sh 37
```

Không truyền số worker thì dừng/restart tất cả. Truyền một hoặc nhiều số thì chỉ tác động các worker đó.

## Test 1 account

```bash
ACCOUNT_CSV=account-test.csv headless-runtime/scripts/build-workers.sh 1
headless-runtime/scripts/start-workers.sh
headless-runtime/scripts/logs-workers.sh 1
```

Khi test xong:

```bash
headless-runtime/scripts/stop-workers.sh
```
