#!/usr/bin/env python3
"""Nhận mã quà tặng Okanechan cho nhân vật level cao nhất mỗi tài khoản.

Luồng mỗi tài khoản:
  1. Đăng nhập, chọn nhân vật level cao nhất và tải map.
  2. Về Làng Tone (map 22), mở menu Okanechan (NPC 24).
  3. Chọn ``Mã quà tặng`` và gửi GIFT_CODE qua hộp nhập command 92.
  4. Đăng nhập lại bằng client hành trang, chỉ xóa các item có ID nằm trong
     delllllllllll.txt bằng command 14.
  5. Dùng chính phiên hành trang đó để nhận thư quà tặng.

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
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
# Chờ ngắn giữa phiên giftcode và phiên hành trang. Nếu server báo đăng nhập
# quá nhanh thì tăng lại lên 5-11 giây.
LOGIN_DELAY = 3.0
MAIL_SESSION_DELAY = 1.5
MAIL_RETRY_DELAY = 2.0
MAIL_RETRY_ATTEMPTS = 2
MAP_WAIT_TIMEOUT = 20.0
GIFT_SESSION_ATTEMPTS = 3
GIFT_RETRY_DELAY = 2.0
MENU_TIMEOUT = 20.0
THREAD_START_DELAY = 2.0
GIFT_CODE = "trungthu"
FAILED_ACCOUNTS_FILE = ROOT_DIR / "nhangiftcode-failed.csv"

CMD_TEXT_BOX = 92
CMD_SERVER_INFO = -24
GIFT_SUCCESS = "success"
GIFT_FAILED = "failed"
GIFT_UNKNOWN = "unknown"

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
            return GIFT_SUCCESS
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


def _retryable_gift_error(message: str) -> bool:
    normalized = normalize_text(message)
    return any(term in normalized for term in (
        "timeout",
        "socket",
        "winerror",
        "khong ket noi",
        "khong chon duoc nhan vat",
        "tai map",
        "chuyen map",
        "khong ve duoc lang tone",
        "khong tim thay okanechan",
        "menu okanechan",
        "hop nhap ma",
    ))


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
                        status == GIFT_SUCCESS
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
                        GIFT_FAILED: "❌ Mã quà tặng thất bại",
                        GIFT_UNKNOWN: (
                            "⚠️ Server không trả kết quả mã quà, "
                            "sẽ xác minh qua thư"
                        ),
                    }[status]
                    print(f"    {prefix}: {message}")
                    if status in (GIFT_SUCCESS, GIFT_UNKNOWN):
                        return result
                    if not _retryable_gift_error(message):
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
        except (AttributeError, ConnectionError, OSError, RuntimeError) as exc:
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
    client = item_module.NSOClient(host, port)
    result = {
        "matched": 0,
        "deleted": 0,
        "delete_failed": 0,
        "mail": {},
        "mail_errors": [],
        "error": None,
    }
    try:
        if not client.login(username, password):
            result["error"] = "Không đăng nhập được client hành trang"
            return result
        if character_name not in client.characters:
            result["error"] = f"Không còn thấy nhân vật {character_name} trong client hành trang"
            return result
        if not client.select_character(character_name):
            result["error"] = f"Không chọn được nhân vật {character_name} để dọn item"
            return result

        targets = [
            item for item in client.bag
            if item is not None and item.template_id in item_ids
        ]
        result["matched"] = len(targets)
        print(f"  🧹 Hành trang: tìm thấy {len(targets)} item thuộc danh sách cần xóa")

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
    except Exception as exc:
        result["error"] = str(exc)
        return result
    finally:
        client.disconnect()


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


def process_account(account_number, total_accounts, username, password, item_ids):
    """Xử lý một tài khoản; không ghi file dùng chung trong worker."""
    totals = {
        "accounts": 0,
        "gift_success": 0,
        "gift_failed": 0,
        "gift_unknown": 0,
        "matched": 0,
        "deleted": 0,
        "delete_failed": 0,
        "mail_claimed": 0,
        "mail_deleted": 0,
        "mail_kept": 0,
        "mail_bag_full": 0,
        "failed": 0,
    }
    failures = []
    failed_accounts = []

    def record_failure(reason):
        reason = str(reason).strip() or "Lỗi không rõ nguyên nhân"
        failures.append(f"{username}: {reason}")
        failed_accounts.append((username, password, reason))

    print(f"\n[{account_number}/{total_accounts}] Tài khoản {username}")
    gift = gift_stage(DEFAULT_HOST, DEFAULT_PORT, username, password)
    if not gift["ready"]:
        totals["failed"] += 1
        record_failure(gift["message"])
        return {"totals": totals, "failures": failures, "failed_accounts": failed_accounts}

    totals["accounts"] += 1
    totals[f"gift_{gift['status']}"] += 1

    # Không bán item nếu server đã xác nhận mã thất bại. Đây là bước có
    # thay đổi hành trang, chỉ nên chạy khi mã đã được nhận hoặc server
    # chưa trả kết quả rõ ràng (trường hợp quà có thể đã vào thư).
    if gift["status"] == GIFT_FAILED:
        totals["failed"] += 1
        record_failure(
            f"Nhân vật {gift['character_name']}: {gift['message']}"
        )
        print("  ⏭️ Bỏ qua dọn item/nhận thư vì mã quà tặng thất bại")
        return {"totals": totals, "failures": failures, "failed_accounts": failed_accounts}

    # Gift code vừa gửi có thể còn đang được server ghi vào hộp thư;
    # đồng thời server giới hạn đăng nhập lại quá nhanh.
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
        totals["failed"] += 1
        record_failure(
            f"Nhân vật {gift['character_name']}: {cleaned['error']}"
        )
        return {"totals": totals, "failures": failures, "failed_accounts": failed_accounts}

    totals["matched"] += cleaned["matched"]
    totals["deleted"] += cleaned["deleted"]
    totals["delete_failed"] += cleaned["delete_failed"]
    mail_stats = cleaned["mail"]
    totals["mail_claimed"] += mail_stats.get("claimed", 0)
    totals["mail_deleted"] += mail_stats.get("deleted", 0)
    totals["mail_kept"] += mail_stats.get("kept", 0)
    totals["mail_bag_full"] += mail_stats.get("bag_full", 0)
    mail_results = mail_stats.get("mail_results", [])
    gift_mail_records = [
        mail for mail in mail_results if is_giftcode_mail(mail)
    ]
    gift_mail = next(
        (
            reward for reward in mail_stats.get("rewards", [])
            if is_giftcode_mail(reward)
        ),
        None,
    )
    if gift["status"] == GIFT_UNKNOWN:
        if gift_mail is not None:
            totals["gift_unknown"] -= 1
            totals["gift_success"] += 1
            gift["status"] = GIFT_SUCCESS
            print(
                f"  ✅ Xác nhận mã quà qua thư id={gift_mail['mail_id']} "
                "dù server không trả packet kết quả."
            )
        elif mail_stats.get("rewards"):
            print(
                "  ⚠️ Đã đọc được chi tiết thư nhưng chưa nhận diện được "
                "đó là thư giftcode."
            )

    # Chỉ đưa vào file chạy lại khi riêng thư giftcode chưa nhận được.
    # Các thư khác bị đầy rương/lỗi không làm tài khoản bị ghi lại.
    not_received_gift_mails = [
        mail for mail in gift_mail_records
        if not mail.get("is_received") and not mail.get("claimed")
    ]
    gift_retry_reason = None
    if not gift_mail_records and not gift.get("already_received"):
        gift_retry_reason = "không tìm thấy thư giftcode trong danh sách thư"
    elif not_received_gift_mails and not gift.get("already_received"):
        mail_ids = ", ".join(
            str(mail["mail_id"]) for mail in not_received_gift_mails
        )
        gift_retry_reason = (
            f"chưa nhận được thư giftcode (mail_id={mail_ids})"
        )
    if gift_retry_reason:
        totals["failed"] += 1
        record_failure(
            f"Nhân vật {gift['character_name']}: {gift_retry_reason}"
        )

    return {"totals": totals, "failures": failures, "failed_accounts": failed_accounts}


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

    try:
        accounts = read_accounts(args.csv_file)
        item_module = load_item_delete_module()
        item_ids = item_module.read_item_ids(DEFAULT_ITEMS_FILE)
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

    print(
        f"NSO NHẬN GIFTCODE: {len(accounts)} tài khoản | "
        f"mã={GIFT_CODE} | item cần xóa={len(item_ids)}"
    )
    totals = {
        "accounts": 0,
        "gift_success": 0,
        "gift_failed": 0,
        "gift_unknown": 0,
        "matched": 0,
        "deleted": 0,
        "delete_failed": 0,
        "mail_claimed": 0,
        "mail_deleted": 0,
        "mail_kept": 0,
        "mail_bag_full": 0,
        "failed": 0,
    }
    failures = []
    failed_accounts = []

    start_time = time.monotonic()

    def run_account(account_number, username, password):
        target_time = start_time + (account_number - 1) * args.thread_delay
        wait = target_time - time.monotonic()
        if wait > 0:
            print(
                f"[{account_number}/{len(accounts)}] Chờ {wait:.1f} giây "
                "trước khi khởi động luồng"
            )
            time.sleep(wait)
        return process_account(
            account_number,
            len(accounts),
            username,
            password,
            item_ids,
        )

    with ThreadPoolExecutor(max_workers=args.luong, thread_name_prefix="giftcode") as executor:
        jobs = {
            executor.submit(
                run_account,
                account_number,
                username,
                password,
            ): (username, password)
            for account_number, (username, password) in enumerate(accounts, start=1)
        }
        for future in as_completed(jobs):
            username, password = jobs[future]
            try:
                result = future.result()
            except Exception as exc:
                # Worker lỗi ngoài dự kiến vẫn được ghi vào danh sách chạy lại.
                result = {
                    "totals": {key: 0 for key in totals},
                    "failures": [f"{username}: Lỗi worker: {exc}"],
                    "failed_accounts": [(username, password, f"Lỗi worker: {exc}")],
                }
                result["totals"]["failed"] = 1
            for key in totals:
                totals[key] += result["totals"].get(key, 0)
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
