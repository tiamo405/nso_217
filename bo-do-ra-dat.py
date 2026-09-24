#!/usr/bin/env python3
"""Dọn item, dùng item cấu hình rồi bỏ vật phẩm không khóa ra đất.

Luồng xử lý mỗi tài khoản:
  1. Đăng nhập và chọn nhân vật đầu tiên.
  2. Bán/xóa item có ID trong ``delllllllllll.txt``.
  3. Đi tới map 22, chuyển sang khu 80.
  4. Sử dụng từng item trong ``item_open.txt`` bằng command ``11``.
  5. Sau mỗi lần sử dụng, bỏ item không khóa ra đất rồi mở item kế tiếp.

Lưu ý quan trọng:
  - ``-12`` là lệnh bỏ item ra đất, không phải lệnh bán.
  - ``14`` là lệnh bán/xóa item trong ``delllllllllll.txt``.
  - Một lần throw theo slot sẽ bỏ cả chồng item ở slot đó, giống client Java.

Mặc định chạy trên ``account-bodo.csv``:

    python3 bo-do-ra-dat.py

Có thể xem trước danh sách item mà không bỏ item:

    python3 bo-do-ra-dat.py --dry-run
"""

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import importlib.util
import sys
import threading
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Optional, Sequence

from nhanexp_and_doiyenquaxu import MapState, OfflineExpClient, read_accounts


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-bodo.csv"
DEFAULT_SELL_ITEMS_FILE = ROOT_DIR / "delllllllllll.txt"
DEFAULT_OPEN_ITEMS_FILE = ROOT_DIR / "item_open.txt"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
TARGET_MAP_ID = 22
TARGET_ZONE_ID = 85
DEFAULT_DROP_DELAY = 1.0 # Thời gian nghỉ giữa mỗi item bỏ ra đất (giây)
USE_SETTLE_DELAY = 0.8 # Thời gian chờ server gửi packet quantity sau khi dùng item (giây)
DEFAULT_DROP_RETRIES = 3
DEFAULT_DROP_RETRY_DELAY = 1.0


def _load_inventory_module():
    """Nạp parser hành trang đã dùng ổn định trong nhangiftcode.py."""
    source = ROOT_DIR / "del-item-hanhtrang.py"
    spec = importlib.util.spec_from_file_location("drop_inventory_logic", source)
    if spec is None or spec.loader is None:
        raise ImportError(f"Không thể nạp parser hành trang: {source}")
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


_INVENTORY_MODULE = _load_inventory_module()
_INVENTORY_TEMPLATE_LOCK = threading.RLock()

# del-item-hanhtrang.py giữ ItemTemplate trong cache module dùng chung. Khi
# chạy nhiều worker, khóa cả lúc cập nhật template và lúc đọc bag để một
# worker không đọc đúng lúc worker khác đang clear/update cache.
_ORIGINAL_PARSE_ITEM_TEMPLATES = _INVENTORY_MODULE.NSOClient._parse_item_templates


def _threadsafe_parse_item_templates(client, data):
    with _INVENTORY_TEMPLATE_LOCK:
        return _ORIGINAL_PARSE_ITEM_TEMPLATES(client, data)


_INVENTORY_MODULE.NSOClient._parse_item_templates = _threadsafe_parse_item_templates


@dataclass
class DropResult:
    processed: bool = False
    sell_matched: int = 0
    sold: int = 0
    sell_failed: int = 0
    open_matched: int = 0
    used: int = 0
    use_failed: int = 0
    matched: int = 0
    dropped: int = 0
    failed: int = 0


