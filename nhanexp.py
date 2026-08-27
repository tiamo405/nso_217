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
python nhanexp.py [--host HOST] [--port PORT] [--character-index INDEX | --character-name NAME]
  --host HOST: địa chỉ server (mặc định Nsm1.ninjasm.net)
  --port PORT: cổng server (mặc định 14444)
  --character-index INDEX: chỉ xử lý nhân vật index này (0-based)
  --character-name NAME: chỉ xử lý nhân vật đúng tên này (ổn định hơn index)
  --max-characters N: chỉ xử lý tối đa N nhân vật đầu tiên (mặc định 0 = không giới hạn)
  --dry-run: chỉ xem level/trạng thái mong muốn, không di chuyển hay bấm NPC
  --login-delay SECONDS: chờ sau khi đăng nhập trước nhân vật đầu tiên (mặc định 11.0)
  --character-delay SECONDS: chờ sau khi xử lý mỗi nhân vật (mặc định 11.0)
  --account-delay SECONDS: chờ sau khi xử lý mỗi tài khoản (mặc định 11.0)
Lưu ý:
  - Chỉ nhận EXP Offline miễn phí 0 Lượng = 100% (option 0)
"""


import argparse
import csv
import sys
import time
import unicodedata
from dataclasses import dataclass, field
from pathlib import Path
import importlib

from hoatdong import NSOActivityClient, NSOMessage, NSOReader


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-nhanexp.csv"
DEFAULT_HOST = "Nsm1.ninjasm.net"
DEFAULT_PORT = 14444
DEFAULT_DELAY = 11.0
TONE_MAP_ID = 22
TAJIMA_NPC_ID = 12
STATUS_MENU_ID = 6
OFFLINE_EXP_MENU_ID = 7
FREE_EXP_OPTION_ID = 0
VILLAGE_MAPS = {10, 17, 22, 32, 38, 43, 48}
VILLAGE_MENU_IDS = {10: 1, 17: 2, 22: 3, 32: 4, 38: 5, 43: 6, 48: 7}

# Tái sử dụng graph đã port trực tiếp từ TileMap.fieldBZ của Java client.
PYTHON_WORKER_DIR = ROOT_DIR / "python-worker"
if str(PYTHON_WORKER_DIR) not in sys.path:
    sys.path.insert(0, str(PYTHON_WORKER_DIR))
# from models.map_graph import MAP_GRAPH, find_map_path  # noqa: E402
MAP_GRAPH = importlib.import_module("models.map_graph").MAP_GRAPH
find_map_path = importlib.import_module("models.map_graph").find_map_path


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


@dataclass
class FailureRecord:
    username: str
    character_index: int | None
    character_name: str | None
    reason: str


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

    def __init__(self, host: str, port: int):
        super().__init__(host, port)
        self.map_state = MapState()

    def select_character_and_load_map(self, character_name: str) -> bool:
        if not any(name == character_name for name, _, _ in self.characters):
            print(f"    ❌ Không còn thấy nhân vật {character_name} sau khi đăng nhập lại")
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
                print(f"    ❌ Chọn nhân vật: {self._read_server_error(data)}")
                return False
            if command == self.CMD_SUB_COMMAND and data:
                game_ready = NSOReader(data).read_byte() == -127 or game_ready
            elif command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)
            if game_ready and self.map_state.map_id >= 0:
                return True
        print("    ❌ Timeout khi chờ thông tin nhân vật/map")
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
                print(f"    ↪ Chuyển map: {self._read_server_error(data)}")
                continue
            if command == self.CMD_MAP_LOAD and data:
                self.map_state = self._parse_map(data)
                if self.map_state.map_id != old_map_id:
                    return True
        return False

    def move_to_tone(self, max_steps: int = 20) -> bool:
        for _ in range(max_steps):
            current = self.map_state.map_id
            if current == TONE_MAP_ID:
                return True
            path = find_map_path(current, TONE_MAP_ID)
            if not path or len(path) < 2:
                print(f"    ❌ Không tìm được đường map {current} → {TONE_MAP_ID}")
                return False
            next_map = path[1]
            neighbors = MAP_GRAPH.get(current, [])
            if next_map not in neighbors:
                print(f"    ❌ Graph thiếu cạnh map {current} → {next_map}")
                return False

            # Các cạnh trực tiếp giữa làng trong TileMap.fieldAK không phải
            # waypoint. Java mã hóa thành GameScr.fieldAB(7, villageMenu, 0):
            # giao dịch NPC ID 7 và chọn làng đích (Tone = menu 3).
            if current in VILLAGE_MAPS and next_map in VILLAGE_MAPS:
                npc = self.map_state.find_npc(7)
                menu_id = VILLAGE_MENU_IDS.get(next_map)
                if npc is None or menu_id is None:
                    print(f"    ❌ Không tìm thấy NPC chuyển làng cho {current} → {next_map}")
                    return False
                print(f"    🗺️ Làng {current} → {next_map} qua NPC 7, menu {menu_id}")
                self.move_character(npc.x, self.map_state.char_y)
                time.sleep(0.5)
                self._choose_npc_menu(7, menu_id)
                if not self._wait_for_map_change(current):
                    print(f"    ❌ Timeout chuyển làng {current} → {next_map}")
                    return False
                continue

            waypoint_index = neighbors.index(next_map)
            if waypoint_index >= len(self.map_state.waypoints):
                print(f"    ❌ Map {current}: cần waypoint {waypoint_index}, "
                      f"server chỉ trả {len(self.map_state.waypoints)}")
                return False
            waypoint = self.map_state.waypoints[waypoint_index]
            x = (waypoint.min_x + waypoint.max_x) // 2
            y = waypoint.max_y
            print(f"    🗺️ Map {current} → {next_map} qua waypoint {waypoint_index}")
            self.move_character(x, y)
            time.sleep(0.35)
            self._request_change_map()
            if not self._wait_for_map_change(current):
                print(f"    ❌ Timeout chuyển map {current} → {next_map}")
                return False
        return self.map_state.map_id == TONE_MAP_ID

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
        print(f"    🔁 {message}")
        if state == desired_enabled:
            return True, message

        self.open_tajima()
        self.choose_tajima_menu(STATUS_MENU_ID)
        state, message = self._wait_status_result()
        if state is None:
            return False, message
        print(f"    🔁 {message}")
        if state != desired_enabled:
            expected = "bật" if desired_enabled else "tắt"
            return False, f"Server chưa chuyển trạng thái về {expected}"
        return True, message

    def request_offline_exp_options(self, timeout: float = 15):
        self.choose_tajima_menu(OFFLINE_EXP_MENU_ID)
        deadline = time.time() + timeout
        while time.time() < deadline:
            command, data = self.receive(max(0.2, deadline - time.time()))
            if command is None:
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
            return True, "server đã cập nhật EXP nhân vật"
        if messages:
            text = " | ".join(messages)
            normalized = normalize_text(text)
            received_exp = "da nhan duoc" in normalized and "exp" in normalized
            no_exp = any(value in normalized for value in (
                "khong co", "chua co", "exp luu tru", "kinh nghiem luu tru",
            ))
            return received_exp or no_exp, text
        return False, "timeout chờ kết quả nhận Exp Offline"


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
    parser.add_argument("--max-characters", type=int, default=0)
    parser.add_argument("--dry-run", action="store_true",
                        help="Chỉ xem level/trạng thái mong muốn, không di chuyển hay bấm NPC")
    parser.add_argument("--login-delay", type=float, default=DEFAULT_DELAY)
    parser.add_argument("--character-delay", type=float, default=DEFAULT_DELAY)
    parser.add_argument("--account-delay", type=float, default=DEFAULT_DELAY)
    return parser


def discover_characters(host, port, username, password):
    client = OfflineExpClient(host, port)
    try:
        if not client.connect() or not client.login(username, password):
            return []
        return list(client.characters)
    finally:
        client.disconnect()


def process_character(client: OfflineExpClient, level: int):
    print(f"    Map hiện tại: {client.map_state.map_id} ({client.map_state.name})")
    if not client.move_to_tone():
        return False, "Không về được Làng Tone"
    print("    ✅ Đã ở Làng Tone")
    # Làng đông người phát liên tục packet di chuyển/đánh quái. Xả backlog
    # trước khi gửi menu để response Tajima không bị chậm sau hàng trăm packet.
    client.drain(2.0)
    if not client.open_tajima():
        return False, "Không tìm thấy Tajima (NPC 12) tại Làng Tone"

    desired_enabled = level >= 42
    expected = "[Đang bật]" if desired_enabled else "[Đang tắt]"
    print(f"    Trạng thái yêu cầu theo level {level}: {expected} Không nhận kinh nghiệm")
    ok, result = client.ensure_no_exp_state(desired_enabled)
    if not ok:
        return False, result
    print(f"    ✅ Trạng thái đã đúng: {expected}")

    # Giao dịch lại với Tajima như luồng mobile trước khi mở Exp Offline.
    if not client.open_tajima():
        return False, "Không mở lại được Tajima"
    options, error = client.request_offline_exp_options()
    if options is None:
        return False, error
    print(f"    Menu Exp Offline: {options}")
    if not options:
        return False, "Menu Exp Offline rỗng"
    free_option = normalize_text(options[FREE_EXP_OPTION_ID])
    if "0 luong" not in free_option or "100%" not in free_option:
        return False, f"Từ chối nhận: option 0 ngoài dự kiến ({options[0]})"

    ok, result = client.claim_free_offline_exp()
    if not ok:
        return False, result
    print(f"    🎁 Đã chọn 0 Lượng = 100%: {result}")
    return True, result


def main():
    args = build_parser().parse_args()
    if not args.csv_file.is_file():
        print(f"❌ Không tìm thấy CSV: {args.csv_file}")
        return 2
    if args.character_index is not None and args.character_index < 0:
        print("❌ --character-index không được âm")
        return 2
    if args.character_index is not None and args.character_name:
        print("❌ Chỉ dùng một trong --character-index hoặc --character-name")
        return 2
    if args.max_characters < 0:
        print("❌ --max-characters không được âm")
        return 2
    try:
        accounts = read_accounts(args.csv_file)
    except Exception as exc:
        print(f"❌ Không đọc được CSV: {exc}")
        return 2

    print(f"NSO NHẬN EXP OFFLINE: {len(accounts)} tài khoản; "
          f"mode={'CHỈ XEM' if args.dry_run else 'CẬP NHẬT + NHẬN EXP'}")
    totals = {"characters": 0, "success": 0, "failed": 0}
    failures = []

    for account_number, (username, password) in enumerate(accounts, start=1):
        print(f"\n[{account_number}/{len(accounts)}] Tài khoản {username}")
        characters = discover_characters(args.host, args.port, username, password)
        if not characters:
            totals["failed"] += 1
            failures.append(FailureRecord(username, None, None,
                                          "Không đăng nhập/lấy được danh sách nhân vật"))
            continue
        indexes = list(range(len(characters)))
        if args.character_name:
            indexes = [index for index, character in enumerate(characters)
                       if character[0] == args.character_name]
        elif args.character_index is not None:
            indexes = ([args.character_index]
                       if args.character_index < len(characters) else [])
        if args.max_characters:
            indexes = indexes[:args.max_characters]
        if not indexes:
            totals["failed"] += 1
            failures.append(FailureRecord(username, args.character_index, None,
                                          (f"Không có nhân vật phù hợp"
                                           f" ({args.character_name or args.character_index})")))
            continue

        if args.dry_run:
            for index in indexes:
                name, level, school = characters[index]
                target = "BẬT" if level >= 42 else "TẮT"
                print(f"  [{index}] {name} lv{level} ({school}) → cần {target}")
                totals["characters"] += 1
            continue

        print(f"  ⏳ Chờ {max(0, args.login_delay):g} giây trước nhân vật đầu tiên...")
        time.sleep(max(0, args.login_delay))
        for position, index in enumerate(indexes):
            name, level, school = characters[index]
            print(f"  [{index}] {name} lv{level} ({school})")
            client = OfflineExpClient(args.host, args.port)
            try:
                if not client.connect() or not client.login(username, password):
                    raise RuntimeError("Không kết nối/đăng nhập lại được")
                if not client.select_character_and_load_map(name):
                    raise RuntimeError("Không chọn được nhân vật hoặc tải map")
                totals["characters"] += 1
                ok, reason = process_character(client, level)
                if ok:
                    totals["success"] += 1
                else:
                    totals["failed"] += 1
                    failures.append(FailureRecord(username, index, name, reason))
                    print(f"    ❌ {reason}")
            except Exception as exc:
                totals["failed"] += 1
                failures.append(FailureRecord(username, index, name, str(exc)))
                print(f"    ❌ Lỗi xử lý: {exc}")
            finally:
                client.disconnect()
            if position + 1 < len(indexes):
                print(f"  ⏳ Chờ {max(0, args.character_delay):g} giây trước nhân vật tiếp...")
                time.sleep(max(0, args.character_delay))
        if account_number < len(accounts):
            time.sleep(max(0, args.account_delay))

    print("\nTỔNG KẾT: "
          f"nhân vật={totals['characters']}, thành công={totals['success']}, "
          f"lỗi={totals['failed']}")
    if failures:
        print(f"\nDANH SÁCH CẦN CHẠY LẠI ({len(failures)}):")
        for failure in failures:
            character = ("chưa xác định" if failure.character_index is None else
                         f"[{failure.character_index}] {failure.character_name or 'không rõ'}")
            print(f"  - tài khoản={failure.username} | nhân vật={character} | "
                  f"lỗi={failure.reason}")
    return 0 if totals["failed"] == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
