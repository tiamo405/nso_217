# Uplevel

Runtime riêng. Mã trong `overrides/` ghi đè class nền lúc build; `src/` giữ nguyên.
Build đọc `src/` và runtime overrides hiện có, chỉ ghi output vào `uplevel/build/`.
Account build nằm trong `uplevel/build/`; log đơn nằm trong `uplevel/run/`, log worker nằm trong `uplevel/workers/`.

```bash
# Build override
./uplevel/scripts/build.sh

# Chạy AS20; mode 0 mặc định chọn lớp Kiếm
./uplevel/scripts/run.sh 0

# Chọn trường khác bằng mode 2..6 (a20t, a20u, a20c, a20d, a20q)
./uplevel/scripts/run.sh 2

# Dừng
./uplevel/scripts/stop.sh
```

Mặc định dùng `account-as20.csv` và server Nsm1. Đổi CSV bằng biến
`UPLEVEL_ACCOUNT_CSV`. Dùng `UPLEVEL_FOREGROUND=1` để chạy trực tiếp và xem log
trên file `uplevel/run/as20.log`.

## Chạy nhiều worker

Mỗi worker nhận một phần account, dùng `home` và log riêng:

```bash
# Chia account-as20.csv thành 4 worker và build runtime
./uplevel/scripts/build-workers.sh 4

# Chạy mode Kiếm; đổi mode bằng --mode 2..6
./uplevel/scripts/start-workers.sh --mode 1

# Xem một worker hoặc tất cả worker
./uplevel/scripts/logs-workers.sh 1
./uplevel/scripts/logs-workers.sh

# Dừng worker
./uplevel/scripts/stop-workers.sh
```

Log nằm tại `uplevel/workers/worker-01/stdout.log` và
`uplevel/workers/worker-01/java-errors.log`. `run.sh` xóa log cũ khi start;
dùng `UPLEVEL_LOG_APPEND=1` để giữ chế độ append.
