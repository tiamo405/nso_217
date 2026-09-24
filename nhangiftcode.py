#!/usr/bin/env python3
"""Nhận mã quà tặng Okanechan cho nhân vật level cao nhất mỗi tài khoản.

Luồng mỗi tài khoản:
  1. Đăng nhập, chọn nhân vật level cao nhất và tải map.
  2. Về Làng Tone (map 22), mở menu Okanechan (NPC 24).
  3. Chọn ``Mã quà tặng`` và gửi GIFT_CODE qua hộp nhập command 92.
  4. Đăng nhập lại bằng client hành trang, chỉ xóa các item có ID nằm trong
     delllllllllll.txt bằng command 14.
  5. Đóng phiên hành trang, mở phiên mới để nhận thư quà tặng.
  6. Nếu NPC báo đã dùng một lần mà không còn thư, kiểm tra item_open.txt.

Mã quà được đặt tại GIFT_CODE để dễ sửa, không nhận qua command line.
"""

import argparse
import csv
import importlib.util
import os
import sys
import tempfile
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

from hoatdong import NSOMessage, NSOReader, read_accounts
from mail_client import NSOMailClient
from nhanexp_and_doiyenquaxu import (
    OKANECHAN_NPC_ID,
    TONE_MAP_ID,
    OfflineExpClient,
    normalize_text,
)


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-hoatdong.csv"
DEFAULT_ITEMS_FILE = ROOT_DIR / "delllllllllll.txt"
DEFAULT_OPEN_ITEMS_FILE = ROOT_DIR / "item_open.txt"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
# Chờ ngắn giữa phiên giftcode và phiên hành trang. Nếu server báo đăng nhập
# quá nhanh thì tăng lại lên 5-11 giây.
LOGIN_DELAY = 3.0
MAIL_SESSION_DELAY = 1.5
STEP_RETRY_DELAY = 2.0
MAIL_RETRY_DELAY = STEP_RETRY_DELAY
MAIL_RETRY_ATTEMPTS = 2
ITEM_SESSION_ATTEMPTS = 2
MAP_WAIT_TIMEOUT = 20.0
GIFT_SESSION_ATTEMPTS = 3
GIFT_RETRY_DELAY = STEP_RETRY_DELAY
ACCOUNT_RETRY_ATTEMPTS = 3
ACCOUNT_RETRY_DELAY = 5.0
MENU_TIMEOUT = 20.0
THREAD_START_DELAY = 2.0
GIFT_CODE = "trungthu"
FAILED_ACCOUNTS_FILE = ROOT_DIR / "nhangiftcode-failed.csv"

CMD_TEXT_BOX = 92
CMD_SERVER_INFO = -24
GIFT_SUCCESS = "success"
GIFT_FAILED = "failed"
GIFT_UNKNOWN = "unknown"
GIFT_ALREADY_RECEIVED = "already_received"

ACCOUNT_SUCCESS = "success"
ACCOUNT_BAG_FULL = "bag_full"
ACCOUNT_RETRY = "retry"

_ITEM_MODULE = None
_ITEM_TEMPLATE_LOCK = threading.RLock()


def write_failed_accounts(path, failed_accounts):
    """Ghi danh sách tài khoản chưa nhận giftcode để chạy lại."""
    path = Path(path)
    temporary_path = None
    try:
        with tempfile.NamedTemporaryFile(
            mode="w",
            encoding="utf-8-sig",
            newline="",
            dir=path.parent,
            prefix=f".{path.name}.",
            suffix=".tmp",
            delete=False,
        ) as handle:
            temporary_path = Path(handle.name)
            writer = csv.writer(handle)
            writer.writerow(("username", "password", "reason"))
            writer.writerows(failed_accounts)
        os.replace(temporary_path, path)
    except Exception:
        if temporary_path is not None:
            try:
                temporary_path.unlink()
            except OSError:
                pass
        raise


def is_giftcode_mail(mail):
    """Nhận diện riêng thư giftcode, không lẫn các thư quà khác."""
    text = normalize_text(
        f"{mail.get('title', '')} {mail.get('content', '')}"
    )
    return any(term in text for term in (
        "ma qua tang",
        "gift code",
        "giftcode",
        "trung thu",
    ))


