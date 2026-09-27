#!/usr/bin/env python3
"""Bỏ vật phẩm không khóa ra đất.

Luồng xử lý mỗi tài khoản:
  1. Đăng nhập và chọn nhân vật đầu tiên.
  2. Đi tới map 69, chuyển sang khu 22.
  3. Bỏ toàn bộ item không khóa ra đất.

Lưu ý quan trọng:
  - ``-12`` là lệnh bỏ item ra đất, không phải lệnh bán.
  - Một lần throw theo slot sẽ bỏ cả chồng item ở slot đó, giống client Java.

Mặc định chạy trên ``account-bodo.csv``:

    python3 bo_do_ra_dat.py

Vật phẩm có ID trong ``item_vinh_vien.txt`` chỉ được bỏ nếu server báo
``isExpires=False``. Vật phẩm có hạn trong danh sách này sẽ được giữ lại.

Có thể xem trước danh sách item mà không bỏ item:

    python3 bo_do_ra_dat.py --dry-run
"""

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import importlib.util
import sys
import threading
import time
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional, Sequence

from nhanexp_and_doiyenquaxu import MapState, OfflineExpClient, read_accounts


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-bodo.csv"
DEFAULT_PERMANENT_ITEMS_FILE = ROOT_DIR / "item_vinh_vien.txt"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
TARGET_MAP_ID = 69
TARGET_ZONE_ID = 22
DEFAULT_DROP_DELAY = 1.0 # Thời gian nghỉ giữa mỗi item bỏ ra đất (giây)
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
    matched: int = 0
    dropped: int = 0
    skipped_expiring: int = 0
    permanent_dropped: dict[int, tuple[str, int, int]] = field(default_factory=dict)
    failed: int = 0


class DropItemClient(_INVENTORY_MODULE.NSOClient):
    """Client dùng parser hành trang chuẩn và bổ sung thao tác map."""

    CMD_DROP_ITEM = -12
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

def drop_unlocked_items(
    client: DropItemClient,
    username: str,
    character_name: str,
    permanent_item_ids,
    args: argparse.Namespace,
) -> DropResult:
    """Bỏ item không khóa, giữ item có hạn thuộc danh sách vĩnh viễn."""
    result = DropResult()
    unlocked_items = [
        item for item in client.bag if item is not None and not item.is_lock
    ]
    expiring_permanent_items = [
        item
        for item in unlocked_items
        if item.template_id in permanent_item_ids and item.is_expires
    ]
    targets = [
        item
        for item in unlocked_items
        if not (item.template_id in permanent_item_ids and item.is_expires)
    ]
    result.matched = len(targets)
    result.skipped_expiring = len(expiring_permanent_items)

    locked_count = sum(1 for item in client.bag if item is not None and item.is_lock)
    print(
        f"    🎒 Hành trang {username}/{character_name}: "
        f"{len(client.bag)} slot, không khóa={len(unlocked_items)}, "
        f"sẽ bỏ={len(targets)}, giữ item có hạn={len(expiring_permanent_items)}, "
        f"khóa={locked_count}"
    )

    for item in expiring_permanent_items:
        print(
            f"      ⏭️ Giữ lại item có hạn slot={item.index:02d} | "
            f"id={item.template_id} | {item.name}"
        )

    # Giống thao tác thủ công theo slot; xử lý slot cao xuống thấp để không
    # bị ảnh hưởng nếu server cập nhật lại danh sách slot trong lúc throw.
    targets = sorted(targets, key=lambda value: value.index, reverse=True)
    for item_number, item in enumerate(targets):
        if args.dry_run:
            reason = (
                "vĩnh viễn trong item_vinh_vien.txt"
                if item.template_id in permanent_item_ids
                else "không nằm trong item_vinh_vien.txt"
            )
            print(
                f"      [DRY-RUN] Sẽ bỏ đất slot={item.index:02d} | "
                f"id={item.template_id} | {item.name} | "
                f"số lượng={item.quantity} | lý do={reason}"
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
            if item.template_id in permanent_item_ids:
                name, stacks, quantity = result.permanent_dropped.get(
                    item.template_id, (item.name, 0, 0)
                )
                result.permanent_dropped[item.template_id] = (
                    name,
                    stacks + 1,
                    quantity + item.quantity,
                )
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
    permanent_item_ids,
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

        # Xả packet di chuyển còn tồn trước khi bỏ item.
        client.drain(0.8)
        drop_result = drop_unlocked_items(
            client,
            username,
            character_name,
            permanent_item_ids,
            args,
        )
        result.matched = drop_result.matched
        result.dropped = drop_result.dropped
        result.skipped_expiring = drop_result.skipped_expiring
        result.failed += drop_result.failed
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
            "Bỏ item không khóa ra đất"
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
        help="Di chuyển/đọc bag nhưng không bỏ item",
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
    if not DEFAULT_PERMANENT_ITEMS_FILE.is_file():
        print(f"❌ Không tìm thấy file item: {DEFAULT_PERMANENT_ITEMS_FILE}")
        return 2
    try:
        accounts = read_accounts(args.csv_file)
        permanent_item_ids = _INVENTORY_MODULE.read_item_ids(
            DEFAULT_PERMANENT_ITEMS_FILE
        )
    except Exception as exc:
        print(f"❌ Không đọc được dữ liệu đầu vào: {exc}")
        return 2
    if args.max_accounts:
        accounts = accounts[:args.max_accounts]
    if not accounts:
        print("❌ CSV không có tài khoản hợp lệ")
        return 2
    if not permanent_item_ids:
        print(
            f"⚠️ {DEFAULT_PERMANENT_ITEMS_FILE.name} không có item ID hợp lệ; "
            "mọi item không khóa sẽ được bỏ"
        )
    mode = "DRY-RUN" if args.dry_run else "BỎ ĐẤT THẬT"
    print(
        f"NSO BỎ ĐỒ RA ĐẤT: {len(accounts)} tài khoản | "
        f"map={args.map_id}, khu={args.zone_id} | "
        f"ID kiểm tra vĩnh viễn={len(permanent_item_ids)} | "
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
            username, password, args, permanent_item_ids
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
            total.skipped_expiring += result.skipped_expiring
            total.failed += result.failed
            for template_id, stats in result.permanent_dropped.items():
                name, stacks, quantity = stats
                old_name, old_stacks, old_quantity = total.permanent_dropped.get(
                    template_id, (name, 0, 0)
                )
                total.permanent_dropped[template_id] = (
                    old_name,
                    old_stacks + stacks,
                    old_quantity + quantity,
                )

    print(
        f"\n=== HOÀN TẤT: {accounts_done}/{len(accounts)} tài khoản có kết quả | "
        f"item được chọn bỏ={total.matched} | đã bỏ đất={total.dropped} | "
        f"giữ item có hạn={total.skipped_expiring} | "
        f"thất bại={total.failed} ==="
    )
    if total.permanent_dropped:
        print("=== ITEM VĨNH VIỄN ĐÃ BỎ ===")
        for template_id, (name, stacks, quantity) in sorted(
            total.permanent_dropped.items()
        ):
            print(
                f"  id={template_id} | {name} | "
                f"stack={stacks} | tổng số lượng={quantity}"
            )
    elif args.dry_run:
        print("=== ITEM VĨNH VIỄN ĐÃ BỎ: DRY-RUN, chưa bỏ item thật ===")
    else:
        print("=== ITEM VĨNH VIỄN ĐÃ BỎ: không có ===")
    return 1 if total.failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
