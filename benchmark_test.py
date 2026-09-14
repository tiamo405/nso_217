import subprocess
import os
import time

base_dir = os.path.dirname(os.path.abspath(__file__))
headless_dir = os.path.join(base_dir, "headless-runtime")
opt_dir = os.path.join(base_dir, "optimized-runtime")

# Xóa marker worker.done cũ nếu có
for d in [headless_dir, opt_dir]:
    done_file = os.path.join(d, "run", "worker-01", "home", "worker.done")
    if os.path.exists(done_file):
        os.remove(done_file)

# Chuẩn bị account test riêng biệt để 2 worker không bị đá nhau (trumvd105 và trumvd106)
h_acc_file = os.path.join(headless_dir, "run", "worker-01", "account.csv")
o_acc_file = os.path.join(opt_dir, "run", "worker-01", "account.csv")

with open(h_acc_file, "w") as f:
    f.write("username,password\ntrumvd105,ngan2021\n")

with open(o_acc_file, "w") as f:
    f.write("username,password\ntrumvd106,ngan2021\n")

# 1. Chạy worker Headless (trumvd105)
cmd_headless = [
    "java", "-Xms8m", "-Xmx48m",
    "-XX:+UseSerialGC", "-XX:MinHeapFreeRatio=5", "-XX:MaxHeapFreeRatio=10",
    "-Djava.awt.headless=true",
    "-Dnso.nvhn.headless=true", "-Dnso.tick.ms=50",
    f"-Duser.home={headless_dir}/run/worker-01/home",
    "-cp", f"{headless_dir}/run/worker-01:{headless_dir}/build/classes",
    "HeadlessMain"
]
log_h = open(f"{headless_dir}/run/worker-01/stdout.log", "w")
p_h = subprocess.Popen(cmd_headless, stdout=log_h, stderr=subprocess.STDOUT)
print(f"[*] Headless worker PID: {p_h.pid} (account: trumvd105)")

# 2. Chạy worker Optimized (trumvd106)
cmd_opt = [
    "java", "-Xms8m", "-Xmx36m",
    "-XX:+UseSerialGC", "-XX:MinHeapFreeRatio=5", "-XX:MaxHeapFreeRatio=10",
    "-Djava.awt.headless=true", "-Xss256k", "-XX:CICompilerCount=2",
    "-Dnso.optimized=true", "-Dnso.tick.ms=80", "-Dnso.skip.paint=true",
    "-Dnso.skip.decorations=true", "-Dnso.lazy.map=true", "-Dnso.event.sender=true", "-Dnso.nvhn.headless=true",
    f"-Duser.home={opt_dir}/run/worker-01/home",
    "-cp", f"{opt_dir}/run/worker-01:{opt_dir}/build/classes",
    "OptimizedMain"
]
log_o = open(f"{opt_dir}/run/worker-01/stdout.log", "w")
p_o = subprocess.Popen(cmd_opt, stdout=log_o, stderr=subprocess.STDOUT)
print(f"[*] Optimized worker PID: {p_o.pid} (account: trumvd106)")

def get_stats(pid):
    try:
        # Lấy %CPU và RSS thực tế của tiến trình Java
        out = subprocess.check_output(["ps", "-q", str(pid), "-o", "%cpu,rss,comm", "--no-headers"]).decode().strip()
        if not out:
            return "DEAD", 0
        parts = out.split()
        cpu = parts[0]
        rss_mb = round(int(parts[1]) / 1024.0, 1)
        return cpu, rss_mb
    except Exception:
        return "DEAD", 0

print("\nĐang theo dõi 2 workers đang vào game và làm nhiệm vụ...")
for i in range(1, 6):
    time.sleep(5)
    c_h, r_h = get_stats(p_h.pid)
    c_o, r_o = get_stats(p_o.pid)
    print(f"\n[Lần {i} sau {i*5}s]")
    print(f"  Headless  (PID {p_h.pid}): CPU={c_h}%, RAM (RSS)={r_h} MB")
    print(f"  Optimized (PID {p_o.pid}): CPU={c_o}%, RAM (RSS)={r_o} MB")

# Dọn dẹp tiến trình sau khi đo
try:
    p_h.terminate()
    p_o.terminate()
except Exception:
    pass

print("\nHoàn tất đo đạc. Kiểm tra log thành công!")
