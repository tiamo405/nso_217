#!/usr/bin/env python3
"""Đăng nhập lần lượt các tài khoản/nhân vật và bán các vật phẩm trong hành trang
có template_id trùng với danh sách trong file txt (mặc định delllllllllll.txt).

Logic chuẩn Java Client NSO 217:
  - Bán vật phẩm trong hành trang bằng CMD 14 (saleItem):
      Gửi: CMD 14, writeByte(slot), nếu quantity > 1: writeInt(quantity)
      Nhận: CMD 14 xác nhận [slot:1][yen:4][sold_qty:2]
  - File txt hỗ trợ ngăn cách bằng dấu phẩy, chấm phẩy, xuống dòng, hoặc khoảng trắng.

Ví dụ sử dụng:
    python3 item_sell_client.py account-del.csv
    python3 item_sell_client.py account.csv --items-file delllllllllll.txt
    python3 item_sell_client.py account.csv --item-ids 761,736,403
    python3 item_sell_client.py account.csv --dry-run
"""

import argparse
import csv
import logging
import os
import random
import re
import socket
import struct
import sys
import time
from dataclasses import dataclass
from io import BytesIO
from pathlib import Path
from typing import Dict, List, Optional, Sequence, Set, Tuple


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-del.csv"
DEFAULT_ITEMS_FILE = ROOT_DIR / "delllllllllll.txt"
DEFAULT_HOST = "Nsotk1.nsotk.online"
DEFAULT_PORT = 14444
DEFAULT_CLIENT_VERSION = "2.1.7"

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s",
    datefmt="%H:%M:%S",
    stream=sys.stdout,
)
logger = logging.getLogger("ItemSellClient")


# ─────────────────────────────────────────────────────────────────────────────
# Binary Message & Reader / Writer (giao thức J2ME / NSO)
# ─────────────────────────────────────────────────────────────────────────────

class NSOMessage:
    """Tạo binary message theo protocol NSO."""

    def __init__(self, command: int):
        self.command = command
        self.buffer = BytesIO()

    def write_byte(self, value: int):
        self.buffer.write(struct.pack("b", value))

    def write_ubyte(self, value: int):
        self.buffer.write(struct.pack("B", value))

    def write_short(self, value: int):
        self.buffer.write(struct.pack(">h", value))

    def write_int(self, value: int):
        self.buffer.write(struct.pack(">i", value))

    def write_long(self, value: int):
        self.buffer.write(struct.pack(">q", value))

    def write_boolean(self, value: bool):
        self.buffer.write(struct.pack("B", 1 if value else 0))

    def write_utf(self, value: str):
        encoded = value.encode("utf-8")
        self.write_short(len(encoded))
        self.buffer.write(encoded)

    def packet(self) -> bytes:
        data = self.buffer.getvalue()
        return struct.pack("b", self.command) + struct.pack(">H", len(data)) + data


class NSOReader:
    """Đọc binary stream từ server NSO."""

    def __init__(self, data: bytes):
        self.buffer = BytesIO(data)

    def _read_exact(self, size: int) -> bytes:
        data = self.buffer.read(size)
        if len(data) != size:
            raise EOFError(f"Thiếu dữ liệu: cần {size}, còn {len(data)} byte")
        return data

    def read_byte(self) -> int:
        return struct.unpack("b", self._read_exact(1))[0]

    def read_ubyte(self) -> int:
        return self._read_exact(1)[0]

    def read_short(self) -> int:
        return struct.unpack(">h", self._read_exact(2))[0]

    def read_ushort(self) -> int:
        return struct.unpack(">H", self._read_exact(2))[0]

    def read_int(self) -> int:
        return struct.unpack(">i", self._read_exact(4))[0]

    def read_long(self) -> int:
        return struct.unpack(">q", self._read_exact(8))[0]

    def read_boolean(self) -> bool:
        return self.read_ubyte() != 0

    def read_utf(self) -> str:
        size = self.read_short()
        if size < 0:
            raise ValueError(f"Độ dài UTF âm: {size}")
        return self._read_exact(size).decode("utf-8", errors="replace")

    def remaining(self) -> int:
        pos = self.buffer.tell()
        self.buffer.seek(0, 2)
        end = self.buffer.tell()
        self.buffer.seek(pos)
        return end - pos


