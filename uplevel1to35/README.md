# Uplevel1to35 — nhiệm vụ chính tuyến level 1 → 35 liền một mạch

Ghép `uplevel/` (As20, nhiệm vụ 0 → 17, lv 1 → ~25) và `uplevel35/` (As35, nhiệm vụ 18 → 23,
lv 25 → 35) thành một runtime: tài khoản chạy hết As20 rồi tự sang As35, không phải chuyển CSV sang
runtime khác.

**Không chứa logic nhiệm vụ.** Logic vẫn nằm ở `uplevel/` và `uplevel35/`; build đọc trực tiếp hai
folder đó, nên sửa nhiệm vụ ở folder gốc thì build lại `uplevel1to35` là có. Folder này chỉ có:

```
uplevel1to35/
  src/Uplevel1to35.java     chọn giai đoạn theo ctaskId (as20 / as35 / done)
  transforms/apply.py       vá AccountAutoManager, Controller, As35 (tìm needle, sai là dừng build)
  scripts/                  build / build-workers / start / stop / status / logs (.sh + .cmd Windows)
```

## Chạy

```bash
# account-1to35.csv ở repo root, 2 cột: username,password
./uplevel1to35/scripts/build-workers.sh 4               # build + chia 4 worker (round-robin)
./uplevel1to35/scripts/start-workers.sh --mode 1        # chạy tất cả; mode = lớp chọn ở NV9 (1 Kiếm, 2..6)
./uplevel1to35/scripts/start-workers.sh 2 --debug       # chỉ worker-02, bật log DEBUG của As35
./uplevel1to35/scripts/status-workers.sh                # PID, RAM, dòng STATE cuối ([as20] / [as35])
./uplevel1to35/scripts/logs-workers.sh 1 --grep         # chỉ dòng UPLEVEL*/AUTO của worker-01
./uplevel1to35/scripts/logs-workers.sh --errors         # java-errors.log
./uplevel1to35/scripts/stop-workers.sh                  # dừng tất cả
```

Windows: cùng tên file `.cmd`, cùng tham số (vd. `uplevel1to35\scripts\build-workers.cmd 4` rồi
`uplevel1to35\scripts\start-workers.cmd --mode 1`), xem `windows/README.md`.

- Log: `uplevel1to35/workers/worker-XX/stdout.log`, `java-errors.log` (start xóa log cũ;
  `UPLEVEL1TO35_LOG_APPEND=1` để giữ).
- Hết account → `workers/worker-XX/home/worker.done`; start bỏ qua worker này.
- Biến môi trường: `UPLEVEL1TO35_ACCOUNT_CSV` (mặc định `account-1to35.csv`), `UPLEVEL1TO35_SERVER`
  (mặc định `180.93.114.122:14444`), `UPLEVEL1TO35_MODE`, `UPLEVEL1TO35_TARGET`=35,
  `UPLEVEL1TO35_PROCESS_PER_CHARACTER`=true, `UPLEVEL1TO35_PROXY`/`_PROXY_FILE`, `UPLEVEL1TO35_SKIP_BUILD=1`
  (build-workers chỉ chia lại account), `UPLEVEL1TO35_KEEP_STAGING=1` (giữ `build/src` đã ghép để soi),
  `JAVA_XMX`=36m, `JAVA_OPTS`. Biến nhiệm vụ 12 của uplevel (`UPLEVEL_CRYSTAL_ID`, `UPLEVEL_UPGRADE_SAFE`...)
  và `-Dnso.uplevel35.*` của uplevel35 dùng y như ở folder gốc.
- Proxy: giống uplevel (`proxy-tk.txt`, chia đều theo khối worker, probe trước khi start, proxy lỗi →
  không start; `--no-proxy` để chạy IP máy).
- Web monitor: `./uplevel_monitor/run.sh` có thêm nút lọc `uplevel1to35`.

## Luồng mỗi nhân vật

`AccountAutoManager.onGameReady` → `Uplevel1to35.phase(me)`:

| ctaskId | Level | Giai đoạn |
|---|---|---|
| `< 18` | bất kỳ | **as20**: `As20(mode)` như `uplevel/` (quà tân thủ, NV12 đập đồ, NV17 hộ tống Jaian...) |
| `18..23` | `< 35`, hoặc đang NV23 | **as35**: `As35` như `uplevel35/` |
| còn lại | | **done**: bỏ qua nhân vật, log `UPLEVEL1TO35 GAME READY ... phase=done` |

Chuyển giai đoạn: As20 trả xong NV17 (server chuyển `ctaskId` 18) → `onAs20Finished` ghi
`home/uplevel-progress.properties` với **đúng nhân vật hiện tại** rồi thoát mã 3 → `worker-loop.sh` chạy
JVM mới → đăng nhập lại chính nhân vật đó → `phase=as35` → As35. Đổi JVM để static của As20
(giftcode, shop, rương, skill...) không dính sang As35. Log: `UPLEVEL1TO35 PHASE as20 -> as35`,
`UPLEVEL1TO35 RELAUNCH ...`, `===== NEXT PROCESS #n =====`. Nếu tắt
`UPLEVEL1TO35_PROCESS_PER_CHARACTER` thì chuyển As35 ngay trong JVM hiện tại.

As35 xong (đủ lv35 và đã trả NV23, hoặc gặp nhiệm vụ chưa có logic) → nhân vật/tài khoản tiếp theo.
Đăng nhập lại giữa chừng (mất kết nối, As35 kẹt đường) cũng đi qua `phase()` nên luôn vào đúng giai đoạn.

## Build ghép những gì

`scripts/build.sh` ghép trong thư mục tạm, không sửa folder nào khác:

1. `src/` + `optimized-runtime/src` + `optimized-runtime/overrides`
2. `uplevel/scripts/apply-transforms.sh`: `uplevel/overrides/*` + toàn bộ vá As20/As10/Controller/...
   (cũng chính là bước mà `uplevel/scripts/build.sh` dùng)
3. `uplevel35/src/**` và `uplevel1to35/src/**` (class mới; trùng tên file có sẵn → lỗi build)
4. `transforms/apply.py`
5. `javac`, map, `account.csv`

Patch trong `uplevel35/patches/` **không** dùng ở đây: 001/004 nhắm `src/AccountAutoManager.java` nhưng
uplevel thay cả file đó (004 = chế độ JVM riêng, uplevel đã có); 002 (log lời NPC) và nửa sau của 003
(NPE khi thiếu RMS skill) được viết lại trong `transforms/apply.py`; nửa đầu 003 (lưu cả 2 khóa RMS
skill) uplevel đã làm. Sửa patch ở `uplevel35/` mà liên quan tới 4 chỗ đó thì sửa cả `apply.py`.

Hai bản logic buff (Nấm linh chi / Mũ Noel / thức ăn) vẫn để riêng: As20 dùng bản trong `As20`,
As35 dùng `Uplevel35Support`. As35 nhận ra buff còn hiệu lực (nấm 5 giờ, mũ đang đội) nên không mua lại.
