# Hướng dẫn chạy NSO Optimized Runtime trên Windows Server 2012 R2

Hệ thống đã được tích hợp bộ công cụ chạy đa nền tảng (hỗ trợ Windows 10/11 & Windows Server 2012 R2) bằng Python 3.8+ và Java 11.

---

## 1. Yêu cầu môi trường trên VPS Windows Server 2012 R2

Bạn đã cài đặt sẵn:
- **Java**: 11.0.22 (đã có `java` và `javac`)
- **Python**: 3.8+ (đã tích hợp vào `PATH`)

> Cài dependency cho Web Dashboard bằng lệnh:
> ```cmd
> pip install -r web_control\requirements.txt
> ```
> Trong đó có `psutil`, thư viện dùng để nhận diện chính xác Supervisor/worker và đo RAM/CPU.

---

## 2. Các file Batch tiện ích (Thư mục `optimized-runtime/windows/`)

Bạn có thể chạy trực tiếp bằng cách **nhấp đúp chuột** vào các file `.bat`:

| File Batch | Chức năng |
| :--- | :--- |
| `run-web.bat` | **Khởi động Web Dashboard** (`web_control_win/`: cùng giao diện, API, lịch và khung log với bản Ubuntu, chỉ phần điều khiển tiến trình dùng `win_manager.py` / `ta_thu_manager.py`) |
| `build.bat` | Biên dịch mã nguồn Java thành các class tối ưu (`OptimizedMain`) |
| `build-workers.bat` | Nhập số worker muốn chia từ `account.csv` và tự động tạo thư mục `worker-XX` |
| `supervise.bat` | Chạy Supervisor tự động giám sát, tự restart nếu crash hoặc tới chu kỳ |
| `status.bat` | Xem bảng trạng thái các Worker (PID, RAM, CPU, Tiến độ, Nhân vật...) |
| `logs.bat` | Xem log theo thời gian thực (nhập số worker hoặc Enter để xem toàn bộ) |
| `stop.bat` | Dừng toàn bộ các Worker và Supervisor |
| `reset.bat` | Xóa các marker hoàn tất để có thể chạy lại từ đầu |
| `run.bat` | CLI tổng hợp (chạy qua lệnh cmd: `run.bat status`, `run.bat start 1 2`...) |

---

## 3. Hoặc chạy trực tiếp qua lệnh CMD / PowerShell

Mở CMD/PowerShell tại thư mục gốc của project:

```cmd
:: 1. Biên dịch source code
python optimized-runtime\windows\win_manager.py build

:: 2. Chia account thành 10 workers (mặc định sẽ tự build luôn)
python optimized-runtime\windows\win_manager.py build-workers 10

:: 3. Chạy Supervisor (restart nếu stdout.log im lặng 300 giây
::    hoặc trạng thái tiến độ AUTO NVHN lặp 5 lần liên tiếp)
python optimized-runtime\windows\win_manager.py supervise --delay 30

:: 4. Xem bảng trạng thái các worker
python optimized-runtime\windows\win_manager.py status

:: 5. Khởi động các worker cụ thể
python optimized-runtime\windows\win_manager.py start 1 2 3

:: 6. Dừng tất cả worker
python optimized-runtime\windows\win_manager.py stop

:: 7. Xem log realtime của worker 1
python optimized-runtime\windows\win_manager.py logs 1
```

---

## 4. Đặc điểm nổi bật trên Windows

Supervisor kiểm tra `stdout.log` mỗi 20 giây. Nếu worker còn process nhưng log
không thay đổi ít nhất 300 giây, worker sẽ được dừng và khởi động lại. Có thể
đổi ngưỡng bằng biến môi trường `STALE_LOG_SECONDS`; đặt `0` để tắt watchdog log.
Nếu cùng một đoạn tiến độ `AUTO NVHN STATUS` (`nvhn=x/20` và `progress=x/y`)
hoặc cùng một sự kiện `AUTO NVHN` lặp liên tiếp 5 lần trong đoạn log gần nhất,
worker cũng được khởi động lại. Riêng thông báo NPC25 “Hãy nhận nhiệm vụ mỗi
ngày...” được đếm trong 20 dòng gần nhất nên vẫn phát hiện được dù xen giữa có
status/log khác. Đổi ngưỡng bằng `REPEATED_STATUS_LIMIT`; đặt `0` để tắt kiểm
tra lặp.

