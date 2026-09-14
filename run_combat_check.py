import subprocess
import os
import time

base_dir = os.path.dirname(os.path.abspath(__file__))
opt_dir = os.path.join(base_dir, "optimized-runtime")
worker_dir = os.path.join(opt_dir, "run", "worker-01")

# Xóa worker.done nếu có để bot chạy tiếp
done_file = os.path.join(worker_dir, "home", "worker.done")
if os.path.exists(done_file):
    os.remove(done_file)

# Đảm bảo account test trumvd106
acc_file = os.path.join(worker_dir, "account.csv")
with open(acc_file, "w") as f:
    f.write("username,password\ntrumvd106,ngan2021\n")

cmd_opt = [
    "java", "-Xms8m", "-Xmx36m",
    "-XX:+UseSerialGC", "-XX:MinHeapFreeRatio=5", "-XX:MaxHeapFreeRatio=10",
    "-Djava.awt.headless=true", "-Xss256k", "-XX:CICompilerCount=2",
    "-Dnso.optimized=true", "-Dnso.tick.ms=80", "-Dnso.skip.paint=true",
    "-Dnso.skip.decorations=true", "-Dnso.lazy.map=true", "-Dnso.event.sender=true", "-Dnso.nvhn.headless=true",
    f"-Duser.home={worker_dir}/home",
    "-cp", f"{worker_dir}:{opt_dir}/build/classes",
    "OptimizedMain"
]

log_file_path = os.path.join(worker_dir, "stdout.log")
log_o = open(log_file_path, "w")
p_o = subprocess.Popen(cmd_opt, stdout=log_o, stderr=subprocess.STDOUT)
print(f"[*] Đã khởi chạy Optimized Worker PID: {p_o.pid}")
print("[*] Đang theo dõi log trực tiếp cho đến khi nhân vật di chuyển và đánh quái...")
print("=" * 60)

start_time = time.time()
last_pos = 0

combat_detected = False
while time.time() - start_time < 90:  # theo dõi tối đa 90 giây
    if p_o.poll() is not None:
        print("\n[!] Worker đã dừng!")
        break

    with open(log_file_path, "r", errors="replace") as f:
        f.seek(last_pos)
        new_text = f.read()
        last_pos = f.tell()

    if new_text:
        lines = new_text.splitlines()
        for line in lines:
            line_str = line.strip()
            if not line_str:
                continue
            # In ra các dòng quan trọng liên quan đến map, di chuyển, nhiệm vụ, quái và đánh
            keywords = [
                "AUTO NVHN", "AUTO TA THU", "map", "tới", "quái", "kill", "đánh",
                "task", "Mob", "Attack", "hp", "FIGHT", "Zone", "zone", "Khu"
            ]
            if any(k.lower() in line_str.lower() for k in keywords):
                print(line_str)
            if any(k in line_str for k in ["đánh", "Attack", "FIGHT", "kill", "tiêu diệt", "bắt đầu NVHN"]):
                combat_detected = True

    time.sleep(1)

print("=" * 60)
try:
    p_o.terminate()
except Exception:
    pass

if combat_detected:
    print("[✓] Xác nhận: Nhân vật đã di chuyển và thực hiện hành động đánh/combat thành công!")
else:
    print("[*] Kết thúc phiên theo dõi.")
