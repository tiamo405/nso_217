#!/usr/bin/env python3
"""Đăng nhập lần lượt các tài khoản/nhân vật và bán item được cấu hình trong hành trang.

Ví dụ:
    python3 item_sell_client.py account.csv
    python3 item_sell_client.py account.csv --item-ids 761,736,403
    python3 item_sell_client.py account.csv --dry-run
    python3 item_sell_client.py account.csv --scope both

Mặc định danh sách item được đọc từ delllllllllll.txt và chỉ bán trong hành trang.
Muốn dọn rương, truyền thêm ``--scope box`` hoặc ``--scope both``.
CSV có định dạng: username,password
"""

import argparse
import csv
import importlib
import logging
import os
import re
import sys
import threading
import time
from dataclasses import dataclass
from typing import List, Optional, Sequence, Set, Tuple


ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
PYTHON_WORKER_DIR = os.path.join(ROOT_DIR, "python-worker")
DEFAULT_ITEMS_FILE = os.path.join(ROOT_DIR, "delllllllllll.txt")
BAG_UPGRADE_ITEMS = ((283, 3), (801, 4), (1179, 5))
sys.path.insert(0, PYTHON_WORKER_DIR)

# ``python-worker`` có dấu gạch ngang nên không thể import như một Python package
# thông thường. Nạp các module sau khi thêm đường dẫn tuyệt đối để cả CLI lẫn IDE
# không báo lỗi ở các câu lệnh ``from ... import ...``.
config = importlib.import_module("config.config")
NSOSocketClient = importlib.import_module("network.socket_client").NSOSocketClient
NSOController = importlib.import_module("protocol.controller").NSOController
NSOService = importlib.import_module("protocol.service").NSOService


logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s",
    datefmt="%H:%M:%S",
    stream=sys.stdout,
)
logger = logging.getLogger("ItemSellClient")


@dataclass
class SellResult:
    sold: int = 0
    failed: int = 0
    matched: int = 0
    upgrade_bags_used: int = 0
    upgrade_bags_failed: int = 0


def read_accounts(csv_path: str) -> List[Tuple[str, str]]:
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


def parse_item_ids(value: str) -> Set[int]:
    ids: Set[int] = set()
    for token in re.split(r"[\s,;]+", value):
        token = token.strip()
        if not token or token.startswith("#"):
            continue
        try:
            item_id = int(token)
        except ValueError as exc:
            raise ValueError(f"Item ID không hợp lệ: {token!r}") from exc
        if item_id < 0 or item_id > 32767:
            raise ValueError(f"Item ID ngoài phạm vi short: {item_id}")
        ids.add(item_id)
    return ids


def read_item_ids(path: str) -> Set[int]:
    with open(path, "r", encoding="utf-8") as handle:
        lines = [line.split("#", 1)[0] for line in handle]
    return parse_item_ids("\n".join(lines))