def load_item_delete_module():
    """Nạp NSOClient/read_item_ids từ file xóa item hiện có.

    Tên file có dấu ``-`` nên không thể import bằng cú pháp Python thông
    thường. Nạp module động giúp dùng đúng parser hành trang và command 14,
    tránh chép lại một bản protocol khác trong file này.
    """
    global _ITEM_MODULE
    if _ITEM_MODULE is not None:
        return _ITEM_MODULE
    source = ROOT_DIR / "del-item-hanhtrang.py"
    spec = importlib.util.spec_from_file_location("gift_item_delete_logic", source)
    if spec is None or spec.loader is None:
        raise ImportError(f"Không thể nạp logic xóa item: {source}")
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)

    original_parse_templates = module.NSOClient._parse_item_templates
    original_parse_bag = module.NSOClient._parse_character_info_and_bag

    def parse_item_templates(client, data):
        with _ITEM_TEMPLATE_LOCK:
            return original_parse_templates(client, data)

    def parse_character_info_and_bag(client, reader):
        with _ITEM_TEMPLATE_LOCK:
            return original_parse_bag(client, reader)

    module.NSOClient._parse_item_templates = parse_item_templates
    module.NSOClient._parse_character_info_and_bag = parse_character_info_and_bag
    _ITEM_MODULE = module
    return module


class GiftCodeClient(OfflineExpClient):
    """Client map dùng để mở menu và gửi mã quà tặng."""

    def send(self, message):
        if self.sock is None or self.key is None:
            raise ConnectionError("Socket giftcode đã đóng")
        try:
            return super().send(message)
        except (AttributeError, ConnectionError, OSError):
            self.disconnect()
            raise

    def select_character_and_load_map(self, character_name: str) -> bool:
        try:
            return super().select_character_and_load_map(character_name)
        except (AttributeError, ConnectionError, OSError) as exc:
            print(f"    ⚠️ Chọn nhân vật/map lỗi: {exc}")
            self.disconnect()
            return False

    def _wait_for_map_change(self, old_map_id: int, timeout: float = MAP_WAIT_TIMEOUT):
        return super()._wait_for_map_change(old_map_id, timeout)

    def move_to_tone(self, max_steps: int = 20) -> bool:
        try:
            return super().move_to_tone(max_steps)
        except (AttributeError, ConnectionError, OSError) as exc:
            print(f"    ⚠️ Chuyển map lỗi: {exc}")
            self.disconnect()
            return False

    def request_okanechan_menu(self, timeout: float = MENU_TIMEOUT):
        try:
            return super().request_okanechan_menu(timeout)
        except (AttributeError, ConnectionError, OSError) as exc:
            self.disconnect()
            return None, str(exc)

    def receive(self, timeout: float = 10):
        command, data = super().receive(timeout)
        if command is None and self.last_receive_error not in (None, "timeout"):
            self.disconnect()
        return command, data

    def _classify_gift_message(self, message: str):
        normalized = normalize_text(message)
        already_received_terms = (
            "moi nguoi chi duoc su dung 1 lan",
            "chi duoc su dung 1 lan",
        )
        failure_terms = (
            "khong ton tai",
            "khong hop le",
            "da duoc su dung",
            "da su dung",
            "het han",
            "sai ma",
            "that bai",
            "khong duoc phep",
        )
        success_terms = (
            "thanh cong",
            "nhan duoc",
            "da nhan",
            "qua tang",
            "gift code",
            "giftcode",
        )
        if any(term in normalized for term in already_received_terms):
            return GIFT_ALREADY_RECEIVED
        if any(term in normalized for term in failure_terms):
            return GIFT_FAILED
        if "thu moi" in normalized:
            return GIFT_SUCCESS
        if any(term in normalized for term in success_terms):
            return GIFT_SUCCESS
        return GIFT_UNKNOWN

    def _wait_gift_code_input(self, timeout: float = MENU_TIMEOUT):
        """Chờ command 92 và trả về (prompt, textbox_id)."""
        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                return None, None, self._read_server_error(data)
            if command != CMD_TEXT_BOX or not data:
                continue
            try:
                reader = NSOReader(data)
                prompt = reader.read_utf()
                textbox_id = reader.read_short()
                return prompt, textbox_id, None
            except Exception as exc:
                return None, None, f"Hộp nhập mã không hợp lệ: {exc}"
        return None, None, "timeout chờ hộp nhập mã quà tặng"

    def _wait_gift_result(self, timeout: float = 8.0):
        """Đọc thông báo server sau khi gửi mã.

        Server dùng -26 cho lỗi và -24 cho thông báo như ``Bạn có thư mới``;
        packet menu/map có thể xen kẽ. Nếu hết thời gian không rõ kết quả,
        trả unknown để vẫn kiểm tra hộp thư ở bước sau.
        """
        deadline = time.time() + timeout
        messages = []
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command in (self.CMD_SERVER_ERROR, CMD_SERVER_INFO) and data:
                message = self._read_server_error(data)
                messages.append(message)
                print(f"    ↩ Server mã quà cmd={command}: {message}")
                status = self._classify_gift_message(message)
                if status != GIFT_UNKNOWN:
                    return status, message
                continue
        if messages:
            return GIFT_UNKNOWN, " | ".join(messages)
        return GIFT_UNKNOWN, "server chưa trả thông báo kết quả mã quà"

    def submit_gift_code(self, menu_index: int):
        self._choose_npc_menu(OKANECHAN_NPC_ID, menu_index)
        prompt, textbox_id, error = self._wait_gift_code_input()
        if error:
            return GIFT_FAILED, error

        print(f"    📝 Hộp nhập mã: {prompt or 'không có tiêu đề'}")
        message = NSOMessage(CMD_TEXT_BOX)
        message.write_short(textbox_id)
        message.write_utf(GIFT_CODE)
        self.send(message)
        print(f"    🎟️ Đã gửi mã quà tặng: {GIFT_CODE}")
        return self._wait_gift_result()

    def receive_gift_code(self):
        if self.map_state.map_id != TONE_MAP_ID:
            if not self.move_to_tone():
                return GIFT_FAILED, "Không về được Làng Tone (map 22)"
        print(f"    ✅ Đã ở Làng Tone (map {self.map_state.map_id})")

        npc = self.map_state.find_npc(OKANECHAN_NPC_ID)
        if npc is None:
            return GIFT_FAILED, f"Không tìm thấy Okanechan (NPC {OKANECHAN_NPC_ID})"
        self.move_character(npc.x, self.map_state.char_y)
        time.sleep(0.8)

        options, error = self.request_okanechan_menu()
        if options is None:
            return GIFT_FAILED, error
        print("    Menu Okanechan:")
        for index, option in enumerate(options):
            print(f"      [{index}] {option}")

        target_index = next(
            (
                index for index, option in enumerate(options)
                if "ma qua tang" in normalize_text(option)
            ),
            None,
        )
        if target_index is None:
            return GIFT_FAILED, "Không tìm thấy mục 'Mã quà tặng' trong menu Okanechan"
        print(f"    👉 Chọn Mã quà tặng ở menu index {target_index}")
        return self.submit_gift_code(target_index)


