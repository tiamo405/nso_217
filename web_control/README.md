# NSO Headless Web Control

FastAPI dashboard bọc quanh các script trong `headless-runtime/scripts`. Runtime Java vẫn chạy native, không dùng Docker và không dùng MicroEmulator.

## Chạy nhanh trên VPS

```bash
git clone <repository-url>
cd NSO_217
sudo ./headless-runtime/scripts/bootstrap-vps.sh
```

Bootstrap cài OpenJDK/Python, tạo `.venv`, sinh `.env`, cài và bật service `nso-headless-web`. Nếu chưa có Tailscale, script tải installer chính thức từ `tailscale.com`. Dashboard chỉ listen tại `127.0.0.1:8080`.

Muốn triển khai không tương tác bằng auth key dùng một lần/ephemeral:

```bash
sudo TS_AUTHKEY='tskey-auth-...' ./headless-runtime/scripts/bootstrap-vps.sh
```

Auth key chỉ tồn tại trong environment của lần chạy, không được ghi vào `.env`. Đặt `INSTALL_TAILSCALE=0` nếu không muốn bootstrap cài Tailscale.

Sau khi Tailscale đã kết nối:

```bash
sudo tailscale serve --bg http://127.0.0.1:8189
tailscale serve status
```

Không dùng Tailscale Funnel vì Funnel công khai service ra Internet.

## Chạy thủ công khi phát triển

```bash
./headless-runtime/scripts/run-web-control.sh
```

Launcher ưu tiên `.venv` nếu có; nếu không, nó dùng `python3` và các thư viện đã cài trên máy. Mở thẳng `http://127.0.0.1:8080`, không có màn hình đăng nhập.

Dashboard không có password riêng. FastAPI chỉ listen trên `127.0.0.1`; truy cập từ máy khác nên đi qua Tailscale Serve hoặc SSH tunnel. Không đổi host thành `0.0.0.0` nếu máy đang mở trực tiếp ra Internet.

## Quy trình dashboard

1. Upload `account.csv` có header `username,password`.
2. Chọn số worker.
3. Nhấn Build & Run.
4. Xem status, live log; Stop, Start hoặc Restart từng worker.
5. Stop tất cả sẽ dừng supervisor và worker, đồng thời ghi nhớ không tự bật lại sau reboot.

`Stop` tại một worker tạo marker `.paused`, dừng Java và giữ supervisor chạy cho
các worker còn lại. Supervisor không tự bật lại worker có trạng thái `PAUSED`.
Nhấn `Start` worker đó để xóa marker và chạy lại; nút `Restart` dùng để khởi
động lại một worker đang hoạt động. Trạng thái pause được giữ khi restart
dashboard hoặc VPS; build lại danh sách worker sẽ xóa trạng thái pause cũ.

Dashboard hiển thị thời điểm `stdout.log` cập nhật gần nhất trong bảng worker. Cửa sổ live log cũng thêm thời gian cho từng đợt dữ liệu mới nhận được.

Supervisor mặc định restart worker đang chạy nếu `stdout.log` im lặng quá 5 phút. Có thể cấu hình trong `.env`:

```env
STALE_LOG_SECONDS=300
```

Đặt `600` cho 10 phút hoặc `0` để tắt watchdog log. Sau khi đổi `.env` trên VPS, restart `nso-headless-web` và restart supervisor để tiến trình mới nhận cấu hình.

Dashboard không có chức năng chạy shell tùy ý và không hiển thị password trong CSV.

## Lệnh quản trị

```bash
sudo systemctl restart nso-headless-web
sudo systemctl status nso-headless-web --no-pager
sudo journalctl -u nso-headless-web -f
```

Các script CLI cũ vẫn hoạt động:

```bash
headless-runtime/scripts/status-workers.sh
headless-runtime/scripts/status-workers.sh --json
headless-runtime/scripts/supervise-workers.sh
```

Không nên chạy hai supervisor cùng lúc. Dashboard nhận diện supervisor hiện có qua `headless-runtime/workers/supervisor.pid`.

## Kiểm thử

```bash
python3 -m unittest -v tests.test_web_control
bash -n headless-runtime/build-headless.sh headless-runtime/scripts/*.sh scripts/*.sh
python3 -m compileall -q web_control tests
```
