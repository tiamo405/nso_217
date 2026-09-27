#!/usr/bin/env python3
"""Đặt trạng thái không nhận EXP theo level và nhận EXP Offline miễn phí.

Luồng mobile 4.1.1 đã đối chiếu với Java client và response thật:
  - Tajima: NPC template ID 12 tại Làng Tone (map 22)
  - cmd 29, (0, 12, 6, 0): đổi trạng thái "Không nhận kinh nghiệm"
  - cmd 29, (0, 12, 7, 0): mở menu Nhận Exp Offline
  - cmd 63: danh sách mức nhận EXP Offline
  - cmd 29, (0, 12, 0, 0): chọn "0 Lượng = 100%"

Quy tắc trạng thái:
  - level >= 42: [Đang bật] Không nhận kinh nghiệm
  - level < 42:  [Đang tắt] Không nhận kinh nghiệm

Cách dùng :
python nhanexp_and_doiyenquaxu.py [--host HOST] [--port PORT] [--character-index INDEX | --character-name NAME]
  --host HOST: địa chỉ server (mặc định Nsm4.ninjasm.net)
  --port PORT: cổng server (mặc định 14444)
  --character-index INDEX: chỉ xử lý nhân vật index này (0-based)
  --character-name NAME: chỉ xử lý nhân vật đúng tên này (ổn định hơn index)
  --max-characters N: xử lý tối đa N nhân vật, sort lv giảm dần (mặc định 1; 0 = tất cả)
  --max-accounts N: chỉ chạy tối đa N tài khoản đầu tiên (mặc định 0 = không giới hạn)
  --dry-run: chỉ xem level/trạng thái mong muốn, không di chuyển hay bấm NPC
  --login-delay SECONDS: chờ sau khi đăng nhập trước nhân vật đầu tiên (mặc định 11.0)
  --character-delay SECONDS: chờ sau khi xử lý mỗi nhân vật (mặc định 11.0)
  --account-delay SECONDS: chờ sau khi xử lý mỗi tài khoản (mặc định 11.0)
  --nhan-exp: bật nhận EXP Offline (mặc định)
  --bo-qua-exp: bỏ qua toàn bộ bước EXP Offline, chỉ đổi Yên qua Xu
  --luong N: số tài khoản chạy song song (mặc định 1)
  --retry-attempts N: số lần retry sau lần chạy đầu cho mỗi tài khoản (mặc định 2)
  --retry-delay SECONDS: chờ giữa các lần retry tài khoản (mặc định 5.0)
  --failed-csv PATH: lưu tài khoản còn lỗi sau retry; mặc định thêm -failed vào tên CSV input
  --log-dir PATH: thư mục log riêng từng luồng; mặc định log/nhanexp_and_doiyenquaxu
  --log-file PATH: ghi log; mặc định thêm .log vào tên CSV input
Lưu ý:
  - Chỉ nhận EXP Offline miễn phí 0 Lượng = 100% (option 0)
  - Mỗi tài khoản xử lý số nhân vật theo --max-characters, sort level giảm dần
  - Bỏ qua nhân vật có level < 20
"""


import argparse
import csv
from concurrent.futures import ThreadPoolExecutor, as_completed
import os
import tempfile
import threading
import time
import unicodedata
from dataclasses import dataclass, field
from pathlib import Path

from hoatdong import NSOActivityClient, NSOMessage, NSOReader
from map_graph import MAP_GRAPH, find_map_path


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-nhanexp.csv"
# DEFAULT_HOST = "Nsm4.ninjasm.net" # sv4 k có nhận exp nên thêm biến --bo-qua-exp để bỏ qua bước nhận exp
# server TK
DEFAULT_HOST = "Nsotk1.nsotk.online"
DEFAULT_PORT = 14444
DEFAULT_DELAY = 3.0
DEFAULT_RETRY_ATTEMPTS = 2
DEFAULT_RETRY_DELAY = 5.0
EXCHANGE_RESPONSE_TIMEOUT = 20.0
EXCHANGE_REQUEST_ATTEMPTS = 2
OFFLINE_EXP_RESPONSE_TIMEOUT = 20.0
OFFLINE_EXP_REQUEST_ATTEMPTS = 2
TONE_MAP_ID = 22
TAJIMA_NPC_ID = 12
OKANECHAN_NPC_ID = 24
STATUS_MENU_ID = 6
OFFLINE_EXP_MENU_ID = 7
FREE_EXP_OPTION_ID = 0
MIN_CHARACTER_LEVEL = 20
VILLAGE_MAPS = {10, 17, 22, 32, 38, 43, 48}
VILLAGE_MENU_IDS = {10: 1, 17: 2, 22: 3, 32: 4, 38: 5, 43: 6, 48: 7}
# Okanechan có thể có ở làng hoặc trường tùy phiên bản/server. Ưu tiên
# kiểm tra map hiện tại, sau đó lần lượt tìm ở các map làng/trường.
OKANECHAN_SEARCH_MAPS = (22, 10, 17, 32, 38, 43, 48, 1, 27, 72)
EXCHANGE_RECEIVED = "received"
EXCHANGE_ALREADY_RECEIVED = "already_received"
EXCHANGE_SKIPPED = "skipped"
EXCHANGE_NOT_RECEIVED = "not_received"
EXCHANGE_UNKNOWN = "unknown"
EXCHANGE_NOT_ATTEMPTED = "not_attempted"
EXP_RECEIVED = "received"
EXP_NO_DATA = "no_data"
EXP_UNKNOWN = "unknown"
EXP_NOT_ATTEMPTED = "not_attempted"

# Giữ log từng dòng nguyên vẹn khi nhiều worker cùng ghi stdout.
_ORIGINAL_PRINT = print
_PRINT_LOCK = threading.Lock()
_LOG_HANDLE = None
_THREAD_LOG = threading.local()


