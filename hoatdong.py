#!/usr/bin/env python3
"""Nhận quà Hoạt Động, Điểm Danh và Phúc Lợi của NSO Mobile 4.1.1.

Protocol được đối chiếu từ GameAssembly.dll/IL2CPP:
  - cmd -46, action 0: danh mục Hoạt Động
  - cmd -46, action 1, int subID: chi tiết mục
  - cmd -46, action 2, int activityId, int milestoneId: nhận rương mốc
  - cmd -46, action 3, int activityId, int day: nhận quà điểm danh
  - cmd -46, action 99: lấy danh mục Phúc Lợi
  - cmd -47, action 2, int subId, int packageId: nhận gói Phúc Lợi
  - cmd 107, byte confirmId: xác nhận hộp thoại nhận gói

Mặc định script xử lý mọi tài khoản trong account-hoatdong.csv và mọi nhân vật.
Chạy ``python3 hoatdong.py --dry-run`` để chỉ xem trạng thái, không nhận quà.
"""

import argparse
import csv
import random
import socket
import struct
import time
from dataclasses import dataclass
from io import BytesIO
from pathlib import Path


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-hoatdong.csv"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
DEFAULT_LOGIN_DELAY = 11.0  # Server chặn nếu đăng nhập lại trong vòng 10 giây


class NSOMessage:
    def __init__(self, command: int):
        self.command = command
        self.buffer = BytesIO()

    def write_byte(self, value: int):
        self.buffer.write(struct.pack("b", value))

    def write_int(self, value: int):
        self.buffer.write(struct.pack(">i", value))

    def write_short(self, value: int):
        self.buffer.write(struct.pack(">h", value))

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

    def read_int(self) -> int:
        return struct.unpack(">i", self._read_exact(4))[0]

    def read_long(self) -> int:
        return struct.unpack(">q", self._read_exact(8))[0]

    def read_short(self) -> int:
        return struct.unpack(">h", self._read_exact(2))[0]

    def read_boolean(self) -> bool:
        return self.read_ubyte() != 0

    def read_utf(self) -> str:
        size = self.read_short()
        if size < 0:
            raise ValueError(f"Độ dài UTF âm: {size}")
        return self._read_exact(size).decode("utf-8", errors="replace")

    def remaining(self) -> int:
        position = self.buffer.tell()
        self.buffer.seek(0, 2)
        end = self.buffer.tell()
        self.buffer.seek(position)
        return end - position


@dataclass
class ActivitySubTab:
    main_id: int
    main_name: str
    sub_id: int
    sub_name: str
    content_type: int
    is_new: bool


@dataclass
class Milestone:
    milestone_id: int
    required_progress: int
    claimable: bool
    claimed: bool


@dataclass
class AttendanceReward:
    day: int
    reward_type: int
    value: int
    item_id: int
    status: int

    @property
    def claimable(self) -> bool:
        return self.status == 1

    @property
    def claimed(self) -> bool:
        return self.status == 2


@dataclass
class TimelineDetail:
    activity_id: int
    name: str
    progress: int
    milestones: list


@dataclass
class AttendanceDetail:
    activity_id: int
    name: str
    rewards: list


@dataclass
class PackageEntry:
    package_id: int
    title: str
    status: int
    action_text: str

    @property
    def claimable(self) -> bool:
        # status=1 cũng được dùng cho gói có nút "Mua". Chỉ "Nhận" mới là
        # quà miễn phí đủ điều kiện; không tự động mua gói trả phí.
        return self.status == 1 and self.action_text.strip().casefold() == "nhận"

    @property
    def claimed(self) -> bool:
        return self.status == 2 or "đã nhận" in self.action_text.strip().casefold()


@dataclass
class PackageDetail:
    activity_id: int
    name: str
    packages: list
    current_progress: int


@dataclass
class FailureRecord:
    username: str
    character_index: int | None
    character_name: str | None
    stage: str
    reason: str


