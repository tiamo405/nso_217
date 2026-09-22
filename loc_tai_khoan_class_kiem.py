#!/usr/bin/env python3
"""Lọc các nhân vật có class Kiếm từ danh sách tài khoản.

Mặc định đọc ``clone.csv`` và ghi kết quả vào
``account-class-kiem.csv``. Mỗi dòng kết quả tương ứng với một nhân vật:
username, password, tên nhân vật, level và class.

Ví dụ:
    python3 loc_tai_khoan_class_kiem.py
    python3 loc_tai_khoan_class_kiem.py --input accounts.csv --output kiem.csv
"""

import argparse
import csv
import time
import unicodedata
from pathlib import Path

from hoatdong import NSOActivityClient


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_INPUT = ROOT_DIR / "clone.csv"
DEFAULT_OUTPUT = ROOT_DIR / "account-class-kiem.csv"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
DEFAULT_DELAY = 2.0


def normalize_text(value: str) -> str:
    """Chuẩn hóa text để so sánh không phân biệt hoa thường và dấu."""
    value = unicodedata.normalize("NFD", value.casefold())
    value = "".join(char for char in value if unicodedata.category(char) != "Mn")
    return value.replace("đ", "d").strip()


def read_accounts(csv_path: Path) -> list[tuple[str, str]]:
    """Đọc username/password từ CSV, bỏ qua header và dòng trống."""
    accounts = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for line_number, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if len(row) < 2:
                raise ValueError(f"Dòng {line_number} thiếu username/password")

            username, password = row[0].strip(), row[1].strip()
            if normalize_text(username) == "username" and normalize_text(password) == "password":
                continue
            if username and password:
                accounts.append((username, password))
    return accounts


def write_results(output_path: Path, rows: list[dict[str, object]]) -> None:
    """Ghi kết quả dạng UTF-8 có BOM để mở trực tiếp bằng Excel."""
    fieldnames = ["username", "password", "character", "level", "class"]
    with output_path.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def scan_accounts(
    accounts: list[tuple[str, str]],
    host: str,
    port: int,
    delay: float,
) -> list[dict[str, object]]:
    """Đăng nhập từng tài khoản và lấy các nhân vật có class Kiếm."""
    results = []

    for index, (username, password) in enumerate(accounts, start=1):
        print(f"[{index}/{len(accounts)}] Đang kiểm tra {username}...", flush=True)
        client = NSOActivityClient(host, port)
        try:
            if not client.connect():
                print("  ❌ Không kết nối được server", flush=True)
                continue
            if not client.login(username, password):
                print("  ❌ Đăng nhập thất bại hoặc không lấy được nhân vật", flush=True)
                continue

            matches = [character for character in client.characters
                       if len(character) >= 3 and normalize_text(str(character[2])) == "kiem"]
            for character_name, level, school in matches:
                results.append({
                    "username": username,
                    "password": password,
                    "character": character_name,
                    "level": level,
                    "class": school,
                })
                print(f"  ✅ {character_name} - level {level} - {school}", flush=True)

            if not matches:
                print("  - Không có nhân vật class Kiếm", flush=True)
        except Exception as exc:
            print(f"  ❌ Lỗi khi xử lý tài khoản: {exc}", flush=True)
        finally:
            client.disconnect()

        if index < len(accounts) and delay > 0:
            time.sleep(delay)

    return results


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Đăng nhập các tài khoản trong CSV và lọc nhân vật class Kiếm."
    )
    parser.add_argument("--input", type=Path, default=DEFAULT_INPUT,
                        help=f"CSV tài khoản (mặc định: {DEFAULT_INPUT.name})")
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT,
                        help=f"CSV kết quả (mặc định: {DEFAULT_OUTPUT.name})")
    parser.add_argument("--host", default=DEFAULT_HOST,
                        help=f"Server game (mặc định: {DEFAULT_HOST})")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT,
                        help=f"Cổng server (mặc định: {DEFAULT_PORT})")
    parser.add_argument("--delay", type=float, default=DEFAULT_DELAY,
                        help="Số giây chờ giữa các tài khoản (mặc định: 11)")
    return parser


def main() -> int:
    args = build_parser().parse_args()
    try:
        accounts = read_accounts(args.input)
        results = scan_accounts(accounts, args.host, args.port, max(0, args.delay))
        write_results(args.output, results)
    except FileNotFoundError:
        print(f"❌ Không tìm thấy file: {args.input}")
        return 1
    except ValueError as exc:
        print(f"❌ CSV không hợp lệ: {exc}")
        return 1

    print(f"\n✅ Hoàn tất: tìm thấy {len(results)} nhân vật class Kiếm.")
    print(f"📄 Đã lưu kết quả vào: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
