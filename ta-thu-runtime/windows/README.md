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

Chỉ có một server game: `180.93.114.122:14444` (có thể ghi đè bằng
`NSO_SERVER=host:port`). Trạng thái
theo ngày được giữ ở `ta-thu-runtime\ta-thu-state`, nên build lại worker không
làm Tà Thú chạy trùng lần trong cùng ngày.

Worker Tà Thú chạy trên nền optimized (`OptimizedMain`, cùng bộ `-Dnso.*` với NVHN,
log có giờ). Proxy dùng chung `optimized-runtime/scripts/proxy_support.py`: khi
`NSO_PROXY_WORKERS_PER_PROXY` > 0 (Web Dashboard tick **Dùng proxy**) worker được chia
đều cho `proxy-tk.txt` và JVM nhận `NSO_SOCKS5_PROXY`; status JSON có trường `proxy`.
`start` / `restart` nhận `--delay 0 <số worker>` để web Start/Restart từng worker.
