# Ghi chú kỹ thuật AUTO NVHN và menu NPC25

> Đọc tài liệu này trước khi sửa `src/AutoNvhn.java`, `src/Controller.java`,
> `src/GameScr.java` hoặc logic di chuyển NVHN. Các giá trị menu dưới đây là
> mapping đã đối chiếu với protocol hiện tại của game.

## 1. Cách gửi một nút menu NPC

`GameScr.fieldAB(npcId, menuIndex, actionIndex)` cuối cùng gọi:

```java
Service.gI().menu((byte) 0, npcId, menuIndex, actionIndex);
```

Protocol được ghi trong `src/Service.java:483-492` theo thứ tự:

```text
byte type, byte npcId, byte menuIndex, byte actionIndex
```

Không được nhầm `menuIndex` với `actionIndex`.

## 2. NPC25: menu NVHN

Khi mở NPC25, server có thể gửi một số menu động trước nhóm menu tĩnh. Client
đếm số menu động vào `GameScr.fieldGH` tại `src/Controller.java:1615-1629`.

Vì vậy `fieldGH` là **offset menu do server gửi**, không phải cố định là nút
“Làm nhanh” và không nên tự đổi thành `0`.

Nhóm menu NVHN hiện có thứ tự:

| `actionIndex` | Nút trong game | Khi nào được gọi |
|---:|---|---|
| `0` | Nhận | Chưa có `fieldAY` tại trường |
| `1` | Hủy | Không dùng trong luồng tự động hiện tại |
| `2` | Hoàn Thành | Đã đủ `count >= maxCount`, đã quay về trường |
| `3` | Đi làm NV | Đã nhận task, còn chưa đủ số lượng, đang ở trường |

Các lệnh đúng trong `AutoNvhn` là:

```java
GameScr.fieldAB(25, GameScr.fieldGH, 0); // Nhận NVHN
GameScr.fieldAB(25, GameScr.fieldGH, 2); // Hoàn thành NVHN
GameScr.fieldAB(25, GameScr.fieldGH, 3); // Đi làm NVHN
```

## 3. Trình tự hoạt động bắt buộc

### Nhận nhiệm vụ

Ở trường, nếu `fieldAY == null`:

1. Gửi action `0`.
2. Chờ server trả task.
3. Đọc lại `Char.fieldAM(0)` để lấy `TaskOrder`.

### Đi làm nhiệm vụ

Ở trường, nếu đã có `fieldAY` và chưa đủ `maxCount`:

1. Gửi action `3` “Đi làm NV”.
2. Chờ chuyển map/kết quả server.
3. Tiếp tục route tới `fieldAY.mapId` và đánh `fieldAY.killId`.

Nếu bỏ action `3`, bot có thể vẫn giữ task cục bộ nhưng đứng ở trường với log
giống:

```text
map=27(Trường Haruna) state=đang tới map nhiệm vụ progress=0/23
```

Đây là lỗi đã từng xảy ra khi action `3` bị xóa.

### Hoàn thành và nhận task tiếp theo

Khi `fieldAY.count >= fieldAY.maxCount`:

1. Di chuyển về trường.
2. Gửi action `2` “Hoàn Thành”.
3. Đặt `waitingForNewTask = true`, xóa task cục bộ.
4. Sau khi server xác nhận, gửi action `0` để nhận task tiếp theo.

Server đôi khi trả thoáng qua các câu:

```text
Con hãy hoàn thành nhiệm vụ rồi quay lại đây.
Nhiệm vụ lần trước ta giao, con vẫn chưa hoàn thành.
```

Không được lập tức kết luận action `3` sai từ hai câu này. Đây thường là do
action nhận task mới được gửi sát sau action hoàn thành. Chỉ coi là lỗi khi
message lặp liên tục và không có task mới/map mới.

## 4. Ý nghĩa các trạng thái log

| Log | Ý nghĩa |
|---|---|
| `state=đang nhận nhiệm vụ` | Chưa có task cục bộ, đang gọi action `0` |
| `state=đang tới map nhiệm vụ` tại map trường | Đã có task, cần action `3` để bắt đầu đi làm |
| `state=đang di chuyển tới map X` | Đã rời trường và đang route tới map task |
| `state=đang đánh ...` | Đã tới map task, đang đánh đúng mob |
| `state=đang trả nhiệm vụ` | Đã đủ `count/maxCount`, đang về trường |
| `AUTO NVHN: server đã cấp nhiệm vụ mới` | Server đã cấp task tiếp theo thành công |

## 5. Các message NPC25 đặc biệt

- `Hãy nhận nhiệm vụ mỗi ngày từ ta rồi mới sử dụng tính năng này.`
  - Cho biết server chưa thấy task được nhận hoặc task cục bộ bị stale.
  - Luồng hiện tại chỉ recovery một lần bằng cách xóa task cục bộ `taskId=0`.
  - Không retry vô hạn.

- `không có nhiệm vụ phù hợp với cấp độ và tiến trình... hoàn thành nhiệm vụ
  chính tuyến...`
  - Không phải lỗi menu.
  - Nhân vật không đủ tiến trình chính tuyến; chuyển nhân vật, không gọi lại
    NPC25 vô hạn.

- `Hôm nay con đã làm hết nhiệm vụ ta giao...`
  - Đạt giới hạn ngày; kết thúc phần NVHN của nhân vật/tài khoản.

## 6. Những lỗi đã từng xảy ra

### Xóa action `3`

Đã từng xóa:

```java
GameScr.fieldAB(25, GameScr.fieldGH, 3);
```

vì nhầm rằng action `3` là nguyên nhân của message “Hãy nhận nhiệm vụ...”.
Test thực tế với tài khoản `luongdzvd1` cho thấy sau khi khôi phục action `3`,
bot đã đi từ map 27 sang map 52, hoàn thành `23/23`, quay về trường và nhận task
mới. Do đó không được xóa action `3` nếu chưa kiểm tra protocol mới.

### Ép `fieldGH = 0`

Không được coi `fieldGH` là action index. `fieldGH` là số menu động đứng trước
menu tĩnh. Hardcode `0` có thể chạy ở một layout menu nhưng gửi sai menu khi
server thêm menu động.

## 7. Checklist trước khi sửa

- Đọc phần mapping ở tài liệu này.
- Kiểm tra cả ba tham số của `GameScr.fieldAB`.
- Kiểm tra `GameScr.fieldGH` được cập nhật từ command menu server.
- Không xóa action `3` chỉ vì thấy một message NPC25 đơn lẻ.
- Không retry NPC25 vô hạn.
- Build lại `optimized-runtime` sau khi sửa.
- Test tối thiểu một task hoàn chỉnh:
  - nhận `0/x`;
  - rời map trường;
  - tiến độ tăng;
  - hoàn thành `x/x`;
  - quay về trường;
  - nhận task mới.
- Lưu log có các dòng `AUTO NVHN STATUS`, `AUTO NVHN NPC25` và
  `server đã cấp nhiệm vụ mới` để đối chiếu.

## 8. File nguồn liên quan

- `src/AutoNvhn.java`: state machine NVHN và các action NPC25.
- `src/GameScr.java`: `fieldGH` và `fieldAB`.
- `src/Service.java`: đóng gói packet menu command `29`.
- `src/Controller.java`: đọc menu động và cập nhật `GameScr.fieldGH`.
- `src/TaskOrder.java`: cấu trúc task (`count`, `maxCount`, `killId`, `mapId`).
