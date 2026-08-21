# Hướng dẫn chạy NSO Auto NVHN nhiều worker

Bộ script này chia `account.csv` thành nhiều nhóm, tạo một JAR riêng cho từng nhóm và chạy mỗi JAR bằng một MicroEmulator headless độc lập.

## 1. Cấu trúc hoạt động

Ví dụ `account.csv` có 90 tài khoản và build 10 worker:

```text
account.csv (90 tài khoản)
        |
        +-- worker-01/account.csv (9 tài khoản)
        +-- worker-02/account.csv (9 tài khoản)
        ...
        +-- worker-10/account.csv (9 tài khoản)
```

Mỗi thư mục trong `dist/workers` có:

```text
worker-01/
├── account.csv       CSV riêng để kiểm tra
├── client_217.jar    JAR chứa chính CSV riêng này
├── home/             RMS và dữ liệu MicroEmulator riêng
├── stdout.log        Log AUTO NVHN
├── java-errors.log   Lỗi Java/MicroEmulator
└── bot.pid           PID khi worker đang chạy
```

`build-workers.sh` không compile source Java. Nó lấy `dist/client_217.jar` làm JAR nền, copy JAR này cho từng worker rồi thay `account.csv` bên trong mỗi JAR.

## 2. Yêu cầu

- Java/JDK 8 trở lên.
- Apache Ant để compile source.
- Lệnh `jar` và `unzip`.
- MicroEmulator có class `org.microemu.app.Headless`.
- Linux hoặc Windows chạy qua WSL2 được khuyến nghị.

Kiểm tra nhanh:

```bash
java -version
ant -version
jar --version
```

## 3. Chuẩn bị account.csv

File `account.csv` nằm ở thư mục gốc repo:

```csv
username,password
account_01,password_01
account_02,password_02
```

Không thêm dấu cách ở đầu username/password. Dòng đầu phải là header.

## 4. Build source và chia worker

Đứng tại thư mục gốc repo.

Nếu worker đang chạy, dừng trước vì target `all` xóa thư mục `dist`:

```bash
./scripts/stop-workers.sh
```

Compile toàn bộ source và tạo lại `dist/client_217.jar`:

```bash
ant -f build-docker.xml all
```

Chia CSV và tạo 10 JAR riêng:

```bash
./scripts/build-workers.sh 10
```

Có thể đổi số worker, ví dụ 5 worker:

```bash
./scripts/build-workers.sh 5
```

Số tài khoản được chia cân bằng. Nếu không chia hết, các worker đầu sẽ nhận nhiều hơn một tài khoản.

Kiểm tra số tài khoản thật sự được nhúng trong từng JAR:

```bash
for file in dist/workers/worker-*/client_217.jar; do
    printf '%s: ' "$file"
    unzip -p "$file" account.csv | tail -n +2 | sed '/^[[:space:]]*$/d' | wc -l
done
```

Sau mỗi lần sửa source Java, phải chạy lại cả hai bước:

```bash
./scripts/stop-workers.sh
ant -f build-docker.xml all
./scripts/build-workers.sh 10
```

Danh sách item cần xóa khỏi rương được đọc từ `delllllllllll.txt` ở thư mục gốc. Các ID cách nhau bằng dấu `;`. File này được đóng gói vào từng JAR khi build; sau khi sửa danh sách phải build Docker và build worker lại.

Nếu chỉ thay đổi danh sách tài khoản, không cần compile Java:

```bash
./scripts/stop-workers.sh
./scripts/build-workers.sh 10
```

## 5. Cấu hình đường dẫn MicroEmulator

Mặc định script sử dụng:

```text
/home/namtp/Downloads/game-teamobi/Microemulator.jar
```

Có thể truyền đường dẫn khác khi chạy:

```bash
MICROEMULATOR_JAR=/duong-dan/Microemulator.jar ./scripts/start-workers.sh
```

Hoặc export một lần cho terminal hiện tại:

```bash
export MICROEMULATOR_JAR=/duong-dan/Microemulator.jar
```

RMS mẫu mặc định được lấy từ:

```text
/home/namtp/.microemulator/suite-NSO_217
```

Đổi RMS mẫu nếu cần:

```bash
RMS_TEMPLATE_DIR=/duong-dan/suite-NSO_217 ./scripts/start-workers.sh
```

## 6. Chạy worker

Chạy một lần, worker bị thoát sẽ không tự bật lại:

```bash
./scripts/start-workers.sh
```