class DropItemClient(_INVENTORY_MODULE.NSOClient):
    """Client dùng parser hành trang chuẩn và bổ sung thao tác map."""

    CMD_DROP_ITEM = -12
    CMD_USE_ITEM = 11
    CMD_BAG_ITEM_ADD = 8
    CMD_BAG_ITEM_STACK_ADD = 9
    CMD_BAG_ITEM_QUANTITY = 7
    CMD_BAG_ITEM_REMOVE = 10
    CMD_ITEM_USE_QUANTITY = 18
    CMD_CHANGE_ZONE = 28
    CMD_MAP_LOAD = -18
    CMD_MOVE = 1
    CMD_CHANGE_MAP = -17

    def __init__(self, host: str, port: int):
        super().__init__(host, port)

        # Tái sử dụng parser map của client mobile; protocol packet map giống
        # nhau, còn parser hành trang lấy từ del-item-hanhtrang.py để tránh
        # lệch layout giữa client 2.1.7 và server hiện tại.
        self.map_state = MapState()

    _parse_map = staticmethod(OfflineExpClient._parse_map)
    move_character = OfflineExpClient.move_character
    _request_change_map = OfflineExpClient._request_change_map
    _wait_for_map_change = OfflineExpClient._wait_for_map_change
    move_to_map = OfflineExpClient.move_to_map
    drain = OfflineExpClient.drain

    @staticmethod
    def _read_server_error(data: bytes) -> str:
        try:
            return _INVENTORY_MODULE.NSOReader(data).read_utf()
        except Exception:
            return data.hex()

    def select_character_and_load_map(self, character_name: str) -> bool:
        """Chọn nhân vật đầu tiên, chờ cả thông tin bag và map load."""
        if character_name not in self.characters:
            print(f"    ❌ Không tìm thấy nhân vật {character_name}")
            return False

        self.bag = []
        message = _INVENTORY_MODULE.NSOMessage(self.CMD_NOT_MAP)
        message.write_byte(self.CMD_SELECT_CHAR)
        message.write_utf(character_name)
        self.send(message)

        game_ready = False
        deadline = time.time() + 25.0
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                continue
            if command == self.CMD_SERVER_ERROR:
                print(f"    ❌ Chọn nhân vật: {self._read_server_error(data)}")
                return False
            if command == self.CMD_SUB_COMMAND and data:
                reader = _INVENTORY_MODULE.NSOReader(data)
                if reader.read_byte() == -127:
                    with _INVENTORY_TEMPLATE_LOCK:
                        if not _INVENTORY_MODULE.NSOClient._parse_character_info_and_bag(
                            self, reader
                        ):
                            return False
                    game_ready = True
            elif command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)

            if game_ready and self.map_state.map_id >= 0:
                return True

        print("    ❌ Timeout khi chờ thông tin nhân vật/map")
        return False

    def change_zone(
        self,
        zone_id: int,
        map_id: int,
        timeout: float = 12.0,
        item_slot: int = -1,
    ) -> bool:
        """Chuyển khu bằng command 28 và xác nhận lại map packet.

        Payload Java là ``(zoneId, itemSlot)``. ``itemSlot=-1`` là đổi khu
        bình thường; chỉ các map đặc biệt dùng item dịch chuyển mới truyền
        slot item.
        """
        if self.map_state.zone_id == zone_id:
            return True

        message = _INVENTORY_MODULE.NSOMessage(self.CMD_CHANGE_ZONE)
        message.write_byte(zone_id)
        message.write_byte(item_slot)
        self.send(message)

        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                continue
            if command == self.CMD_SERVER_ERROR:
                print(f"    ❌ Chuyển khu: {self._read_server_error(data)}")
                return False
            if command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)
                if (
                    self.map_state.map_id == map_id
                    and self.map_state.zone_id == zone_id
                ):
                    return True

        print(f"    ❌ Timeout xác nhận map {map_id}, khu {zone_id}")
        return False

    def throw_item(self, slot: int, timeout: float = 8.0) -> bool:
        """Bỏ cả stack ở slot ra đất bằng command -12.

        Đây là lệnh tương ứng ``Service.throwItem(indexUI)``. Không có
        quantity trong packet và tuyệt đối không gọi ``saleItem``/command 14.
        """
        message = _INVENTORY_MODULE.NSOMessage(self.CMD_DROP_ITEM)
        message.write_byte(slot)
        self.send(message)

        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                continue
            if command == self.CMD_SERVER_ERROR:
                print(f"      ❌ Server từ chối bỏ slot {slot}: {self._read_server_error(data)}")
                return False
            if command in (
                self.CMD_BAG_ITEM_ADD,
                self.CMD_BAG_ITEM_STACK_ADD,
                self.CMD_BAG_ITEM_QUANTITY,
                self.CMD_BAG_ITEM_REMOVE,
                self.CMD_ITEM_USE_QUANTITY,
            ):
                if data:
                    self._apply_bag_packet(command, data)
                continue
            if command != self.CMD_DROP_ITEM or not data:
                continue

            reader = _INVENTORY_MODULE.NSOReader(data)
            confirmed_slot = reader.read_byte()
            if confirmed_slot != slot:
                continue
            reader.read_short()  # itemMapID
            reader.read_short()  # x
            reader.read_short()  # y
            if 0 <= slot < len(self.bag):
                self.bag[slot] = None
            return True

        print(f"      ❌ Timeout xác nhận bỏ slot {slot}")
        return False

    def _apply_bag_packet(self, command: int, data: bytes):
        """Cập nhật bag theo packet server; trả event (slot, consumed)."""
        reader = _INVENTORY_MODULE.NSOReader(data)
        if command == self.CMD_BAG_ITEM_ADD:
            slot = reader.read_ubyte()
            template_id = reader.read_short()
            template = _INVENTORY_MODULE._GLOBAL_ITEM_TEMPLATES.get(template_id)
            is_lock = reader.read_boolean()
            upgrade = 0
            if template and (
                template.is_type_body()
                or template.is_type_mounts()
                or template.is_type_ngoc_kham()
            ):
                upgrade = reader.read_byte()
            is_expires = reader.read_boolean()
            quantity = reader.read_ushort() if reader.remaining() >= 2 else 1
            name = template.name if template else f"Item {template_id}"
            if slot >= len(self.bag):
                self.bag.extend([None] * (slot + 1 - len(self.bag)))
            self.bag[slot] = _INVENTORY_MODULE.BagItem(
                index=slot,
                template_id=template_id,
                name=name,
                quantity=max(1, quantity),
                is_lock=is_lock,
                upgrade=upgrade,
                is_expires=is_expires,
            )
            return None

        slot = reader.read_ubyte()
        if command == self.CMD_BAG_ITEM_STACK_ADD:
            quantity = reader.read_short() if reader.remaining() >= 2 else 1
            if 0 <= slot < len(self.bag) and self.bag[slot] is not None:
                self.bag[slot].quantity += quantity
            return None
        if command == self.CMD_BAG_ITEM_QUANTITY:
            quantity = reader.read_short() if reader.remaining() >= 2 else 0
            if 0 <= slot < len(self.bag) and self.bag[slot] is not None:
                self.bag[slot].quantity = quantity
                if quantity <= 0:
                    self.bag[slot] = None
            return (slot, 1) if quantity >= 0 else None
        if command == self.CMD_BAG_ITEM_REMOVE:
            if 0 <= slot < len(self.bag):
                self.bag[slot] = None
            return (slot, 1)
        if command == self.CMD_ITEM_USE_QUANTITY:
            consumed = reader.read_short() if reader.remaining() >= 2 else 1
            item = self.bag[slot] if 0 <= slot < len(self.bag) else None
            if item is not None:
                item.quantity -= consumed
                if item.quantity <= 0:
                    self.bag[slot] = None
            return (slot, consumed)
        return None

    def use_item_once(self, slot: int, timeout: float = 8.0) -> bool:
        """Dùng đúng một item trong stack và chờ server cập nhật quantity."""
        item = self.bag[slot] if 0 <= slot < len(self.bag) else None
        if item is None:
            return False
        previous_quantity = item.quantity
        message = _INVENTORY_MODULE.NSOMessage(self.CMD_USE_ITEM)
        message.write_byte(slot)
        self.send(message)

        deadline = time.time() + timeout
        use_ack = False
        consumed = False
        settle_deadline = None
        while time.time() < deadline:
            wait_deadline = settle_deadline or deadline
            command, data = self.receive(
                min(0.2, max(0.05, wait_deadline - time.time()))
            )
            if command is None:
                if settle_deadline and time.time() >= settle_deadline:
                    break
                continue
            if command == self.CMD_SERVER_ERROR and data:
                print(
                    f"      ❌ Server từ chối dùng slot {slot}: "
                    f"{self._read_server_error(data)}"
                )
                return False
            if not data:
                continue
            if command == self.CMD_USE_ITEM:
                reader = _INVENTORY_MODULE.NSOReader(data)
                use_ack = reader.read_ubyte() == slot
                continue

            event = self._apply_bag_packet(command, data)
            if event and event[0] == slot:
                current = self.bag[slot] if slot < len(self.bag) else None
                if current is None or current.quantity < previous_quantity:
                    if not consumed:
                        print(
                            f"      ↩ Mở slot={slot}: server cmd={command}, "
                            f"quantity còn={current.quantity if current else 0}"
                        )
                    consumed = True
                    settle_deadline = min(
                        deadline, time.time() + USE_SETTLE_DELAY
                    )
                    continue

            if consumed and settle_deadline and time.time() >= settle_deadline:
                break

        if consumed:
            return True

        # Một số server áp dụng item mở rộng nhưng không gửi packet quantity.
        if use_ack and self.connected:
            item = self.bag[slot] if 0 <= slot < len(self.bag) else None
            if item is not None:
                item.quantity -= 1
                if item.quantity <= 0:
                    self.bag[slot] = None
            print(f"      ⚠️ Mở slot={slot}: server không gửi packet quantity")
            return True

        print(f"      ❌ Timeout xác nhận dùng slot {slot}")
        return False


