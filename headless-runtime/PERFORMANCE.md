# Tối ưu và đo NVHN

Các launcher NVHN (`scripts/start-workers.sh`, `run-one.sh`) mặc định dùng profile
`optimized`. Chỉ launcher này bật `nso.nvhn.headless`; launcher Tà Thú và client
emulator không tự bật tối ưu. Cần build lại để launcher sử dụng code mới.

## Cấu hình

| Biến môi trường | optimized | baseline | Ý nghĩa |
| --- | --- | --- | --- |
| `HEADLESS_TICK_MS` | 50 | 0 | Chu kỳ cập nhật tính bằng ms; 0 dùng giá trị RMS. Chấp nhận 0..1000. |
| `HEADLESS_SKIP_PERIODIC_GC` | 1 | 0 | Bỏ riêng `System.gc()` mỗi 100 tick tại `GameScr.gameAD()`. |
| `HEADLESS_SKIP_AUTO_POPUP` | 1 | 0 | Bỏ popup từ `GameScr.fieldAC()`, giữ các log auto hiện có. |
| `HEADLESS_SKIP_DECORATIONS` | 1 | 0 | Bỏ tạo/tải ảnh `EffectAuto`, đèn lồng, cập nhật mây và minimap. |
| `HEADLESS_EVENT_SENDER` | 1 | 0 | Hàng đợi chờ tín hiệu thay cho kiểm tra mỗi 10 ms. |
| `HEADLESS_METRICS` | 1 | 1 | Ghi số đo và sự kiện từng nhân vật. |
| `HEADLESS_METRICS_LABEL` | optimized | baseline | Nhãn phiên đo, nên đặt riêng khi thay một công tắc. |

Chọn profile bằng `HEADLESS_PROFILE=baseline` hoặc `optimized`. Mỗi biến bên trên
có thể ghi đè riêng. Các công tắc nhận đúng `0` hoặc `1`; tham số sai bị từ chối
trước khi launcher tạo worker. Không sửa/xóa RMS. Log `HEADLESS CONFIG` sau khi
đọc RMS ghi cả `rms_tick_ms` và `effective_tick_ms`.

`EffectAuto` là lớp ảnh hoạt họa, khác với logic auto nhiệm vụ. Các hiệu ứng có
liên quan trạng thái combat, HP/MP, buff, tọa độ, menu NPC và callback server vẫn
được xử lý. Camera chính vẫn giữ nguyên vì nhiều đoạn client dùng vùng nhìn để
quyết định cập nhật đối tượng. Những lời gọi GC khác, heap và chu kỳ auto 100 ms
không bị đổi trong đợt này.

Hàng đợi mới giữ FIFO, chờ đủ khóa trước khi gửi, gửi ngoài khóa hàng đợi, đánh
thức khi có packet/khóa/đóng kết nối. Mỗi phiên có một generation; packet chờ của
phiên đóng bị loại và sender cũ không được ghi vào socket của phiên mới.

## Build và áp dụng

Dừng supervisor NVHN trước bằng Ctrl+C tại terminal đang chạy supervisor hoặc
Stop tất cả trong dashboard NVHN. Sau đó dừng worker NVHN và build classes:

```bash
headless-runtime/scripts/stop-workers.sh
headless-runtime/build-headless.sh
```

Không cần chạy `build-workers.sh`: lệnh đó chia lại tài khoản và thay thư mục
worker, không thích hợp để giữ tiến độ/RMS khi chỉ cập nhật code. Bản build mới
không tác động đến JVM đã khởi động; phải start lại worker sau build.

Chạy một worker chưa hoàn tất để lấy mốc so sánh, giữ nguyên heap:

```bash
HEADLESS_PROFILE=baseline \
HEADLESS_METRICS_LABEL=baseline-30ms \
JAVA_XMS=8m JAVA_XMX=48m \
headless-runtime/scripts/start-workers.sh 1
```

Profile baseline dùng RMS nên nhãn `baseline-30ms` chỉ đúng nếu log xác nhận 30 ms.
Lượt thử riêng việc cố định tick:

```bash
HEADLESS_PROFILE=baseline HEADLESS_TICK_MS=50 \
HEADLESS_METRICS_LABEL=tick50 \
headless-runtime/scripts/start-workers.sh 2
```

Lượt bật toàn bộ tối ưu:

```bash
HEADLESS_PROFILE=optimized HEADLESS_METRICS_LABEL=optimized-v1 \
headless-runtime/scripts/start-workers.sh 3
```

Các số worker chỉ là ví dụ. Launcher giữ nguyên quy tắc bỏ qua worker đang chạy,
`.paused` hoặc đã có `home/worker.done`; không xóa marker để ép chạy lại một lượt
đã làm xong. Cấu hình truyền vào `supervise-workers.sh` được kế thừa khi supervisor
khởi động lại worker. Cần giữ cùng cấu hình trong một nhóm đo.

Ví dụ chỉ thử GC sau khi đã cố định tick:

```bash
HEADLESS_PROFILE=baseline HEADLESS_TICK_MS=50 HEADLESS_SKIP_PERIODIC_GC=1 \
HEADLESS_METRICS_LABEL=tick50-gc \
headless-runtime/scripts/start-workers.sh 4
```

Để trở lại hành vi trước tối ưu, khởi động lại với `HEADLESS_PROFILE=baseline`.
Thêm `HEADLESS_METRICS=0` nếu muốn tắt cả bộ đo. Không chạy hai phiên cùng tài khoản.