# ─────────────────────────────────────────────────────────────────────────────
# Models & Cấu hình Item
# ─────────────────────────────────────────────────────────────────────────────

@dataclass
class ItemTemplate:
    id: int
    name: str
    type: int
    gender: int
    level: int
    part: int
    is_up_to_up: bool

    def is_type_body(self) -> bool:
        return 0 <= self.type <= 15

    def is_type_mounts(self) -> bool:
        return 29 <= self.type <= 33

    def is_type_ngoc_kham(self) -> bool:
        return self.type == 34


@dataclass
class BagItem:
    index: int
    template_id: int
    name: str
    quantity: int
    is_lock: bool
    upgrade: int = 0
    is_expires: bool = False


@dataclass
class SellResult:
    matched: int = 0
    sold: int = 0
    failed: int = 0


def parse_item_ids(content: str) -> Set[int]:
    """Parse chuỗi chứa các item id ngăn cách bởi ;, phẩy, dấu cách hoặc xuống dòng."""
    ids: Set[int] = set()
    for token in re.split(r"[\s,;]+", content):
        token = token.strip()
        if not token or token.startswith("#"):
            continue
        try:
            item_id = int(token)
            if 0 <= item_id <= 32767:
                ids.add(item_id)
        except ValueError:
            continue
    return ids


def read_item_ids(path: Path | str) -> Set[int]:
    """Đọc danh sách item ID từ file txt."""
    with open(path, "r", encoding="utf-8") as handle:
        return parse_item_ids(handle.read())


def read_accounts(csv_path: Path | str) -> List[Tuple[str, str]]:
    """Đọc file tài khoản csv (username,password)."""
    accounts: List[Tuple[str, str]] = []
    with open(csv_path, "r", encoding="utf-8-sig", newline="") as handle:
        for line_number, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if row[0].strip().lower() in {"username", "user", "acc", "tai_khoan"}:
                continue
            if len(row) < 2 or not row[0].strip() or not row[1].strip():
                logger.warning("Bỏ qua dòng CSV %s vì thiếu username/password", line_number)
                continue
            accounts.append((row[0].strip(), row[1].strip()))
    return accounts


# Bộ nhớ đệm ItemTemplate dùng chung giữa các phiên kết nối
_GLOBAL_ITEM_TEMPLATES: Dict[int, ItemTemplate] = {}


# ─────────────────────────────────────────────────────────────────────────────
# Socket Game Client (J2ME Client NSO 217)
# ─────────────────────────────────────────────────────────────────────────────