def configure_log(path: Path):
    global _LOG_HANDLE
    if _LOG_HANDLE is not None:
        _LOG_HANDLE.close()
    _LOG_HANDLE = Path(path).open("w", encoding="utf-8", buffering=1)


def close_log():
    global _LOG_HANDLE
    if _LOG_HANDLE is not None:
        _LOG_HANDLE.close()
        _LOG_HANDLE = None


def configure_lane_log(path: Path):
    _THREAD_LOG.handle = Path(path).open(
        "w", encoding="utf-8", buffering=1,
    )


def close_lane_log():
    handle = getattr(_THREAD_LOG, "handle", None)
    if handle is not None:
        handle.close()
        _THREAD_LOG.handle = None


def clear_log_files(path: Path, main_log: Path):
    path = Path(path)
    targets = set(path.glob("luong*.log"))
    targets.add(path / "main.log")
    if main_log.parent.resolve() == path.resolve():
        targets.add(main_log)
    for target in targets:
        if target.is_file() or target.is_symlink():
            target.unlink()


def log(*args, **kwargs):
    kwargs.setdefault("flush", True)
    with _PRINT_LOCK:
        _ORIGINAL_PRINT(*args, **kwargs)
        handle = getattr(_THREAD_LOG, "handle", None) or _LOG_HANDLE
        if handle is not None:
            file_kwargs = dict(kwargs)
            file_kwargs["file"] = handle
            _ORIGINAL_PRINT(*args, **file_kwargs)


@dataclass
class Waypoint:
    min_x: int
    min_y: int
    max_x: int
    max_y: int


@dataclass
class NPC:
    template_id: int
    status: int
    x: int
    y: int


@dataclass
class MapState:
    map_id: int = -1
    name: str = ""
    zone_id: int = -1
    char_x: int = 0
    char_y: int = 0
    waypoints: list[Waypoint] = field(default_factory=list)
    npcs: list[NPC] = field(default_factory=list)

    def find_npc(self, template_id: int) -> NPC | None:
        candidates = [npc for npc in self.npcs if npc.template_id == template_id]
        if not candidates:
            return None
        active = [npc for npc in candidates if npc.status != 15] or candidates
        return min(active, key=lambda npc: (
            abs(self.char_x - npc.x) + abs(self.char_y - npc.y)
        ))


def normalize_text(value: str) -> str:
    value = unicodedata.normalize("NFD", value.casefold())
    value = "".join(char for char in value if unicodedata.category(char) != "Mn")
    return value.replace("đ", "d")


def read_accounts(csv_path: Path):
    accounts = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for line_number, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if len(row) < 2:
                raise ValueError(f"Dòng {line_number} thiếu username/password")
            username, password = row[0].strip(), row[1].strip()
            if username.casefold() == "username" and password.casefold() == "password":
                continue
            if username and password:
                accounts.append((username, password))
    return accounts