def gift_stage(host: str, port: int, username: str, password: str):
    """Đăng nhập và xử lý mã quà; mỗi lần retry dùng socket mới."""
    result = {
        "ready": False,
        "character_name": None,
        "status": GIFT_FAILED,
        "message": "",
        "already_received": False,
    }
    for attempt in range(1, GIFT_SESSION_ATTEMPTS + 1):
        client = GiftCodeClient(host, port)
        result["ready"] = False
        try:
            if not client.connect():
                result["message"] = "Không kết nối được server giftcode"
            elif not client.login(username, password):
                result["message"] = "Không kết nối/đăng nhập được"
            elif not client.characters:
                result["message"] = "Tài khoản không có nhân vật"
            else:
                character_name, character_level, _ = max(
                    client.characters,
                    key=lambda character: character[1],
                )
                result["character_name"] = character_name
                print(
                    f"  👤 Chọn nhân vật level cao nhất: "
                    f"{character_name} (level {character_level})"
                )
                if not client.select_character_and_load_map(character_name):
                    result["message"] = (
                        "Không chọn được nhân vật hoặc tải map"
                    )
                else:
                    result["ready"] = True
                    client.drain(1.0)
                    status, message = client.receive_gift_code()
                    result["status"] = status
                    result["message"] = message
                    result["already_received"] = (
                        status == GIFT_ALREADY_RECEIVED
                        and any(
                            term in normalize_text(message)
                            for term in (
                                "moi nguoi chi duoc su dung 1 lan",
                                "chi duoc su dung 1 lan",
                            )
                        )
                    )
                    prefix = {
                        GIFT_SUCCESS: "✅ Mã quà tặng thành công",
                        GIFT_ALREADY_RECEIVED: (
                            "ℹ️ Tài khoản đã sử dụng mã quà tặng trước đó"
                        ),
                        GIFT_FAILED: "❌ Mã quà tặng thất bại",
                        GIFT_UNKNOWN: (
                            "⚠️ Server không trả kết quả mã quà, "
                            "sẽ xác minh qua thư"
                        ),
                    }[status]
                    print(f"    {prefix}: {message}")
                    if status in (
                        GIFT_SUCCESS,
                        GIFT_ALREADY_RECEIVED,
                        GIFT_UNKNOWN,
                    ):
                        return result
        except (AttributeError, ConnectionError, OSError) as exc:
            result["ready"] = False
            result["message"] = str(exc)
            print(f"    ❌ Lỗi kết nối giftcode: {exc}")
        except Exception as exc:
            result["ready"] = False
            result["message"] = str(exc)
            print(f"    ❌ Lỗi luồng mã quà: {exc}")
        finally:
            client.disconnect()

        if attempt < GIFT_SESSION_ATTEMPTS:
            print(
                f"  ⚠️ Phiên giftcode lỗi, mở kết nối mới sau "
                f"{GIFT_RETRY_DELAY:g} giây "
                f"({attempt + 1}/{GIFT_SESSION_ATTEMPTS})"
            )
            time.sleep(GIFT_RETRY_DELAY)
    return result