def sell_configured_items(
    client: DropItemClient,
    username: str,
    character_name: str,
    item_ids,
    args: argparse.Namespace,
) -> tuple[int, int, int]:
    """Bán/xóa item trong danh sách delllllllllll.txt."""
    targets = [
        item for item in client.bag
        if item is not None and item.template_id in item_ids
    ]
    print(
        f"    🧹 Dọn item {username}/{character_name}: "
        f"tìm thấy {len(targets)} item cần bán/xóa"
    )

    sold = 0
    failed = 0
    targets = sorted(targets, key=lambda value: value.index, reverse=True)
    for item in targets:
        if args.dry_run:
            print(
                f"      [DRY-RUN] Sẽ bán/xóa slot={item.index:02d} | "
                f"id={item.template_id} | {item.name} | số lượng={item.quantity}"
            )
            continue

        print(
            f"      🗑️ Bán/xóa slot={item.index:02d} | "
            f"id={item.template_id} | {item.name} | số lượng={item.quantity}"
        )
        if client.sell_item(item.index, item.quantity):
            sold += 1
            print(f"      ✅ Đã bán/xóa item slot={item.index:02d}")
        else:
            failed += 1
            print(f"      ❌ Bán/xóa thất bại slot={item.index:02d}")

    return len(targets), sold, failed


