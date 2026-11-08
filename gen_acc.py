import csv

PASSWORD = "cuibapvh123"  # đổi tay ở đây nếu muốn
OUTPUT_FILE = "account-as20.csv"

with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["username", "password"])

   # for i in range(2160, 7, -1):
   #     writer.writerow([f"luonghihi{i:03d}", PASSWORD])

    for i in range(1, 1001):
        writer.writerow([f"luongclone{i}", PASSWORD])

print(f"Da tao xong file {OUTPUT_FILE}")
print("Range: luongdzvd1-1200")
print(f"Password: {PASSWORD}")