class NSOActivityClient:
    CMD_NOT_LOGIN = -29
    CMD_NOT_MAP = -28
    CMD_SUB_COMMAND = -30
    CMD_KEY_EXCHANGE = -27
    CMD_SERVER_ERROR = -26
    CMD_ACTIVITY = -46
    CMD_WELFARE = -47
    CMD_UI_CONFIRM = 107

    CMD_SET_CLIENT = -125
    CMD_LOGIN = -127
    CMD_SELECT_CHAR = -126

    ACT_CATEGORIES = 0
    ACT_DETAIL = 1
    ACT_CLAIM_MILESTONE = 2
    ACT_CLAIM_ATTENDANCE = 3

    CONTENT_TIMELINE = 1
    CONTENT_PACKAGES = 2
    CONTENT_ATTENDANCE = 4

    def __init__(self, host: str, port: int, quiet_packets: bool = True):
        self.host = host
        self.port = port
        self.quiet_packets = quiet_packets
        self.sock = None
        self.key = None
        self.key_read_position = 0
        self.key_write_position = 0
        self.characters = []
        self.pending_bootstrap = set()
        self.last_receive_error = None

    def connect(self) -> bool:
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.settimeout(15)
            self.sock.connect((self.host, self.port))
            self._exchange_key()
            return bool(self.key)
        except Exception as exc:
            print(f"  ❌ Không thể kết nối: {exc}")
            self.disconnect()
            return False

    def disconnect(self):
        if self.sock is not None:
            try:
                self.sock.close()
            except OSError:
                pass
        self.sock = None

    def _recv_exact(self, size: int) -> bytes:
        data = bytearray()
        while len(data) < size:
            chunk = self.sock.recv(size - len(data))
            if not chunk:
                raise ConnectionError(f"Socket đóng khi mới nhận {len(data)}/{size} byte")
            data.extend(chunk)
        return bytes(data)

    def _encrypt_byte(self, value: int) -> int:
        result = (self.key[self.key_write_position] ^ value) & 0xFF
        self.key_write_position = (self.key_write_position + 1) % len(self.key)
        return result

    def _decrypt_byte(self, value: int) -> int:
        result = (self.key[self.key_read_position] ^ value) & 0xFF
        self.key_read_position = (self.key_read_position + 1) % len(self.key)
        return result

    def _exchange_key(self):
        self.sock.sendall(struct.pack("b", self.CMD_KEY_EXCHANGE) + b"\x00\x00")
        command = struct.unpack("b", self._recv_exact(1))[0]
        if command != self.CMD_KEY_EXCHANGE:
            raise RuntimeError(f"Sai packet key exchange: command={command}")
        size = struct.unpack(">H", self._recv_exact(2))[0]
        payload = self._recv_exact(size)
        if not payload or payload[0] > len(payload) - 1:
            raise RuntimeError("Key exchange trả dữ liệu không hợp lệ")
        raw_key = list(payload[1:1 + payload[0]])
        for index in range(len(raw_key) - 1):
            raw_key[index + 1] ^= raw_key[index]
        self.key = raw_key
        self.key_read_position = 0
        self.key_write_position = 0

    def send(self, message: NSOMessage):
        packet = message.packet()
        encrypted = bytes(self._encrypt_byte(value) for value in packet)
        self.sock.sendall(encrypted)

    def receive(self, timeout: float = 10):
        self.last_receive_error = None
        self.sock.settimeout(timeout)
        try:
            command = self._decrypt_byte(self._recv_exact(1)[0])
            if command == 224:  # signed -32: packet dùng length int32
                command = self._decrypt_byte(self._recv_exact(1)[0])
                length_bytes = bytes(
                    self._decrypt_byte(value) for value in self._recv_exact(4)
                )
                size = int.from_bytes(length_bytes, "big")
            else:
                length_bytes = self._recv_exact(2)
                high = self._decrypt_byte(length_bytes[0])
                low = self._decrypt_byte(length_bytes[1])
                size = (high << 8) | low
            encrypted = self._recv_exact(size) if size else b""
            payload = bytes(self._decrypt_byte(value) for value in encrypted)
            if command >= 128:
                command -= 256
            return command, payload
        except socket.timeout:
            self.last_receive_error = "timeout"
            return None, None
        except Exception as exc:
            self.last_receive_error = str(exc)
            return None, None

    def drain(self, seconds: float = 1.5):
        deadline = time.time() + seconds
        while time.time() < deadline:
            command, _ = self.receive(max(0.15, deadline - time.time()))
            if command is None:
                break

    def _send_not_map(self, subcommand: int):
        message = NSOMessage(self.CMD_NOT_MAP)
        message.write_byte(subcommand)
        self.send(message)

    def _set_client_type(self):
        message = NSOMessage(self.CMD_NOT_LOGIN)
        message.write_byte(self.CMD_SET_CLIENT)
        message.write_byte(4)
        message.write_byte(1)
        message.write_boolean(True)
        message.write_int(480)
        message.write_int(800)
        message.write_boolean(True)
        message.write_boolean(True)
        message.write_utf("Unity Mobile")
        message.write_int(0)
        message.write_byte(0)
        message.write_byte(0)
        message.write_int(0)
        message.write_utf("0")
        self.send(message)

    def login(self, username: str, password: str) -> bool:
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
            if command == -43:
                continue
            if command == self.CMD_SERVER_ERROR:
                print(f"  ❌ Đăng nhập: {self._read_server_error(data)}")
                return False
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
                count = reader.read_byte()
                self.characters = []
                for _ in range(count):
                    reader.read_byte()
                    name = reader.read_utf()
                    school = reader.read_utf()
                    level = reader.read_ubyte()
                    for _ in range(4):
                        reader.read_short()
                    self.characters.append((name, level, school))
                return bool(self.characters)
        print(f"  ❌ Đăng nhập không hoàn tất: {self.last_receive_error or 'timeout'}")
        return False

    def select_character(self, index: int) -> bool:
        if index < 0 or index >= len(self.characters):
            return False
        message = NSOMessage(self.CMD_NOT_MAP)
        message.write_byte(self.CMD_SELECT_CHAR)
        message.write_utf(self.characters[index][0])
        self.send(message)
        deadline = time.time() + 25
        while time.time() < deadline:
            command, data = self.receive(min(5, max(0.2, deadline - time.time())))
            if command is None:
                continue
            if command == self.CMD_SERVER_ERROR:
                print(f"  ❌ Chọn nhân vật: {self._read_server_error(data)}")
                return False
            if command == self.CMD_SUB_COMMAND and data:
                if NSOReader(data).read_byte() == -127:
                    return True
        print("  ❌ Timeout khi chọn nhân vật")
        return False

    @staticmethod
    def _read_server_error(data: bytes) -> str:
        try:
            return NSOReader(data).read_utf()
        except Exception:
            return data.hex()

    def _send_activity(self, action: int, *integers: int):
        message = NSOMessage(self.CMD_ACTIVITY)
        message.write_byte(action)
        for value in integers:
            message.write_int(value)
        self.send(message)

    def _wait_activity(self, action: int, identifier=None, timeout: float = 12):
        return self._wait_command_action(
            self.CMD_ACTIVITY, action, identifier=identifier, timeout=timeout
        )

    def _wait_command_action(self, command_expected: int, action: int,
                             identifier=None, timeout: float = 12):
        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                return None, self._read_server_error(data)
            if command != command_expected or not data or data[0] != action:
                continue
            if identifier is not None:
                if len(data) < 5 or struct.unpack(">i", data[1:5])[0] != identifier:
                    continue
            return data, None
        return None, self.last_receive_error or "timeout"

    def request_welfare_categories(self):
        # Mobile gửi request qua TIN_TUC (-46), action 99; server phản hồi
        # danh mục bằng PHUC_LOI (-47), action 0.
        self._send_activity(99)
        data, error = self._wait_command_action(
            self.CMD_WELFARE, self.ACT_CATEGORIES
        )
        if data is None:
            raise RuntimeError(f"Không lấy được danh mục Phúc Lợi: {error}")
        return self._parse_categories(data, "Phúc Lợi")

    def request_categories(self):
        self._send_activity(self.ACT_CATEGORIES)
        data, error = self._wait_activity(self.ACT_CATEGORIES)
        if data is None:
            raise RuntimeError(f"Không lấy được danh mục Hoạt Động: {error}")
        return self._parse_categories(data, "Hoạt Động")

    @staticmethod
    def _parse_categories(data: bytes, label: str):
        reader = NSOReader(data)
        if reader.read_ubyte() != NSOActivityClient.ACT_CATEGORIES:
            raise ValueError(f"Sai action danh mục {label}")
        tabs = []
        main_count = reader.read_ubyte()
        for _ in range(main_count):
            main_id = reader.read_int()
            main_name = reader.read_utf()
            sub_count = reader.read_ubyte()
            for _ in range(sub_count):
                tabs.append(ActivitySubTab(
                    main_id=main_id,
                    main_name=main_name,
                    sub_id=reader.read_int(),
                    sub_name=reader.read_utf(),
                    content_type=reader.read_int(),
                    is_new=reader.read_boolean(),
                ))
        if reader.remaining():
            raise ValueError(f"Danh mục {label} còn thừa {reader.remaining()} byte")
        return tabs

    def request_detail(self, tab: ActivitySubTab):
        self._send_activity(self.ACT_DETAIL, tab.sub_id)
        data, error = self._wait_activity(self.ACT_DETAIL, tab.sub_id)
        if data is None:
            raise RuntimeError(f"Không lấy được chi tiết {tab.sub_name}: {error}")
        reader = NSOReader(data)
        reader.read_ubyte()
        activity_id = reader.read_int()
        name = reader.read_utf()
        content_type = reader.read_int()
        if content_type == self.CONTENT_TIMELINE:
            detail = self._parse_timeline(reader, activity_id, name)
        elif content_type == self.CONTENT_PACKAGES:
            detail = self._parse_packages(reader, activity_id, name)
        elif content_type == self.CONTENT_ATTENDANCE:
            detail = self._parse_attendance(reader, activity_id, name)
        else:
            raise ValueError(
                f"{name}: contentType={content_type} chưa hỗ trợ ({reader.remaining()} byte)"
            )
        if reader.remaining():
            raise ValueError(f"{name}: parser còn thừa {reader.remaining()} byte")
        return detail

    @staticmethod
    def _parse_packages(reader: NSOReader, activity_id: int, name: str):
        count = reader.read_byte()
        if count < 0:
            raise ValueError(f"Số gói âm: {count}")
        packages = []
        for _ in range(count):
            package_id = reader.read_int()
            title = reader.read_utf()
            reader.read_int()   # điều kiện/giá hiển thị
            reader.read_int()   # lượng thưởng
            reader.read_long()  # xu thưởng
            reader.read_long()  # yên thưởng
            item_count = reader.read_byte()
            if item_count < 0:
                raise ValueError(f"Số vật phẩm âm trong gói {package_id}: {item_count}")
            for _ in range(item_count):
                reader.read_int()  # item template id
                reader.read_int()  # số lượng
            status = reader.read_byte()
            action_text = reader.read_utf()
            packages.append(PackageEntry(package_id, title, status, action_text))

        # Server gửi thêm tiến độ hiện tại (giờ chơi hoặc Coin tích lũy).
        current_progress = reader.read_int() if reader.remaining() == 4 else 0
        return PackageDetail(activity_id, name, packages, current_progress)

    @staticmethod
    def _parse_timeline(reader: NSOReader, activity_id: int, name: str):
        progress = reader.read_int()
        daily_count = reader.read_byte()
        if daily_count < 0:
            raise ValueError(f"Số nhiệm vụ âm: {daily_count}")
        for _ in range(daily_count):
            reader.read_utf()
            reader.read_int()      # điểm mỗi lần tham gia
            reader.read_int()      # số lần hiện tại
            reader.read_int()      # số lần tối đa
            reader.read_boolean()  # hoàn thành
        milestone_count = reader.read_byte()
        if milestone_count < 0:
            raise ValueError(f"Số mốc âm: {milestone_count}")
        milestones = []
        for _ in range(milestone_count):
            milestone_id = reader.read_int()
            claimable = reader.read_boolean()
            claimed = reader.read_boolean()
            milestones.append(Milestone(
                milestone_id=milestone_id,
                required_progress=milestone_id,
                claimable=claimable,
                claimed=claimed,
            ))
        return TimelineDetail(activity_id, name, progress, milestones)

    @staticmethod
    def _parse_attendance(reader: NSOReader, activity_id: int, name: str):
        count = reader.read_byte()
        if count < 0:
            raise ValueError(f"Số ngày âm: {count}")
        rewards = []
        for _ in range(count):
            rewards.append(AttendanceReward(
                day=reader.read_byte(),
                reward_type=reader.read_ubyte(),
                value=reader.read_int(),
                item_id=reader.read_int(),
                status=reader.read_ubyte(),
            ))
        return AttendanceDetail(activity_id, name, rewards)

    def claim_milestone(self, activity_id: int, milestone_id: int):
        self._send_activity(self.ACT_CLAIM_MILESTONE, activity_id, milestone_id)
        # Response claim của một số server không lặp lại activityId ở offset 1.
        # Mỗi thời điểm chỉ có một claim đang chờ nên lọc theo action là đủ.
        return self._wait_activity(self.ACT_CLAIM_MILESTONE, timeout=5)

    def claim_attendance(self, activity_id: int, day: int):
        self._send_activity(self.ACT_CLAIM_ATTENDANCE, activity_id, day)
        return self._wait_activity(self.ACT_CLAIM_ATTENDANCE, timeout=5)

    def claim_package(self, sub_id: int, package_id: int):
        message = NSOMessage(self.CMD_WELFARE)
        message.write_byte(self.ACT_CLAIM_MILESTONE)
        message.write_int(sub_id)
        message.write_int(package_id)
        self.send(message)

        # Server không phát quà ngay mà mở OPEN_UI_CONFIRM_ID (cmd 107):
        # byte confirmId + UTF nội dung. Java client gửi lại cmd 107 cùng
        # confirmId khi người chơi bấm đồng ý.
        deadline = time.time() + 5
        confirmation_prompt = None
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                return None, self._read_server_error(data)
            if command == self.CMD_UI_CONFIRM and data:
                if confirmation_prompt is not None:
                    return None, "server gửi lặp hộp thoại confirm"
                try:
                    reader = NSOReader(data)
                    confirm_id = reader.read_ubyte()
                    prompt = reader.read_utf()
                except Exception as exc:
                    return None, f"confirm không hợp lệ: {exc}"
                normalized = prompt.strip().casefold()
                if "nhận gói" not in normalized or "mua" in normalized:
                    return None, f"từ chối confirm ngoài dự kiến: {prompt}"
                confirmation = NSOMessage(self.CMD_UI_CONFIRM)
                confirmation.write_byte(confirm_id)
                self.send(confirmation)
                confirmation_prompt = prompt
                # Chờ packet hoàn tất của chính gói này trước khi gửi gói kế
                # tiếp, nếu không action 2 cũ sẽ làm lệch hàng đợi confirm.
                deadline = time.time() + 5
                continue
            if (command == self.CMD_WELFARE and data
                    and data[0] == self.ACT_CLAIM_MILESTONE):
                if confirmation_prompt is not None:
                    return confirmation_prompt, None
                # Có thể còn packet hoàn tất cũ trong socket; chưa coi đây là
                # kết quả của request hiện tại nếu chưa thấy hộp thoại của nó.
                continue
        phase = "hoàn tất" if confirmation_prompt else "confirm"
        return None, self.last_receive_error or f"timeout chờ {phase}"