def open_and_drop_items(
    client: DropItemClient,
    username: str,
    character_name: str,
    item_ids,
    args: argparse.Namespace,
) -> DropResult:
    """Mở từng item một, rồi bỏ item không khóa trước khi mở tiếp."""
    result = DropResult()

    while True:
        if args.max_opens and result.used >= args.max_opens:
            print(f"    ⏹️ Đạt giới hạn test --max-opens={args.max_opens}")
            return result

        targets = sorted(
            (
                item for item in client.bag
                if item is not None and item.template_id in item_ids
            ),
            key=lambda value: value.index,
            reverse=True,
        )
        if not targets:
            print(f"    🧰 Không còn item trong {DEFAULT_OPEN_ITEMS_FILE.name}")
            drop_result = drop_unlocked_items(
                client,
                username,
                character_name,
                args,
            )
            result.matched += drop_result.matched
            result.dropped += drop_result.dropped
            result.failed += drop_result.failed
            return result

        if args.dry_run:
            for item in targets:
                print(
                    f"      [DRY-RUN] Sẽ mở slot={item.index:02d} | "
                    f"id={item.template_id} | {item.name} | số lượng={item.quantity}"
                )
            drop_result = drop_unlocked_items(
                client,
                username,
                character_name,
                args,
            )
            result.open_matched = len(targets)
            result.matched += drop_result.matched
            result.dropped += drop_result.dropped
            result.failed += drop_result.failed
            return result

        source = targets[0]
        result.open_matched += 1
        print(
            f"    🧰 Mở 1 item slot={source.index:02d} | "
            f"id={source.template_id} | số lượng trước={source.quantity}"
        )
        if not client.use_item_once(source.index):
            result.use_failed += 1
            result.failed += 1
            print(f"      ❌ Không xác nhận được mở slot={source.index:02d}")
            return result
        result.used += 1

        drop_result = drop_unlocked_items(
            client,
            username,
            character_name,
            args,
        )
        result.matched += drop_result.matched
        result.dropped += drop_result.dropped
        result.failed += drop_result.failed
        if drop_result.failed:
            print("      ❌ Dừng mở tiếp vì bước bỏ item gặp lỗi")
            return result


