# Uplevel Monitor

Web chỉ xem (không điều khiển) cho worker `uplevel/workers/`, `uplevel35/workers/` và `uplevel1to35/workers/`. Chỉ dùng Python stdlib.

```bash
./uplevel_monitor/run.sh                 # http://0.0.0.0:8190
UPLEVEL_MONITOR_HOST=0.0.0.0 UPLEVEL_MONITOR_PORT=8091 ./uplevel_monitor/run.sh
```

Mỗi worker hiển thị: runtime, worker, trạng thái (Đang chạy / Đổi JVM / Xong / Dừng), account (thứ tự/tổng),
tên ingame, level, exp / exp cần lên cấp, ô trống hành trang, lượng, xu, yên, map, task, CPU (% một core), RAM,
và 3 mốc thời gian để phát hiện worker bị ngắt:

| Cột | Ý nghĩa | Trễ nghĩa là |
|---|---|---|
| Log cuối | mtime của `stdout.log` | không còn in gì |
| Heartbeat | lần cuối bot ghi `home/status.properties` (5 giây/lần) | JVM treo hoặc chết |
| Tiến triển | lần cuối exp/level/nhiệm vụ/map đổi | bot sống nhưng đứng yên |

Nguồn dữ liệu: `home/status.properties` do `uplevel/overrides/UplevelStatus.java` ghi (uplevel35 build copy
cùng file này). Worker chạy bản build cũ chưa có file này thì monitor đọc dòng `STATE` cuối trong log
(cột trạng thái ghi "từ log", thiếu hành trang/lượng/xu/yên) — build lại để có đủ.

Bấm một dòng (hoặc nút **Thông tin** / **Log** ở cột cuối) để mở ngăn chi tiết cố định bên phải màn hình
(phím `J`/`K` đổi worker, `Esc` đóng; URL giữ `#w=<runtime>/<worker>&tab=info|log` để tải lại trang vẫn mở đúng):

- **Thông tin worker**: số liệu nhân vật đang chơi + bảng mọi tài khoản trong `account.csv` (Xong / Đang chạy /
  Dừng giữa chừng / Chờ) kèm từng nhân vật và level. Nguồn: `home/roster.properties` do `UplevelStatus.java`
  ghi khi vào game (danh sách nhân vật + level server gửi lúc chọn nhân vật; nhân vật đang chơi lấy level trực
  tiếp). Build cũ chưa có file này thì lấy dòng `STATUS` cuối của từng nhân vật trong log (ghi "từ log").
- **Log**: `stdout.log` / `java-errors.log`, 400 dòng cuối, tự làm mới 3 giây.

Bot cũng in mỗi 60 giây (hoặc khi lên cấp / đổi nhiệm vụ) một dòng có mốc thời gian:

```
UPLEVEL35 STATUS ts=2026-11-09 11:22:18 acc=acctest4 name=testbon level=31 exp=241249/4622331 bag=19/42 luong=870 xu=200000 yen=5891510 map=35 task=22
```

Biến môi trường: `UPLEVEL_WORKERS_DIR`, `UPLEVEL35_WORKERS_DIR` để trỏ thư mục worker khác.

Windows: `uplevel_monitor\run.cmd` (CPU/RAM/PID đọc qua psutil hoặc Win32 API trong `winproc.py`), xem `windows/README.md`.