class CharacterSession:
    """Một kết nối game, có theo dõi packet chuyển rương và xác nhận bán."""

    SERVER_CMD_BAG_ITEM_QUANTITY = 7
    SERVER_CMD_BAG_ITEM_REMOVE = 10
    SERVER_CMD_USE_ITEM = 11
    SERVER_CMD_SALE_ITEM = 14
    SERVER_CMD_BOX_TO_BAG = 16
    SERVER_CMD_MESSAGE = -26
    SERVER_CMD_DIALOG = -25

    def __init__(self, username: str, host: str, port: int, timeout: float):
        self.username = username
        self.client = NSOSocketClient(host, port, timeout=timeout)
        self.service = NSOService(self.client)
        self.controller = NSOController(service=self.service)
        self.controller.current_username = username
        self._sold_slots: Set[int] = set()
        self._used_slots: Set[int] = set()
        self._pending_use_slot: Optional[int] = None
        self._moved_box_slots = {}
        self._message_serial = 0
        self._state_condition = threading.Condition()
        self.client.on_message_callback = self._handle_message

    def _handle_message(self, message) -> None:
        if message.command == self.SERVER_CMD_SALE_ITEM:
            try:
                reader = message.reader()
                slot = reader.read_unsigned_byte()
                bag = self.controller.character.bag
                self.controller.character.yen = reader.read_int()
                sold_quantity = reader.read_short() if reader.available() >= 2 else 1
                if 0 <= slot < len(bag) and bag[slot] is not None:
                    bag[slot].quantity -= sold_quantity
                    if bag[slot].quantity <= 0:
                        bag[slot] = None
                with self._state_condition:
                    self._sold_slots.add(slot)
                    self._state_condition.notify_all()
            except Exception as exc:
                logger.warning("Không đọc được packet xác nhận bán item: %s", exc)
            return

        if message.command == self.SERVER_CMD_BAG_ITEM_REMOVE:
            try:
                slot = message.reader().read_unsigned_byte()
                bag = self.controller.character.bag
                if 0 <= slot < len(bag):
                    bag[slot] = None
                # Một số loại túi mở rộng chỉ trả CMD 10 (xóa item đã dùng),
                # không trả CMD 11 như vật phẩm dùng thông thường.
                with self._state_condition:
                    if slot == self._pending_use_slot:
                        self._used_slots.add(slot)
                    self._state_condition.notify_all()
            except Exception as exc:
                logger.debug("Không đọc được packet xóa item khỏi túi: %s", exc)
            return

        if message.command == self.SERVER_CMD_USE_ITEM:
            try:
                reader = message.reader()
                slot = reader.read_unsigned_byte()
                # CMD 11 tự nó đã là xác nhận dùng thành công. Đánh dấu ngay,
                # không phụ thuộc phần chỉ số nhân vật phía sau có đủ hay không.
                with self._state_condition:
                    self._used_slots.add(slot)
                    self._state_condition.notify_all()
                char = self.controller.character
                if reader.available() >= 13:
                    char.speed = reader.read_byte()
                    char.max_hp = reader.read_int()
                    char.max_mp = reader.read_int()
                    reader.read_short()  # eff5BuffHp
                    reader.read_short()  # eff5BuffMp
            except Exception as exc:
                logger.warning("Không đọc được packet xác nhận dùng item: %s", exc)
            return

        if message.command == self.SERVER_CMD_BOX_TO_BAG:
            try:
                reader = message.reader()
                box_slot = reader.read_unsigned_byte()
                bag_slot = reader.read_unsigned_byte()
                box = self.controller.character.box
                bag = self.controller.character.bag
                item = box[box_slot] if box is not None and 0 <= box_slot < len(box) else None
                if item is not None and 0 <= bag_slot < len(bag):
                    box[box_slot] = None
                    if bag[bag_slot] is None:
                        item.index_ui = bag_slot
                        item.type_ui = 3
                        bag[bag_slot] = item
                    else:
                        bag[bag_slot].quantity += item.quantity
                with self._state_condition:
                    self._moved_box_slots[box_slot] = bag_slot
                    self._state_condition.notify_all()
            except Exception as exc:
                logger.warning("Không đọc được packet chuyển item từ rương: %s", exc)
            return

        if message.command == self.SERVER_CMD_BAG_ITEM_QUANTITY:
            try:
                reader = message.reader()
                slot = reader.read_unsigned_byte()
                quantity = reader.read_short()
                bag = self.controller.character.bag
                if 0 <= slot < len(bag) and bag[slot] is not None:
                    bag[slot].quantity = quantity
                # Vật phẩm dạng stack có thể chỉ được xác nhận bằng CMD 7.
                with self._state_condition:
                    if slot == self._pending_use_slot:
                        self._used_slots.add(slot)
                    self._state_condition.notify_all()
            except Exception as exc:
                logger.debug("Không đọc được packet cập nhật số lượng item: %s", exc)
            return

        self.controller.handle_message(message)
        if message.command in {self.SERVER_CMD_MESSAGE, self.SERVER_CMD_DIALOG}:
            with self._state_condition:
                self._message_serial += 1
                self._state_condition.notify_all()

    def connect_and_login(self, password: str, login_timeout: float) -> bool:
        reason = "timeout chờ danh sách nhân vật"
        for attempt in range(1, 4):
            if not self.client.connect():
                reason = "không kết nối được server"
            else:
                self.controller.reset_for_account()
                self.controller.current_username = self.username
                if not self.service.send_login(
                    self.username, password, config.DEFAULT_CLIENT_VERSION
                ):
                    reason = "không gửi được packet đăng nhập"
                else:
                    deadline = time.time() + login_timeout
                    while self.client.connected and time.time() < deadline:
                        if self.controller.is_in_character_select:
                            return True
                        time.sleep(0.1)
                    reason = self.controller.last_server_message or reason

            self.client.disconnect()
            if attempt < 3:
                retry_delay = 11.0 if "đăng nhập quá nhanh" in reason.lower() else 3.0
                logger.warning(
                    "Tài khoản %s đăng nhập chưa thành công (%s), chờ %.0f giây rồi thử lần %s/3",
                    self.username,
                    reason,
                    retry_delay,
                    attempt + 1,
                )
                time.sleep(retry_delay)

        logger.error("Tài khoản %s đăng nhập thất bại: %s", self.username, reason)
        return False

    def enter_character(self, character_name: str, game_timeout: float) -> bool:
        self.controller.is_game_ready = False
        if not self.service.select_character(character_name):
            return False
        deadline = time.time() + game_timeout
        while self.client.connected and time.time() < deadline:
            if self.controller.is_game_ready:
                return True
            time.sleep(0.1)
        logger.error("Timeout chờ vào nhân vật %s/%s", self.username, character_name)
        return False

    def sell_slot(self, slot: int, quantity: int, confirm_timeout: float) -> bool:
        with self._state_condition:
            self._sold_slots.discard(slot)

        if not self.service.sale_item(slot, quantity):
            return False

        deadline = time.time() + confirm_timeout
        with self._state_condition:
            while slot not in self._sold_slots and self.client.connected:
                remaining = deadline - time.time()
                if remaining <= 0:
                    break
                self._state_condition.wait(timeout=remaining)
            return slot in self._sold_slots

    def request_box(self, timeout: float) -> bool:
        if self.controller.box_loaded:
            return True
        attempt_timeout = min(5.0, timeout)
        for attempt in range(1, 4):
            logger.info("ITEM SELL BOX: yêu cầu dữ liệu rương lần=%s/3", attempt)
            if not self.service.request_item(4):
                continue
            deadline = time.time() + attempt_timeout
            while self.client.connected and time.time() < deadline:
                if self.controller.box_loaded:
                    return True
                time.sleep(0.1)
            if not self.client.connected:
                return False

        # Một số nhân vật có rương trống/chưa khởi tạo và server không gửi CMD 31.
        # Sau ba lần yêu cầu trên kết nối còn sống, coi đây là rương trống hợp lệ.
        logger.warning(
            "ITEM SELL BOX: server không trả CMD 31 sau 3 lần; coi rương trống và tiếp tục"
        )
        self.controller.character.box = []
        self.controller.box_loaded = True
        return True

    def move_box_item(self, box_slot: int, timeout: float) -> Optional[int]:
        with self._state_condition:
            self._moved_box_slots.pop(box_slot, None)
        if not self.service.item_box_to_bag(box_slot):
            return None

        deadline = time.time() + timeout
        with self._state_condition:
            while box_slot not in self._moved_box_slots and self.client.connected:
                remaining = deadline - time.time()
                if remaining <= 0:
                    break
                self._state_condition.wait(timeout=remaining)
            return self._moved_box_slots.get(box_slot)

    def use_slot(self, slot: int, timeout: float) -> Tuple[bool, str]:
        with self._state_condition:
            self._used_slots.discard(slot)
            self._pending_use_slot = slot
            initial_message_serial = self._message_serial
        if not self.service.use_item(slot):
            with self._state_condition:
                self._pending_use_slot = None
            return False, "không gửi được packet dùng item"

        deadline = time.time() + timeout
        result = (False, "timeout chờ server xác nhận")
        with self._state_condition:
            while self.client.connected:
                if slot in self._used_slots:
                    result = (True, "")
                    break
                if self._message_serial > initial_message_serial:
                    result = (
                        False,
                        self.controller.last_server_message or "server từ chối dùng item",
                    )
                    break
                remaining = deadline - time.time()
                if remaining <= 0:
                    break
                self._state_condition.wait(timeout=remaining)
            self._pending_use_slot = None
            # Túi mở rộng trên server hiện tại được áp dụng nhưng không gửi
            # packet 7/10/11 về client. Khi kết nối vẫn còn sống và không có
            # thông báo từ chối, coi lệnh đã được server tiếp nhận để tránh
            # báo timeout giả.
            if (
                not result[0]
                and result[1] == "timeout chờ server xác nhận"
                and self.client.connected
            ):
                result = (True, "")
        return result

    def disconnect(self) -> None:
        self.client.disconnect()


