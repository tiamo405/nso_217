# Uplevel35 — nhiệm vụ chính tuyến level 25 → 35

Runtime riêng, tách khỏi `uplevel/`. Nền là `optimized-runtime` (chỉ đọc).
**Không sửa `src/`, `uplevel/`, `optimized-runtime/`**: build ghép mọi thứ trong
thư mục tạm.

```
uplevel35/
  patches/          diff -p1 tính trên src/, áp theo thứ tự tên
    001-accountmanager-uplevel35.patch  onGameReady -> As35, bỏ qua lv ngoài 25..34, header "account,password"
    002-controller-npc-message.patch    log lời NPC (Uplevel35.onNpcMessage)
    003-controller-skill-rms.patch      sửa NPE "buf is null" khi home mới chưa có RMS skill
  src/              class MỚI (không được trùng tên file trong src/)
    Uplevel35.java          cấu hình (-D...) + log
    As35.java               vòng chính: buff -> nhận/làm/trả nhiệm vụ
    Uplevel35Support.java   Nấm linh chi, Mũ Noel, thức ăn
    tasks/Uplevel35Task.java   lớp cơ sở cho 1 nhiệm vụ
    tasks/TaskBaRei26.java     ctaskId 18
    tasks/TaskBaRei27.java     ctaskId 19 (múc nước)
    tasks/TaskBaRei29.java     ctaskId 20 (Fujiwara -> hang heo rừng)
  scripts/          build / worker / log / kiểm tra patch
```

## Chạy

```bash
# account-35.csv ở repo root, 2 cột: account,password
./uplevel35/scripts/build-workers.sh 4          # build + chia 4 worker
./uplevel35/scripts/start-workers.sh            # chạy tất cả (cách nhau 3s)
./uplevel35/scripts/start-workers.sh 2 --debug  # chỉ worker-02, bật log debug
./uplevel35/scripts/status-workers.sh           # PID, RAM, dòng STATE cuối
./uplevel35/scripts/logs-workers.sh 1 --grep    # chỉ dòng UPLEVEL35 của worker-01
./uplevel35/scripts/logs-workers.sh --errors    # java-errors.log
./uplevel35/scripts/stop-workers.sh             # dừng tất cả
```

- Log: `uplevel35/workers/worker-XX/stdout.log`, `java-errors.log` (start sẽ xóa log cũ;
  `UPLEVEL35_LOG_APPEND=1` để giữ).
- Hết account -> tạo `workers/worker-XX/home/worker.done`; start bỏ qua worker này.
- Build lại khi worker đang chạy sẽ bị từ chối ở `build-workers.sh`; `build.sh` đơn lẻ
  thì vẫn chạy được nhưng nên stop trước (JVM đang chạy nạp class lười từ `build/`).
- Biến môi trường: `UPLEVEL35_SERVER` (mặc định `180.93.114.122:14444`),
  `UPLEVEL35_MIN`=25, `UPLEVEL35_TARGET`=35, `JAVA_XMX`=36m,
  `JAVA_OPTS="-Dnso.uplevel35.bottle.id=... -Dnso.uplevel35.bottle.wait=6000"`.
- Test đường đi: `JAVA_OPTS="-Dnso.uplevel35.testmap=63" ./uplevel35/scripts/start-workers.sh 1`
  -> chỉ đi tới map 63 rồi dừng; log `ROUTE tới map ...` hoặc `ROUTE lỗi ... fieldAK=...`.

## Luồng mỗi nhân vật

1. `AccountAutoManager.onGameReady` (patch 001): level ngoài `[25, 35)` -> bỏ qua.
2. `As35`: bật tự cộng tiềm năng + kỹ năng, chọn skill tấn công cao nhất.
3. Mỗi tick giữ buff theo thứ tự: Nấm linh chi (Goosho, map 72, 10 lượng) ->
   Mũ Noel (túi -> rương -> shop thời trang) -> thức ăn theo level (NPC 4 ở trường).
4. Nhiệm vụ (`ctaskId`):
   - chưa nhận -> đi tới NPC giao (bảng `GameScr.mapTasks/tasks` do server gửi) và nhận;
   - bước cuối (`index == subNames.length-1`) -> về NPC trả;
   - bước giữa -> `tasks/TaskXxx.tick()`.
   - `ctaskId` chưa có class -> log đầy đủ tên/bước nhiệm vụ rồi chuyển nhân vật.
5. Đạt level 35 -> chuyển nhân vật / tài khoản.

## Nhiệm vụ đã có

