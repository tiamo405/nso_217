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

Màn hình gồm 3 thẻ điều khiển (NVHN, Tà Thú, Hệ thống), panel **Cấu hình** thu gọn,
bảng worker và khung **Log** trượt bên phải. Nút nào không dùng được ở trạng thái
hiện tại sẽ bị khoá (rê chuột để xem lý do); thao tác dừng/build luôn hỏi xác nhận.

1. Upload `account.csv` (header `username,password`) ở thẻ **Hệ thống**.
2. Mở **Cấu hình**, đặt số worker, giãn cách, tick **Dùng proxy** và lịch rồi bấm
   **Lưu cấu hình** (một nút lưu tất cả).
   - **Dùng proxy**: chia đều worker cho các proxy trong `proxy-tk.txt` giống uplevel
     (W worker / P proxy, khối liên tiếp: mỗi proxy W//P worker, W%P proxy đầu thêm 1;
     VD 10 worker / 4 proxy = 3+3+2+2). Proxy không kết nối được bị bỏ qua khi chia.
3. Thẻ **NVHN**:
   - **Build & Run**: dừng NVHN, build lại, chia worker từ account.csv rồi chạy.
   - **Chỉ Build**: build và chia worker, chưa chạy.
   - **▶ Start**: chạy tiếp tiến độ hiện có (nếu Tà Thú đang chạy sẽ bị dừng trước).
   - **■ Stop**: chỉ dừng NVHN, không ảnh hưởng Tà Thú.
4. Thẻ **Tà Thú**:
   - **▶ Start Tà Thú**: chạy thủ công, không chờ NVHN. Có worker cũ thì dùng lại, có
     thể tick "Chia lại worker" để build lại từ account.csv. Bị khoá khi NVHN đang chạy
     (hai runtime dùng chung account).
   - **■ Stop Tà Thú**: dừng và ghi `ta_thu_desired=false`, scheduler không tự bật lại.
5. **⏹ Stop tất cả**: dừng NVHN + Tà Thú, tắt tự khởi động (kể cả sau reboot).
6. **Chạy lại từ đầu theo lịch (GMT+7)**: đến **Lần đầu lúc** và sau đó cứ mỗi
   **N giờ**, hệ thống ngắt Tà Thú, build lại từ đầu (chia lại worker, xoá tiến độ cũ)
   rồi chạy NVHN. **Auto Tà Thú khi NVHN xong** chạy worker Tà Thú cùng số thứ tự khi
   worker NVHN tương ứng hoàn tất.

Không còn "Restart worker định kỳ" riêng (mặc định 0): việc chạy lại do lịch ở trên
đảm nhiệm. Đổi số worker / proxy khi Supervisor đang chạy cần Stop rồi Start.

### Bảng worker

- Tab **NVHN** / **Tà Thú**, lọc theo trạng thái (Đang chạy, Dừng, Tạm dừng, Xong,
  **Có vấn đề** = lỗi/PID cũ/log im lặng > 5 phút), tìm theo worker hoặc nhân vật.
- Tick nhiều worker để **Start / Restart / Stop hàng loạt**.
- Cột Worker hiện **TK i/n · username**: worker đang chạy tài khoản thứ i trên n tài
  khoản của nó (lấy từ dòng "đăng nhập tài khoản/account" trong log).
- NVHN: tiến độ `NV x/20`, mục tiêu, map. Tà Thú: thanh máu boss, map/khu (🔒 đã
  khoá, 🔎 đang tìm), HP/MP, kết quả ✔ kết liễu / ✖ bị cướp / ☠ chết (tính trong
  phiên chạy hiện tại).
- **Sự kiện cuối** là dòng log có ý nghĩa gần nhất (bỏ qua dòng kỹ thuật), có màu
  theo mức độ và thời gian "x giây trước". Bấm vào để mở log.

`Stop` tại một worker tạo marker `.paused`, dừng Java và giữ supervisor chạy cho
các worker còn lại; `Start` xoá marker và chạy lại. Áp dụng cho cả NVHN và Tà Thú.
Build lại danh sách worker sẽ xoá trạng thái pause cũ.

### Khung log

- Chọn worker, runtime (NVHN/Tà Thú) và loại log: **Log** (`stdout.log`), **Lỗi Java**
  (`java-errors.log`), **Supervisor**.
- Mỗi dòng có giờ, tag, nội dung, màu theo mức `debug/info/ok/warn/error`. Mặc định
  ẩn `debug` (menu server, trace đăng nhập, FIGHT/FILTER mỗi 10s, sát thương nhận…).
  Dòng trùng liên tiếp được gộp thành `×N`; mỗi phiên chạy có dải phân cách `START`.
- Tìm kiếm có tô sáng, tắt/bật xuống dòng, tự cuộn (kéo lên sẽ dừng và hiện nút
  "↓ N dòng mới"), Copy phần đang hiển thị, Tải file gốc.
- Log Tà Thú có **thẻ trận đấu**: máu boss, tốc độ trừ máu/giây và thời gian còn lại,
  HP/MP, số đòn gửi / server xác nhận, trạng thái skill.
- Tab **Build** hiển thị output build riêng, không cắt luồng log worker.

Giờ trên từng dòng do Java ghi (`-Dnso.log.timestamp=true`, bật mặc định trong
`optimized-runtime/scripts/tuning-options.sh`, tắt bằng `NSO_LOG_TIMESTAMP=false`).
Dòng cũ không có giờ thì hiển thị giờ server nhận được (in nghiêng).

API log: `GET /api/logs?runtime=&worker=&kind=&lines=&level=&q=`,
`GET /api/logs/stream` (SSE theo dòng, id = byte offset để nối lại không mất dòng),
`GET /api/logs/download`.

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
python3 -m unittest -v tests.test_web_control tests.test_logparse
bash -n optimized-runtime/scripts/*.sh
python3 -m compileall -q web_control tests
```