def read_accounts(csv_path: Path):
    accounts = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for line_number, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if len(row) < 2:
                raise ValueError(f"Dòng {line_number} của CSV thiếu username/password")
            username, password = row[0].strip(), row[1].strip()
            if username.lower() == "username" and password.lower() == "password":
                continue
            if username and password:
                accounts.append((username, password))
    return accounts


def find_daily_tabs(tabs):
    timeline = next((tab for tab in tabs
                     if tab.content_type == NSOActivityClient.CONTENT_TIMELINE
                     and tab.main_name.casefold() == "hàng ngày"), None)
    attendance = next((tab for tab in tabs
                       if tab.content_type == NSOActivityClient.CONTENT_ATTENDANCE
                       and tab.main_name.casefold() == "hàng ngày"), None)
    return timeline, attendance


def find_welfare_tabs(tabs):
    wanted = {
        8: (5, "Tân Thủ"),
        20: (6, "Tích giờ chơi"),
        21: (6, "Nạp tháng"),
        22: (6, "Nạp tổng"),
    }
    found = {}
    for tab in tabs:
        expected = wanted.get(tab.sub_id)
        if expected is None:
            continue
        expected_main_id, expected_sub_name = expected
        # Tên main tab từ server từng xuất hiện cả "Tích lũy" và "Tích luỹ".
        # ID là khóa protocol ổn định; vẫn kiểm tra tên mục con để tránh gửi nhầm.
        if (tab.main_id == expected_main_id
                and tab.sub_name.casefold() == expected_sub_name.casefold()):
            found[tab.sub_id] = tab
    return found


