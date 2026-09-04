# Tà Thú worker scripts

- `build-workers.sh N`: build runtime rồi chia `account.csv` cho N worker.
- `start-workers.sh [worker_number...]`: chạy stage `full` mặc định.
- `stop-workers.sh [worker_number...]`: dừng worker đã chọn hoặc tất cả.
- `restart-workers.sh`: stop rồi start.
- `status-workers.sh [--json]`: xem PID, trạng thái và log gần nhất.
- `logs-workers.sh [worker_number]`: lọc log `AUTO TA THU`.
- `supervise-workers.sh`: tự khởi động lại worker lỗi hoặc im log.

Có thể truyền `TA_THU_STAGE=observe|shop|orders|receive|fight|full` cho
`start-workers.sh`. Production dùng `full`.