def receive_mail_on_fresh_session(
    host: str,
    port: int,
    username: str,
    password: str,
    character_name: str,
):
    """Nhận thư bằng socket mới, tránh tái sử dụng phiên xóa item."""
    last_error = None
    for attempt in range(1, MAIL_RETRY_ATTEMPTS + 1):
        mail_client = NSOMailClient(host, port)
        try:
            if not mail_client.connect():
                raise ConnectionError("Không kết nối được phiên nhận thư")
            if not mail_client.login(username, password):
                raise ConnectionError("Không đăng nhập được phiên nhận thư")
            try:
                character_index = mail_client.characters.index(character_name)
            except ValueError as exc:
                raise RuntimeError(
                    f"Không tìm thấy nhân vật {character_name} trong phiên nhận thư"
                ) from exc
            if not mail_client.select_character(character_index):
                raise ConnectionError(
                    f"Không chọn được nhân vật {character_name} trong phiên nhận thư"
                )
            mail_client.receive_all_mail(
                delete_after_claim=True,
                include_read_mail_details=True,
            )
            if not mail_client.connected or mail_client.sock is None:
                raise ConnectionError("Socket phiên nhận thư đã đóng")
            return (
                dict(mail_client.last_mail_stats),
                list(mail_client.last_mail_errors),
            )
        except (
            AttributeError,
            ConnectionError,
            EOFError,
            OSError,
            RuntimeError,
            ValueError,
        ) as exc:
            last_error = exc
            if attempt < MAIL_RETRY_ATTEMPTS:
                print(
                    f"  ⚠️ Phiên thư lỗi ({exc}), thử lại sau "
                    f"{MAIL_RETRY_DELAY:g} giây ({attempt + 1}/"
                    f"{MAIL_RETRY_ATTEMPTS})"
                )
                time.sleep(MAIL_RETRY_DELAY)
        finally:
            mail_client.disconnect()
    raise RuntimeError(f"Không nhận được thư sau {MAIL_RETRY_ATTEMPTS} lần: {last_error}")