def print_timeline(detail: TimelineDetail):
    print(f"    Hoạt Động: {detail.progress}/100 điểm")
    for item in sorted(detail.milestones, key=lambda value: value.required_progress):
        if item.claimed:
            state = "đã nhận"
        elif item.claimable:
            state = "CÓ THỂ NHẬN"
        else:
            state = "chưa đủ điều kiện"
        print(f"      - Rương mốc {item.required_progress}: {state}")


def print_attendance(detail: AttendanceDetail):
    claimable = [reward.day for reward in detail.rewards if reward.claimable]
    claimed = [reward.day for reward in detail.rewards if reward.claimed]
    locked = [reward.day for reward in detail.rewards
              if not reward.claimable and not reward.claimed]
    print(f"    Điểm Danh: nhận được={claimable or 'không'}")
    print(f"      đã nhận={claimed or 'không'}")
    print(f"      chưa đủ điều kiện={locked or 'không'}")


def print_packages(detail: PackageDetail):
    suffix = f"; tiến độ={detail.current_progress}" if detail.current_progress else ""
    print(f"    Phúc Lợi → {detail.name}{suffix}")
    for package in detail.packages:
        if package.claimed:
            state = "đã nhận"
        elif package.claimable:
            state = "CÓ THỂ NHẬN"
        elif package.action_text.strip().casefold() == "mua":
            state = "gói mua — bỏ qua"
        else:
            state = package.action_text.strip() or f"chưa đủ điều kiện (status={package.status})"
        print(f"      - [{package.package_id}] {package.title}: {state}")


