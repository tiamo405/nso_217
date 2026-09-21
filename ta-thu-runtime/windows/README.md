# Tà Thú runtime trên Windows

Web Dashboard Windows gọi `ta_thu_manager.py`, không gọi các script Bash trong
`ta-thu-runtime/scripts/`. Bộ điều khiển này dùng cùng `account.csv`, Java
classes và marker của runtime Tà Thú nhưng chạy được trên Windows Server khi
chỉ có Python + Java + `psutil`.

Chạy thủ công từ thư mục gốc repository:

```cmd
python ta-thu-runtime\windows\ta_thu_manager.py build-workers 10
python ta-thu-runtime\windows\ta_thu_manager.py supervise --delay 30 --interval 20
python ta-thu-runtime\windows\ta_thu_manager.py status --json
python ta-thu-runtime\windows\ta_thu_manager.py stop
```

Server được chọn qua biến môi trường `NSO_SERVER=tk` hoặc
`NSO_SERVER=ninjamobile`; Web Dashboard tự truyền lựa chọn đang lưu. Trạng thái
theo ngày được giữ ở `ta-thu-runtime\ta-thu-state`, nên build lại worker không
làm Tà Thú chạy trùng lần trong cùng ngày.
