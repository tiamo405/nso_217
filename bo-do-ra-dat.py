#!/usr/bin/env python3
"""Bỏ toàn bộ vật phẩm không khóa trong hành trang ra đất.

Luồng xử lý mỗi tài khoản:
  1. Đăng nhập và chọn nhân vật đầu tiên.
  2. Đi tới map 70, chuyển sang khu 20.
  3. Đọc hành trang, chỉ lấy item ``is_lock == False``.
  4. Gửi command ``-12`` (throwItem) theo từng slot.

Lưu ý quan trọng:
  - ``-12`` là lệnh bỏ item ra đất, không phải lệnh bán.
  - Không gửi command 14 (saleItem).
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
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
TARGET_MAP_ID = 22
TARGET_ZONE_ID = 80


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
    failed: int = 0


class DropItemClient(_INVENTORY_MODULE.NSOClient):
    """Client dùng parser hành trang chuẩn và bổ sung thao tác map."""

    CMD_DROP_ITEM = -12
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


def drop_unlocked_items(
    client: DropItemClient,
    username: str,
    character_name: str,
    args: argparse.Namespace,
) -> DropResult:
    """Bỏ tất cả item không khóa trong bag của nhân vật hiện tại."""
    result = DropResult()
    targets = [item for item in client.bag if item is not None and not item.is_lock]
    result.matched = len(targets)

    locked_count = sum(1 for item in client.bag if item is not None and item.is_lock)
    print(
        f"    🎒 Hành trang {username}/{character_name}: "
        f"{len(client.bag)} slot, không khóa={len(targets)}, khóa={locked_count}"
    )

    # Giống thao tác thủ công theo slot; xử lý slot cao xuống thấp để không
    # bị ảnh hưởng nếu server cập nhật lại danh sách slot trong lúc throw.
    for item in sorted(targets, key=lambda value: value.index, reverse=True):
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
        if client.throw_item(item.index, timeout=args.throw_timeout):
            result.dropped += 1
            print(f"      ✅ Đã bỏ xuống đất slot={item.index:02d}")
        else:
            result.failed += 1
            print(f"      ❌ Bỏ thất bại slot={item.index:02d}")

        if args.action_delay > 0:
            time.sleep(args.action_delay)

    return result


def process_account(username: str, password: str, args: argparse.Namespace) -> DropResult:
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
            result.failed = 1
            return result
        print(f"    ✅ Đã tới map {client.map_state.map_id}")

        if not client.change_zone(
            args.zone_id, args.map_id, timeout=args.zone_timeout
        ):
            result.failed = 1
            return result
        if (
            client.map_state.map_id != args.map_id
            or client.map_state.zone_id != args.zone_id
        ):
            print(
                f"    ❌ Chưa ở đúng vị trí cuối: "
                f"map={client.map_state.map_id}, khu={client.map_state.zone_id}"
            )
            result.failed = 1
            return result
        print(f"    ✅ Đúng vị trí mục tiêu: map={args.map_id}, khu={args.zone_id}")

        # Xả packet di chuyển còn tồn trước khi gửi lệnh throw đầu tiên.
        client.drain(0.8)
        result = drop_unlocked_items(client, username, character_name, args)
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
            "Chọn nhân vật đầu tiên, đi map 70 khu 20 và bỏ toàn bộ "
            "item không khóa trong hành trang ra đất"
        )
    )
    parser.add_argument(
        "csv_file",
        nargs="?",
        type=Path,
        default=DEFAULT_CSV,
        help=f"CSV tài khoản (mặc định: {DEFAULT_CSV.name})",
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
    parser.add_argument("--action-delay", type=float, default=0.7)
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
        help="Di chuyển và đọc bag nhưng không gửi lệnh bỏ item",
    )
    return parser


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    if not args.csv_file.is_file():
        print(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if args.map_id < 0 or args.zone_id < 0:
        print("❌ map-id và zone-id không được âm")
        return 2
    if args.max_map_steps <= 0:
        print("❌ max-map-steps phải lớn hơn 0")
        return 2
    if args.luong <= 0:
        print("❌ --luong phải lớn hơn 0")
        return 2

    try:
        accounts = read_accounts(args.csv_file)
    except Exception as exc:
        print(f"❌ Không đọc được CSV: {exc}")
        return 2
    if not accounts:
        print("❌ CSV không có tài khoản hợp lệ")
        return 2

    mode = "DRY-RUN" if args.dry_run else "BỎ ĐẤT THẬT"
    print(
        f"NSO BỎ ĐỒ RA ĐẤT: {len(accounts)} tài khoản | "
        f"map={args.map_id}, khu={args.zone_id} | "
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
        result = process_account(username, password, args)
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

    print(
        f"\n=== HOÀN TẤT: {accounts_done}/{len(accounts)} tài khoản có kết quả | "
        f"item không khóa={total.matched} | đã bỏ đất={total.dropped} | "
        f"thất bại={total.failed} ==="
    )
    return 1 if total.failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