def process_welfare(client: NSOActivityClient, dry_run: bool):
    tabs = client.request_welfare_categories()
    target_tabs = find_welfare_tabs(tabs)
    missing = [sub_id for sub_id in (8, 20, 21, 22) if sub_id not in target_tabs]
    if missing:
        available = ", ".join(f"{tab.main_name}/{tab.sub_name}:{tab.sub_id}"
                              for tab in tabs)
        raise RuntimeError(f"Thiếu mục Phúc Lợi subID={missing}. Server trả: {available}")

    targets = []
    for sub_id in (8, 20, 21, 22):
        detail = client.request_detail(target_tabs[sub_id])
        print_packages(detail)
        targets.extend((sub_id, package.package_id)
                       for package in detail.packages if package.claimable)

    if dry_run:
        print("    🔎 Dry-run Phúc Lợi: không gửi request nhận gói")
        return 0, 0, []
    if not targets:
        print("    ℹ️ Phúc Lợi không có gói miễn phí đủ điều kiện để nhận")
        return 0, 0, []

    for sub_id, package_id in targets:
        confirmation, error = client.claim_package(sub_id, package_id)
        label = target_tabs[sub_id].sub_name
        if error:
            print(f"    ↪ {label}, gói {package_id}: chưa xác nhận được ({error})")
        else:
            print(f"    🎁 Đã xác nhận {label}, gói {package_id}: {confirmation}")

    claimed = set()
    package_labels = {}
    for sub_id in sorted({sub_id for sub_id, _ in targets}):
        refreshed = client.request_detail(target_tabs[sub_id])
        claimed.update((sub_id, package.package_id)
                       for package in refreshed.packages if package.claimed)
        package_labels.update({
            (sub_id, package.package_id): package.title
            for package in refreshed.packages
        })
    failed_targets = [target for target in targets if target not in claimed]
    success = len(targets) - len(failed_targets)
    failed = len(failed_targets)
    failure_details = [
        (f"{target_tabs[sub_id].sub_name}, gói {package_id} "
         f"({package_labels.get((sub_id, package_id), 'không rõ tên')}) chưa chuyển sang Đã nhận")
        for sub_id, package_id in failed_targets
    ]
    print(f"    ✅ Xác minh Phúc Lợi: đã nhận={success}, chưa nhận được={failed}")
    return success, failed, failure_details