def sell_character_items(
    username: str,
    password: str,
    character_name: str,
    item_ids: Set[int],
    args,
    existing_session: Optional[CharacterSession] = None,
) -> Optional[SellResult]:
    session = existing_session or CharacterSession(
        username, args.host, args.port, args.socket_timeout
    )
    try:
        if existing_session is None and not session.connect_and_login(password, args.login_timeout):
            return None
        if not session.enter_character(character_name, args.game_timeout):
            return None

        # is_game_ready được bật ngay khi nhận thông tin nhân vật, trong khi
        # server vẫn còn gửi dữ liệu map. CMD dùng túi gửi trong giai đoạn này
        # có thể bị server bỏ qua mà không trả lỗi hay packet xác nhận.
        if args.ready_delay > 0:
            logger.info(
                "ITEM SELL: chờ %.1f giây để nhân vật tải map hoàn tất",
                args.ready_delay,
            )
            time.sleep(args.ready_delay)

        bag = session.controller.character.bag
        result = SellResult()
        logger.info(
            "ITEM SELL: username=%s nv=%s bag=%s scope=%s",
            username,
            character_name,
            len(bag),
            args.scope,
        )

        if not args.upgrade_only and args.scope in {"bag", "both"}:
            bag_targets = [
                (
                    slot,
                    item.template_id,
                    item.template.name or "?",
                    max(1, item.quantity),
                    item.is_lock,
                )
                for slot, item in enumerate(bag)
                if item is not None and item.template_id in item_ids
            ]
            result.matched += len(bag_targets)
            for slot, template_id, item_name, quantity, is_locked in reversed(bag_targets):
                logger.info(
                    "ITEM SELL BAG: %s item id=%s name=%s slot=%s quantity=%s locked=%s",
                    "sẽ bán" if args.dry_run else "đang bán",
                    template_id,
                    item_name,
                    slot,
                    quantity,
                    is_locked,
                )
                if not args.dry_run:
                    if session.sell_slot(slot, quantity, args.confirm_timeout):
                        result.sold += 1
                        logger.info(
                            "ITEM SELL BAG: đã bán thành công item id=%s name=%s slot=%s",
                            template_id,
                            item_name,
                            slot,
                        )
                    else:
                        result.failed += 1
                        logger.warning(
                            "ITEM SELL BAG: server chưa xác nhận item id=%s name=%s slot=%s",
                            template_id,
                            item_name,
                            slot,
                        )
                    time.sleep(args.action_delay)

        if not args.upgrade_only and args.scope in {"box", "both"}:
            if not session.request_box(args.game_timeout):
                logger.error("ITEM SELL BOX: không nhận được dữ liệu rương của %s", character_name)
                return None
            box = session.controller.character.box
            box_targets = [
                (slot, item.template_id, max(1, item.quantity), item.is_lock)
                for slot, item in enumerate(box)
                if item is not None and item.template_id in item_ids
            ]
            result.matched += len(box_targets)
            box_item_ids = [item.template_id for item in box if item is not None]
            logger.info(
                "ITEM SELL BOX: rương=%s item_ids=%s target=%s",
                len(box),
                box_item_ids,
                len(box_targets),
            )
            for box_slot, template_id, quantity, is_locked in reversed(box_targets):
                logger.info(
                    "ITEM SELL BOX: %s item id=%s box_slot=%s quantity=%s locked=%s",
                    "sẽ chuyển và bán" if args.dry_run else "đang chuyển sang túi",
                    template_id,
                    box_slot,
                    quantity,
                    is_locked,
                )
                if args.dry_run:
                    continue
                bag_slot = session.move_box_item(box_slot, args.confirm_timeout)
                if bag_slot is None:
                    result.failed += 1
                    logger.warning("ITEM SELL BOX: không chuyển được item id=%s box_slot=%s", template_id, box_slot)
                    continue
                moved_item = bag[bag_slot] if 0 <= bag_slot < len(bag) else None
                sell_quantity = max(1, moved_item.quantity) if moved_item is not None else quantity
                if session.sell_slot(bag_slot, sell_quantity, args.confirm_timeout):
                    result.sold += 1
                    logger.info(
                        "ITEM SELL BOX: đã bán thành công item id=%s box_slot=%s bag_slot=%s quantity=%s",
                        template_id,
                        box_slot,
                        bag_slot,
                        sell_quantity,
                    )
                else:
                    result.failed += 1
                    logger.warning(
                        "ITEM SELL BOX: server chưa xác nhận bán item id=%s bag_slot=%s",
                        template_id,
                        bag_slot,
                    )
                time.sleep(args.action_delay)

        # Sau khi dọn xong, tải lại rương và thử dùng túi vải theo đúng thứ tự cấp.
        if not session.request_box(args.game_timeout):
            logger.error("ITEM BAG UPGRADE: không nhận được dữ liệu rương của %s", character_name)
            return None
        box = session.controller.character.box
        bag_items = [
            (item.template_id, item.template.name or "?")
            for item in bag
            if item is not None
        ]
        logger.info(
            "ITEM BAG UPGRADE: username=%s nv=%s kiểm tra túi vải cấp 3, 4, 5 "
            "trong hành trang rồi tới rương; bag_items=%s",
            username,
            character_name,
            bag_items,
        )
        for template_id, bag_level in BAG_UPGRADE_ITEMS:
            found_in_bag = next(
                (
                    (slot, item)
                    for slot, item in enumerate(bag)
                    if item is not None and item.template_id == template_id
                ),
                None,
            )
            found_in_box = next(
                (
                    (slot, item)
                    for slot, item in enumerate(box)
                    if item is not None and item.template_id == template_id
                ),
                None,
            ) if found_in_bag is None else None

            if found_in_bag is None and found_in_box is None:
                logger.info(
                    "ITEM BAG UPGRADE: không có túi vải cấp %s id=%s trong hành trang/rương, chuyển cấp tiếp theo",
                    bag_level,
                    template_id,
                )
                continue

            if found_in_bag is not None:
                bag_slot, item = found_in_bag
                logger.info(
                    "ITEM BAG UPGRADE: tìm thấy túi vải cấp %s id=%s trong hành trang bag_slot=%s locked=%s",
                    bag_level,
                    template_id,
                    bag_slot,
                    item.is_lock,
                )
                source = "hành trang"
            else:
                box_slot, item = found_in_box
                logger.info(
                    "ITEM BAG UPGRADE: tìm thấy túi vải cấp %s id=%s trong rương box_slot=%s locked=%s",
                    bag_level,
                    template_id,
                    box_slot,
                    item.is_lock,
                )
                source = "rương"

            if args.dry_run:
                logger.info(
                    "ITEM BAG UPGRADE: DRY-RUN sẽ dùng túi vải cấp %s id=%s từ %s",
                    bag_level,
                    template_id,
                    source,
                )
                continue

            if found_in_bag is None:
                bag_slot = session.move_box_item(box_slot, args.confirm_timeout)
                if bag_slot is None:
                    result.upgrade_bags_failed += 1
                    logger.warning(
                        "ITEM BAG UPGRADE: không chuyển được túi cấp %s id=%s từ rương; hành trang có thể đầy",
                        bag_level,
                        template_id,
                    )
                    continue

            logger.info(
                "ITEM BAG UPGRADE: đang dùng túi vải cấp %s id=%s bag_slot=%s",
                bag_level,
                template_id,
                bag_slot,
            )
            used, reason = session.use_slot(bag_slot, args.confirm_timeout)
            if used:
                result.upgrade_bags_used += 1
                if 0 <= bag_slot < len(bag):
                    bag[bag_slot] = None
                logger.info(
                    "ITEM BAG UPGRADE: đã dùng thành công túi vải cấp %s id=%s",
                    bag_level,
                    template_id,
                )
            else:
                result.upgrade_bags_failed += 1
                logger.warning(
                    "ITEM BAG UPGRADE: không dùng được túi vải cấp %s id=%s: %s; tiếp tục cấp kế tiếp",
                    bag_level,
                    template_id,
                    reason,
                )
            time.sleep(args.action_delay)
        return result
    finally:
        session.disconnect()


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Đăng nhập mọi tài khoản/nhân vật và bán item được chọn trong rương/túi."
    )
    parser.add_argument(
        "csv_file",
        nargs="?",
        default=os.path.join(ROOT_DIR, "account-del.csv"),
        help="File CSV có hai cột username,password (mặc định: account-del.csv)",
    )
    item_source = parser.add_mutually_exclusive_group()
    item_source.add_argument(
        "--items-file",
        default=DEFAULT_ITEMS_FILE,
        help=f"File chứa Item ID, mặc định: {DEFAULT_ITEMS_FILE}",
    )
    item_source.add_argument(
        "--item-ids",
        help="Danh sách Item ID ngăn cách bởi dấu phẩy/chấm phẩy, ví dụ 761,736,403",
    )
    parser.add_argument("--host", default=config.DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=config.DEFAULT_PORT)
    parser.add_argument(
        "--scope",
        choices=("box", "bag", "both"),
        default="bag",
        help="Nơi cần dọn: bag=hành trang (mặc định), box=rương, both=cả hai",
    )
    parser.add_argument("--socket-timeout", type=float, default=config.SOCKET_TIMEOUT)
    parser.add_argument("--login-timeout", type=float, default=15.0)
    parser.add_argument("--game-timeout", type=float, default=20.0)
    parser.add_argument("--confirm-timeout", type=float, default=3.0)
    parser.add_argument(
        "--ready-delay",
        type=float,
        default=3.0,
        help="Thời gian chờ map ổn định trước khi bán/dùng item (mặc định: 3 giây)",
    )
    parser.add_argument("--action-delay", type=float, default=0.5)
    parser.add_argument("--character-delay", type=float, default=11.0)
    parser.add_argument("--account-delay", type=float, default=11.0)
    parser.add_argument(
        "--upgrade-only",
        action="store_true",
        help="Chỉ kiểm tra/dùng túi mở rộng, không bán item",
    )
    parser.add_argument(
        "--max-characters",
        type=int,
        default=0,
        help="Số nhân vật tối đa mỗi tài khoản; 0 là tất cả (mặc định)",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Chỉ liệt kê item khớp, không gửi lệnh bán",
    )
    return parser


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    if not os.path.isfile(args.csv_file):
        logger.error("Không tìm thấy file CSV: %s", args.csv_file)
        return 2

    try:
        accounts = read_accounts(args.csv_file)
        item_ids = parse_item_ids(args.item_ids) if args.item_ids else read_item_ids(args.items_file)
    except (OSError, ValueError) as exc:
        logger.error("Không đọc được cấu hình: %s", exc)
        return 2

    if not accounts:
        logger.error("CSV không có tài khoản hợp lệ")
        return 2
    if not item_ids:
        logger.error("Danh sách Item ID trống; không thực hiện bán")
        return 2
    if args.max_characters < 0:
        logger.error("--max-characters không được là số âm")
        return 2

    logger.info(
        "ITEM SELL: bắt đầu accounts=%s item_ids=%s scope=%s mode=%s",
        len(accounts),
        len(item_ids),
        args.scope,
        "DRY-RUN" if args.dry_run else ("UPGRADE-ONLY" if args.upgrade_only else "SELL"),
    )

    total_characters = 0
    total_matched = 0
    total_sold = 0
    total_failed = 0
    total_upgrade_bags_used = 0
    total_upgrade_bags_failed = 0
    session_failures = 0

    for account_index, (username, password) in enumerate(accounts, start=1):
        logger.info("ITEM SELL: tài khoản %s (%s/%s)", username, account_index, len(accounts))
        first_session = CharacterSession(
            username, args.host, args.port, args.socket_timeout
        )
        if not first_session.connect_and_login(password, args.login_timeout):
            first_session.disconnect()
            session_failures += 1
            continue
        names = [summary.name for summary in first_session.controller.character_list]
        if args.max_characters:
            names = names[:args.max_characters]
        logger.info("ITEM SELL: tài khoản %s có %s nhân vật: %s", username, len(names), names)
        if not names:
            first_session.disconnect()
            continue

        for character_index, character_name in enumerate(names, start=1):
            logger.info(
                "ITEM SELL: vào nhân vật %s (%s/%s)",
                character_name,
                character_index,
                len(names),
            )
            result = sell_character_items(
                username,
                password,
                character_name,
                item_ids,
                args,
                existing_session=first_session if character_index == 1 else None,
            )
            if character_index == 1:
                first_session = None
            if result is None:
                logger.warning(
                    "ITEM SELL: phiên đầu của %s chưa đọc/xử lý được dữ liệu; chờ %.0f giây rồi thử lại nhân vật",
                    character_name,
                    args.character_delay,
                )
                time.sleep(args.character_delay)
                result = sell_character_items(
                    username,
                    password,
                    character_name,
                    item_ids,
                    args,
                )
            total_characters += 1
            if result is None:
                session_failures += 1
            else:
                total_matched += result.matched
                total_sold += result.sold
                total_failed += result.failed
                total_upgrade_bags_used += result.upgrade_bags_used
                total_upgrade_bags_failed += result.upgrade_bags_failed
            if character_index < len(names):
                time.sleep(args.character_delay)

        if account_index < len(accounts):
            time.sleep(args.account_delay)

    logger.info(
        "ITEM SELL SUMMARY: accounts=%s characters=%s matched=%s sold=%s failed=%s "
        "upgrade_bags_used=%s upgrade_bags_failed=%s session_errors=%s",
        len(accounts),
        total_characters,
        total_matched,
        total_sold,
        total_failed,
        total_upgrade_bags_used,
        total_upgrade_bags_failed,
        session_failures,
    )
    return 1 if total_failed or session_failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