Web Dashboard Windows dùng chung giao diện với Ubuntu (xem `web_control/README.md`):
thẻ NVHN / Tà Thú / Hệ thống, panel **Cấu hình**, bảng worker (TK i/n, thanh máu
boss Tà Thú, thao tác hàng loạt) và khung log có giờ, lọc mức độ, tìm kiếm.

- **Không còn "Restart worker định kỳ"** (mặc định 0). Việc chạy lại từ đầu do lịch
  **Lần đầu lúc / Lặp lại mỗi N giờ** đảm nhiệm: dừng Tà Thú, build lại, chạy NVHN.
  Chạy tay vẫn có thể đặt `PERIODIC_RESTART_SECONDS` nếu cần.
- **Dùng proxy** (một ô tick cho cả NVHN và Tà Thú): chia đều worker cho các proxy
  trong `proxy-tk.txt` theo khối liên tiếp giống uplevel (VD 10 worker / 4 proxy =
  3+3+2+2). Proxy lỗi bị bỏ qua; file rỗng thì chạy mạng của máy. Chạy tay:
  `set NSO_PROXY_WORKERS_PER_PROXY=0` để tắt, giá trị > 0 để bật.
- Mỗi dòng log có giờ `HH:mm:ss` (`-Dnso.log.timestamp=true`, tắt bằng
  `set NSO_LOG_TIMESTAMP=false`); watchdog lặp trạng thái bỏ qua prefix giờ.

Dashboard Windows cũng hỗ trợ chu kỳ tự động NVHN -> Tà Thú. Khi bật **Auto
Tà Thú khi NVHN xong**, đến giờ định kỳ hệ thống sẽ dừng cả hai runtime,
build và chạy lại NVHN. Khi toàn bộ worker NVHN có `worker.done` và
Supervisor NVHN đã kết thúc, hệ thống dùng bộ điều khiển Python native của
`ta-thu-runtime/windows/` để build và chạy Tà Thú. Bộ điều khiển này không phụ
thuộc Git Bash/WSL; nó kiểm tra đúng Java process theo `-Dnso.runtime=ta-thu`
trước khi stop/restart.

Tà Thú dùng một lượt (`1/1`), marker cuối là
`ta-thu-runtime/workers/worker-XX/worker.done`. Watchdog vẫn restart worker Tà
Thú nếu log im lặng 300 giây hoặc trạng thái Tà Thú lặp liên tiếp. Đến lần lịch
tiếp theo, Tà Thú bị dừng và chu kỳ NVHN mới bắt đầu.

### CSV lỗi NVHN

Nếu NPC25 báo nhân vật chưa mở khóa khu vực phù hợp, lỗi được ghi riêng theo
worker tại:

```text
optimized-runtime\run\nvhn-errors\worker-01.csv
optimized-runtime\run\nvhn-errors\worker-02.csv
```

Mỗi worker có một file riêng để tránh nhiều JVM ghi đồng thời vào cùng một CSV.
Thư mục này nằm ngoài `workers` nên vẫn giữ dữ liệu sau khi Build Workers lại.
Có thể import tất cả file `worker-*.csv`; dữ liệu không chứa password.

1. **Chạy hoàn toàn ẩn (Headless No-Window)**:
   - Các worker Java chạy ngầm với cờ `DETACHED_PROCESS` & `CREATE_NO_WINDOW`, không làm lag màn hình hoặc bật lên hàng chục cửa sổ đen.
2. **Tiết kiệm RAM tối đa**:
   - Sử dụng `-XX:+UseSerialGC`, `-Xss256k` (giảm stack size) và `-XX:CICompilerCount=2`.
   - Mỗi worker chỉ tiêu tốn khoảng **18MB - 28MB RAM** trên JVM 11.
3. Worker chỉ chạy một lượt; sau khi có `worker.done`, Supervisor kết thúc. Chu kỳ
   Build & Run định kỳ sẽ tạo lại worker và chạy lại toàn bộ NVHN.