class NSOClient:
    CMD_KEY_EXCHANGE = -27
    CMD_NOT_MAP = -28
    CMD_NOT_LOGIN = -29
    CMD_SUB_COMMAND = -30
    CMD_SERVER_ERROR = -26
    CMD_SALE_ITEM = 14

    CMD_SET_CLIENT = -125
    CMD_LOGIN = -127
    CMD_SELECT_CHAR = -126

    def __init__(self, host: str, port: int, timeout: float = 12.0):
        self.host = host
        self.port = port
        self.timeout = timeout
        self.sock: Optional[socket.socket] = None
        self.key: Optional[List[int]] = None
        self.key_read_pos = 0
        self.key_write_pos = 0
        self.characters: List[str] = []
        self.bag: List[Optional[BagItem]] = []
        self.last_server_message = ""
        self.connected = False

    def connect(self) -> bool:
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.settimeout(self.timeout)
            self.sock.connect((self.host, self.port))
            self._exchange_key()
            self.connected = bool(self.key)
            return self.connected
        except Exception as exc:
            logger.error("Không thể kết nối tới %s:%s: %s", self.host, self.port, exc)
            self.disconnect()
            return False

    def disconnect(self) -> None:
        if self.sock is not None:
            try:
                self.sock.close()
            except OSError:
                pass
        self.sock = None
        self.connected = False

    def _recv_exact(self, size: int) -> bytes:
        data = bytearray()
        while len(data) < size:
            chunk = self.sock.recv(size - len(data))
            if not chunk:
                raise ConnectionError(f"Socket đóng (nhận {len(data)}/{size} byte)")
            data.extend(chunk)
        return bytes(data)

    def _encrypt_byte(self, val: int) -> int:
        r = (self.key[self.key_write_pos] ^ val) & 0xFF
        self.key_write_pos = (self.key_write_pos + 1) % len(self.key)
        return r

    def _decrypt_byte(self, val: int) -> int:
        r = (self.key[self.key_read_pos] ^ val) & 0xFF
        self.key_read_pos = (self.key_read_pos + 1) % len(self.key)
        return r

    def _exchange_key(self) -> None:
        self.sock.sendall(struct.pack("b", self.CMD_KEY_EXCHANGE) + b"\x00\x00")
        cmd = struct.unpack("b", self._recv_exact(1))[0]
        if cmd != self.CMD_KEY_EXCHANGE:
            raise ConnectionError(f"Sai packet key exchange: cmd={cmd}")
        size = struct.unpack(">H", self._recv_exact(2))[0]
        payload = self._recv_exact(size)
        if not payload or payload[0] > len(payload) - 1:
            raise ConnectionError("Key exchange trả payload không hợp lệ")
        raw_key = list(payload[1:1 + payload[0]])
        for i in range(len(raw_key) - 1):
            raw_key[i + 1] ^= raw_key[i]
        self.key = raw_key
        self.key_read_pos = 0
        self.key_write_pos = 0

    def send(self, message: NSOMessage) -> None:
        raw_pkt = message.packet()
        encrypted = bytes(self._encrypt_byte(b) for b in raw_pkt)
        self.sock.sendall(encrypted)

    def receive(self, timeout: Optional[float] = None) -> Tuple[Optional[int], Optional[bytes]]:
        self.sock.settimeout(timeout or self.timeout)
        try:
            cmd = self._decrypt_byte(self._recv_exact(1)[0])
            if cmd == 224:  # 0xE0: 4-byte length
                cmd = self._decrypt_byte(self._recv_exact(1)[0])
                len_bytes = bytes(self._decrypt_byte(b) for b in self._recv_exact(4))
                size = int.from_bytes(len_bytes, "big")
            else:
                lb = self._recv_exact(2)
                size = (self._decrypt_byte(lb[0]) << 8) | self._decrypt_byte(lb[1])
            encrypted = self._recv_exact(size) if size else b""
            payload = bytes(self._decrypt_byte(b) for b in encrypted)
            if cmd >= 128:
                cmd -= 256
            return cmd, payload
        except (socket.timeout, TimeoutError):
            return None, None
        except Exception:
            self.connected = False
            return None, None

    def _send_not_map(self, sub_cmd: int) -> None:
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(sub_cmd)
        self.send(msg)

    def _set_client_type(self) -> None:
        """Gửi clientType=1 (J2ME) đúng như Service.java trong client NSO 217."""
        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_SET_CLIENT)
        msg.write_byte(1)           # CLIENT_TYPE: 1 (J2ME)
        msg.write_byte(1)           # zoomLevel
        msg.write_boolean(True)     # isGPRS
        msg.write_int(240)          # width
        msg.write_int(320)          # height
        msg.write_boolean(True)     # isQwerty
        msg.write_boolean(True)     # isTouch
        msg.write_utf("Nokia6300/2.0 (06.01) Profile/MIDP-2.0 Configuration/CLDC-1.1")
        msg.write_byte(0)
        msg.write_int(0)
        msg.write_byte(0)           # languageID
        msg.write_int(0)            # userProvider
        msg.write_utf("0")          # clientAgent
        self.send(msg)

    def _parse_item_templates(self, data: bytes) -> None:
        global _GLOBAL_ITEM_TEMPLATES
        try:
            r = NSOReader(data)
            r.read_byte()  # sub -119
            r.read_byte()  # version
            opt_count = r.read_ubyte()
            for _ in range(opt_count):
                r.read_utf()
                r.read_byte()
            tmpl_count = r.read_short()
            templates = {}
            for tid in range(tmpl_count):
                itype = r.read_byte()
                gender = r.read_byte()
                name = r.read_utf()
                r.read_utf()  # desc
                level = r.read_byte()
                r.read_short()  # icon
                part = r.read_short()
                up = r.read_boolean()
                templates[tid] = ItemTemplate(tid, name, itype, gender, level, part, up)
            _GLOBAL_ITEM_TEMPLATES.clear()
            _GLOBAL_ITEM_TEMPLATES.update(templates)
            logger.debug("Đã tải %d ItemTemplates từ server", len(_GLOBAL_ITEM_TEMPLATES))
        except Exception as exc:
            logger.warning("Không phân tích được ItemTemplates: %s", exc)

    def login(self, username: str, password: str, login_timeout: float = 15.0) -> bool:
        """Đăng nhập tài khoản và lấy danh sách nhân vật."""
        for attempt in range(1, 4):
            if not self.connected and not self.connect():
                time.sleep(3.0)
                continue

            self._set_client_type()
            msg = NSOMessage(self.CMD_NOT_LOGIN)
            msg.write_byte(self.CMD_LOGIN)
            msg.write_utf(username)
            msg.write_utf(password)
            msg.write_utf(DEFAULT_CLIENT_VERSION)
            msg.write_utf("")
            msg.write_utf("")
            msg.write_utf("".join(str(random.randint(0, 8)) for _ in range(12)))
            msg.write_byte(0)
            msg.write_utf("VALID_CLIENT_KEY")
            self.send(msg)

            deadline = time.time() + login_timeout
            while self.connected and time.time() < deadline:
                cmd, data = self.receive(timeout=min(2.0, max(0.2, deadline - time.time())))
                if cmd is None:
                    continue
                if cmd == self.CMD_SERVER_ERROR and data:
                    try:
                        self.last_server_message = NSOReader(data).read_utf()
                    except Exception:
                        self.last_server_message = ""
                    logger.warning("Server thông báo khi đăng nhập %s: %s", username, self.last_server_message)
                    return False

                if cmd == self.CMD_NOT_MAP and data:
                    r = NSOReader(data)
                    sub = r.read_byte()
                    if sub == -123:
                        if not _GLOBAL_ITEM_TEMPLATES:
                            for req in (-122, -121, -120, -119):
                                self._send_not_map(req)
                        else:
                            self._send_not_map(-101)
                    elif sub == -119:
                        self._parse_item_templates(data)
                        self._send_not_map(-101)
                    elif sub in (-122, -121, -120):
                        pass
                    elif sub == -126:
                        count = r.read_byte()
                        self.characters = []
                        for _ in range(count):
                            r.read_byte()  # gender
                            char_name = r.read_utf()
                            r.read_utf()   # class
                            r.read_ubyte() # level
                            for _ in range(4):
                                r.read_short()
                            self.characters.append(char_name)
                        return True

            self.disconnect()
            if attempt < 3:
                delay = 11.0 if "quá nhanh" in self.last_server_message.lower() else 3.0
                logger.warning(
                    "Tài khoản %s chưa vào được danh sách nhân vật, thử lại sau %.0fs (lần %d/3)",
                    username, delay, attempt + 1
                )
                time.sleep(delay)

        return False

    def select_character(self, character_name: str, timeout: float = 15.0) -> bool:
        """Chọn nhân vật và đọc thông tin hành trang từ sub -127."""
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(self.CMD_SELECT_CHAR)
        msg.write_utf(character_name)
        self.send(msg)

        deadline = time.time() + timeout
        while self.connected and time.time() < deadline:
            cmd, data = self.receive(timeout=min(2.0, max(0.2, deadline - time.time())))
            if cmd is None:
                continue
            if cmd == self.CMD_SERVER_ERROR and data:
                try:
                    self.last_server_message = NSOReader(data).read_utf()
                except Exception:
                    pass
                logger.error("Lỗi khi chọn nhân vật %s: %s", character_name, self.last_server_message)
                return False
            if cmd == self.CMD_SUB_COMMAND and data:
                r = NSOReader(data)
                sub = r.read_byte()
                if sub == -127:
                    return self._parse_character_info_and_bag(r)
        return False

    def _parse_character_info_and_bag(self, r: NSOReader) -> bool:
        """Parse đúng layout sub -127 của J2ME Client NSO 217."""
        try:
            r.read_int()       # charID
            clan = r.read_utf()
            if clan != "":
                r.read_byte()  # ctypeClan
            r.read_byte()      # ctaskId
            r.read_byte()      # cgender
            r.read_short()     # head
            r.read_byte()      # cspeed
            r.read_utf()       # cName
            r.read_byte()      # cPk
            r.read_byte()      # cTypePk
            r.read_int()       # cMaxHP
            r.read_int()       # cHP
            r.read_int()       # cMaxMP
            r.read_int()       # cMP
            r.read_long()      # cEXP
            r.read_long()      # cExpDown
            r.read_short()     # eff5BuffHp
            r.read_short()     # eff5BuffMp
            r.read_byte()      # nClass
            r.read_short()     # pPoint
            r.read_short()     # potential[0]
            r.read_short()     # potential[1]
            r.read_int()       # potential[2]
            r.read_int()       # potential[3]
            r.read_short()     # sPoint

            skill_count = r.read_byte()
            for _ in range(skill_count):
                r.read_short()

            r.read_int()       # xu
            r.read_int()       # yen
            r.read_int()       # luong

            bag_len = r.read_ubyte()
            self.bag = [None] * bag_len

            for slot in range(bag_len):
                template_id = r.read_short()
                if template_id != -1:
                    tmpl = _GLOBAL_ITEM_TEMPLATES.get(template_id)
                    is_lock = r.read_boolean()
                    upgrade = 0
                    if tmpl and (tmpl.is_type_body() or tmpl.is_type_mounts() or tmpl.is_type_ngoc_kham()):
                        upgrade = r.read_byte()
                    is_expires = r.read_boolean()
                    quantity = r.read_ushort()
                    name = tmpl.name if tmpl else f"Item {template_id}"
                    self.bag[slot] = BagItem(
                        index=slot,
                        template_id=template_id,
                        name=name,
                        quantity=max(1, quantity),
                        is_lock=is_lock,
                        upgrade=upgrade,
                        is_expires=is_expires,
                    )
            return True
        except Exception as exc:
            logger.error("Lỗi phân tích hành trang nhân vật: %s", exc)
            return False

    def sell_item(self, slot: int, quantity: int = 1, timeout: float = 3.0) -> bool:
        """Gửi CMD 14 bán/xóa item trong hành trang theo đúng Service.java.

        Java:
            var3 = new Message((byte) 14)
            var3.writer().writeByte(slot)
            if (quantity > 1) {
                var3.writer().writeInt(quantity)
            }
        """
        msg = NSOMessage(self.CMD_SALE_ITEM)
        msg.write_byte(slot)
        if quantity > 1:
            msg.write_int(quantity)
        self.send(msg)

        deadline = time.time() + timeout
        while self.connected and time.time() < deadline:
            cmd, data = self.receive(timeout=min(1.0, max(0.2, deadline - time.time())))
            if cmd is None:
                continue
            if cmd == self.CMD_SALE_ITEM and data:
                r = NSOReader(data)
                confirmed_slot = r.read_ubyte()
                if confirmed_slot == slot:
                    if 0 <= slot < len(self.bag) and self.bag[slot] is not None:
                        # Server có thể trừ dần số lượng hoặc xóa luôn
                        sold_qty = r.read_short() if r.remaining() >= 2 else quantity
                        item = self.bag[slot]
                        item.quantity -= sold_qty
                        if item.quantity <= 0:
                            self.bag[slot] = None
                    return True
            elif cmd == self.CMD_SERVER_ERROR and data:
                try:
                    self.last_server_message = NSOReader(data).read_utf()
                    logger.warning("Server từ chối bán item slot %d: %s", slot, self.last_server_message)
                except Exception:
                    pass
                return False
        return False