def inspect_bag_for_items(
    host: str,
    port: int,
    username: str,
    password: str,
    character_name: str,
    item_ids,
):
    """Kiểm tra item xác minh trong rương bằng phiên hành trang mới."""
    item_module = load_item_delete_module()
    last_error = None
    for attempt in range(1, ITEM_SESSION_ATTEMPTS + 1):
        client = item_module.NSOClient(host, port)
        try:
            if not client.login(username, password):
                raise ConnectionError("Không đăng nhập được phiên kiểm tra rương")
            if character_name not in client.characters:
                raise RuntimeError(
                    f"Không tìm thấy nhân vật {character_name} khi kiểm tra rương"
                )
            if not client.select_character(character_name):
                raise ConnectionError(
                    f"Không chọn được nhân vật {character_name} khi kiểm tra rương"
                )

            matched_ids = sorted({
                item.template_id
                for item in client.bag
                if item is not None and item.template_id in item_ids
            })
            print(f"  🔎 Kiểm tra rương: tìm thấy item xác minh {matched_ids}")
            return {"matched_ids": matched_ids, "error": None}
        except (
            AttributeError,
            ConnectionError,
            EOFError,
            OSError,
            RuntimeError,
            ValueError,
        ) as exc:
            last_error = exc
            if attempt < ITEM_SESSION_ATTEMPTS:
                print(
                    f"  ⚠️ Kiểm tra rương lỗi ({exc}), thử lại sau "
                    f"{GIFT_RETRY_DELAY:g} giây ({attempt + 1}/"
                    f"{ITEM_SESSION_ATTEMPTS})"
                )
                time.sleep(GIFT_RETRY_DELAY)
        finally:
            client.disconnect()
    return {"matched_ids": [], "error": str(last_error)}


def delete_items_and_receive_mail(
    host: str,
    port: int,
    username: str,
    password: str,
    character_name: str,
    item_ids,
):
    """Đăng nhập nhân vật đã chọn, xóa item cấu hình rồi nhận thư."""
    item_module = load_item_delete_module()
    result = {
        "matched": 0,
        "deleted": 0,
        "delete_failed": 0,
        "mail": {},
        "mail_errors": [],
        "error": None,
    }
    last_error = None
    for attempt in range(1, ITEM_SESSION_ATTEMPTS + 1):
        client = item_module.NSOClient(host, port)
        try:
            if not client.login(username, password):
                raise ConnectionError("Không đăng nhập được client hành trang")
            if character_name not in client.characters:
                raise RuntimeError(
                    f"Không còn thấy nhân vật {character_name} trong client hành trang"
                )
            if not client.select_character(character_name):
                raise ConnectionError(
                    f"Không chọn được nhân vật {character_name} để dọn item"
                )

            targets = [
                item for item in client.bag
                if item is not None and item.template_id in item_ids
            ]
            result["matched"] += len(targets)
            print(
                f"  🧹 Hành trang: tìm thấy {len(targets)} item thuộc danh sách cần xóa"
            )

            # Xóa từ slot cao xuống để không ảnh hưởng các slot chưa xử lý.
            for item in sorted(targets, key=lambda value: value.index, reverse=True):
                print(
                    f"    🗑️ Xóa item slot={item.index} id={item.template_id} "
                    f"({item.name}) số lượng={item.quantity} khóa={item.is_lock}"
                )
                if client.sell_item(item.index, item.quantity):
                    result["deleted"] += 1
                    print(f"    ✅ Đã xóa item id={item.template_id}")
                else:
                    result["delete_failed"] += 1
                    print(f"    ❌ Xóa item thất bại id={item.template_id}")

            client.disconnect()
            time.sleep(MAIL_SESSION_DELAY)
            mail_stats, mail_errors = receive_mail_on_fresh_session(
                host,
                port,
                username,
                password,
                character_name,
            )
            result["mail"] = mail_stats
            result["mail_errors"] = mail_errors
            return result
        except (
            AttributeError,
            ConnectionError,
            EOFError,
            OSError,
            RuntimeError,
            ValueError,
        ) as exc:
            last_error = exc
            if attempt < ITEM_SESSION_ATTEMPTS:
                print(
                    f"  ⚠️ Phiên hành trang/thư lỗi ({exc}), thử lại sau "
                    f"{GIFT_RETRY_DELAY:g} giây ({attempt + 1}/"
                    f"{ITEM_SESSION_ATTEMPTS})"
                )
                time.sleep(GIFT_RETRY_DELAY)
        finally:
            client.disconnect()
    result["error"] = str(last_error)
    return result


def build_parser():
    parser = argparse.ArgumentParser(
        description="Nhận gift code trungthu cho nhân vật level cao nhất mỗi tài khoản"
    )
    parser.add_argument("csv_file", nargs="?", type=Path, default=DEFAULT_CSV)
    parser.add_argument(
        "--max-accounts", type=int, default=0,
        help="Chỉ chạy N tài khoản đầu tiên; 0 là không giới hạn",
    )
    parser.add_argument(
        "--luong", type=int, default=1,
        help="Số luồng xử lý song song; mặc định 1",
    )
    parser.add_argument(
        "--thread-delay",
        type=float,
        default=THREAD_START_DELAY,
        help="Khoảng cách khởi động account giữa các luồng (mặc định: 2 giây)",
    )
    return parser


