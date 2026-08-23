# NSO Python Multi-Worker Headless Bot Engine

Hệ thống Bot tự động hóa hoàn toàn độc lập cho game Ninja School Online (NSO), kết nối trực tiếp qua TCP Socket và tối ưu hóa tài nguyên (CPU / RAM) ở mức tối đa.

---

## 🌟 Tính năng Nổi bật

1. **Siêu nhẹ (Ultra-Low Memory & CPU):**
   * Không sử dụng JVM, không render đồ họa.
   * Tiêu thụ chỉ **~15MB – 25MB RAM / Worker** (1 VPS có thể chạy hàng trăm tài khoản).
2. **Kiến trúc Multi-Worker độc lập:**
   * Chia tài khoản thành từng nhóm độc lập (mỗi Worker chạy 1 Process riêng xử lý **4 – 5 tài khoản**).
   * Cách ly hoàn toàn lỗi: Một tài khoản/worker bị lỗi không ảnh hưởng đến các worker khác.
3. **Xử lý trọn vẹn 3 Nhân vật / Tài khoản (Có lọc Level):**
   * Đăng nhập tài khoản $\rightarrow$ Lấy danh sách 3 nhân vật ($0 \rightarrow 1 \rightarrow 2$).
   * **Điều kiện cấp độ:** Chỉ nhân vật có **Level $\ge$ 30** mới thực hiện chuỗi nhiệm vụ; nhân vật $< 30$ sẽ tự động bỏ qua để chuyển sang nhân vật kế tiếp.
4. **Đầy đủ Chuỗi Nhiệm vụ Tự động:**
   * **Dọn rương & Bán rác:** Đọc danh sách ID từ `config/delete_items.txt`, rút đồ rác từ rương về túi và bán.
   * **Chuẩn bị nhân vật:**
     * Mua & sử dụng thức ăn theo mốc cấp độ (Lv 10, 20, 30, 40, 50).
     * Chọn Skill chiến đấu theo cấp (Skill 5 nếu Lv $\ge 30$, fallback Skill 1).
     * Mua & đội Mũ Noel (Nam: 351, Nữ: 352) từ NPC Goosho nếu chưa có.
     * Tới Okaza (Map 72) mua vé may mắn và lật hình 2 lượt.
     * Lưu tọa độ tại Kamakura NPC 5 ở trường.
   * **Auto NVHN:** Nhận nhiệm vụ từ NPC 25 $\rightarrow$ Đánh quái $\rightarrow$ Hoàn thành NV (lặp lại $20/20$ lần).
   * **Đi Hang động:** Tới NPC Kanata (NPC 0) $\rightarrow$ Vào hang động tương ứng với mốc cấp độ (Hang 3x – 9x).

---

## 📁 Cấu trúc Thư mục

```
python-worker/
├── config/
│   ├── config.py                 # Cấu hình Host, Port, Version, timeouts, delay
│   └── delete_items.txt          # Danh sách Item ID cần xóa khi dọn rương/túi
├── network/
│   ├── socket_client.py          # Kết nối TCP Socket, nhận/gửi nhị phân
│   ├── encryption.py             # Rolling XOR Cipher & Key exchange
│   ├── message.py                # Binary DataStream Reader & Writer (Big-Endian)
│   └── constants.py              # Bảng mã Command Bytes
├── models/
│   ├── character.py              # Model Nhân vật & tóm tắt nhân vật
│   ├── item.py                   # Model Item & ItemTemplate
│   ├── skill.py                  # Model Skill
│   ├── map_data.py               # Model Map, NPC, Mob, Waypoint
│   └── task.py                   # Model NVHN
├── protocol/
│   ├── service.py                # Đóng gói và gửi các packet lên server
│   └── controller.py             # Bắt và giải mã packets từ server
├── tasks/
│   ├── base_task.py              # Base Task interface
│   ├── box_cleaner.py            # Dọn rương và bán rác theo list ID
│   ├── preparer.py               # Thức ăn, Skill, Mũ Noel, Lật hình Okaza, Lưu tọa độ
│   ├── nvhn.py                   # Vòng lặp nhận, đánh quái và trả NVHN (20/20)
│   └── cave.py                   # Vào hang động qua NPC Kanata
├── bot/
│   ├── character_runner.py       # Điều phối chuỗi 4 tasks cho 1 nhân vật
│   └── account_runner.py         # Quản lý 1 tài khoản, lặp 3 nhân vật (Lv >= 30)
├── worker/
│   └── worker_main.py            # Entry point chạy 1 worker process
├── scripts/
│   ├── build-workers.sh          # Chia account.csv thành các worker (4-5 acc/worker)
│   ├── start-workers.sh          # Khởi động toàn bộ workers chạy nền
│   ├── stop-workers.sh           # Dừng an toàn toàn bộ workers
│   ├── status-workers.sh         # Xem bảng trạng thái CPU, RAM và tiến độ
│   ├── logs-workers.sh           # Xem log realtime của từng worker
│   └── restart-workers.sh        # Khởi động lại workers
├── requirements.txt
└── README.md
```

---

## 🚀 Hướng dẫn Sử dụng

### 1. Chuẩn bị danh sách tài khoản
Tạo file `account.csv` ở thư mục gốc repo hoặc đặt tại `python-worker/accounts.csv`:
```csv
username,password
acc_01,pass_01
acc_02,pass_02
acc_03,pass_03
...
```

### 2. Cấp quyền thực thi cho scripts
```bash
chmod +x python-worker/scripts/*.sh
```

### 3. Chia danh sách Worker (Mỗi worker 4-5 tài khoản)
```bash
# Chia 5 tài khoản / worker (mặc định)
./python-worker/scripts/build-workers.sh

# Hoặc chỉ định rõ file CSV và số tài khoản / worker:
./python-worker/scripts/build-workers.sh account.csv 4
```

### 4. Khởi động toàn bộ Workers
```bash
./python-worker/scripts/start-workers.sh
```

### 5. Xem trạng thái và CPU/RAM
```bash
./python-worker/scripts/status-workers.sh
```

### 6. Xem Log realtime của Worker bất kỳ
```bash
# Xem log worker-01
./python-worker/scripts/logs-workers.sh 1

# Hoặc xem worker-02
./python-worker/scripts/logs-workers.sh worker-02
```

### 7. Dừng Workers
```bash
./python-worker/scripts/stop-workers.sh
```