# ─────────────────────────────────────────────────────────────────────────────
# Logic xử lý bán đồ
# ─────────────────────────────────────────────────────────────────────────────

def sell_bag_items_for_character(
    client: NSOClient,
    username: str,
    character_name: str,
    item_ids: Set[int],
    args: argparse.Namespace,
) -> SellResult:
    result = SellResult()
    bag = client.bag

    targets: List[BagItem] = [
        item for item in bag
        if item is not None and item.template_id in item_ids
    ]
    result.matched = len(targets)

    logger.info(
        "Hành trang %s/%s: có %d slot, tìm thấy %d vật phẩm cần bán",
        username, character_name, len(bag), len(targets)
    )

    # Bán từ slot cao xuống slot thấp
    for item in sorted(targets, key=lambda it: it.index, reverse=True):
        if args.dry_run:
            logger.info(
                "  [DRY-RUN] Sẽ bán slot=%02d | id=%-5d | %s | số lượng=%d | lock=%s",
                item.index, item.template_id, item.name, item.quantity, item.is_lock
            )
            continue

        logger.info(
            "  Đang bán slot=%02d | id=%-5d | %s | số lượng=%d | lock=%s",
            item.index, item.template_id, item.name, item.quantity, item.is_lock
        )

        success = client.sell_item(item.index, item.quantity, timeout=args.confirm_timeout)
        if success:
            result.sold += 1
            logger.info("  ✅ Đã bán thành công: slot=%02d | id=%d (%s)", item.index, item.template_id, item.name)
        else:
            result.failed += 1
            logger.warning("  ❌ Bán thất bại: slot=%02d | id=%d (%s)", item.index, item.template_id, item.name)

        if args.action_delay > 0:
            time.sleep(args.action_delay)

    return result


