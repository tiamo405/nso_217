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

Mỗi tài khoản có tối đa 3 nhân vật (3 slot ở SelectCharScr). Tên nv2/nv3 lấy
theo tên THẬT của nv1 đang có trên server, thêm "h" (nv2) hoặc "b" (nv3):

    nv1 = lglinhlinhmot → nv2 = lglinhlinhmoth, nv3 = lglinhlinhmotb

nv1 tên cũ dài sẵn 15 ký tự (lgkhong...) thì đổi "khong" → "linh" trước khi
thêm chữ: lgkhongkhongmot → nv2 = lglinhlinhmoth. Không cắt cụt tên vì dễ
trùng (lgkhongkhongbay cắt → lgkhongkhongbah = nv2 của lgkhongkhongba).
Tên vẫn vượt 15 ký tự thì báo lỗi slot đó. Nếu tạo nv1 lỗi thì không tạo
nv2/nv3 (tránh nv2 thành nhân vật đầu tiên của tài khoản).

Protocol (đối chiếu src/Service.java createChar):
    cmd -28, byte -125, UTF name, byte gender, byte hairId

Ví dụ:
    python3 tao_nhan_vat.py accounts.csv                 # tạo đủ 3 nhân vật
    python3 tao_nhan_vat.py accounts.csv --so-nv 1       # chỉ tạo nv1 như cũ
    python3 tao_nhan_vat.py accounts.csv --gender 1 --workers 5
    python3 tao_nhan_vat.py accounts.csv --check         # đăng nhập xem tên nv1, in tên sẽ tạo
    python3 tao_nhan_vat.py accounts.csv --dry-run       # không đăng nhập, giả định nv1 theo số TK
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
MAX_CHARS = 3
# Đuôi tên theo slot: nv1 giữ nguyên, nv2 thêm "h", nv3 thêm "b".
SLOT_SUFFIXES = ("", "h", "b")
INGAME_COLUMNS = ("ingame", "ingame2", "ingame3")

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


def name_error(name: str):
    """Trả về lý do lỗi nếu tên ngoài giới hạn độ dài, ngược lại None."""
    if MIN_NAME_LENGTH <= len(name) <= MAX_NAME_LENGTH:
        return None
    return f"tên '{name}' dài {len(name)} ký tự, ngoài giới hạn {MIN_NAME_LENGTH}-{MAX_NAME_LENGTH}"


def suffix_stem(base: str) -> str:
    """Phần gốc để ghép h/b cho nv2/nv3.

    Tên nv1 cũ dạng lgkhong... có thể dài sẵn 15 ký tự, thêm 1 chữ là quá
    giới hạn → đổi "khong" thành "linh" như cách đặt tên mới:
        lgkhongkhongmot → lglinhlinhmot → nv2 lglinhlinhmoth
    """
    if len(base) + 1 <= MAX_NAME_LENGTH:
        return base
    return base.replace("khong", "linh")


def find_base_name(names: list[str]) -> str:
    """Tên nv1 trong danh sách: tên không phải nv2/nv3 của tên khác.

    Không phụ thuộc thứ tự server trả về danh sách nhân vật.
    """
    derived = {suffix_stem(name) + suffix for name in names for suffix in SLOT_SUFFIXES[1:]}
    for name in names:
        if name not in derived:
            return name
    return names[0]


def planned_names(base: str, count: int) -> list[str]:
    stem = suffix_stem(base)
    return [base] + [stem + suffix for suffix in SLOT_SUFFIXES[1:count]]


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
            # Một số server vào thẳng game sau khi tạo → phải đăng nhập lại
            # mới tạo tiếp được nhân vật khác.
            if command == self.CMD_SUB_COMMAND and data:
                if NSOReader(data).read_byte() == -127:
                    self.in_game = True
                    return True, "Đã vào game với nhân vật mới"
        return False, "Timeout chờ server phản hồi tạo nhân vật"


def open_session(account, password, args):
    """Kết nối + đăng nhập. Trả về (client, danh sách tên nhân vật)."""
    client = CreateCharClient(args.host, args.port)
    if not client.connect():
        client.disconnect()
        raise ConnectionError("không kết nối được")
    characters = client.login_any(account, password)
    if characters is None:
        client.disconnect()
        raise ConnectionError("đăng nhập thất bại")
    return client, [c[0] for c in characters]


def finish_result(result, notes, failed, created, index, total, account, mode=None):
    detail = "; ".join(notes)
    if failed:
        prefix, icon = "lỗi", "❌"
    elif mode:
        prefix, icon = mode, "🔎"
    elif created:
        prefix, icon = "ok", "✅"
    else:
        prefix, icon = "bỏ qua", "⏭️ "
    result["status"] = f"{prefix}: {detail}"
    result["created"] = created
    log(f"[{index}/{total}] {icon} {account}: {detail}")
    return result