def drop_unlocked_items(
    client: DropItemClient,
    username: str,
    character_name: str,
    args: argparse.Namespace,
) -> DropResult:
    """Bỏ toàn bộ item không khóa còn lại."""
    result = DropResult()
    unlocked_items = [
        item for item in client.bag if item is not None and not item.is_lock
    ]
    targets = unlocked_items
    result.matched = len(targets)

    locked_count = sum(1 for item in client.bag if item is not None and item.is_lock)
    print(
        f"    🎒 Hành trang {username}/{character_name}: "
        f"{len(client.bag)} slot, không khóa={len(unlocked_items)}, "
        f"sẽ bỏ={len(targets)}, khóa={locked_count}"
    )

    # Giống thao tác thủ công theo slot; xử lý slot cao xuống thấp để không
    # bị ảnh hưởng nếu server cập nhật lại danh sách slot trong lúc throw.
    targets = sorted(targets, key=lambda value: value.index, reverse=True)
    for item_number, item in enumerate(targets):
        if args.dry_run:
            print(
                f"      [DRY-RUN] Sẽ bỏ đất slot={item.index:02d} | "
                f"id={item.template_id} | {item.name} | số lượng={item.quantity}"
            )
            continue

        # Không cho phép tiếp tục nếu session đã rời vị trí mục tiêu.
        if (
            client.map_state.map_id != args.map_id
            or client.map_state.zone_id != args.zone_id
        ):
            print(
                f"      ❌ Dừng vì vị trí không còn đúng: "
                f"map={client.map_state.map_id}, khu={client.map_state.zone_id}"
            )
            result.failed += 1
            break

        print(
            f"      🗑️ Bỏ ra đất slot={item.index:02d} | "
            f"id={item.template_id} | {item.name} | số lượng={item.quantity}"
        )
        dropped = False
        for attempt in range(1, args.drop_retries + 1):
            if client.throw_item(item.index, timeout=args.throw_timeout):
                dropped = True
                break
            if attempt < args.drop_retries:
                print(
                    f"      ⚠️ Bỏ slot={item.index:02d} thất bại, nghỉ "
                    f"{args.drop_retry_delay:g} giây rồi thử lại "
                    f"({attempt + 1}/{args.drop_retries})"
                )
                if args.drop_retry_delay > 0:
                    time.sleep(args.drop_retry_delay)

        if dropped:
            result.dropped += 1
            print(f"      ✅ Đã bỏ xuống đất slot={item.index:02d}")
        else:
            result.failed += 1
            print(
                f"      ❌ Bỏ thất bại slot={item.index:02d} sau "
                f"{args.drop_retries} lần thử"
            )
            break

        if item_number < len(targets) - 1 and args.drop_delay > 0:
            time.sleep(args.drop_delay)

    return result