Cách khuyến nghị là chạy supervisor. Worker bị thoát sẽ được kiểm tra và bật lại:

```bash
./scripts/supervise-workers.sh
```

Giữ terminal supervisor mở. Nhấn `Ctrl+C` để dừng supervisor và toàn bộ worker.

Mặc định mỗi worker được mở cách nhau 3 giây để tránh tạo quá nhiều kết nối cùng lúc. Thay đổi bằng:

```bash
START_DELAY=5 ./scripts/supervise-workers.sh
```

Supervisor mặc định kiểm tra worker mỗi 20 giây:

```bash
CHECK_INTERVAL=30 ./scripts/supervise-workers.sh
```

Khởi động lại worker mà không dùng supervisor:

```bash
./scripts/restart-workers.sh
```

Dừng supervisor và toàn bộ worker từ terminal khác:

```bash
./scripts/stop-workers.sh
```

## 7. Xem trạng thái và log

Xem worker đang chạy, PID, RAM và log AUTO NVHN gần nhất:

```bash
./scripts/status-workers.sh
```

Theo dõi log AUTO NVHN của tất cả worker:

```bash
./scripts/logs-workers.sh
```

Chỉ theo dõi worker số 3:

```bash
./scripts/logs-workers.sh 3
```

Xem toàn bộ log Java của một worker:

```bash
tail -F dist/workers/worker-03/java-errors.log
```

Đổi bộ lọc log:

```bash
LOG_FILTER='AUTO NVHN STATUS' ./scripts/logs-workers.sh
```

## 8. Cấu hình RAM

Cấu hình mặc định trong `start-workers.sh`:

```bash
JAVA_XMS=16m
JAVA_XMX=96m
```

- `JAVA_XMS`: heap khởi tạo của mỗi Java process.
- `JAVA_XMX`: heap tối đa của mỗi Java process.
- Đây không phải tổng RAM của process. MicroEmulator và bộ nhớ native có thể làm RSS thực tế cao hơn đáng kể.

Ví dụ giới hạn heap mỗi worker ở 80 MB:

```bash
JAVA_XMS=16m JAVA_XMX=80m ./scripts/supervise-workers.sh
```

Nên thử từ `96m`, sau đó giảm xuống `80m` hoặc `64m`. Nếu thấy `OutOfMemoryError` trong `java-errors.log`, tăng `JAVA_XMX`.

Để lưu cấu hình dùng thường xuyên, có thể sửa các giá trị mặc định ở đầu `scripts/start-workers.sh` hoặc tạo lệnh riêng:

```bash
export JAVA_XMS=16m
export JAVA_XMX=80m
export START_DELAY=3
./scripts/supervise-workers.sh
```

## 9. Giới hạn CPU và tổng RAM trên Linux

`JAVA_XMX` chỉ giới hạn heap. Muốn giới hạn cứng tổng RAM và CPU của cả nhóm worker, dùng systemd/cgroup:

```bash
JAVA_XMS=16m JAVA_XMX=80m \
systemd-run --user --scope \
    -p CPUQuota=300% \
    -p MemoryMax=2200M \
    ./scripts/supervise-workers.sh
```

- `CPUQuota=100%`: công suất tối đa tương đương 1 CPU core.
- `CPUQuota=300%`: tương đương tối đa 3 CPU core.
- `MemoryMax=2200M`: tổng RAM tối đa của supervisor và tất cả worker là 2,2 GB.

Nếu đặt `MemoryMax` quá thấp, hệ điều hành có thể kill worker. Hãy theo dõi `status-workers.sh` và `java-errors.log`.

`nice` chỉ giảm độ ưu tiên, không phải giới hạn CPU cứng:

```bash
nice -n 10 ./scripts/supervise-workers.sh
```

## 10. Chạy trên Windows

### Cách khuyến nghị: WSL2

Các script hiện tại viết bằng Bash và sử dụng `nohup`, `ps`, `/proc`, vì vậy WSL2 ổn định hơn Git Bash hoặc CMD.

1. Mở PowerShell bằng quyền Administrator và cài WSL:

```powershell
wsl --install
```