class OfflineExpClient(NSOActivityClient):
    CMD_MAP_LOAD = -18
    CMD_MOVE = 1
    CMD_CHANGE_MAP = -17
    CMD_MENU = 29
    CMD_DYNAMIC_MENU = 63
    CMD_OPEN_MENU = 40
    CMD_SERVER_INFO = -24

    def __init__(self, host: str, port: int):
        super().__init__(host, port)
        self.map_state = MapState()
        self.exchange_status = EXCHANGE_NOT_ATTEMPTED
        self.exp_status = EXP_NOT_ATTEMPTED

    def select_character_and_load_map(self, character_name: str) -> bool:
        if not any(name == character_name for name, _, _ in self.characters):
            log(f"    ❌ Không còn thấy nhân vật {character_name} sau khi đăng nhập lại")
            return False
        message = NSOMessage(self.CMD_NOT_MAP)
        message.write_byte(self.CMD_SELECT_CHAR)
        # Thứ tự server có thể đổi sau khi vừa chơi một nhân vật. Luôn chọn
        # theo tên đã khám phá, không dùng lại index của phiên đăng nhập trước.
        message.write_utf(character_name)
        self.send(message)

        game_ready = False
        deadline = time.time() + 25
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                log(f"    ❌ Chọn nhân vật: {self._read_server_error(data)}")
                return False
            if command == self.CMD_SUB_COMMAND and data:
                game_ready = NSOReader(data).read_byte() == -127 or game_ready
            elif command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)
            if game_ready and self.map_state.map_id >= 0:
                return True
        log("    ❌ Timeout khi chờ thông tin nhân vật/map")
        return False

    @staticmethod
    def _parse_map(data: bytes) -> MapState:
        reader = NSOReader(data)
        state = MapState()
        state.map_id = reader.read_ubyte()
        reader.read_byte()  # tile ID
        reader.read_byte()  # background ID
        reader.read_byte()  # map type
        state.name = reader.read_utf()
        state.zone_id = reader.read_byte()
        state.char_x = reader.read_short()
        state.char_y = reader.read_short()

        waypoint_count = reader.read_byte()
        if waypoint_count < 0:
            raise ValueError(f"Số waypoint âm: {waypoint_count}")
        for _ in range(waypoint_count):
            state.waypoints.append(Waypoint(
                reader.read_short(), reader.read_short(),
                reader.read_short(), reader.read_short(),
            ))

        mob_count = reader.read_byte()
        if mob_count < 0:
            raise ValueError(f"Số quái âm: {mob_count}")
        for _ in range(mob_count):
            for _ in range(5):
                reader.read_boolean()
            reader.read_short()   # template ID
            reader.read_byte()    # sys type
            reader.read_int()     # HP
            reader.read_ubyte()   # level
            reader.read_int()     # max HP
            reader.read_short()   # x
            reader.read_short()   # y
            reader.read_byte()    # status
            reader.read_byte()    # boss level
            reader.read_boolean()

        dummy_count = reader.read_byte()
        if dummy_count < 0:
            raise ValueError(f"Số bù nhìn âm: {dummy_count}")
        for _ in range(dummy_count):
            reader.read_utf()
            reader.read_short()
            reader.read_short()

        npc_count = reader.read_byte()
        if npc_count < 0:
            raise ValueError(f"Số NPC âm: {npc_count}")
        for _ in range(npc_count):
            state.npcs.append(NPC(
                status=reader.read_byte(),
                x=reader.read_short(),
                y=reader.read_short(),
                template_id=reader.read_byte(),
            ))

        item_count = reader.read_byte()
        if item_count < 0:
            raise ValueError(f"Số item map âm: {item_count}")
        for _ in range(item_count):
            reader.read_short()  # item map ID
            reader.read_short()  # item template ID
            reader.read_short()  # x
            reader.read_short()  # y
        return state

    def move_character(self, x: int, y: int):
        message = NSOMessage(self.CMD_MOVE)
        message.write_short(x)
        message.write_short(y)
        self.send(message)
        self.map_state.char_x = x
        self.map_state.char_y = y

    def _request_change_map(self):
        self.send(NSOMessage(self.CMD_CHANGE_MAP))

    def _choose_npc_menu(self, npc_id: int, menu_id: int, option_id: int = 0):
        message = NSOMessage(self.CMD_MENU)
        for value in (0, npc_id, menu_id, option_id):
            message.write_byte(value)
        self.send(message)

    def _wait_for_map_change(self, old_map_id: int, timeout: float = 10) -> bool:
        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                log(f"    ↪ Chuyển map: {self._read_server_error(data)}")
                continue
            if command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)
                if self.map_state.map_id != old_map_id:
                    return True
        return False

    def move_to_map(self, target_map_id: int, max_steps: int = 20) -> bool:
        for _ in range(max_steps):
            current = self.map_state.map_id
            if current == target_map_id:
                return True
            path = find_map_path(current, target_map_id)
            if not path or len(path) < 2:
                log(f"    ❌ Không tìm được đường map {current} → {target_map_id}")
                return False
            next_map = path[1]
            neighbors = MAP_GRAPH.get(current, [])
            if next_map not in neighbors:
                log(f"    ❌ Graph thiếu cạnh map {current} → {next_map}")
                return False

            # Các cạnh trực tiếp giữa làng trong TileMap.fieldAK không phải
            # waypoint. Java mã hóa thành GameScr.fieldAB(7, villageMenu, 0):
            # giao dịch NPC ID 7 và chọn làng đích (Tone = menu 3).
            if current in VILLAGE_MAPS and next_map in VILLAGE_MAPS:
                npc = self.map_state.find_npc(7)
                menu_id = VILLAGE_MENU_IDS.get(next_map)
                if npc is None or menu_id is None:
                    log(f"    ❌ Không tìm thấy NPC chuyển làng cho {current} → {next_map}")
                    return False
                log(f"    🗺️ Làng {current} → {next_map} qua NPC 7, menu {menu_id}")
                self.move_character(npc.x, self.map_state.char_y)
                time.sleep(0.5)
                self._choose_npc_menu(7, menu_id)
                if not self._wait_for_map_change(current):
                    log(f"    ❌ Timeout chuyển làng {current} → {next_map}")
                    return False
                continue

            waypoint_index = neighbors.index(next_map)
            if waypoint_index >= len(self.map_state.waypoints):
                log(f"    ❌ Map {current}: cần waypoint {waypoint_index}, "
                      f"server chỉ trả {len(self.map_state.waypoints)}")
                return False
            waypoint = self.map_state.waypoints[waypoint_index]
            x = (waypoint.min_x + waypoint.max_x) // 2
            y = waypoint.max_y
            log(f"    🗺️ Map {current} → {next_map} qua waypoint {waypoint_index}")
            self.move_character(x, y)
            time.sleep(0.35)
            self._request_change_map()
            if not self._wait_for_map_change(current):
                log(f"    ❌ Timeout chuyển map {current} → {next_map}")
                return False
        return self.map_state.map_id == target_map_id

    def move_to_tone(self, max_steps: int = 20) -> bool:
        return self.move_to_map(TONE_MAP_ID, max_steps)

    def move_to_okanechan(self, max_steps: int = 20) -> bool:
        """Tìm một map có Okanechan rồi di chuyển tới đó."""
        if self.map_state.find_npc(OKANECHAN_NPC_ID) is not None:
            return True

        checked_maps = {self.map_state.map_id}
        for target_map_id in OKANECHAN_SEARCH_MAPS:
            if target_map_id in checked_maps:
                continue
            checked_maps.add(target_map_id)
            if not self.move_to_map(target_map_id, max_steps):
                continue
            if self.map_state.find_npc(OKANECHAN_NPC_ID) is not None:
                return True
        return False

    def open_tajima(self) -> bool:
        npc = self.map_state.find_npc(TAJIMA_NPC_ID)
        if npc is None:
            return False
        self.move_character(npc.x, self.map_state.char_y)
        # openMenu(40) chỉ dựng giao diện phía client và server hiện không trả
        # nội dung cho Tajima. Packet có tác dụng thực tế là menu(29); chờ đủ
        # để server cập nhật vị trí trước khi gửi lựa chọn.
        time.sleep(0.8)
        return True

    def choose_tajima_menu(self, menu_id: int, option_id: int = 0):
        self._choose_npc_menu(TAJIMA_NPC_ID, menu_id, option_id)

    def _wait_status_result(self, timeout: float = 12):
        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR and data:
                text = self._read_server_error(data)
                normalized = normalize_text(text)
                if "khong nhan kinh nghiem" in normalized:
                    if "da bat" in normalized:
                        return True, text
                    if "da tat" in normalized:
                        return False, text
        return None, "timeout chờ trạng thái Tajima"

    def ensure_no_exp_state(self, desired_enabled: bool):
        # Menu là nút toggle và server không gửi trạng thái khi chỉ mở NPC.
        # Bấm một lần để đọc trạng thái mới; nếu chưa đúng thì bấm lần hai.
        self.choose_tajima_menu(STATUS_MENU_ID)
        state, message = self._wait_status_result()
        if state is None:
            return False, message
        log(f"    🔁 {message}")
        if state == desired_enabled:
            return True, message

        self.open_tajima()
        self.choose_tajima_menu(STATUS_MENU_ID)
        state, message = self._wait_status_result()
        if state is None:
            return False, message
        log(f"    🔁 {message}")
        if state != desired_enabled:
            expected = "bật" if desired_enabled else "tắt"
            return False, f"Server chưa chuyển trạng thái về {expected}"
        return True, message

    def request_offline_exp_options(
        self,
        timeout: float = OFFLINE_EXP_RESPONSE_TIMEOUT,
    ):
        for attempt in range(OFFLINE_EXP_REQUEST_ATTEMPTS):
            if attempt:
                log(
                    f"    🔁 Retry riêng menu Exp Offline "
                    f"({attempt + 1}/{OFFLINE_EXP_REQUEST_ATTEMPTS})"
                )
            self.choose_tajima_menu(OFFLINE_EXP_MENU_ID)
            deadline = time.time() + timeout
            while time.time() < deadline:
                command, data = self.receive(
                    min(0.75, max(0.2, deadline - time.time()))
                )
                if command is None:
                    if self.last_receive_error == "timeout":
                        continue
                    break
                if command == self.CMD_SERVER_ERROR and data:
                    text = self._read_server_error(data)
                    return None, text
                if command == self.CMD_DYNAMIC_MENU and data:
                    reader = NSOReader(data)
                    options = []
                    try:
                        while reader.remaining():
                            options.append(reader.read_utf())
                    except Exception as exc:
                        return None, f"Menu Exp Offline không hợp lệ: {exc}"
                    return options, None
        return None, "timeout chờ menu Exp Offline"

    def claim_free_offline_exp(self, timeout: float = 12):
        self.exp_status = EXP_NOT_ATTEMPTED
        self.choose_tajima_menu(FREE_EXP_OPTION_ID)
        deadline = time.time() + timeout
        messages = []
        exp_updated = False
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR and data:
                messages.append(self._read_server_error(data))
                break
            if command == self.CMD_SUB_COMMAND and data:
                subcommand = NSOReader(data).read_byte()
                if subcommand == -124:
                    exp_updated = True
                    break
        if exp_updated:
            self.exp_status = EXP_RECEIVED
            return self.exp_status, "server đã cập nhật EXP nhân vật"
        if messages:
            text = " | ".join(messages)
            normalized = normalize_text(text)
            received_exp = "da nhan duoc" in normalized and "exp" in normalized
            no_exp = any(value in normalized for value in (
                "khong co", "chua co", "exp luu tru", "kinh nghiem luu tru",
            ))
            self.exp_status = EXP_RECEIVED if received_exp else (
                EXP_NO_DATA if no_exp else EXP_UNKNOWN
            )
            return self.exp_status, text
        self.exp_status = EXP_UNKNOWN
        return self.exp_status, "timeout chờ kết quả nhận Exp Offline"

    @staticmethod
    def _read_utf_list(data: bytes):
        reader = NSOReader(data)
        values = []
        while reader.remaining():
            values.append(reader.read_utf())
        return values

    def request_okanechan_menu(self, timeout: float = 12):
        message = NSOMessage(self.CMD_OPEN_MENU)
        message.write_short(OKANECHAN_NPC_ID)
        self.send(message)

        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
                break
            if command == self.CMD_SERVER_ERROR:
                return None, self._read_server_error(data)
            if command in (self.CMD_OPEN_MENU, self.CMD_DYNAMIC_MENU):
                try:
                    return self._read_utf_list(data), None
                except Exception as exc:
                    return None, f"Menu Okanechan không hợp lệ: {exc}"
        return None, "timeout chờ menu Okanechan"

    def _wait_for_exchange_response(self, timeout: float = EXCHANGE_RESPONSE_TIMEOUT):
        deadline = time.time() + timeout
        response_commands = {
            self.CMD_SERVER_ERROR,
            self.CMD_SERVER_INFO,
            -7,
            38,
            39,
            self.CMD_DYNAMIC_MENU,
            self.CMD_OPEN_MENU,
        }
        while time.time() < deadline:
            command, data = self.receive(
                min(0.75, max(0.2, deadline - time.time()))
            )
            if command is None:
                # Socket timeout means no packet in this poll, not final failure.
                if self.last_receive_error == "timeout":
                    continue
                break
            if command in response_commands:
                return command, data or b""
        return None, None

    def _classify_exchange_response(self, command: int, data: bytes):
        if command in (self.CMD_SERVER_ERROR, self.CMD_SERVER_INFO):
            message = self._read_server_error(data)
            if self._already_exchanged_this_week(message):
                return EXCHANGE_ALREADY_RECEIVED, message
            if self._insufficient_activity(message):
                return EXCHANGE_SKIPPED, message
            if command == self.CMD_SERVER_INFO:
                normalized = normalize_text(message)
                if any(term in normalized for term in (
                    "can toi thieu", "toi thieu", "khong du", "khong the",
                    "that bai", "khong nhan duoc", "khong nhan",
                )):
                    return EXCHANGE_NOT_RECEIVED, message
                if any(term in normalized for term in (
                    "thanh cong", "nhan duoc", "doi yen sang xu",
                )):
                    return EXCHANGE_RECEIVED, message
                return EXCHANGE_UNKNOWN, message
            return EXCHANGE_NOT_RECEIVED, message
        if command == -7:
            if len(data) == 4:
                delta = NSOReader(data).read_int()
                return EXCHANGE_RECEIVED, f"Xu +{delta}, Yên -{delta}"
            return EXCHANGE_RECEIVED, "server đã cập nhật Xu/Yên"
        if command == 38:
            reader = NSOReader(data)
            reader.read_short()  # NPC ID
            message = reader.read_utf()
            if self._already_exchanged_this_week(message):
                return EXCHANGE_ALREADY_RECEIVED, message
            return EXCHANGE_NOT_RECEIVED, message
        if command == 39:
            reader = NSOReader(data)
            reader.read_short()  # NPC ID
            return EXCHANGE_UNKNOWN, reader.read_utf()
        return EXCHANGE_UNKNOWN, f"server trả command {command}"

    @staticmethod
    def _insufficient_activity(message: str) -> bool:
        normalized = normalize_text(message)
        return "diem hoat dong" in normalized and "toi thieu" in normalized

    @staticmethod
    def _already_exchanged_this_week(message: str) -> bool:
        normalized = normalize_text(message)
        return (
            "da doi yen sang xu" in normalized
            and "tuan nay" in normalized
        )

    def exchange_yen_to_xu(self):
        """Bấm Đổi Yên qua Xu và trả kết quả nhận/không nhận rõ ràng."""
        self.exchange_status = EXCHANGE_NOT_ATTEMPTED
        npc = self.map_state.find_npc(OKANECHAN_NPC_ID)
        if npc is None:
            return False, "Không tìm thấy Okanechan (NPC 24)"

        self.move_character(npc.x, self.map_state.char_y)
        time.sleep(0.8)
        options, error = self.request_okanechan_menu()
        if options is None:
            return False, error

        target_index = next(
            (
                index for index, option in enumerate(options)
                if "doi yen qua xu" in normalize_text(option)
            ),
            None,
        )
        if target_index is None:
            return False, "Không tìm thấy nút 'Đổi Yên qua Xu'"

        status = EXCHANGE_UNKNOWN
        message = "server chưa trả kết quả"
        for attempt in range(EXCHANGE_REQUEST_ATTEMPTS):
            if attempt:
                log(
                    f"    🔁 Retry riêng request đổi Xu "
                    f"({attempt + 1}/{EXCHANGE_REQUEST_ATTEMPTS})"
                )
            self._choose_npc_menu(OKANECHAN_NPC_ID, target_index)
            command, data = self._wait_for_exchange_response()
            if command is None:
                status, message = EXCHANGE_UNKNOWN, "server chưa trả kết quả"
            else:
                try:
                    status, message = self._classify_exchange_response(command, data)
                except Exception as exc:
                    status, message = EXCHANGE_UNKNOWN, f"không đọc được response: {exc}"
            if status != EXCHANGE_UNKNOWN:
                break

        self.exchange_status = status
        if status == EXCHANGE_RECEIVED:
            return True, f"✅ Đổi Yên qua Xu: ĐÃ NHẬN ĐƯỢC ({message})"
        if status == EXCHANGE_ALREADY_RECEIVED:
            return True, f"ℹ️ Đổi Yên qua Xu: ĐÃ XỬ LÝ TRONG TUẦN ({message})"
        if status == EXCHANGE_SKIPPED:
            return True, f"⏭️ Đổi Yên qua Xu: BỎ QUA — {message}"
        if status == EXCHANGE_NOT_RECEIVED:
            return True, f"⏭️ Đổi Yên qua Xu: BỎ QUA — NPC phản hồi: {message}"
        return False, f"⚠️ Đổi Yên qua Xu: CHƯA XÁC ĐỊNH — {message}"


