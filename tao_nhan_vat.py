#!/usr/bin/env python3
"""Tạo nhân vật cho các tài khoản chưa có nhân vật.

Đọc CSV có 2 cột ``account,password``. Với mỗi tài khoản, lấy dãy số ở cuối
tên tài khoản (namtp1 → 1, namtp999 → 999), đệm đủ 3 chữ số rồi đọc từng chữ
số thành chữ không dấu để làm tên nhân vật:

    namtp1    → 001 → lglinhlinhmot
    namtp25   → 025 → lglinhhainam
    namtp999  → 999 → lgchinchinchin
    namtp1000 → 1000 → lgmotngan     (số tròn nghìn đọc là <x>ngan)

Số 0 đọc là "linh" (4 ký tự) thay vì "khong" để tên luôn nằm trong giới hạn
5-15 ký tự của server (dài nhất: lg + 3 x 4 = 14).

Tiền tố mặc định là "lg", đổi bằng --prefix.

Protocol (đối chiếu src/Service.java createChar):
    cmd -28, byte -125, UTF name, byte gender, byte hairId

Ví dụ:
    python3 tao_nhan_vat.py accounts.csv
    python3 tao_nhan_vat.py accounts.csv --gender 1 --workers 5
    python3 tao_nhan_vat.py accounts.csv --dry-run      # chỉ in tên sẽ tạo
"""

import argparse
import csv
import random
import re
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

from hoatdong import NSOActivityClient, NSOMessage, NSOReader


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_INPUT = ROOT_DIR / "account-tao-nv.csv"
DEFAULT_OUTPUT = ROOT_DIR / "ket-qua-tao-nv.csv"
DEFAULT_HOST = "180.93.114.122"
DEFAULT_PORT = 14444
DEFAULT_DELAY = 2.0

DIGIT_WORDS = {
    "0": "linh", "1": "mot", "2": "hai", "3": "ba", "4": "bon",
    "5": "nam", "6": "sau", "7": "bay", "8": "tam", "9": "chin",
}
# Lấy từ CreateCharScr.hairID: index 0 = nữ, 1 = nam.
HAIR_IDS = {0: (11, 26, 27, 28), 1: (2, 23, 24, 25)}
SUB_CREATE_CHAR = -125
MIN_NAME_LENGTH = 5
MAX_NAME_LENGTH = 15

print_lock = threading.Lock()


def log(message: str):
    with print_lock:
        print(message, flush=True)