| ctaskId | class | Bước |
|---|---|---|
| 18 | `TaskBaRei26` | 0: đạt lv26 (đánh map 50) · 1: 150 Hầu tửu (mob 26, map 50) · 2: 150 Xác châu chấu (mob 27, map 11) · 3: trả Bà Rei (npc 18, map 32) |
| 19 | `TaskBaRei27` | 0: đạt lv27 (map 50) · 1: 50 lần Lấy nước — map 63 tại (1885,336) dùng Bình rỗng (id 219) · 2: trả Bà Rei (npc 18, map 32) |
| 20 | `TaskBaRei29` | 0: đạt lv29 (map 49) · 1: map 8 nói chuyện Fujiwara (npc 15) -> map ẩn 74 Hang Inoshishi, đánh Thủ lĩnh heo rừng (#69, thủ lĩnh) và nhặt Rìu Bạc (id 221) · 2: trả Bà Rei |
| 22 | `TaskMatsurugi32` | 0: đạt lv32 (map 35) · 1: map 35 đánh Xương khô (#33), nhặt 200 Chìa khóa cơ quan — **id 230** (có template 231 trùng tên, đừng tra theo tên); count đứng yên 3 phút thì tự đổi khu (`-Dnso.uplevel35.key.stallMs`) · 2: trả Matsurugi |
| 23 | `TaskMatsurugi35` | "NV truy tìm địa đồ" · 0: đạt lv34 (map 35; từ Oshin 48 đi vòng qua Làng chài 32 vì dịch chuyển thẳng 48->35 lỗi) · 1: map 35 đứng (1861,432) dùng chìa khóa Matsurugi đưa -> map ẩn, đánh hết quái, nhặt tấm địa đồ (type 25, tự học id); không còn chìa (chết) -> về Matsurugi xin chìa mới · 2: trả Matsurugi. Id chìa/map ẩn/vật phẩm in log `M35 KEY/HIDDEN/LOOT`, ghi đè bằng `-Dnso.uplevel35.m35.key/hidden/loot`. Nhân vật lv35 vẫn chạy nếu đang ở task 23 |

Buff chung (As35 mỗi tick): Nấm linh chi (hiệu ứng type 18) và thức ăn (type 0) chạy song song; học sách võ công trong túi khi đủ level và chưa có skill (so tên sách với tên skill bỏ "Chiêu "), sau đó chọn lại skill đánh cao nhất (theo id, không dùng `Char.gameAA(template)` vì so tham chiếu) để Code.java cộng điểm kỹ năng vào skill đó.

Đã chạy thật trên acctest1: 18, 19, 20 hoàn thành; tiếp theo `ctaskId` 21 (npc 23, map 48, chưa có logic).
`pickAndFight` dùng cờ `MOB_ANY` để đánh cả tinh anh/thủ lĩnh; `grind` chỉ đánh quái thường.

## Thêm nhiệm vụ mới

1. Tạo `uplevel35/src/tasks/TaskTenNhiemVu.java` kế thừa `Uplevel35Task`
   (`taskId()`, `label()`, `tick(bot, me, task)` theo `task.index`).
2. Thêm `new TaskTenNhiemVu()` vào mảng `TASKS` trong `As35.java`.
3. Helper có sẵn trên `As35`: `goMap(map)`, `grind(map, mobId, pickItemId)`,
   `levelUp(level, map, mobId)`, `standAt(x, y, tol)`, `talkToTaskNpc(lý do)`,
   `itemIdByName("tên")`, `findBagItemByName("tên")`; trên `Uplevel35Support`:
   `findItem`, `takeFromBox`, `walkToNpc`.
4. Chưa biết `ctaskId`/tên bước? Chạy thử: dòng `UPLEVEL35 TASK ...` in `ctaskId`,
   tên và các bước kèm số lượng.

## Sửa patch

Patch được sinh bằng `scripts/make-patches.py` (đọc `src/`, sửa trong bộ nhớ, ghi
`patches/*.patch`). Sửa transform trong file đó rồi:

```bash
python3 uplevel35/scripts/make-patches.py .
./uplevel35/scripts/check-patches.sh   # dry-run trên src/ hiện tại
```

Patch chỉ nên đụng file mà `optimized-runtime/overrides` **không** ghi đè
(`check-patches.sh` cảnh báo nếu có). Logic mới đặt trong `uplevel35/src/`.
`UPLEVEL35_KEEP_STAGING=1 ./uplevel35/scripts/build.sh` giữ lại `build/src` đã ghép
để xem code sau khi patch.

## Mỗi nhân vật một tiến trình (JVM)

Client giữ trạng thái game trong biến `static` (`Char.myChar`, `Auto.fieldAL`, `GameScr.*`...), nên chạy
nhiều nhân vật trong một JVM làm nhân vật sau kế thừa trạng thái nhân vật trước (đã gặp: nhân vật lv2
dùng skill của Ninja kiếm lv25 nên đánh bù nhìn không ăn). Vì vậy `start-workers.sh` chạy
`scripts/worker-loop.sh`, vòng lặp này chạy java với `-Dnso.process.per.character=true`:

- Xong một nhân vật, `AccountAutoManager` ghi tiến độ vào `workers/worker-XX/home/uplevel-progress.properties`
  (tài khoản, nhân vật, thứ tự danh sách nhân vật) rồi thoát mã 3; vòng lặp chạy JVM mới, JVM mới đọc tiến độ và làm tiếp.
- Hết tài khoản: ghi `home/worker.done`, xoá file tiến độ, thoát mã 0 → vòng lặp dừng.
- PID: `loop.pid` = worker-loop.sh, `bot.pid` = java hiện tại. `stop-workers.sh` dừng loop trước rồi tới java.
- Tắt chế độ này: `UPLEVEL35_PROCESS_PER_CHARACTER=false` (chạy lại kiểu cũ, mọi nhân vật trong một JVM).
- Chạy lại từ đầu: xoá `home/uplevel-progress.properties` (hoặc build-workers lại).

## Web monitor

Xem trạng thái mọi worker (lv, exp, hành trang, lượng/xu/yên, CPU, heartbeat): `./uplevel_monitor/run.sh` → http://127.0.0.1:8090. Chi tiết: `uplevel_monitor/README.md`.
