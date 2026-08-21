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

## Theo dõi

```bash
headless-runtime/scripts/status-workers.sh
headless-runtime/scripts/logs-workers.sh
headless-runtime/scripts/logs-workers.sh 3
```

Đổi bộ lọc log:

```bash
LOG_FILTER='AUTO NVHN HANG' headless-runtime/scripts/logs-workers.sh
```

## Dừng / restart

```bash
headless-runtime/scripts/stop-workers.sh
headless-runtime/scripts/restart-workers.sh
```

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
