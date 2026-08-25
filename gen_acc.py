import csv

PASSWORD = "ngan2021"  # đổi tay ở đây nếu muốn
OUTPUT_FILE = "account-del.csv"

with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["username", "password"])

    for i in range(0, 0):
        writer.writerow([f"luongclone{i:03d}", PASSWORD])

    for i in range(1001, 1003):
        writer.writerow([f"luongdzvd{i}", PASSWORD])

print(f"Da tao xong file {OUTPUT_FILE}")
print("Range: luongcdzvd1001-1002")
print(f"Password: {PASSWORD}")
