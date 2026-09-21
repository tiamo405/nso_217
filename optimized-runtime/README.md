# NSO Ultra-Optimized Standalone Runtime

Standalone runtime siêu tối ưu hóa tài nguyên (RAM & CPU) cho NSO Client 217.

## Điểm cải tiến so với bản gốc và headless-runtime

1. **Triệt tiêu toàn bộ lệnh vẽ màn hình (`SKIP_PAINT = true`)**:
   - `MotherCanvas` và `GameCanvas` không gọi `repaint()` và `serviceRepaints()`.
   - Giảm 100% chi phí phân bổ đồ họa và overhead của pipeline vẽ.
2. **Loại bỏ vòng lặp tính toán hoạt ảnh (`SKIP_DECORATIONS = true`)**:
   - Bỏ qua cập nhật animation quái (`Mob.gameAA()`), đèn lồng (`Lanterns`), rung màn hình (`shaking`), camera easing, hiệu ứng sấm sét (`gameBP`), trứng rơi (`EggMonters`).
   - Vẫn giữ nguyên 100% logic vật lý di chuyển của nhân vật (`Char.pxw()`) và nhặt đồ (`vItemMap`).
3. **Lazy-loading 160 Maps (`LAZY_MAP = true`)**:
   - Thay vì nạp sẵn 160 file nhị phân vào RAM lúc khởi động, chỉ khi nhân vật chuyển sang `mapID` mới thì file map đó mới được tải vào RAM. Giúp khởi động tức thì và giảm heap.
4. **Điều tốc vòng lặp an toàn (`TICK_MS = 80ms` ~ 12.5 FPS)**:
   - Khớp hoàn hảo với chu kỳ 100ms của luồng tự đánh quái/boss (`Code.run()`).
   - Không làm chậm việc nhận quái từ mạng TCP hay hồi chiêu skill (vì đều tính theo `System.currentTimeMillis()`).
5. **Image Flyweight Pattern**:
   - Dùng singleton dummy image cho các hàm `Image.createImage()` để triệt tiêu việc cấp phát rác bộ nhớ cho Garbage Collector (GC).

---

## Hướng dẫn sử dụng

### 1. Biên dịch Standalone Runtime

```bash
chmod +x optimized-runtime/build-optimized.sh optimized-runtime/scripts/*.sh
./optimized-runtime/build-optimized.sh
```

### 2. Quản lý Workers

```bash
# Chia tài khoản thành 10 workers và tạo thư mục worker-XX
./optimized-runtime/scripts/build-workers.sh 10

# Build chỉ chia account/compile class, không kết nối server. Chọn server lúc chạy:
# Chạy tất cả workers bằng TK (mỗi worker cách nhau 3 giây)
./optimized-runtime/scripts/start-workers.sh --server tk

# Hoặc chạy bằng NinjaMobile
./optimized-runtime/scripts/start-workers.sh --server ninjamobile

# Hoặc NinjaMobile SV4 (nsm4.ninjasm.net)
./optimized-runtime/scripts/start-workers.sh --server ninjamobileSV4

# Xem trạng thái CPU, RAM và tiến độ
./optimized-runtime/scripts/status-workers.sh

# Xem live log của các workers
./optimized-runtime/scripts/logs-workers.sh
./optimized-runtime/scripts/logs-workers.sh 1   # Chỉ xem worker 1

# Chạy supervisor bằng server đã chọn; tự restart nếu worker crash, stdout.log im lặng,
# hoặc trạng thái tiến độ AUTO NVHN bị lặp 5 lần liên tiếp
./optimized-runtime/scripts/supervise-workers.sh --server tk

# Dừng toàn bộ workers
./optimized-runtime/scripts/stop-workers.sh
```

### CSV lỗi NVHN theo worker

Khi NPC25 trả về lỗi không có nhiệm vụ phù hợp với cấp độ/tiến trình, worker
ghi một dòng vào file CSV riêng:

```text
optimized-runtime/run/nvhn-errors/worker-01.csv
optimized-runtime/run/nvhn-errors/worker-02.csv
```

Các file này được tách theo worker để nhiều JVM không ghi chung một file. Thư
mục nằm ngoài `workers/` nên không bị xóa khi chia/build lại worker. CSV gồm
tài khoản, nhân vật, worker, server, level, map, lượt chạy và nội dung lỗi;
không ghi password. Khi cần tổng hợp, import toàn bộ các file có mẫu
`worker-*.csv` trong thư mục này.

---

## Cấu hình Biến môi trường (`optimized-runtime/scripts/tuning-options.sh`)

Supervisor kiểm tra `stdout.log` mỗi 20 giây. Nếu worker còn process nhưng log
không thay đổi ít nhất 300 giây, worker sẽ được dừng và khởi động lại. Có thể
đổi ngưỡng bằng `STALE_LOG_SECONDS`; đặt `0` để tắt watchdog log. Ngoài ra,
nếu cùng một đoạn tiến độ `AUTO NVHN STATUS` (`nvhn=x/20` và `progress=x/y`)
hoặc cùng một sự kiện `AUTO NVHN` lặp liên tiếp 5 lần trong đoạn log gần nhất,
worker cũng được khởi động lại. Riêng thông báo NPC25 “Hãy nhận nhiệm vụ mỗi
ngày...” được đếm trong 20 dòng gần nhất nên vẫn phát hiện được dù xen giữa có
status/log khác. Đổi ngưỡng bằng `REPEATED_STATUS_LIMIT`; đặt `0` để tắt kiểm
tra lặp.

| Biến | Mặc định | Ý nghĩa |
| :--- | :--- | :--- |
| `JAVA_XMS` | `8m` | Heap khởi điểm cho mỗi worker JVM |
| `JAVA_XMX` | `36m` | Heap tối đa cho mỗi worker JVM |
| `NSO_TICK_MS` | `80` | Chu kỳ tick (ms) của MotherCanvas (80ms = 12.5 FPS) |
| `STALE_LOG_SECONDS` | `300` | Restart nếu `stdout.log` không đổi; `0` để tắt |
| `REPEATED_STATUS_LIMIT` | `5` | Restart nếu trạng thái/sự kiện `AUTO NVHN` lặp; `0` để tắt |
| `PERIODIC_RESTART_SECONDS` | `10800` | Restart từng worker sau 3 giờ từ lần start gần nhất; `0` để tắt |
| `START_DELAY` / `--delay` | `15` khi chạy script trực tiếp | Giãn cách giữa các worker khi Supervisor khởi động; Web Dashboard mặc định `30` giây |
| `NSO_SKIP_PAINT`| `true` | Tắt hoàn toàn repaint & vẽ giao diện |
| `NSO_SKIP_DECORATIONS` | `true` | Bỏ qua các hoạt ảnh đồ họa |
| `NSO_LAZY_MAP` | `true` | Chỉ nạp map khi nhân vật bước vào map |