def process_account(
    username: str,
    password: str,
    item_ids: Set[int],
    args: argparse.Namespace,
) -> Tuple[int, SellResult]:
    """Xử lý tất cả nhân vật của một tài khoản."""
    client = NSOClient(args.host, args.port, timeout=args.socket_timeout)
    total_result = SellResult()
    characters_processed = 0

    try:
        logger.info("Đăng nhập tài khoản: %s", username)
        if not client.login(username, password, login_timeout=args.login_timeout):
            logger.error("Không thể đăng nhập tài khoản %s", username)
            return 0, total_result

        char_names = list(client.characters)
        if args.max_characters > 0:
            char_names = char_names[:args.max_characters]

        logger.info("Tài khoản %s có %d nhân vật: %s", username, len(char_names), char_names)

        for idx, char_name in enumerate(char_names):
            # Nếu không phải nhân vật đầu tiên trên cùng một session, ta cần kết nối lại
            # vì NSO protocol client J2ME thường reset socket khi đổi nhân vật
            if idx > 0:
                client.disconnect()
                time.sleep(args.character_delay)
                client = NSOClient(args.host, args.port, timeout=args.socket_timeout)
                if not client.login(username, password, login_timeout=args.login_timeout):
                    logger.error("Đăng nhập lại thất bại cho nhân vật %s", char_name)
                    continue

            logger.info("➡️ Vào nhân vật: %s (%d/%d)", char_name, idx + 1, len(char_names))
            if not client.select_character(char_name, timeout=args.game_timeout):
                logger.error("Không thể vào nhân vật %s", char_name)
                continue

            characters_processed += 1
            if args.ready_delay > 0:
                time.sleep(args.ready_delay)

            char_res = sell_bag_items_for_character(client, username, char_name, item_ids, args)
            total_result.matched += char_res.matched
            total_result.sold += char_res.sold
            total_result.failed += char_res.failed

    finally:
        client.disconnect()

    return characters_processed, total_result


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Đăng nhập tài khoản/nhân vật và bán các vật phẩm trong hành trang có ID trùng với file txt."
    )
    parser.add_argument(
        "csv_file",
        nargs="?",
        default=str(DEFAULT_CSV),
        help=f"File CSV tài khoản (username,password), mặc định: {DEFAULT_CSV.name}",
    )
    item_group = parser.add_mutually_exclusive_group()
    item_group.add_argument(
        "--items-file",
        default=str(DEFAULT_ITEMS_FILE),
        help=f"File chứa danh sách Item ID cần bán, mặc định: {DEFAULT_ITEMS_FILE.name}",
    )
    item_group.add_argument(
        "--item-ids",
        help="Danh sách Item ID ngăn cách bởi dấu phẩy hoặc chấm phẩy (ví dụ: 761,736,403)",
    )
    parser.add_argument("--host", default=DEFAULT_HOST, help=f"Địa chỉ server (mặc định: {DEFAULT_HOST})")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT, help=f"Cổng server (mặc định: {DEFAULT_PORT})")
    parser.add_argument("--socket-timeout", type=float, default=12.0)
    parser.add_argument("--login-timeout", type=float, default=15.0)
    parser.add_argument("--game-timeout", type=float, default=15.0)
    parser.add_argument("--confirm-timeout", type=float, default=3.0)
    parser.add_argument("--action-delay", type=float, default=0.4, help="Thời gian nghỉ giữa mỗi lần bán (giây)")
    parser.add_argument("--character-delay", type=float, default=5.0, help="Thời gian nghỉ giữa các nhân vật (giây)")
    parser.add_argument("--account-delay", type=float, default=11.0, help="Thời gian nghỉ giữa các tài khoản (giây)")
    parser.add_argument("--ready_delay", type=float, default=1.0, help="Thời gian chờ sau khi vào map (giây)")
    parser.add_argument("--max-characters", type=int, default=0, help="Giới hạn số nhân vật mỗi nick (0: tất cả)")
    parser.add_argument("--dry-run", action="store_true", help="Chỉ quét và xem danh sách item, không gửi lệnh bán")
    return parser


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)

    if not os.path.isfile(args.csv_file):
        logger.error("Không tìm thấy file CSV: %s", args.csv_file)
        return 2

    # Đọc danh sách Item ID
    try:
        if args.item_ids:
            item_ids = parse_item_ids(args.item_ids)
        else:
            if not os.path.isfile(args.items_file):
                logger.error("Không tìm thấy file danh sách item: %s", args.items_file)
                return 2
            item_ids = read_item_ids(args.items_file)
    except Exception as exc:
        logger.error("Lỗi khi đọc danh sách Item ID: %s", exc)
        return 2

    if not item_ids:
        logger.error("Danh sách Item ID cần bán trống!")
        return 2

    accounts = read_accounts(args.csv_file)
    if not accounts:
        logger.error("File CSV không có tài khoản hợp lệ nào")
        return 2

    logger.info(
        "=== BẮT ĐẦU BÁN ĐỒ HÀNH TRANG: %d tài khoản, %d Item ID cần bán, Chế độ=%s ===",
        len(accounts), len(item_ids), "DRY-RUN" if args.dry_run else "BÁN THẬT"
    )

    total_acc_done = 0
    total_chars_done = 0
    grand_result = SellResult()

    for acc_idx, (username, password) in enumerate(accounts, start=1):
        logger.info(
            "--------------------------------------------------\n"
            "[%d/%d] Xử lý tài khoản: %s", acc_idx, len(accounts), username
        )
        chars_count, res = process_account(username, password, item_ids, args)
        total_chars_done += chars_count
        grand_result.matched += res.matched
        grand_result.sold += res.sold
        grand_result.failed += res.failed
        if chars_count > 0:
            total_acc_done += 1

        if acc_idx < len(accounts) and args.account_delay > 0:
            logger.info("Nghỉ %.0f giây trước tài khoản tiếp theo...", args.account_delay)
            time.sleep(args.account_delay)

    logger.info(
        "=== HOÀN TẤT: %d/%d tài khoản thành công | %d nhân vật | Khớp: %d | Đã bán: %d | Thất bại: %d ===",
        total_acc_done, len(accounts), total_chars_done,
        grand_result.matched, grand_result.sold, grand_result.failed
    )
    return 1 if grand_result.failed > 0 else 0


if __name__ == "__main__":
    raise SystemExit(main())