def process_account(index, total, account, password, args):
    result = {"account": account, "password": password, "status": "", "created": 0}
    result.update({column: "" for column in INGAME_COLUMNS})
    try:
        default_base = args.prefix + ingame_from_account(account, args.pad)
    except ValueError as exc:
        result["status"] = f"lỗi: {exc}"
        log(f"[{index}/{total}] ❌ {exc}")
        return result

    notes, failed, created = [], False, 0

    if args.dry_run:
        # Không đăng nhập nên giả định nv1 đúng tên tính từ số tài khoản.
        # Tài khoản có nv1 tên cũ (vd lgkhong...) cần --check mới chính xác.
        for slot, name in enumerate(planned_names(default_base, args.so_nv), start=1):
            result[INGAME_COLUMNS[slot - 1]] = name
            error = name_error(name)
            failed = failed or bool(error)
            notes.append(f"nv{slot} {name}" + (f" lỗi: {error}" if error else ""))
        return finish_result(result, notes, failed, 0, index, total, account, mode="dry-run")

    client = None
    try:
        client, existing = open_session(account, password, args)
        base = find_base_name(existing) if existing else default_base

        for slot, name in enumerate(planned_names(base, args.so_nv), start=1):
            result[INGAME_COLUMNS[slot - 1]] = name
            if name in existing:
                notes.append(f"nv{slot} {name} có sẵn")
                continue
            if len(existing) >= args.so_nv:
                notes.append(f"nv{slot} bỏ qua: đã đủ {len(existing)} nhân vật ({', '.join(existing)})")
                continue
            error = name_error(name)
            if error:
                notes.append(f"nv{slot} lỗi: {error}")
                failed = True
                continue
            if args.check:
                notes.append(f"nv{slot} {name} sẽ tạo")
                continue

            if client is None:
                # Lần tạo trước đã vào game hoặc mất kết nối → đăng nhập lại.
                time.sleep(args.create_delay)
                client, existing = open_session(account, password, args)
                if name in existing:
                    notes.append(f"nv{slot} {name} có sẵn")
                    continue
            elif created:
                time.sleep(args.create_delay)

            gender = args.gender if args.gender is not None else random.randint(0, 1)
            hair = random.choice(HAIR_IDS[gender])
            ok, detail = client.create_character(name, gender, hair)
            if ok:
                created += 1
                notes.append(f"nv{slot} {name} tạo mới")
                listed = [c[0] for c in (client.characters or [])]
                existing = listed if name in listed else existing + [name]
                if getattr(client, "in_game", False):
                    client.disconnect()
                    client = None
                continue

            notes.append(f"nv{slot} {name} lỗi: {detail}")
            failed = True
            if slot == 1:
                # Không có nv1 thì nv2 sẽ thành nhân vật đầu → lần sau đoán sai tên gốc.
                break
            client.disconnect()
            client = None

        return finish_result(result, notes, failed, created, index, total, account,
                             mode="check" if args.check else None)
    except Exception as exc:
        notes.append(str(exc))
        return finish_result(result, notes, True, created, index, total, account)
    finally:
        if client is not None:
            client.disconnect()
        # Nghỉ trong chính luồng này, không chặn các luồng khác.
        if args.delay > 0:
            time.sleep(args.delay)


def write_results(path: Path, rows):
    with path.open("w", encoding="utf-8-sig", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=["account", "password", *INGAME_COLUMNS, "status"],
                                extrasaction="ignore")
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
    parser.add_argument("--so-nv", type=int, choices=range(1, MAX_CHARS + 1), default=MAX_CHARS,
                        help="Số nhân vật cần có trên mỗi tài khoản (mặc định 3: nv1, nv1+h, nv1+b)")
    parser.add_argument("--create-delay", type=float, default=1.5,
                        help="Giây nghỉ giữa 2 lần tạo nhân vật trên cùng tài khoản")
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--dry-run", action="store_true",
                      help="Không đăng nhập, giả định nv1 theo số tài khoản, chỉ in tên sẽ tạo")
    mode.add_argument("--check", action="store_true",
                      help="Đăng nhập đọc tên nv1 thật, in tên sẽ tạo + lỗi độ dài, không tạo")
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
    new_chars = sum(row.get("created", 0) for row in results)
    if args.dry_run or args.check:
        print(f"\n🔎 Kiểm tra: {len(results) - failed} TK ổn | ❌ Lỗi: {failed}")
    else:
        print(f"\n✅ TK có tạo mới: {created} ({new_chars} nhân vật) | ⏭️ Bỏ qua: {skipped} | ❌ Lỗi: {failed}")
    print(f"📄 Kết quả: {args.output}")
    return 0 if failed == 0 else 2


if __name__ == "__main__":
    raise SystemExit(main())
