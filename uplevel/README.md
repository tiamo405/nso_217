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

Mặc định dùng `account-as20.csv` và server `180.93.114.122:14444`. Đổi CSV bằng biến
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

## Chọn server

Chỉ có một server game: `180.93.114.122:14444`. Script truyền `-Dnso.server`
(đọc biến `UPLEVEL_SERVER`, mặc định `180.93.114.122:14444`). Có thể ghi đè bằng
`host:port` bất kỳ; giá trị tên server cũ (`tk`, `ninjamobile`...) bị bỏ qua.

```bash
UPLEVEL_SERVER=180.93.114.122:14444 ./uplevel/scripts/start-workers.sh --mode 1
```

Server mặc định nằm trong `src/UpdateServer.java` (hằng `m`, chuỗi Base64 dạng
`tên:ip:port:login`) và `server_config.py` (web dashboard).


## Mỗi nhân vật một tiến trình (JVM)

Client giữ trạng thái game trong biến `static` (`Char.myChar`, `Auto.fieldAL`, `GameScr.*`...), nên chạy
nhiều nhân vật trong một JVM làm nhân vật sau kế thừa trạng thái nhân vật trước (đã gặp: nhân vật lv2
dùng skill của Ninja kiếm lv25 nên đánh bù nhìn không ăn). Vì vậy `start-workers.sh` chạy
`scripts/worker-loop.sh`, vòng lặp này chạy java với `-Dnso.process.per.character=true`:

- Xong một nhân vật, `AccountAutoManager` ghi tiến độ vào `workers/worker-XX/home/uplevel-progress.properties`
  (tài khoản, nhân vật, thứ tự danh sách nhân vật) rồi thoát mã 3; vòng lặp chạy JVM mới, JVM mới đọc tiến độ và làm tiếp.
- Hết tài khoản: ghi `home/worker.done`, xoá file tiến độ, thoát mã 0 → vòng lặp dừng.
- PID: `loop.pid` = worker-loop.sh, `bot.pid` = java hiện tại. `stop-workers.sh` dừng loop trước rồi tới java.
- Tắt chế độ này: `UPLEVEL_PROCESS_PER_CHARACTER=false` (chạy lại kiểu cũ, mọi nhân vật trong một JVM).
- Chạy lại từ đầu: xoá `home/uplevel-progress.properties` (hoặc build-workers lại).

## Proxy (giới hạn số tài khoản online / IP)

Server chỉ cho khoảng 100 tài khoản online cùng lúc trên một IP. Proxy SOCKS5 khai báo trong
`proxy-tk.txt` (mỗi dòng `host:port:user:password`, dòng trống / `#` bị bỏ qua):

- **File trống hoặc không có** → mọi worker chạy mạng của máy như cũ.
- **Có P proxy** → worker được chia đều theo khối liên tiếp, mỗi proxy `W/P` worker
  (phần dư cho các proxy đầu). VD `build-workers.sh 50` + 2 proxy: worker-01..25 = proxy 1,
  worker-26..50 = proxy 2; 51 worker → 26 + 25.

```bash
python3 test_proxy_login.py          # test TẤT CẢ proxy (login thử acc đầu account-as20.csv), in dòng lỗi cần xoá
# python3 test_proxy_login.py --no-login   # chỉ test kết nối, không login
./uplevel/scripts/build-workers.sh 50  # in luôn bảng chia proxy
./uplevel/scripts/start-workers.sh --mode 1             # probe proxy rồi chạy
./uplevel/scripts/start-workers.sh --mode 1 --no-proxy  # ép chạy mạng máy dù file có proxy
```

- `build-workers` và `start-workers` gọi `scripts/proxy_assign.py`, ghi proxy vào `workers/worker-XX/.proxy`
  (đã gitignore). `start-workers` probe (SOCKS5 CONNECT tới server game) các proxy được dùng; proxy lỗi → không start.
- JVM nhận env `NSO_SOCKS5_PROXY` + `NSO_PROXY_REQUIRED=1` (`optimized-runtime/src/javax/microedition/io/Connector.java`
  mở SOCKS5); proxy chết thì worker lỗi kết nối chứ không lén chạy bằng IP máy. Env giữ nguyên qua các lần đổi JVM
  giữa các nhân vật. Dòng `network: ...` trong `stdout.log` cho biết worker đi đường nào.
- Biến môi trường: `UPLEVEL_PROXY_FILE` (đổi file proxy), `UPLEVEL_PROXY=0` (= `--no-proxy`).
- Sửa file proxy khi worker đang chạy: stop rồi start lại (worker đang chạy giữ proxy cũ).
- Windows: cùng cách dùng, `uplevel\scripts\build-workers.cmd 50` rồi `uplevel\scripts\start-workers.cmd --mode 1`.

## Web monitor

Xem trạng thái mọi worker (lv, exp, hành trang, lượng/xu/yên, CPU, heartbeat): `./uplevel_monitor/run.sh` → http://127.0.0.1:8090. Chi tiết: `uplevel_monitor/README.md`.

Windows: dùng các file `.cmd` cùng tên trong `scripts/` (vd. `uplevel\scripts\start-workers.cmd --mode 1`), xem `windows/README.md`.