def number_to_name(number: str, pad: int = 3) -> str:
    """'1' → 'linhlinhmot', '999' → 'chinchinchin', '1000' → 'motngan'."""
    value = int(number)
    if value >= 1000 and value % 1000 == 0 and value < 10000:
        return DIGIT_WORDS[str(value // 1000)] + "ngan"
    digits = str(value).zfill(pad)
    return "".join(DIGIT_WORDS[digit] for digit in digits)


def ingame_from_account(account: str, pad: int = 3) -> str:
    match = re.search(r"(\d+)$", account)
    if not match:
        raise ValueError(f"Tài khoản '{account}' không có số ở cuối")
    return number_to_name(match.group(1), pad)


def read_accounts(csv_path: Path) -> list[tuple[str, str]]:
    """Đọc CSV account,password (có hoặc không có header)."""
    accounts = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for line_number, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if len(row) < 2:
                raise ValueError(f"Dòng {line_number} thiếu account/password")
            account, password = row[0].strip(), row[1].strip()
            if account.lower() in ("account", "username") and password.lower() == "password":
                continue
            if account and password:
                accounts.append((account, password))
    return accounts


class CreateCharClient(NSOActivityClient):
    """Client cho phép đăng nhập tài khoản trắng và tạo nhân vật."""

    def _parse_char_list(self, reader: NSOReader):
        count = reader.read_byte()
        characters = []
        for _ in range(count):
            reader.read_byte()  # gender
            name = reader.read_utf()
            school = reader.read_utf()
            level = reader.read_ubyte()
            for _ in range(4):
                reader.read_short()
            characters.append((name, level, school))
        return characters

    def login_any(self, username: str, password: str):
        """Giống login() gốc nhưng trả về list nhân vật kể cả khi rỗng.

        Trả về None nếu đăng nhập thất bại.
        """
        self._set_client_type()
        message = NSOMessage(self.CMD_NOT_LOGIN)
        message.write_byte(self.CMD_LOGIN)
        message.write_utf(username)
        message.write_utf(password)
        message.write_utf("4.1.1")
        message.write_utf("")
        message.write_utf("")
        message.write_utf("".join(str(random.randint(0, 8)) for _ in range(12)))
        message.write_byte(0)
        self.send(message)

        deadline = time.time() + 25
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                log(f"  ❌ [{username}] Đăng nhập: {self._read_server_error(data)}")
                return None
            if command != self.CMD_NOT_MAP or not data:
                continue
            reader = NSOReader(data)
            subcommand = reader.read_byte()
            if subcommand == -123:
                for _ in range(4):
                    reader.read_byte()
                self.pending_bootstrap = {-122, -121, -120, -119}
                for request in (-122, -121, -120, -119):
                    self._send_not_map(request)
            elif subcommand in (-122, -121, -120, -119):
                self.pending_bootstrap.discard(subcommand)
                if not self.pending_bootstrap:
                    self._send_not_map(-101)
            elif subcommand == -126:
                self.characters = self._parse_char_list(reader)
                return self.characters
        log(f"  ❌ [{username}] Đăng nhập không hoàn tất: {self.last_receive_error or 'timeout'}")
        return None

    def create_character(self, name: str, gender: int, hair: int):
        """Gửi lệnh tạo nhân vật. Trả về (ok, thông báo)."""
        message = NSOMessage(self.CMD_NOT_MAP)
        message.write_byte(SUB_CREATE_CHAR)
        message.write_utf(name)
        message.write_byte(gender)
        message.write_byte(hair)
        self.send(message)

        deadline = time.time() + 20
        while time.time() < deadline:
            command, data = self.receive(min(5, max(0.2, deadline - time.time())))
            if command is None:
                if self.last_receive_error not in (None, "timeout"):
                    return False, f"Mất kết nối: {self.last_receive_error}"
                continue
            if command == self.CMD_SERVER_ERROR:
                return False, self._read_server_error(data)
            if command == self.CMD_NOT_MAP and data:
                reader = NSOReader(data)
                if reader.read_byte() == -126:
                    self.characters = self._parse_char_list(reader)
                    names = [c[0] for c in self.characters]
                    if name in names:
                        return True, "Đã có trong danh sách nhân vật"
                    return False, f"Server trả danh sách không có tên mới: {names}"
            # Một số server vào thẳng game sau khi tạo.
            if command == self.CMD_SUB_COMMAND and data:
                if NSOReader(data).read_byte() == -127:
                    return True, "Đã vào game với nhân vật mới"
        return False, "Timeout chờ server phản hồi tạo nhân vật"


def process_account(index, total, account, password, args):
    result = {"account": account, "password": password, "ingame": "", "status": ""}
    try:
        name = args.prefix + ingame_from_account(account, args.pad)
    except ValueError as exc:
        result["status"] = f"lỗi: {exc}"
        log(f"[{index}/{total}] ❌ {exc}")
        return result
    result["ingame"] = name
    if not MIN_NAME_LENGTH <= len(name) <= MAX_NAME_LENGTH:
        result["status"] = f"lỗi: tên '{name}' dài {len(name)} ký tự, ngoài giới hạn {MIN_NAME_LENGTH}-{MAX_NAME_LENGTH}"
        log(f"[{index}/{total}] ❌ {account}: {result['status']}")
        return result

    if args.dry_run:
        result["status"] = "dry-run"
        log(f"[{index}/{total}] {account} → {name}")
        return result

    gender = args.gender if args.gender is not None else random.randint(0, 1)
    hair = random.choice(HAIR_IDS[gender])

    client = CreateCharClient(args.host, args.port)
    try:
        if not client.connect():
            result["status"] = "lỗi: không kết nối được"
            return result
        characters = client.login_any(account, password)
        if characters is None:
            result["status"] = "lỗi: đăng nhập thất bại"
            return result
        if characters:
            existing = ", ".join(c[0] for c in characters)
            result["status"] = f"bỏ qua: đã có nhân vật ({existing})"
            log(f"[{index}/{total}] ⏭️  {account}: đã có nhân vật {existing}")
            return result

        ok, detail = client.create_character(name, gender, hair)
        result["status"] = ("ok: " if ok else "lỗi: ") + detail
        icon = "✅" if ok else "❌"
        log(f"[{index}/{total}] {icon} {account} → {name} ({detail})")
        return result
    except Exception as exc:
        result["status"] = f"lỗi: {exc}"
        log(f"[{index}/{total}] ❌ {account}: {exc}")
        return result
    finally:
        client.disconnect()
        # Nghỉ trong chính luồng này, không chặn các luồng khác.
        if args.delay > 0:
            time.sleep(args.delay)


def write_results(path: Path, rows):
    with path.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["account", "password", "ingame", "status"])
        writer.writeheader()
        writer.writerows(rows)


def build_parser():
    parser = argparse.ArgumentParser(description="Tạo nhân vật cho tài khoản trắng, tên = số cuối tài khoản đọc thành chữ.")
    parser.add_argument("input", nargs="?", type=Path, default=DEFAULT_INPUT,
                        help=f"CSV account,password (mặc định: {DEFAULT_INPUT.name})")
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT,
                        help=f"CSV kết quả (mặc định: {DEFAULT_OUTPUT.name})")
    parser.add_argument("--host", default=DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    parser.add_argument("--gender", type=int, choices=(0, 1), default=None,
                        help="0 = nữ, 1 = nam (mặc định: ngẫu nhiên)")
    parser.add_argument("--prefix", default="lg",
                        help="Chữ thêm vào đầu tên nhân vật (mặc định: lg)")
    parser.add_argument("--pad", type=int, default=3,
                        help="Đệm số về N chữ số trước khi đổi chữ (mặc định 3: 1 → linhlinhmot)")
    parser.add_argument("--workers", type=int, default=6, help="Số tài khoản chạy song song")
    parser.add_argument("--delay", type=float, default=DEFAULT_DELAY,
                        help="Giây mỗi luồng nghỉ sau khi xong 1 tài khoản")
    parser.add_argument("--dry-run", action="store_true", help="Chỉ in tên sẽ tạo, không đăng nhập")
    return parser


def main() -> int:
    args = build_parser().parse_args()
    try:
        accounts = read_accounts(args.input)
    except FileNotFoundError:
        print(f"❌ Không tìm thấy file: {args.input}")
        return 1
    except ValueError as exc:
        print(f"❌ CSV không hợp lệ: {exc}")
        return 1

    total = len(accounts)
    print(f"Đọc {total} tài khoản từ {args.input}")
    results = []
    with ThreadPoolExecutor(max_workers=max(1, args.workers)) as pool:
        futures = []
        for index, (account, password) in enumerate(accounts, start=1):
            futures.append(pool.submit(process_account, index, total, account, password, args))
            # Chỉ giãn nhẹ đợt khởi động đầu để không bắn N login cùng lúc.
            if not args.dry_run and index < args.workers:
                time.sleep(0.5)
        for future in as_completed(futures):
            results.append(future.result())

    order = {account: i for i, (account, _) in enumerate(accounts)}
    results.sort(key=lambda row: order.get(row["account"], 0))
    write_results(args.output, results)

    created = sum(1 for row in results if row["status"].startswith("ok"))
    skipped = sum(1 for row in results if row["status"].startswith("bỏ qua"))
    failed = sum(1 for row in results if row["status"].startswith("lỗi"))
    print(f"\n✅ Tạo mới: {created} | ⏭️ Bỏ qua: {skipped} | ❌ Lỗi: {failed}")
    print(f"📄 Kết quả: {args.output}")
    return 0 if failed == 0 else 2


if __name__ == "__main__":
    raise SystemExit(main())