def build_parser():
    parser = argparse.ArgumentParser(
        description="Cập nhật trạng thái và nhận Exp Offline 0 Lượng tại Tajima"
    )
    parser.add_argument("csv_file", nargs="?", type=Path, default=DEFAULT_CSV)
    parser.add_argument("--host", default=DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    parser.add_argument("--character-index", type=int,
                        help="Chỉ xử lý index nhân vật này; mặc định xử lý tất cả")
    parser.add_argument("--character-name",
                        help="Chỉ xử lý đúng tên nhân vật này (ổn định hơn index)")
    parser.add_argument(
        "--max-characters", type=int, default=1,
        help="Số nhân vật xử lý theo lv giảm dần (mặc định 1; 0 = tất cả)",
    )
    parser.add_argument("--max-accounts", type=int, default=0)
    parser.add_argument("--dry-run", action="store_true",
                        help="Chỉ xem level/trạng thái mong muốn, không di chuyển hay bấm NPC")
    parser.add_argument("--login-delay", type=float, default=DEFAULT_DELAY)
    parser.add_argument("--character-delay", type=float, default=DEFAULT_DELAY)
    parser.add_argument("--account-delay", type=float, default=DEFAULT_DELAY)
    exp_group = parser.add_mutually_exclusive_group()
    exp_group.add_argument(
        "--nhan-exp", dest="receive_exp", action="store_true",
        help="Bật nhận EXP Offline (mặc định)",
    )
    exp_group.add_argument(
        "--bo-qua-exp", dest="receive_exp", action="store_false",
        help="Bỏ qua EXP Offline, chỉ xử lý đổi Yên qua Xu",
    )
    parser.set_defaults(receive_exp=True)
    parser.add_argument(
        "--luong", type=int, default=1,
        help="Số tài khoản chạy song song (mặc định 1)",
    )
    parser.add_argument(
        "--retry-attempts", type=int, default=DEFAULT_RETRY_ATTEMPTS,
        help="Số lần retry sau lần chạy đầu cho mỗi tài khoản",
    )
    parser.add_argument(
        "--retry-delay", type=float, default=DEFAULT_RETRY_DELAY,
        help="Số giây chờ giữa các lần retry tài khoản",
    )
    parser.add_argument(
        "--failed-csv", type=Path,
        help="File CSV lưu tài khoản còn lỗi sau retry; mặc định thêm '-failed' vào tên input",
    )
    parser.add_argument(
        "--log-dir", type=Path,
        help="Thư mục log riêng từng luồng; mặc định log/nhanexp_and_doiyenquaxu",
    )
    parser.add_argument(
        "--log-file", type=Path,
        help="File log; mặc định thêm '.log' vào tên input",
    )
    return parser


def discover_characters(host, port, username, password):
    client = OfflineExpClient(host, port)
    try:
        if not client.connect() or not client.login(username, password):
            return []
        return list(client.characters)
    finally:
        client.disconnect()


def process_character(
    client: OfflineExpClient,
    level: int,
    receive_exp: bool = True,
):
    log(f"    Map hiện tại: {client.map_state.map_id} ({client.map_state.name})")
    if receive_exp:
        if not client.move_to_tone():
            return False, "Không về được Làng Tone"
        log("    ✅ Đã ở Làng Tone")
        # Làng đông người phát liên tục packet di chuyển/đánh quái. Xả backlog
        # trước khi gửi menu để response Tajima không bị chậm sau hàng trăm packet.
        client.drain(2.0)
        if not client.open_tajima():
            return False, "Không tìm thấy Tajima (NPC 12) tại Làng Tone"

        desired_enabled = level >= 42
        expected = "[Đang bật]" if desired_enabled else "[Đang tắt]"
        log(f"    Trạng thái yêu cầu theo level {level}: {expected} Không nhận kinh nghiệm")
        ok, result = client.ensure_no_exp_state(desired_enabled)
        if not ok:
            return False, result
        log(f"    ✅ Trạng thái đã đúng: {expected}")

        # Giao dịch lại với Tajima như luồng mobile trước khi mở Exp Offline.
        if not client.open_tajima():
            return False, "Không mở lại được Tajima"
        options, error = client.request_offline_exp_options()
        if options is None:
            return False, error
        log(f"    Menu Exp Offline: {options}")
        if not options:
            return False, "Menu Exp Offline rỗng"
        free_option = normalize_text(options[FREE_EXP_OPTION_ID])
        if "0 luong" not in free_option or "100%" not in free_option:
            return False, f"Từ chối nhận: option 0 ngoài dự kiến ({options[0]})"

        exp_status, result = client.claim_free_offline_exp()
        if exp_status not in (EXP_RECEIVED, EXP_NO_DATA):
            return False, f"Nhận Exp Offline thất bại: {result}"
        exp_label = "đã nhận EXP" if exp_status == EXP_RECEIVED else "không có EXP lưu trữ"
        log(f"    🎁 Đã chọn 0 Lượng = 100% ({exp_label}): {result}")
    else:
        client.exp_status = EXP_NOT_ATTEMPTED
        log("    ⏭️ Bỏ qua nhận EXP Offline theo cấu hình")

    log("    🔎 Tìm NPC Okanechan để đổi Yên qua Xu...")
    if not client.move_to_okanechan():
        return False, "Không tìm thấy/không đi được tới Okanechan (NPC 24)"
    log(f"    ✅ Đã tới Okanechan tại map {client.map_state.map_id}")
    # Bỏ packet di chuyển còn tồn trước khi mở menu.
    client.drain(1.0)
    ok, result = client.exchange_yen_to_xu()
    if not ok:
        return False, result
    log(f"    {result}")
    return True, result


def _new_stats():
    return {
        "attempts": 0,
        "character_attempts": 0,
        "character_success": 0,
        "character_failed": 0,
        "exp_received": 0,
        "exp_no_data": 0,
        "exp_unknown": 0,
        "exp_not_attempted": 0,
        "exchange_received": 0,
        "exchange_already_received": 0,
        "exchange_skipped": 0,
        "exchange_not_received": 0,
        "exchange_unknown": 0,
        "exchange_not_attempted": 0,
    }


def _add_stats(target, source):
    for key, value in source.items():
        target[key] += value


def write_failed_accounts(path: Path, failed_accounts):
    """Ghi lại account còn lỗi; file có thể dùng trực tiếp làm input lần sau."""
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


def _select_character_indexes(characters, args):
    indexes = list(range(len(characters)))
    if args.character_name:
        indexes = [
            index for index, character in enumerate(characters)
            if character[0] == args.character_name
        ]
    elif args.character_index is not None:
        indexes = ([args.character_index]
                   if args.character_index < len(characters) else [])
    indexes.sort(key=lambda index: (-characters[index][1], index))
    if args.max_characters:
        indexes = indexes[:args.max_characters]
    return indexes


def process_account_attempt(
    args,
    username: str,
    password: str,
):
    """Chạy một lượt đầy đủ; caller quyết định retry toàn tài khoản."""
    stats = _new_stats()
    characters = discover_characters(args.host, args.port, username, password)
    if not characters:
        return {
            "ok": False,
            "reason": "Không đăng nhập/lấy được danh sách nhân vật",
            "stats": stats,
        }

    selected_indexes = _select_character_indexes(characters, args)
    if not selected_indexes:
        return {
            "ok": False,
            "reason": "Không có nhân vật phù hợp bộ lọc",
            "stats": stats,
        }
    log(
        "  🎯 Nhân vật xét theo lv: "
        + ", ".join(
            f"[{index}] {characters[index][0]} lv{characters[index][1]}"
            for index in selected_indexes
        )
    )
    indexes = []
    for index in selected_indexes:
        name, level, _ = characters[index]
        if level < MIN_CHARACTER_LEVEL:
            log(
                f"  ⏭️ Bỏ qua [{index}] {name} lv{level} "
                f"(< lv{MIN_CHARACTER_LEVEL})"
            )
            continue
        indexes.append(index)
    if not indexes:
        log(f"  ⏭️ Không có nhân vật đạt lv{MIN_CHARACTER_LEVEL} để xử lý")
        return {
            "ok": True,
            "reason": "Không có nhân vật đủ level",
            "stats": stats,
            "skipped": False,
        }
    log(
        "  ✅ Nhân vật nhận EXP + đổi Xu: "
        + ", ".join(
            f"[{index}] {characters[index][0]} lv{characters[index][1]}"
            for index in indexes
        )
    )

    if args.dry_run:
        for index in indexes:
            name, level, school = characters[index]
            target = "BẬT" if level >= 42 else "TẮT"
            log(f"  [{index}] {name} lv{level} ({school}) → cần {target} → đổi Xu")
            stats["character_attempts"] += 1
            stats["character_success"] += 1
            stats["exp_not_attempted"] += 1
            stats["exchange_not_attempted"] += 1
        return {"ok": True, "reason": "dry-run", "stats": stats}

    log(f"  ⏳ Chờ {max(0, args.login_delay):g} giây trước nhân vật đầu tiên...")
    time.sleep(max(0, args.login_delay))
    failures = []
    skipped = False
    for position, index in enumerate(indexes):
        name, level, school = characters[index]
        log(f"  [{index}] {name} lv{level} ({school})")
        stats["character_attempts"] += 1
        client = OfflineExpClient(args.host, args.port)
        character_started = False
        try:
            if not client.connect() or not client.login(username, password):
                raise RuntimeError("Không kết nối/đăng nhập lại được")
            if not client.select_character_and_load_map(name):
                raise RuntimeError("Không chọn được nhân vật hoặc tải map")
            character_started = True
            ok, reason = process_character(
                client,
                level,
                getattr(args, "receive_exp", True),
            )
            skipped = skipped or client.exchange_status == EXCHANGE_SKIPPED
            if ok:
                stats["character_success"] += 1
            else:
                stats["character_failed"] += 1
                failures.append(f"[{index}] {name}: {reason}")
                log(f"    ❌ {reason}")
        except Exception as exc:
            stats["character_failed"] += 1
            failures.append(f"[{index}] {name}: {exc}")
            log(f"    ❌ Lỗi xử lý: {exc}")
        finally:
            if character_started:
                stats[f"exp_{client.exp_status}"] += 1
                stats[f"exchange_{client.exchange_status}"] += 1
            client.disconnect()
        if position + 1 < len(indexes):
            log(f"  ⏳ Chờ {max(0, args.character_delay):g} giây trước nhân vật tiếp...")
            time.sleep(max(0, args.character_delay))

    return {
        "ok": not failures,
        "reason": "; ".join(failures),
        "stats": stats,
        "skipped": skipped,
    }


def _failed_account_result(username, password, reason):
    totals = {
        "success": 0,
        "failed": 1,
        "accounts_retried": 0,
        "retries": 0,
        **_new_stats(),
    }
    return {
        "totals": totals,
        "failures": [f"tài khoản={username} | lỗi={reason}"],
        "failed_accounts": [(username, password, reason)],
        "skipped_accounts": [],
    }


def process_account(args, account_number, total_accounts, username, password):
    totals = {
        "success": 0,
        "failed": 0,
        "accounts_retried": 0,
        "retries": 0,
        **_new_stats(),
    }
    failures = []
    failed_accounts = []
    skipped_accounts = []

    log(f"\n[{account_number}/{total_accounts}] Tài khoản {username}", flush=True)
    last_result = None
    for attempt in range(args.retry_attempts + 1):
        if attempt:
            if attempt == 1:
                totals["accounts_retried"] += 1
            totals["retries"] += 1
            log(
                f"  🔁 Retry toàn tài khoản sau {args.retry_delay:g} giây "
                f"({attempt}/{args.retry_attempts})",
                flush=True,
            )
            time.sleep(max(0, args.retry_delay))

        totals["attempts"] += 1
        try:
            result = process_account_attempt(args, username, password)
        except Exception as exc:
            result = {
                "ok": False,
                "reason": f"Lỗi ngoài dự kiến: {exc}",
                "stats": _new_stats(),
            }
        _add_stats(totals, result["stats"])
        last_result = result
        if result.get("skipped"):
            skipped_accounts.append((account_number, username))

        if result["ok"]:
            totals["success"] += 1
            if attempt:
                log(f"  ✅ Tài khoản thành công sau retry lần {attempt}", flush=True)
            break
        if attempt < args.retry_attempts:
            log(f"  ⚠️ {result['reason']}; sẽ retry toàn tài khoản", flush=True)

    if not last_result["ok"]:
        totals["failed"] += 1
        reason = (
            f"hết {args.retry_attempts} lần retry: "
            f"{last_result['reason']}"
        )
        failures.append(f"tài khoản={username} | lỗi={reason}")
        failed_accounts.append((username, password, reason))

    return {
        "totals": totals,
        "failures": failures,
        "failed_accounts": failed_accounts,
        "skipped_accounts": skipped_accounts,
    }


def run_account_lane(args, lane_accounts, total_accounts, lane_number, log_dir):
    results = []
    configure_lane_log(log_dir / f"luong{lane_number}.log")
    try:
        for position, (account_number, username, password) in enumerate(lane_accounts):
            try:
                log(f"  🔐 Session account {username}")
                result = process_account(
                    args,
                    account_number,
                    total_accounts,
                    username,
                    password,
                )
            except Exception as exc:
                result = _failed_account_result(
                    username,
                    password,
                    f"Worker tài khoản lỗi: {exc}",
                )
            results.append(result)
            if (not args.dry_run and position + 1 < len(lane_accounts)
                    and args.account_delay > 0):
                time.sleep(args.account_delay)
        return results
    finally:
        close_lane_log()


def main():
    args = build_parser().parse_args()
    failed_csv = args.failed_csv or args.csv_file.with_name(
        f"{args.csv_file.stem}-failed{args.csv_file.suffix}"
    )
    log_dir = args.log_dir or ROOT_DIR / "log" / "nhanexp_and_doiyenquaxu"
    log_file = args.log_file or log_dir / "main.log"
    if not args.csv_file.is_file():
        log(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if args.character_index is not None and args.character_index < 0:
        log("❌ --character-index không được âm")
        return 2
    if args.character_index is not None and args.character_name:
        log("❌ Chỉ dùng một trong --character-index hoặc --character-name")
        return 2
    if args.max_characters < 0:
        log("❌ --max-characters không được âm")
        return 2
    if args.max_accounts < 0:
        log("❌ --max-accounts không được âm")
        return 2
    if args.retry_attempts < 0:
        log("❌ --retry-attempts không được âm")
        return 2
    if args.retry_delay < 0:
        log("❌ --retry-delay không được âm")
        return 2
    if args.luong < 1:
        log("❌ --luong phải lớn hơn hoặc bằng 1")
        return 2
    if failed_csv.resolve() == args.csv_file.resolve():
        log("❌ --failed-csv không được trùng file CSV đầu vào")
        return 2
    if log_file.resolve() in (args.csv_file.resolve(), failed_csv.resolve()):
        log("❌ --log-file không được trùng CSV đầu vào hoặc file lỗi")
        return 2
    try:
        accounts = read_accounts(args.csv_file)
    except Exception as exc:
        log(f"❌ Không đọc được CSV: {exc}")
        return 2
    if args.max_accounts:
        accounts = accounts[:args.max_accounts]
    if not accounts:
        log("❌ CSV không có tài khoản hợp lệ")
        return 2

    try:
        log_dir.mkdir(parents=True, exist_ok=True) # nếu chưa có thì tạo thư mục log, nếu có thư mục log thì không báo lỗi
        clear_log_files(log_dir, log_file)
        configure_log(log_file)
    except OSError as exc:
        log(f"❌ Không mở được file log: {exc}")
        return 2

    if args.dry_run:
        mode = "CHỈ XEM"
    elif args.receive_exp:
        mode = "CẬP NHẬT + NHẬN EXP"
    else:
        mode = "CẬP NHẬT + BỎ QUA EXP"
    log(f"NSO NHẬN EXP OFFLINE: {len(accounts)} tài khoản; "
          f"mode={mode}; exp={'ON' if args.receive_exp else 'OFF'}; "
          f"retry={args.retry_attempts}; luồng yêu cầu={args.luong}; "
          f"log-dir={log_dir}; log={log_file}")
    totals = {
        "accounts": len(accounts),
        "success": 0,
        "failed": 0,
        "accounts_retried": 0,
        "retries": 0,
        **_new_stats(),
    }
    failures = []
    failed_accounts = []
    skipped_accounts = []

    effective_workers = min(args.luong, len(accounts))
    if effective_workers < args.luong:
        log(
            f"⚠️ Chỉ có {len(accounts)} tài khoản; dùng "
            f"{effective_workers} luồng hiệu dụng",
            flush=True,
        )
    log(f"🚀 Bắt đầu chạy song song {effective_workers} luồng", flush=True)
    lanes = [[] for _ in range(effective_workers)]
    for account_number, (username, password) in enumerate(accounts, start=1):
        lanes[(account_number - 1) % effective_workers].append(
            (account_number, username, password)
        )

    with ThreadPoolExecutor(
        max_workers=effective_workers,
        thread_name_prefix="nhanexp",
    ) as executor:
        jobs = {
            executor.submit(
                run_account_lane,
                args,
                lane,
                len(accounts),
                lane_number,
                log_dir,
            ): lane
            for lane_number, lane in enumerate(lanes, start=1)
        }
        for future in as_completed(jobs):
            try:
                results = future.result()
            except Exception as exc:
                log(f"❌ Worker lỗi: {exc}", flush=True)
                for _, username, password in jobs[future]:
                    result = _failed_account_result(
                        username,
                        password,
                        f"Worker lỗi: {exc}",
                    )
                    _add_stats(totals, result["totals"])
                    failures.extend(result["failures"])
                    failed_accounts.extend(result["failed_accounts"])
                    skipped_accounts.extend(result["skipped_accounts"])
                continue
            for result in results:
                _add_stats(totals, result["totals"])
                failures.extend(result["failures"])
                failed_accounts.extend(result["failed_accounts"])
                skipped_accounts.extend(result["skipped_accounts"])

    write_error = None
    try:
        write_failed_accounts(failed_csv, failed_accounts)
        log(
            f"\n🔁 Đã ghi {len(failed_accounts)} tài khoản còn lỗi vào "
            f"{failed_csv}"
        )
    except OSError as exc:
        write_error = exc
        log(f"\n⚠️ Không ghi được file tài khoản lỗi: {exc}")

    log(
        f"\nTỔNG KẾT: tổng acc={totals['accounts']}, "
        f"thành công={totals['success']}, "
        f"thất bại thật sự={totals['failed']}"
    )
    log(f"\nDANH SÁCH ACC BỎ QUA DO THIẾU ĐIỂM ({len(skipped_accounts)}):")
    for account_number, username in sorted(skipped_accounts):
        log(f"  - [{account_number}] {username}")
    exit_code = 0 if totals["failed"] == 0 and write_error is None else 1
    close_log()
    return exit_code


if __name__ == "__main__":
    raise SystemExit(main())
