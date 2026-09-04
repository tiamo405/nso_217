# NSO Tà Thú Runtime

Runtime headless riêng cho luồng Tà Thú. Thư mục này không sửa source đang chạy
của `headless-runtime`: lúc build, nó copy `src/` vào thư mục tạm, chèn các hook
Tà Thú vào bản copy rồi compile cùng compatibility layer J2ME của
`headless-runtime/src`.

## Luồng mỗi nhân vật

1. Đăng nhập và bỏ qua nhân vật dưới level 30.
2. Bật tự cộng tiềm năng/kỹ năng, chọn skill chiến đấu.
3. Kiểm tra thức ăn và Mũ Noel trong túi/rương trước khi mua.
4. Đọc `countLoopBoos`, `TaskOrder(1)`, túi và rương.
5. Tìm item `268` trong shop Goosho (NPC 30, `typeUI=14`) theo ID, không ghi
   cứng vị trí shop.
6. Lấy lệnh có sẵn trong rương; chỉ mua phần thiếu; dùng tối đa hai lệnh/ngày.
7. Nhận, đánh và trả nhiệm vụ tại NPC 25 cho tới khi server hết lượt hoặc đủ
   bốn nhiệm vụ.
8. Lật hình bằng Phiếu may mắn và đi hang động.
9. Chuyển nhân vật, sau đó chuyển tài khoản.

Tiến độ được lưu theo ngày GMT+7, account và nhân vật trong
`$TA_THU_HOME/ta-thu-state/`. Khi tìm thấy Tà Thú, runtime khóa
`mapId + zoneId + killId`; chết, mất kết nối hoặc restart đều quay lại đúng khu.
Các mốc `finished`, `flipDone` và `caveDone` cũng được lưu để restart không mua
phiếu, lật hình hoặc vào hang lặp lại.

## Build và chạy một tiến trình

```bash
./ta-thu-runtime/build-ta-thu.sh
TA_THU_HOME=ta-thu-runtime/run/test-01 ./ta-thu-runtime/run-one.sh
```

`account.csv` mặc định lấy từ repo root. Có thể dùng file khác:

```bash
TA_THU_ACCOUNT_CSV=account-test.csv ./ta-thu-runtime/build-ta-thu.sh
```

## Stage kiểm thử

```bash
TA_THU_STAGE=observe ./ta-thu-runtime/run-one.sh
TA_THU_STAGE=shop ./ta-thu-runtime/run-one.sh
TA_THU_STAGE=orders ./ta-thu-runtime/run-one.sh
TA_THU_STAGE=receive ./ta-thu-runtime/run-one.sh
TA_THU_STAGE=fight ./ta-thu-runtime/run-one.sh
TA_THU_STAGE=full ./ta-thu-runtime/run-one.sh
```

- `observe`: chỉ đọc lượt Tà Thú, task, túi và rương.
- `shop`: mở shop Goosho và xác minh item 268, không mua.
- `orders`: có giao dịch thật; chuẩn bị nhân vật, mua thiếu và dùng tối đa hai
  Tà Thú Lệnh, sau đó dừng.
- `receive`: nhận một `TaskOrder(1)` rồi dừng, chưa đánh.
- `fight`: tiếp tục task đang có, đánh và trả đúng một nhiệm vụ rồi dừng.
- `full`: chạy toàn bộ luồng production.

Các stage khác `full` chỉ xử lý một nhân vật rồi thoát. Không dùng `orders`,
`receive` hoặc `fight` nếu không muốn thay đổi trạng thái nhân vật trên server.

## Nhiều worker

```bash
./ta-thu-runtime/scripts/build-workers.sh 10
./ta-thu-runtime/scripts/start-workers.sh
./ta-thu-runtime/scripts/status-workers.sh
./ta-thu-runtime/scripts/logs-workers.sh
./ta-thu-runtime/scripts/stop-workers.sh
```

Chạy supervisor:

```bash
./ta-thu-runtime/scripts/supervise-workers.sh
```

Mỗi worker chỉ chạy một lượt. File `worker.done` là kết quả cuối cùng; runtime
không chạy lượt audit thứ hai để tránh lật hình hoặc vào hang lặp lại.

Dashboard hiện có trong `web_control` cũng có thể điều khiển runtime này bằng
cách trỏ cấu hình sang thư mục riêng:

```bash
NSO_HEADLESS_DIR="$PWD/ta-thu-runtime" \
NSO_WEB_RUNTIME_DIR="$PWD/ta-thu-runtime/run/web-control" \
./headless-runtime/scripts/run-web-control.sh
```

Các script chấp nhận cả `TA_THU_WORKERS_DIR` lẫn `HEADLESS_WORKERS_DIR`, nên
chức năng build/start/stop/status trên dashboard dùng đúng worker Tà Thú.

## Xác minh cô lập

```bash
git diff --exit-code -- \
  src/AccountAutoManager.java src/Auto.java src/AutoPrepareNvhn.java \
  src/Controller.java src/GameMidlet.java src/TaThu.java
```

Lệnh trên phải không có output. Các file tùy biến chiến đấu nằm trong
`ta-thu-runtime/overrides`, còn manager/state machine nằm trong
`ta-thu-runtime/src`.