def process_character(client: NSOActivityClient, dry_run: bool):
    tabs = client.request_categories()
    timeline_tab, attendance_tab = find_daily_tabs(tabs)
    if timeline_tab is None or attendance_tab is None:
        available = ", ".join(f"{tab.main_name}/{tab.sub_name}:{tab.content_type}"
                              for tab in tabs)
        raise RuntimeError(f"Không tìm đủ Hoạt Động/Điểm Danh. Server trả: {available}")

    timeline = client.request_detail(timeline_tab)
    attendance = client.request_detail(attendance_tab)
    print_timeline(timeline)
    print_attendance(attendance)

    milestone_targets = [item for item in timeline.milestones
                         if item.claimable and not item.claimed]
    attendance_targets = [item for item in attendance.rewards if item.claimable]
    if dry_run:
        print("    🔎 Dry-run: không gửi request nhận quà")
        return 0, 0, 0, []

    attempts = 0
    for item in milestone_targets:
        attempts += 1
        _, error = client.claim_milestone(timeline.activity_id, item.milestone_id)
        if error:
            print(f"    ↪ Rương mốc {item.milestone_id}: chưa thấy response trực tiếp ({error})")
        else:
            print(f"    🎁 Đã gửi nhận rương mốc {item.milestone_id}")

    for item in attendance_targets:
        attempts += 1
        _, error = client.claim_attendance(attendance.activity_id, item.day)
        if error:
            print(f"    ↪ Điểm danh ngày {item.day}: chưa thấy response trực tiếp ({error})")
        else:
            print(f"    🎁 Đã gửi nhận điểm danh ngày {item.day}")

    if not attempts:
        print("    ℹ️ Không có quà đủ điều kiện để nhận")
        return 0, 0, 0, []

    # Luôn đọc lại trạng thái server; không coi response claim đơn thuần là thành công.
    refreshed_timeline = client.request_detail(timeline_tab)
    refreshed_attendance = client.request_detail(attendance_tab)
    claimed_milestones = {
        item.milestone_id for item in refreshed_timeline.milestones if item.claimed
    }
    claimed_days = {
        item.day for item in refreshed_attendance.rewards if item.claimed
    }
    failed_milestones = [item.milestone_id for item in milestone_targets
                         if item.milestone_id not in claimed_milestones]
    failed_days = [item.day for item in attendance_targets
                   if item.day not in claimed_days]
    milestone_success = sum(item.milestone_id in claimed_milestones
                            for item in milestone_targets)
    attendance_success = sum(item.day in claimed_days
                             for item in attendance_targets)
    failed = attempts - milestone_success - attendance_success
    failure_details = ([f"Rương Hoạt Động mốc {milestone_id} chưa chuyển sang đã nhận"
                        for milestone_id in failed_milestones]
                       + [f"Điểm Danh ngày {day} chưa chuyển sang đã nhận"
                          for day in failed_days])
    print(f"    ✅ Xác minh: rương={milestone_success}, điểm danh={attendance_success}, "
          f"chưa nhận được={failed}")
    return milestone_success, attendance_success, failed, failure_details


