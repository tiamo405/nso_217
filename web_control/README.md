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
./optimized-runtime/scripts/run-web-control.sh
```

Launcher ưu tiên `.venv` nếu có; nếu không, nó dùng `python3` và các thư viện đã cài trên máy. Mở thẳng `http://127.0.0.1:8080`, không có màn hình đăng nhập.

Dashboard không có password riêng. FastAPI chỉ listen trên `127.0.0.1`; truy cập từ máy khác nên đi qua Tailscale Serve hoặc SSH tunnel. Không đổi host thành `0.0.0.0` nếu máy đang mở trực tiếp ra Internet.

## Quy trình dashboard

1. Upload `account.csv` có header `username,password`.
2. Chọn số worker.
3. Chọn **TK (Truyền Kỳ)** hoặc **NinjaMobile** trong ô Server.
4. Nhấn **Chỉ Build** để compile và chia worker; nhấn **Run workers** để khởi động supervisor với server đã chọn.
5. Cấu hình **Hẹn giờ & Tự động (GMT+7)**:
   - **Giờ chạy đầu tiên**: đến mốc này hệ thống sẽ build lại từ đầu (xóa worker done, compile, chia lại account và run supervisor).
   - **Lặp lại sau (giờ)**: sau mỗi lần lịch được kích hoạt, hệ thống chờ đúng số giờ này rồi build & run lại toàn bộ worker. Hai giá trị này luôn dùng cùng nhau, không còn là hai chế độ loại trừ.
   - **Giãn cách worker (giây)**: thời gian chờ giữa hai worker khi lịch tự động khởi động NVHN hoặc Tà Thú; `0` để chạy liên tiếp.
   - **Bật tự động Build & Run NVHN**: bật chu kỳ ở trên.
   - **Auto Tà Thú khi NVHN xong**: Khi toàn bộ worker hoàn tất NVHN (2/2 lượt), hệ thống tự động build và chạy supervisor Tà Thú (`ta-thu-runtime/`).
   - **Ưu tiên NVHN**: Khi đến mốc hẹn giờ của ngày hôm sau, hệ thống tự động ngắt toàn bộ tiến trình Tà Thú để ưu tiên Build & Run lại NVHN.
6. Xem status, tên nhân vật đang chạy, live log; Stop, Start hoặc Restart từng worker.
7. Stop tất cả sẽ dừng cả supervisor NVHN và Tà Thú, đồng thời ghi nhớ không tự bật lại sau reboot.

Trong panel **Supervisor**, có thể cấu hình các giá trị tương tự cho lần chạy thủ công:

- **Restart worker định kỳ (giờ)**: tính từ lần khởi động gần nhất của từng worker; `0` để tắt.
- **Giãn cách khởi động worker tiếp theo (giây)**: thời gian chờ giữa các worker khi Supervisor khởi động; `0` để chạy liên tiếp.

Nhấn **Lưu cấu hình Supervisor**. Nếu Supervisor đang chạy, cần Stop rồi Start lại
để cấu hình mới được truyền vào tiến trình Supervisor.

Nếu đã bật lịch build lại toàn bộ, nên đặt **Restart worker định kỳ** về `0` để
không có hai bộ hẹn giờ cùng tác động lên worker gần cùng một thời điểm.

**Start/Run** supervisor chạy tiếp tiến độ hiện có. Nếu toàn bộ worker đã hoàn
thành 2/2 lượt, nhấn **Build** rồi **Run** để chạy lại từ đầu. Start thất bại
hoặc Stop tất cả sẽ không kích hoạt Auto Tà Thú từ tiến độ NVHN cũ.

`Stop` tại một worker tạo marker `.paused`, dừng Java và giữ supervisor chạy cho
các worker còn lại. Supervisor không tự bật lại worker có trạng thái `PAUSED`.
Nhấn `Start` worker đó để xóa marker và chạy lại; nút `Restart` dùng để khởi
động lại một worker đang hoạt động. Trạng thái pause được giữ khi restart
dashboard hoặc VPS; build lại danh sách worker sẽ xóa trạng thái pause cũ.

Dashboard hiển thị thời điểm `stdout.log` cập nhật gần nhất và cột **Nhân vật** trong bảng worker. Cửa sổ live log cũng thêm thời gian cho từng đợt dữ liệu mới nhận được.

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

Các script CLI:

```bash
optimized-runtime/scripts/status-workers.sh
optimized-runtime/scripts/status-workers.sh --json
optimized-runtime/scripts/supervise-workers.sh
```

Không nên chạy hai supervisor cùng lúc. Dashboard nhận diện supervisor hiện có qua `optimized-runtime/workers/supervisor.pid`.

## Kiểm thử

```bash
python3 -m unittest -v tests.test_web_control
bash -n optimized-runtime/scripts/*.sh
python3 -m compileall -q web_control tests
```