def _new_totals():
    return {
        "accounts": 0,
        "gift_success": 0,
        "gift_failed": 0,
        "gift_unknown": 0,
        "gift_already_received": 0,
        "matched": 0,
        "deleted": 0,
        "delete_failed": 0,
        "mail_claimed": 0,
        "mail_deleted": 0,
        "mail_kept": 0,
        "mail_bag_full": 0,
        "failed": 0,
    }


def _add_totals(target, source):
    for key in target:
        target[key] += source.get(key, 0)


def _gift_mail_records(mail_stats):
    """Lấy thư giftcode từ danh sách thư và reward đã đọc."""
    records = list(mail_stats.get("mail_results", []))
    reward_ids = {
        reward.get("mail_id")
        for reward in mail_stats.get("rewards", [])
        if is_giftcode_mail(reward)
    }
    return [
        mail for mail in records
        if is_giftcode_mail(mail) or mail.get("mail_id") in reward_ids
    ]


def _process_account_attempt(username, password, item_ids, open_item_ids):
    """Chạy một lượt đầy đủ; caller quyết định retry toàn tài khoản."""
    stats = _new_totals()
    gift = gift_stage(DEFAULT_HOST, DEFAULT_PORT, username, password)
    if not gift["ready"]:
        return {
            "outcome": ACCOUNT_RETRY,
            "reason": gift["message"],
            "gift": gift,
            "stats": stats,
        }

    if gift["status"] == GIFT_FAILED:
        return {
            "outcome": ACCOUNT_RETRY,
            "reason": (
                f"Nhân vật {gift['character_name']}: {gift['message']}"
            ),
            "gift": gift,
            "stats": stats,
        }

    print(f"  ⏳ Chờ {LOGIN_DELAY:g} giây trước khi dọn item/nhận thư...")
    time.sleep(LOGIN_DELAY)
    cleaned = delete_items_and_receive_mail(
        DEFAULT_HOST,
        DEFAULT_PORT,
        username,
        password,
        gift["character_name"],
        item_ids,
    )
    if cleaned["error"]:
        return {
            "outcome": ACCOUNT_RETRY,
            "reason": (
                f"Nhân vật {gift['character_name']}: {cleaned['error']}"
            ),
            "gift": gift,
            "stats": stats,
        }

    stats["matched"] += cleaned["matched"]
    stats["deleted"] += cleaned["deleted"]
    stats["delete_failed"] += cleaned["delete_failed"]
    mail_stats = cleaned["mail"]
    stats["mail_claimed"] += mail_stats.get("claimed", 0)
    stats["mail_deleted"] += mail_stats.get("deleted", 0)
    stats["mail_kept"] += mail_stats.get("kept", 0)
    stats["mail_bag_full"] += mail_stats.get("bag_full", 0)

    gift_mail_records = _gift_mail_records(mail_stats)
    received_gift_mails = [
        mail for mail in gift_mail_records
        if mail.get("is_received") or mail.get("claimed")
    ]
    if received_gift_mails:
        mail_ids = ", ".join(
            str(mail.get("mail_id")) for mail in received_gift_mails
        )
        print(f"  ✅ Xác nhận đã nhận thư giftcode: mail_id={mail_ids}")
        return {
            "outcome": ACCOUNT_SUCCESS,
            "reason": "đã nhận thư giftcode",
            "gift": gift,
            "stats": stats,
        }

    pending_gift_mails = [
        mail for mail in gift_mail_records
        if not mail.get("is_received") and not mail.get("claimed")
    ]
    bag_full_mails = [mail for mail in pending_gift_mails if mail.get("bag_full")]
    if bag_full_mails:
        mail_ids = ", ".join(
            str(mail.get("mail_id")) for mail in bag_full_mails
        )
        return {
            "outcome": ACCOUNT_BAG_FULL,
            "reason": f"rương đầy, giữ lại thư giftcode mail_id={mail_ids}",
            "gift": gift,
            "stats": stats,
        }

    if pending_gift_mails:
        mail_ids = ", ".join(
            str(mail.get("mail_id")) for mail in pending_gift_mails
        )
        return {
            "outcome": ACCOUNT_RETRY,
            "reason": f"chưa nhận được thư giftcode mail_id={mail_ids}",
            "gift": gift,
            "stats": stats,
        }

    if gift["already_received"]:
        bag = inspect_bag_for_items(
            DEFAULT_HOST,
            DEFAULT_PORT,
            username,
            password,
            gift["character_name"],
            open_item_ids,
        )
        if bag["error"]:
            return {
                "outcome": ACCOUNT_RETRY,
                "reason": (
                    f"NPC báo đã dùng một lần nhưng kiểm tra rương lỗi: "
                    f"{bag['error']}"
                ),
                "gift": gift,
                "stats": stats,
            }
        if bag["matched_ids"]:
            ids = ", ".join(str(item_id) for item_id in bag["matched_ids"])
            print(f"  ✅ Xác nhận nhận quà qua rương, item_id={ids}")
            return {
                "outcome": ACCOUNT_SUCCESS,
                "reason": f"đã có item xác minh trong rương: {ids}",
                "gift": gift,
                "stats": stats,
            }
        return {
            "outcome": ACCOUNT_RETRY,
            "reason": "NPC báo đã dùng một lần nhưng không có thư và item xác minh",
            "gift": gift,
            "stats": stats,
        }

    return {
        "outcome": ACCOUNT_RETRY,
        "reason": "không tìm thấy thư giftcode sau khi gửi mã",
        "gift": gift,
        "stats": stats,
    }