def process_account(
    username: str,
    password: str,
    args: argparse.Namespace,
    sell_item_ids,
    open_item_ids,
) -> DropResult:
    client = DropItemClient(args.host, args.port)
    result = DropResult()
    try:
        print(f"  🔐 Đăng nhập: {username}")
        if not client.connect() or not client.login(
            username, password, login_timeout=args.login_timeout
        ):
            result.failed = 1
            return result
        if not client.characters:
            print("    ❌ Tài khoản không có nhân vật")
            result.failed = 1
            return result

        character_name = client.characters[0]
        print(f"    👤 Chỉ xử lý nhân vật đầu tiên: {character_name}")
        if not client.select_character_and_load_map(character_name):
            result.failed = 1
            return result

        if args.ready_delay > 0:
            time.sleep(args.ready_delay)

        result.sell_matched, result.sold, result.sell_failed = sell_configured_items(
            client, username, character_name, sell_item_ids, args
        )
        result.failed += result.sell_failed

        print(
            f"    🗺️ Vị trí ban đầu: map={client.map_state.map_id}, "
            f"khu={client.map_state.zone_id}"
        )
        if not client.move_to_map(args.map_id, max_steps=args.max_map_steps):
            print(f"    ❌ Không đi được tới map {args.map_id}")
            result.failed += 1
            return result
        print(f"    ✅ Đã tới map {client.map_state.map_id}")

        if not client.change_zone(
            args.zone_id, args.map_id, timeout=args.zone_timeout
        ):
            result.failed += 1
            return result
        if (
            client.map_state.map_id != args.map_id
            or client.map_state.zone_id != args.zone_id
        ):
            print(
                f"    ❌ Chưa ở đúng vị trí cuối: "
                f"map={client.map_state.map_id}, khu={client.map_state.zone_id}"
            )
            result.failed += 1
            return result
        print(f"    ✅ Đúng vị trí mục tiêu: map={args.map_id}, khu={args.zone_id}")

        # Xả packet di chuyển còn tồn trước khi mở item đầu tiên.
        client.drain(0.8)
        open_result = open_and_drop_items(
            client,
            username,
            character_name,
            open_item_ids,
            args,
        )
        result.open_matched = open_result.open_matched
        result.used = open_result.used
        result.use_failed = open_result.use_failed
        result.matched = open_result.matched
        result.dropped = open_result.dropped
        result.failed += open_result.failed
        result.processed = True
        return result
    except Exception as exc:
        print(f"    ❌ Lỗi xử lý tài khoản {username}: {exc}")
        result.failed += 1
        return result
    finally:
        client.disconnect()


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Dọn item, dùng item cấu hình rồi bỏ item không khóa ra đất"
        )
    )
    parser.add_argument(
        "csv_file",
        nargs="?",
        type=Path,
        default=DEFAULT_CSV,
        help=f"CSV tài khoản (mặc định: {DEFAULT_CSV.name})",
    )
    parser.add_argument(
        "--max-accounts",
        type=int,
        default=0,
        help="Chỉ chạy N tài khoản đầu tiên; 0 là không giới hạn",
    )
    parser.add_argument("--host", default=DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    parser.add_argument("--map-id", type=int, default=TARGET_MAP_ID)
    parser.add_argument("--zone-id", type=int, default=TARGET_ZONE_ID)
    parser.add_argument("--login-timeout", type=float, default=25.0)
    parser.add_argument("--zone-timeout", type=float, default=12.0)
    parser.add_argument("--throw-timeout", type=float, default=8.0)
    parser.add_argument("--max-map-steps", type=int, default=25)
    parser.add_argument(
        "--max-opens",
        type=int,
        default=0,
        help="Giới hạn số lần mở mỗi tài khoản; 0 là không giới hạn",
    )
    parser.add_argument("--ready-delay", type=float, default=1.0)
    parser.add_argument(
        "--drop-delay",
        "--action-delay",
        dest="drop_delay",
        type=float,
        default=DEFAULT_DROP_DELAY,
        help="Thời gian nghỉ giữa mỗi item bỏ ra đất (mặc định: 1 giây)",
    )
    parser.add_argument(
        "--drop-retries",
        type=int,
        default=DEFAULT_DROP_RETRIES,
        help="Số lần thử bỏ mỗi slot (mặc định: 3)",
    )
    parser.add_argument(
        "--drop-retry-delay",
        type=float,
        default=DEFAULT_DROP_RETRY_DELAY,
        help="Thời gian nghỉ giữa các lần thử bỏ slot (mặc định: 1 giây)",
    )
    parser.add_argument(
        "--luong",
        type=int,
        default=1,
        help="Số luồng xử lý tài khoản song song (mặc định: 1; ví dụ: --luong 50)",
    )
    parser.add_argument(
        "--account-delay",
        type=float,
        default=11.0,
        help="Thời gian nghỉ giữa các tài khoản (mặc định 11 giây)",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Di chuyển/đọc bag nhưng không bán, dùng hoặc bỏ item",
    )
    return parser


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    if not args.csv_file.is_file():
        print(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if args.max_accounts < 0:
        print("❌ --max-accounts không được âm")
        return 2
    if args.map_id < 0 or args.zone_id < 0:
        print("❌ map-id và zone-id không được âm")
        return 2
    if args.max_map_steps <= 0:
        print("❌ max-map-steps phải lớn hơn 0")
        return 2
    if args.max_opens < 0:
        print("❌ max-opens không được âm")
        return 2
    if args.drop_delay < 0:
        print("❌ drop-delay không được âm")
        return 2
    if args.drop_retries <= 0:
        print("❌ drop-retries phải lớn hơn 0")
        return 2
    if args.drop_retry_delay < 0:
        print("❌ drop-retry-delay không được âm")
        return 2
    if args.luong <= 0:
        print("❌ --luong phải lớn hơn 0")
        return 2
    for items_file in (DEFAULT_SELL_ITEMS_FILE, DEFAULT_OPEN_ITEMS_FILE):
        if not items_file.is_file():
            print(f"❌ Không tìm thấy file item: {items_file}")
            return 2

    try:
        accounts = read_accounts(args.csv_file)
        sell_item_ids = _INVENTORY_MODULE.read_item_ids(DEFAULT_SELL_ITEMS_FILE)
        open_item_ids = _INVENTORY_MODULE.read_item_ids(DEFAULT_OPEN_ITEMS_FILE)
    except Exception as exc:
        print(f"❌ Không đọc được dữ liệu đầu vào: {exc}")
        return 2
    if args.max_accounts:
        accounts = accounts[:args.max_accounts]
    if not accounts:
        print("❌ CSV không có tài khoản hợp lệ")
        return 2
    if not sell_item_ids:
        print(f"⚠️ {DEFAULT_SELL_ITEMS_FILE.name} không có item ID; bỏ qua bước dọn")
    if not open_item_ids:
        print(f"❌ {DEFAULT_OPEN_ITEMS_FILE.name} không có item ID hợp lệ")
        return 2

    mode = "DRY-RUN" if args.dry_run else "BỎ ĐẤT THẬT"
    print(
        f"NSO BỎ ĐỒ RA ĐẤT: {len(accounts)} tài khoản | "
        f"map={args.map_id}, khu={args.zone_id} | "
        f"dọn={len(sell_item_ids)} ID | dùng={len(open_item_ids)} ID | "
        f"luồng={min(args.luong, len(accounts))} | mode={mode}"
    )

    accounts_done = 0
    total = DropResult()
    effective_workers = min(args.luong, len(accounts))

    def run_account(account_number, username, password, stagger_accounts):
        print(
            f"\n[{account_number}/{len(accounts)}] Tài khoản {username}",
            flush=True,
        )
        result = process_account(
            username, password, args, sell_item_ids, open_item_ids
        )
        # Giữ delay cũ khi chạy 1 luồng. Khi chạy nhiều luồng, không chặn
        # worker khác bằng delay giữa các tài khoản.
        if (
            stagger_accounts
            and account_number < len(accounts)
            and args.account_delay > 0
        ):
            print(
                f"  ⏳ Nghỉ {args.account_delay:g} giây trước tài khoản tiếp theo...",
                flush=True,
            )
            time.sleep(args.account_delay)
        return result

    with ThreadPoolExecutor(
        max_workers=effective_workers,
        thread_name_prefix="bo-do-ra-dat",
    ) as executor:
        futures = {
            executor.submit(
                run_account,
                account_number,
                username,
                password,
                effective_workers == 1,
            ): username
            for account_number, (username, password)
            in enumerate(accounts, start=1)
        }

        for future in as_completed(futures):
            username = futures[future]
            try:
                result = future.result()
            except Exception as exc:
                print(f"❌ Worker tài khoản {username} lỗi: {exc}", flush=True)
                result = DropResult(failed=1)
            if result.processed:
                accounts_done += 1
            total.matched += result.matched
            total.dropped += result.dropped
            total.failed += result.failed
            total.sell_matched += result.sell_matched
            total.sold += result.sold
            total.sell_failed += result.sell_failed
            total.open_matched += result.open_matched
            total.used += result.used
            total.use_failed += result.use_failed

    print(
        f"\n=== HOÀN TẤT: {accounts_done}/{len(accounts)} tài khoản có kết quả | "
        f"dọn khớp={total.sell_matched} | đã dọn={total.sold} | "
        f"dọn lỗi={total.sell_failed} | dùng khớp={total.open_matched} | "
        f"đã dùng={total.used} | dùng lỗi={total.use_failed} | "
        f"item không khóa={total.matched} | đã bỏ đất={total.dropped} | "
        f"thất bại={total.failed} ==="
    )
    return 1 if total.failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