2. Khởi động lại máy, mở Ubuntu và cài Java, Ant, unzip:

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk ant unzip
```

3. Đặt repo trong filesystem của WSL, ví dụ:

```text
/home/ten-user/NSO_217
```

Chạy trực tiếp trong `/home/...` thường nhanh và ổn định hơn `/mnt/c/...`.

4. MicroEmulator phải có đường dẫn WSL. Nếu file nằm ở Windows:

```text
C:\Users\Nam\Downloads\Microemulator.jar
```

thì đường dẫn trong WSL là:

```text
/mnt/c/Users/Nam/Downloads/Microemulator.jar
```

5. Build và chạy:

```bash
cd /home/ten-user/NSO_217
chmod +x scripts/*.sh
ant -f build-docker.xml all
./scripts/build-workers.sh 10

MICROEMULATOR_JAR=/mnt/c/Users/Nam/Downloads/Microemulator.jar \
RMS_TEMPLATE_DIR=/home/ten-user/.microemulator/suite-NSO_217 \
./scripts/supervise-workers.sh
```

6. WSL2 có thể giới hạn tổng CPU/RAM bằng file `%UserProfile%\\.wslconfig` trên Windows:

```ini
[wsl2]
memory=4GB
processors=4
swap=1GB
```

Sau khi sửa, chạy trong PowerShell:

```powershell
wsl --shutdown
```

Sau đó mở lại Ubuntu.

### Windows native

Các file `.sh` hiện tại không hỗ trợ trực tiếp CMD/PowerShell vì chúng phụ thuộc công cụ tiến trình của Linux. Git Bash có thể build/chia JAR, nhưng việc giữ PID, supervisor và dừng process có thể không ổn định.

Nếu chạy Windows native, có thể chạy thủ công từng worker bằng PowerShell:

```powershell
java -Xms16m -Xmx96m `
  -Duser.home="D:\NSO_217\dist\workers\worker-01\home" `
  -cp "D:\tools\Microemulator.jar" `
  org.microemu.app.Headless `
  --id nso-worker-01 `
  --rms file `
  "D:\NSO_217\dist\workers\worker-01\client_217.jar"
```

Với nhiều worker, nên dùng WSL2 hoặc cần viết riêng bộ script PowerShell `.ps1` tương đương.

## 11. Lỗi thường gặp

### `Không tìm thấy MicroEmulator`

Đặt đúng biến đường dẫn:

```bash
MICROEMULATOR_JAR=/duong-dan-dung/Microemulator.jar ./scripts/supervise-workers.sh
```

### JAR vẫn chạy code cũ

Bạn mới chạy `build-workers.sh` nhưng chưa compile source. Làm lại:

```bash
./scripts/stop-workers.sh
ant -f build-docker.xml all
./scripts/build-workers.sh 10
./scripts/supervise-workers.sh
```

### Worker đọc nhầm account

Kiểm tra CSV trong chính JAR:

```bash
unzip -p dist/workers/worker-01/client_217.jar account.csv
```

Không chạy đồng thời `dist/client_217.jar` cũ với các worker vì tài khoản có thể bị đăng nhập trùng.

### Worker bị restart liên tục

Kiểm tra:

```bash
tail -n 100 dist/workers/worker-01/java-errors.log
tail -n 100 dist/workers/worker-01/stdout.log
```

Các nguyên nhân thường gặp: sai đường dẫn MicroEmulator, RMS mẫu thiếu, heap quá thấp, mất mạng hoặc server đóng socket.

## 12. Lệnh chạy nhanh hằng ngày

Sau khi đã build và chia JAR:

```bash
cd /home/namtp/Desktop/code/CLIENT_NSO_217/NSO_217
JAVA_XMS=16m JAVA_XMX=96m ./scripts/supervise-workers.sh
```

Ở terminal thứ hai:

```bash
cd /home/namtp/Desktop/code/CLIENT_NSO_217/NSO_217
./scripts/logs-workers.sh
```

Dừng toàn bộ:

```bash
./scripts/stop-workers.sh
```

## 13. Worker hoàn tất toàn bộ account

Khi một worker đã chạy hết toàn bộ nhân vật của toàn bộ account, client tạo marker:

```text
dist/workers/worker-XX/home/worker.done
```

Worker sau đó tự thoát. Supervisor thấy marker này sẽ ghi trạng thái `DONE` và không khởi động worker lại.

Xem trạng thái:

```bash
./scripts/status-workers.sh
```

Muốn chạy lại các worker đã hoàn tất mà không build lại JAR, xóa toàn bộ marker bằng:

```bash
./scripts/reset-completed-workers.sh
./scripts/supervise-workers.sh
```

Chạy `build-workers.sh` cũng tạo lại thư mục worker nên các marker cũ sẽ được xóa.