def process_account(
    account_number,
    total_accounts,
    username,
    password,
    item_ids,
    open_item_ids,
):
    """Chỉ chuyển account sau thành công, đầy rương hoặc hết retry."""
    totals = _new_totals()
    totals["accounts"] = 1
    failures = []
    failed_accounts = []

    print(f"\n[{account_number}/{total_accounts}] Tài khoản {username}")
    last_attempt = None
    for account_attempt in range(ACCOUNT_RETRY_ATTEMPTS + 1):
        if account_attempt:
            print(
                f"  🔁 Retry toàn tài khoản sau {ACCOUNT_RETRY_DELAY:g} giây "
                f"({account_attempt}/{ACCOUNT_RETRY_ATTEMPTS})"
            )
            time.sleep(ACCOUNT_RETRY_DELAY)

        try:
            attempt = _process_account_attempt(
                username,
                password,
                item_ids,
                open_item_ids,
            )
        except Exception as exc:
            attempt = {
                "outcome": ACCOUNT_RETRY,
                "reason": f"lỗi ngoài dự kiến: {exc}",
                "gift": {
                    "status": GIFT_FAILED,
                    "message": str(exc),
                    "already_received": False,
                },
                "stats": _new_totals(),
            }
        _add_totals(totals, attempt["stats"])
        last_attempt = attempt

        if attempt["outcome"] in (ACCOUNT_SUCCESS, ACCOUNT_BAG_FULL):
            break
        if account_attempt < ACCOUNT_RETRY_ATTEMPTS:
            print(f"  ⚠️ {attempt['reason']}; sẽ retry toàn tài khoản")

    gift = last_attempt["gift"]
    if last_attempt["outcome"] == ACCOUNT_SUCCESS:
        totals["gift_success"] += 1
        if gift["status"] == GIFT_ALREADY_RECEIVED:
            totals["gift_already_received"] += 1
    else:
        totals["failed"] += 1
        if gift["status"] == GIFT_FAILED:
            totals["gift_failed"] += 1
        elif gift["status"] == GIFT_UNKNOWN:
            totals["gift_unknown"] += 1
        elif gift["status"] == GIFT_ALREADY_RECEIVED:
            totals["gift_already_received"] += 1

        reason = last_attempt["reason"]
        if last_attempt["outcome"] == ACCOUNT_BAG_FULL:
            reason = f"{reason}; lưu lại để xử lý sau"
        else:
            reason = (
                f"hết {ACCOUNT_RETRY_ATTEMPTS} lần retry toàn tài khoản: "
                f"{reason}"
            )
        failures.append(f"{username}: {reason}")
        failed_accounts.append((username, password, reason))

    return {
        "totals": totals,
        "failures": failures,
        "failed_accounts": failed_accounts,
    }