def discover_characters(host, port, username, password):
    client = NSOActivityClient(host, port)
    try:
        if not client.connect() or not client.login(username, password):
            return []
        return list(client.characters)
    finally:
        client.disconnect()


def build_parser():
    parser = argparse.ArgumentParser(
        description="Nhận quà Hoạt Động, Điểm Danh và Phúc Lợi NSO Mobile"
    )
    parser.add_argument("csv_file", nargs="?", type=Path, default=DEFAULT_CSV)
    parser.add_argument("--host", default=DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    parser.add_argument("--dry-run", action="store_true",
                        help="Chỉ đọc và in trạng thái, không nhận quà")
    parser.add_argument("--character-index", type=int,
                        help="Chỉ xử lý nhân vật ở index này (mặc định: tất cả)")
    parser.add_argument("--max-characters", type=int, default=0,
                        help="Giới hạn số nhân vật mỗi tài khoản; 0 là không giới hạn")
    parser.add_argument("--login-delay", type=float, default=DEFAULT_LOGIN_DELAY,
                        help="Thời gian nghỉ sau khi lấy danh sách nhân vật (mặc định: 11 giây)")
    parser.add_argument("--character-delay", type=float, default=DEFAULT_LOGIN_DELAY,
                        help="Thời gian nghỉ giữa hai nhân vật (mặc định: 11 giây)")
    parser.add_argument("--account-delay", type=float, default=DEFAULT_LOGIN_DELAY,
                        help="Thời gian nghỉ giữa hai tài khoản (mặc định: 11 giây)")
    return parser


def main():
    args = build_parser().parse_args()
    if not args.csv_file.is_file():
        print(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if args.character_index is not None and args.character_index < 0:
        print("❌ --character-index không được âm")
        return 2
    if args.max_characters < 0:
        print("❌ --max-characters không được âm")
        return 2

    try:
        accounts = read_accounts(args.csv_file)
    except Exception as exc:
        print(f"❌ Không đọc được CSV: {exc}")
        return 2
    print(f"NSO HOẠT ĐỘNG: {len(accounts)} tài khoản; "
          f"mode={'CHỈ XEM' if args.dry_run else 'NHẬN QUÀ'}")

    totals = {
        "characters": 0,
        "milestones": 0,
        "attendance": 0,
        "welfare": 0,
        "failed": 0,
    }
    failures = []
    for account_number, (username, password) in enumerate(accounts, start=1):
        print(f"\n[{account_number}/{len(accounts)}] Tài khoản {username}")
        characters = discover_characters(args.host, args.port, username, password)
        if not characters:
            totals["failed"] += 1
            failures.append(FailureRecord(
                username, None, None, "Đăng nhập",
                "Không đăng nhập được hoặc không lấy được danh sách nhân vật",
            ))
            continue
        indexes = list(range(len(characters)))
        if args.character_index is not None:
            indexes = [args.character_index] if args.character_index < len(characters) else []
        if args.max_characters:
            indexes = indexes[:args.max_characters]
        if not indexes:
            print("  ⚠️ Không có nhân vật phù hợp để xử lý")
            totals["failed"] += 1
            failures.append(FailureRecord(
                username, args.character_index, None, "Chọn nhân vật",
                "Không có nhân vật phù hợp với bộ lọc đã chọn",
            ))
            continue

        # discover_characters() vừa đăng nhập để lấy danh sách rồi ngắt kết nối.
        # Chờ hơn 10 giây trước lần đăng nhập nhân vật đầu tiên để tránh rate limit.
        print(f"  ⏳ Chờ {max(0, args.login_delay):g} giây trước khi đăng nhập nhân vật...")
        time.sleep(max(0, args.login_delay))

        for position, index in enumerate(indexes):
            name, level, school = characters[index]
            print(f"  [{index}] {name} lv{level} ({school})")
            client = NSOActivityClient(args.host, args.port)
            current_stage = "Kết nối/đăng nhập nhân vật"
            try:
                if not client.connect() or not client.login(username, password):
                    totals["failed"] += 1
                    failures.append(FailureRecord(
                        username, index, name, current_stage,
                        "Không kết nối hoặc đăng nhập lại được",
                    ))
                    continue
                current_stage = "Chọn nhân vật"
                if not client.select_character(index):
                    totals["failed"] += 1
                    failures.append(FailureRecord(
                        username, index, name, current_stage,
                        "Không chọn được nhân vật",
                    ))
                    continue
                totals["characters"] += 1
                client.drain()
                current_stage = "Hoạt Động/Điểm Danh"
                milestones, attendance, failed, failure_details = process_character(
                    client, args.dry_run
                )
                totals["milestones"] += milestones
                totals["attendance"] += attendance
                totals["failed"] += failed
                failures.extend(FailureRecord(
                    username, index, name, current_stage, reason
                ) for reason in failure_details)
                current_stage = "Phúc Lợi"
                welfare, welfare_failed, failure_details = process_welfare(
                    client, args.dry_run
                )
                totals["welfare"] += welfare
                totals["failed"] += welfare_failed
                failures.extend(FailureRecord(
                    username, index, name, current_stage, reason
                ) for reason in failure_details)
            except Exception as exc:
                totals["failed"] += 1
                failures.append(FailureRecord(
                    username, index, name, current_stage, str(exc),
                ))
                print(f"    ❌ Lỗi xử lý: {exc}")
            finally:
                client.disconnect()
            if position + 1 < len(indexes):
                time.sleep(max(0, args.character_delay))
        if account_number < len(accounts):
            time.sleep(max(0, args.account_delay))

    print("\nTỔNG KẾT: "
          f"nhân vật={totals['characters']}, rương={totals['milestones']}, "
          f"điểm danh={totals['attendance']}, phúc lợi={totals['welfare']}, "
          f"lỗi/chưa nhận={totals['failed']}")
    if failures:
        retry_accounts = list(dict.fromkeys(item.username for item in failures))
        print(f"\nDANH SÁCH CẦN CHẠY LẠI ({len(failures)} mục):")
        for item in failures:
            if item.character_index is None:
                character = "nhân vật=chưa xác định"
            else:
                name = item.character_name or "không rõ tên"
                character = f"nhân vật=[{item.character_index}] {name}"
            print(f"  - tài khoản={item.username} | {character} | "
                  f"bước={item.stage} | lỗi={item.reason}")
        print("TÀI KHOẢN CẦN CHẠY LẠI: " + ", ".join(retry_accounts))
    return 0 if totals["failed"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