## Dữ liệu đo

Mỗi worker ghi dưới `home/metrics/`:

- `metrics.csv`: mẫu 5 giây và tại đầu/cuối lượt, gồm RSS, VmHWM, heap, CPU tích lũy,
  CPU theo khoảng mẫu, số lần/thời gian GC, số hiệu ứng và thống kê hàng đợi.
- `character-events.csv`: chọn nhân vật, vào map, chuẩn bị xong/bắt đầu NVHN,
  xác nhận trả nhiệm vụ, reconnect, hết NVHN, lật thẻ, xử lý hang và kết thúc.
- `character-results.csv`: một dòng cho mỗi lượt kết thúc, gồm thời gian từng
  công đoạn, giây CPU, RSS trung bình/đỉnh lấy mẫu, GC, reconnect, lỗi quan sát được.

Bộ đo dùng một daemon nhẹ trong JVM, không thêm process Python cho từng worker.
CPU lấy từ process MXBean; thời lượng dùng đồng hồ monotonic. 100% CPU tương ứng
một lõi; có thể vượt 100%. Tất cả file được append, có `run_id`, `attempt_id`,
nhãn cấu hình và pass 1/2 để phân biệt các lần khởi động.

Reconnect cùng tài khoản/nhân vật giữ nguyên `attempt_id`, tính cả thời gian chờ
và cộng dồn công đoạn. Các trạng thái `completed`, `cave_skipped`, `skipped`,
`account_abandoned`, `interrupted` được tách riêng. Hết lượt vào hang và quá số
lần thử không bị ghi là đã vào hang thành công. Hoàn tất xử lý lật thẻ **không**
được coi là server xác nhận đã nhận đủ thưởng.

`confirmed_task_returns` chỉ tăng khi nhận packet xóa task 0 đã đủ mục tiêu sau
khi auto yêu cầu trả nhiệm vụ; packet trùng không tăng thêm. Đây là số lần trả
được xác nhận trong lượt quan sát, không suy ra từ số lần gửi packet hoặc tự gán
20 nhiệm vụ. Nếu server có luồng phản hồi khác, chỉ số này có thể đếm thiếu; xem
tiến độ/log server trước khi kết luận. Giá trị 0 không đủ chứng minh không làm NV.

`error_count` đếm exception ở vòng cập nhật, vòng auto, handler packet và bước
lật thẻ; không đại diện mọi lỗi server hoặc exception bị bắt trong các hàm khác.
Chi tiết lỗi trong events giới hạn một dòng/giây, bộ đếm vẫn tăng đủ ở các hook.

RSS là RAM cả worker trong khoảng xử lý nhân vật, gồm dữ liệu/cache giữ từ nhân
vật trước. `rss_sample_peak_kb` có thể bỏ lỡ đỉnh giữa hai mẫu; `process_hwm_kb`
là đỉnh toàn process, không phải đỉnh riêng nhân vật. Mean RSS là trung bình các
mẫu, không có trọng số theo thời gian. GC và CPU của mọi thread JVM cùng khoảng
đều được tính. Số lần chờ/sent/drop và độ trễ lớn nhất của hàng đợi là tích lũy
toàn process; độ trễ đo từ enqueue đến khi gửi xong. Baseline không có các bộ
đếm sender này, ghi `-1`; độ dài hàng đợi vẫn được đo.

Shutdown thông thường ghi lượt đang làm là `interrupted`. Kill -9/mất điện không
thể ghi dòng cuối: báo cáo liệt kê lượt có sự kiện bắt đầu nhưng chưa có kết quả
trong `unfinished_attempts` (có thể còn đang chạy, không tự kết luận là lỗi).
Login trước khi chọn được nhân vật chưa được phân bổ vào một lượt nhân vật.

Đọc tổng hợp bằng một công cụ dùng chung cho toàn bộ worker:

```bash
python3 headless-runtime/scripts/report_metrics.py
python3 headless-runtime/scripts/report_metrics.py --json
```

Hoặc chỉ định thư mục chứa dữ liệu đã lưu:

```bash
python3 headless-runtime/scripts/report_metrics.py /path/to/measurement --json
```

Báo cáo nhóm theo nhãn, pass, class, level, trạng thái, có làm NVHN hay không và
số lần trả được xác nhận. JSON có thời gian từng công đoạn và CPU toàn lượt trên
mỗi lần trả được xác nhận. Không gộp lượt quét lại đã hết nhiệm vụ vào lượt làm NV.

So sánh cùng số worker, heap, máy và nhóm class/level tương đồng; đổi chéo nhóm
qua nhiều ngày vì một nhân vật đã làm xong NVHN không thể làm lại tương đương
ngay trong ngày. Ghi cả mức tải server và số nhiệm vụ còn lại. Chỉ giữ thay đổi
khi giảm tài nguyên cho lượng công việc tương đương mà thời gian và tỷ lệ lỗi
không xấu đi. Các số đo test offline không phải benchmark game thực tế.

## Kiểm tra offline

```bash
bash tests/test-headless-runtime.sh
python3 -m unittest discover -s tests
```

Test Java build vào thư mục tạm, dùng tài khoản rỗng, không mở socket game. Nó
kiểm tra chờ khóa, giải mã khóa, framing/mã hóa/FIFO, close/reconnect, loại phiên
cũ, observer reconnect, CSV, pass 2, shutdown và profile baseline. Test Python
kiểm tra phân nhóm kết quả và lượt chưa có kết quả.