def main(argv=None):
    args = build_parser().parse_args(argv)
    if args.max_accounts < 0:
        print("❌ --max-accounts không được âm")
        return 2
    if args.luong < 1:
        print("❌ --luong phải lớn hơn 0")
        return 2
    if args.thread_delay < 0:
        print("❌ --thread-delay không được âm")
        return 2
    if not args.csv_file.is_file():
        print(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if not DEFAULT_ITEMS_FILE.is_file():
        print(f"❌ Không tìm thấy file item: {DEFAULT_ITEMS_FILE}")
        return 2
    if not DEFAULT_OPEN_ITEMS_FILE.is_file():
        print(f"❌ Không tìm thấy file item xác minh: {DEFAULT_OPEN_ITEMS_FILE}")
        return 2

    try:
        accounts = read_accounts(args.csv_file)
        item_module = load_item_delete_module()
        item_ids = item_module.read_item_ids(DEFAULT_ITEMS_FILE)
        open_item_ids = item_module.read_item_ids(DEFAULT_OPEN_ITEMS_FILE)
    except Exception as exc:
        print(f"❌ Không đọc được dữ liệu đầu vào: {exc}")
        return 2
    if args.max_accounts:
        accounts = accounts[:args.max_accounts]
    if not accounts:
        print("❌ CSV không có tài khoản hợp lệ")
        return 2
    if not item_ids:
        print("❌ delllllllllll.txt không có item ID hợp lệ")
        return 2
    if not open_item_ids:
        print("❌ item_open.txt không có item ID hợp lệ")
        return 2

    print(
        f"NSO NHẬN GIFTCODE: {len(accounts)} tài khoản | "
        f"mã={GIFT_CODE} | item cần xóa={len(item_ids)} | "
        f"item xác minh={len(open_item_ids)}"
    )
    totals = _new_totals()
    failures = []
    failed_accounts = []

    lane_count = min(args.luong, len(accounts))
    lanes = [[] for _ in range(lane_count)]
    for account_number, (username, password) in enumerate(accounts, start=1):
        lanes[(account_number - 1) % lane_count].append(
            (account_number, username, password)
        )

    def run_lane(lane_number, lane_accounts):
        if lane_number:
            time.sleep(lane_number * args.thread_delay)
        results = []
        for account_number, username, password in lane_accounts:
            try:
                results.append(
                    process_account(
                        account_number,
                        len(accounts),
                        username,
                        password,
                        item_ids,
                        open_item_ids,
                    )
                )
            except Exception as exc:
                reason = f"Lỗi worker: {exc}"
                results.append({
                    "totals": {**_new_totals(), "accounts": 1, "failed": 1},
                    "failures": [f"{username}: {reason}"],
                    "failed_accounts": [(username, password, reason)],
                })
        return results

    with ThreadPoolExecutor(
        max_workers=lane_count,
        thread_name_prefix="giftcode",
    ) as executor:
        jobs = {
            executor.submit(run_lane, lane_number, lane_accounts): lane_number
            for lane_number, lane_accounts in enumerate(lanes)
        }
        for future in as_completed(jobs):
            for result in future.result():
                _add_totals(totals, result["totals"])
                failures.extend(result["failures"])
                failed_accounts.extend(result["failed_accounts"])

    try:
        write_failed_accounts(FAILED_ACCOUNTS_FILE, failed_accounts)
        print(
            f"\n🔁 Đã ghi {len(failed_accounts)} tài khoản chưa nhận giftcode vào "
            f"{FAILED_ACCOUNTS_FILE}"
        )
    except OSError as exc:
        print(f"\n⚠️ Không ghi được file tài khoản lỗi: {exc}")

    print(
        "\nTỔNG KẾT: "
        f"tài khoản={totals['accounts']}, gift thành công={totals['gift_success']}, "
        f"gift thất bại={totals['gift_failed']}, gift chưa xác định={totals['gift_unknown']}, "
        f"đã dùng trước đó={totals['gift_already_received']}, "
        f"item khớp={totals['matched']}, item đã xóa={totals['deleted']}, "
        f"item lỗi={totals['delete_failed']}, thư nhận={totals['mail_claimed']}, "
        f"thư xóa={totals['mail_deleted']}, thư giữ lại={totals['mail_kept']}, "
        f"chật rương={totals['mail_bag_full']}, lỗi={totals['failed']}"
    )
    if failures:
        print("\nCHI TIẾT CẦN KIỂM TRA:")
        for failure in failures:
            print(f"  - {failure}")
    return 0 if totals["failed"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
