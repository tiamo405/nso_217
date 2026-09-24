#!/usr/bin/env python3
"""NSO Mail Client cho giao thức mobile 4.1.1.

MailClient trong GameAssembly.dll dùng cmd -40, action byte 0/1/2/3.
Tự động đọc account-hoatdong.csv, xử lý mọi nhân vật, nhận quà rồi xóa thư.
Chạy: python3 mail_client.py [duong-dan.csv]
"""

import argparse
import csv
from pathlib import Path

import socket
import struct
import time
import random
from io import BytesIO


# ─────────────────────────────────────────────
#  Config - SỬA ĐÂY
# ─────────────────────────────────────────────
HOST     = "Nsm1.ninjasm.net"
# HOST truyen ky
# HOST     = "Nsotk1.nsotk.online"
PORT     = 14444
DEFAULT_CSV = Path(__file__).resolve().parent / "account-hoatdong.csv"
DEFAULT_DELAY = 11.0
# ─────────────────────────────────────────────


class NSOMessage:
    """Tạo binary message theo protocol NSO"""

    def __init__(self, command_byte):
        self.command = command_byte
        self.buffer = BytesIO()

    def write_byte(self, v):   self.buffer.write(struct.pack('b', v))
    def write_ubyte(self, v):  self.buffer.write(struct.pack('B', v))
    def write_short(self, v):  self.buffer.write(struct.pack('>h', v))
    def write_int(self, v):    self.buffer.write(struct.pack('>i', v))
    def write_long(self, v):   self.buffer.write(struct.pack('>q', v))
    def write_boolean(self, v):self.buffer.write(struct.pack('B', 1 if v else 0))

    def write_utf(self, text):
        b = text.encode('utf-8')
        self.buffer.write(struct.pack('>H', len(b)))
        self.buffer.write(b)

    def get_data(self):
        return self.buffer.getvalue()

    def to_raw_packet(self):
        """[command:1][length:2][data:n]"""
        data = self.get_data()
        pkt = BytesIO()
        pkt.write(struct.pack('b', self.command))
        pkt.write(struct.pack('>H', len(data)))
        pkt.write(data)
        return pkt.getvalue()


class NSOReader:
    """Đọc binary message từ server"""

    def __init__(self, data):
        self.buf = BytesIO(data)

    def read_byte(self):
        d = self.buf.read(1)
        if not d: raise EOFError
        return struct.unpack('b', d)[0]

    def read_ubyte(self):
        d = self.buf.read(1)
        if not d: raise EOFError
        return struct.unpack('B', d)[0]

    def read_short(self):
        d = self.buf.read(2)
        if len(d) < 2: raise EOFError
        return struct.unpack('>h', d)[0]

    def read_ushort(self):
        d = self.buf.read(2)
        if len(d) < 2: raise EOFError
        return struct.unpack('>H', d)[0]

    def read_int(self):
        d = self.buf.read(4)
        if len(d) < 4: raise EOFError
        return struct.unpack('>i', d)[0]

    def read_long(self):
        d = self.buf.read(8)
        if len(d) < 8: raise EOFError
        return struct.unpack('>q', d)[0]

    def read_boolean(self):
        return self.read_ubyte() != 0

    def read_utf(self):
        length = self.read_ushort()
        data = self.buf.read(length)
        if len(data) != length:
            raise EOFError("UTF payload bị thiếu")
        return data.decode('utf-8', errors='replace')

    def remaining(self):
        pos = self.buf.tell()
        self.buf.seek(0, 2)
        end = self.buf.tell()
        self.buf.seek(pos)
        return end - pos

    def read_bytes(self, n):
        return self.buf.read(n)


class NSOMailClient:
    CMD_NOT_LOGIN   = -29
    CMD_NOT_MAP     = -28
    CMD_SUB_COMMAND = -30
    CMD_KEY_EXCHANGE= -27
    CMD_MAIL_SYSTEM = -40
    MAIL_LIST = 0
    MAIL_READ = 1
    MAIL_DELETE = 2
    MAIL_CLAIM_ATTACHMENTS = 3

    # NOT_LOGIN sub-commands
    CMD_SET_CLIENT  = -125
    CMD_LOGIN       = -127
    CMD_SELECT_CHAR = -126
    CMD_REGISTER    = -122

    # NOT_MAP sub-commands (gửi lên server)
    CMD_REWARD_PB   = -82   # rewardPB, độc lập với hệ thống thư
    CMD_REWARD_CT   = -79   # nhận quà chiến trường

    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = None
        self.connected = False
        self.key = None
        self.key_pos_r = 0
        self.key_pos_w = 0
        self.characters = []
        self._pending = set()
        self.mails = []
        self._recv_buffer = bytearray()
        self._bag_full = False
        self.last_mail_stats = {
            "claimed": 0,
            "deleted": 0,
            "failed": 0,
            "kept": 0,
            "bag_full": 0,
            "bag_full_mail_ids": [],
            "rewards": [],
            "mail_results": [],
        }
        self.last_mail_errors = []
        self.last_mail_rewards = []

    # ─── Low-level ───────────────────────────

    def _recv_exact(self, n):
        data = bytearray()
        while len(data) < n:
            chunk = self.sock.recv(n - len(data))
            if not chunk:
                raise ConnectionError(f"Socket closed (got {len(data)}/{n})")
            data.extend(chunk)
        return bytes(data)

    def _enc(self, byte_val):
        if not self.key: return byte_val
        r = (self.key[self.key_pos_w] ^ byte_val) & 0xFF
        self.key_pos_w = (self.key_pos_w + 1) % len(self.key)
        return r

    def _dec(self, byte_val):
        if not self.key: return byte_val
        r = (self.key[self.key_pos_r] ^ byte_val) & 0xFF
        self.key_pos_r = (self.key_pos_r + 1) % len(self.key)
        return r

    # ─── Connect ─────────────────────────────

    def connect(self):
        print(f"🔌 Kết nối {self.host}:{self.port}...")
        try:
            self.key = None
            self.key_pos_r = self.key_pos_w = 0
            self._recv_buffer.clear()
            self.characters = []
            self.mails = []
            self._pending.clear()
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.settimeout(15)
            self.sock.connect((self.host, self.port))
            self.connected = True
            print("✅ Đã kết nối")
            self._key_exchange()
            return True
        except Exception as e:
            print(f"❌ Lỗi kết nối: {e}")
            self.disconnect()
            return False

    def disconnect(self):
        if self.sock:
            self.sock.close()
            self.sock = None
        self.connected = False
        print("🔌 Đã ngắt kết nối")

    def _key_exchange(self):
        print("🔑 Key exchange...")
        pkt = struct.pack('b', self.CMD_KEY_EXCHANGE) + struct.pack('>h', 0)
        self.sock.sendall(pkt)

        cmd_b = self._recv_exact(1)
        cmd = struct.unpack('b', cmd_b)[0]
        if cmd != self.CMD_KEY_EXCHANGE:
            raise ConnectionError(f"Key exchange trả cmd không hợp lệ: {cmd}")

        len_b = self._recv_exact(2)
        length = struct.unpack('>H', len_b)[0]
        payload = self._recv_exact(length)

        if not payload or not 0 < payload[0] <= len(payload) - 1:
            raise ConnectionError("Key exchange thiếu khóa hoặc khóa rỗng")
        key_len = payload[0]
        raw_key = list(payload[1:key_len + 1])
        # Chain XOR decode
        for i in range(len(raw_key) - 1):
            raw_key[i + 1] ^= raw_key[i]
        self.key = raw_key
        self.key_pos_r = self.key_pos_w = 0
        print(f"✅ Key nhận được ({len(self.key)} bytes): {bytes(self.key).hex()}")

    # ─── Send / Receive ───────────────────────

    def send_raw(self, msg: NSOMessage):
        """Encrypt và gửi packet"""
        if self.sock is None or not self.connected or self.key is None:
            raise ConnectionError("Socket mail đã đóng")
        pkt = msg.to_raw_packet()
        if self.key:
            pkt = bytes(self._enc(b) for b in pkt)
        try:
            self.sock.sendall(pkt)
        except (AttributeError, ConnectionError, OSError):
            self.disconnect()
            raise

    def recv_packet(self, timeout=10):
        """Giữ frame dở dang qua timeout để không lệch luồng XOR/TCP."""
        deadline = time.monotonic() + timeout
        try:
            while True:
                buf = self._recv_buffer
                header_size = 6 if buf and buf[0] == 224 else 3
                if len(buf) >= header_size:
                    cmd = buf[1] if header_size == 6 else buf[0]
                    length = int.from_bytes(buf[2:6] if header_size == 6 else buf[1:3], 'big')
                    if len(buf) >= header_size + length:
                        data = bytes(buf[header_size:header_size + length])
                        del buf[:header_size + length]
                        return cmd if cmd < 128 else cmd - 256, data
                remaining = deadline - time.monotonic()
                if remaining <= 0:
                    return None, None
                self.sock.settimeout(remaining)
                chunk = self.sock.recv(65536)
                if not chunk:
                    raise ConnectionError("Socket closed")
                buf.extend(self._dec(b) for b in chunk)
        except socket.timeout:
            return None, None
        except Exception as e:
            print(f"❌ recv error: {e}")
            self.disconnect()
            return None, None

    def recv_all(self, timeout=3, max_pkts=50):
        """Nhận tất cả packet trong timeout giây"""
        pkts = []
        deadline = time.time() + timeout
        while time.time() < deadline and len(pkts) < max_pkts:
            remaining = deadline - time.time()
            cmd, data = self.recv_packet(timeout=max(0.2, remaining))
            if cmd is None:
                break
            pkts.append((cmd, data))
        return pkts

    # ─── Login flow ───────────────────────────

    def set_client_type(self):
        """Gửi đúng layout Service.setClientType() của Unity mobile 4.1.1."""
        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_SET_CLIENT)
        msg.write_byte(4)           # GameMidlet.CLIENT_TYPE mặc định mobile
        msg.write_byte(1)           # zoomLevel; writeByte(int) vẫn chỉ ghi 1 byte
        msg.write_boolean(True)     # isGPRS
        msg.write_int(480)          # width
        msg.write_int(800)          # height
        msg.write_boolean(True)     # isQwerty
        msg.write_boolean(True)     # isTouch
        msg.write_utf("Unity Mobile")
        msg.write_int(0)
        msg.write_byte(0)
        msg.write_byte(0)
        msg.write_int(0)            # languageID cũng được ghi bằng int32
        msg.write_utf("0")          # client agent phụ
        self.send_raw(msg)

    def _send_not_map(self, sub_cmd):
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(sub_cmd)
        self.send_raw(msg)

    def login(self, username, password, version="4.1.1"):
        print(f"\n🔐 Đăng nhập: {username}")
        self.set_client_type()

        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_LOGIN)
        msg.write_utf(username)
        msg.write_utf(password)
        msg.write_utf(version)
        msg.write_utf("")
        msg.write_utf("")
        msg.write_utf("".join(str(random.randint(0, 8)) for _ in range(12)))
        msg.write_byte(0)
        self.send_raw(msg)

        # Xử lý response login
        while True:
            cmd, data = self.recv_packet(timeout=15)
            if cmd is None:
                print("❌ Timeout login")
                return False
            result = self._handle_login_pkt(cmd, data)
            if result is not None:
                return result

    def _handle_login_pkt(self, cmd, data):
        r = NSOReader(data)
        try:
            if cmd == -43:          # server time
                r.read_long()
                return None
            if cmd == -26:          # error
                print(f"❌ Server: {r.read_utf()}")
                return False
            if cmd == self.CMD_NOT_MAP:
                sub = r.read_byte()
                if sub == -123:     # version check
                    [r.read_byte() for _ in range(4)]
                    self._pending = {-122, -121, -120, -119}
                    for c in (-122, -121, -120, -119):
                        self._send_not_map(c)
                    return None
                if sub in (-122, -121, -120, -119):
                    self._pending.discard(sub)
                    if not self._pending:
                        self._send_not_map(-101)   # clientOk
                    return None
                if sub == -126:     # char list
                    count = r.read_byte()
                    self.characters = []
                    for _ in range(count):
                        r.read_byte()  # gender
                        name   = r.read_utf()
                        school = r.read_utf()
                        level  = r.read_ubyte()
                        r.read_short(); r.read_short()
                        r.read_short(); r.read_short()
                        self.characters.append(name)
                        print(f"  👤 {name} lv{level} ({school})")
                    print(f"✅ Login OK - {len(self.characters)} nhân vật")
                    return bool(self.characters)
            return None
        except Exception as e:
            print(f"  parse err: {e}")
            return None

    def select_character(self, idx=0):
        if not 0 <= idx < len(self.characters):
            print(f"❌ Không có nhân vật index {idx}")
            return False
        name = self.characters[idx]
        print(f"\n👤 Chọn nhân vật: {name}")
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(self.CMD_SELECT_CHAR)
        msg.write_utf(name)
        self.send_raw(msg)

        # Đợi vào map (cmd -30 sub -127 = player data)
        print("⏳ Đang vào game...")
        deadline = time.time() + 20
        while time.time() < deadline:
            cmd, data = self.recv_packet(timeout=5)
            if cmd is None:
                if not self.connected:
                    return False
                continue
            if cmd == self.CMD_SUB_COMMAND:
                try:
                    sub = NSOReader(data).read_byte()
                    if sub == -127:
                        print("✅ Đã vào game!")
                        return True
                except: pass
            elif cmd == -26:
                print(f"❌ Server: {NSOReader(data).read_utf()}")
                return False
            # drain các packet khác
        print("❌ Timeout vào game")
        return False

    # ─── Reward ──────────────────────────────

    def request_reward_pb(self):
        """Gửi rewardPB qua NOT_MAP -82, không phải nhận quà thư."""
        print("\n🎁 Gửi request rewardPB (-82)...")
        self._send_not_map(self.CMD_REWARD_PB)

    def request_reward_ct(self):
        """Gửi yêu cầu nhận quà chiến trường (rewardCT, cmd NOT_MAP -79)"""
        print("\n🏆 Gửi request nhận quà chiến trường (rewardCT -79)...")
        self._send_not_map(self.CMD_REWARD_CT)

    # ─── Mobile mail protocol ─────────────────

    def _send_mail_action(self, action, mail_id=None):
        """Packet mobile: cmd -40, action byte, optional mailId int32.

        Mã native gọi writeByte(int), nhưng implementation ép int xuống đúng
        một byte trước khi ghi vào buffer.
        """
        if action not in (self.MAIL_LIST, self.MAIL_READ, self.MAIL_DELETE,
                          self.MAIL_CLAIM_ATTACHMENTS):
            raise ValueError("Mail action không hợp lệ")
        if (action == self.MAIL_LIST) != (mail_id is None):
            raise ValueError("MAIL_LIST không có ID; đọc/nhận/xóa phải có ID thư")
        msg = NSOMessage(self.CMD_MAIL_SYSTEM)
        msg.write_byte(action)
        if mail_id is not None:
            msg.write_int(mail_id)
        self.send_raw(msg)

    def _parse_mail_list(self, data):
        """Parse đúng layout HandleMailListResponse của mobile 4.1.1."""
        r = NSOReader(data)
        action = r.read_ubyte()
        if action != self.MAIL_LIST:
            raise ValueError(f"Không phải MAIL_LIST response: action={action}")
        count = r.read_ubyte()
        mails = []
        for _ in range(count):
            mails.append({
                "mail_id": r.read_int(),
                "sender": r.read_utf(),
                "title": r.read_utf(),
                "is_read": r.read_boolean(),
                "is_received": r.read_boolean(),
                "created_time": r.read_long(),
                "expired_time": r.read_long(),
                "has_attachments": r.read_boolean(),
            })
        return mails

    def request_mail_list(self):
        print("\n📬 Yêu cầu danh sách thư mobile (cmd -40, action byte=0)...")
        self.mails = []
        self._send_mail_action(self.MAIL_LIST)
        data = self._wait_mail_packet(self.MAIL_LIST, timeout=12)
        if data is None:
            raise RuntimeError("Không nhận được danh sách thư (cmd -40/action 0)")
        try:
            self.mails = self._parse_mail_list(data)
            print(f"✅ Có {len(self.mails)} thư:")
            for mail in self.mails:
                state = "đã nhận" if mail["is_received"] else "chưa nhận"
                attach = "có quà" if mail["has_attachments"] else "không quà"
                print(f"  id={mail['mail_id']} | {mail['title']} | {state}, {attach}")
        except Exception as e:
            raise RuntimeError(f"Không parse được danh sách thư: {e}") from e
        return self.mails

    def _wait_mail_packet(self, action, mail_id=None, timeout=12):
        """Chờ đúng packet mail, bỏ qua packet map/chat chạy xen kẽ."""
        deadline = time.time() + timeout
        skipped = 0
        while time.time() < deadline:
            cmd, data = self.recv_packet(timeout=max(0.2, deadline - time.time()))
            if cmd is None:
                break
            if cmd in (-26, -24):
                try:
                    msg_text = NSOReader(data).read_utf()
                    print(f"  [mail] ← Server: {msg_text}")
                    normalized = msg_text.casefold()
                    if any(phrase in normalized for phrase in (
                            "hành trang không đủ chỗ",
                            "hành trang đầy",
                            "không đủ chỗ trong hành trang",
                            "túi đồ không đủ chỗ",
                            "rương đầy",
                            "chật rương",
                    )):
                        self._bag_full = True
                        return None
                    if any(k in msg_text.lower() for k in ["thư", "hành trang", "đính kèm", "vật phẩm"]):
                        return None
                except Exception:
                    _log_packet("  [mail] ← ", cmd, data)
                    return None
                skipped += 1
                continue
            if cmd != self.CMD_MAIL_SYSTEM or not data or data[0] != action:
                skipped += 1
                continue
            if mail_id is not None:
                if len(data) < 5 or struct.unpack('>i', data[1:5])[0] != mail_id:
                    skipped += 1
                    continue
            if skipped:
                print(f"  ↪ Đã bỏ qua {skipped} packet game/chat để chờ mail")
            return data
        if skipped:
            print(f"  ↪ Đã bỏ qua {skipped} packet game/chat nhưng chưa thấy response mail")
        return None

    def read_mail(self, mail_id):
        print(f"📖 Đọc thư id={mail_id}")
        self._send_mail_action(self.MAIL_READ, mail_id)

    def claim_mail_attachment(self, mail_id):
        print(f"📦 Nhận quà thư id={mail_id}")
        self._send_mail_action(self.MAIL_CLAIM_ATTACHMENTS, mail_id)

    def delete_mail(self, mail_id):
        print(f"🗑️ Xóa thư id={mail_id}")
        self._send_mail_action(self.MAIL_DELETE, mail_id)

    def _parse_mail_read(self, data):
        """W5.MailClient.HandleMailReadResponse, RVA 0x187FC80."""
        r = NSOReader(data)
        if r.read_ubyte() != self.MAIL_READ:
            raise ValueError("Không phải phản hồi đọc thư")
        mail = {
            "mail_id": r.read_int(),
            "sender": r.read_utf(),
            "title": r.read_utf(),
            "content": r.read_utf(),
            "expired_time": r.read_long(),
            "has_attachments": r.read_boolean(),
        }
        if mail["has_attachments"]:
            mail.update(luong=r.read_int(), xu=r.read_int(), yen=r.read_int(),
                        exp=r.read_long())
            mail["items"] = [
                {"template_id": r.read_short(), "quantity": r.read_int(),
                 "is_lock": r.read_boolean()}
                for _ in range(r.read_ubyte())
            ]
        return mail

    @staticmethod
    def _format_mail_reward(detail):
        """Tạo log đầy đủ phần thưởng trong nội dung thư.

        Thông báo -26 sau khi nhận thường chỉ nói một loại tiền (ví dụ
        ``50.000.000 yên``). Payload đọc thư mới là nơi chứa toàn bộ yên,
        xu, lượng, EXP và danh sách item đính kèm.
        """
        rewards = []
        for key, label in (("luong", "lượng"), ("xu", "xu"),
                           ("yen", "yên"), ("exp", "EXP")):
            value = detail.get(key, 0)
            if value:
                rewards.append(f"{label}={value:,}".replace(",", "."))
        for item in detail.get("items", []):
            locked = ", khóa" if item.get("is_lock") else ""
            rewards.append(
                f"item_id={item['template_id']} x{item['quantity']}{locked}"
            )
        return ", ".join(rewards) if rewards else "không có phần thưởng chi tiết"

    def _record_mail_reward(self, detail):
        """Lưu và in chi tiết quà, không làm thay đổi trạng thái nhận thư."""
        record = {
            "mail_id": detail["mail_id"],
            "title": detail.get("title", ""),
            "luong": detail.get("luong", 0),
            "xu": detail.get("xu", 0),
            "yen": detail.get("yen", 0),
            "exp": detail.get("exp", 0),
            "content": detail.get("content", ""),
            "items": [dict(item) for item in detail.get("items", [])],
        }
        self.last_mail_rewards.append(record)
        print(
            f"  🎁 Chi tiết quà thư id={record['mail_id']} "
            f"({record['title']}): {self._format_mail_reward(detail)}"
        )
        return record

    def _try_record_read_mail_reward(self, mail_id):
        """Đọc lại thư đã mở để lấy chi tiết quà, không làm fail mail."""
        try:
            self.read_mail(mail_id)
            data = self._wait_mail_packet(self.MAIL_READ, mail_id)
            if data is None:
                raise RuntimeError("không nhận được payload chi tiết")
            detail = self._parse_mail_read(data)
            if detail.get("has_attachments"):
                return self._record_mail_reward(detail)
        except (RuntimeError, ValueError, EOFError, OSError) as exc:
            print(f"  ⚠️ Không đọc được chi tiết quà thư {mail_id}: {exc}")
        return None

    def _parse_mail_action_result(self, data):
        """Parse response theo từng handler native của mobile."""
        if len(data) < 5:
            return None
        r = NSOReader(data)
        action = r.read_ubyte()
        if action not in (self.MAIL_READ, self.MAIL_DELETE,
                          self.MAIL_CLAIM_ATTACHMENTS):
            return None
        mail_id = r.read_int()
        if action == self.MAIL_READ:
            self._parse_mail_read(data)
            return action, mail_id, True
        # HandleMailClaimResponse/HandleMailDeleteResponse: mailId + success.
        # Lưu ý: Server trả gói xóa thư với 5 byte (action 1 byte + mail_id 4 byte)
        # hoặc 6 byte kèm cờ boolean.
        if r.remaining() < 1:
            if action == self.MAIL_DELETE:
                return action, mail_id, True
            return None
        return action, mail_id, r.read_boolean()

    def receive_all_mail(self, delete_after_claim=False,
                         include_read_mail_details=False):
        """Nhận thư an toàn và lưu lại thống kê chi tiết của lượt chạy.

        Thư còn quà nhưng chưa nhận sẽ chỉ bị xóa sau khi nhận thành công.
        Nếu hành trang đầy hoặc nhận quà thất bại, thư được giữ nguyên. Thư
        đã nhận từ trước thì không còn quà để mất và sẽ được xóa khi bật
        ``delete_after_claim``. ``include_read_mail_details`` dùng cho luồng
        giftcode để đọc lại thư đã mở và log đầy đủ item đính kèm.

        Giá trị trả về vẫn giữ ba khóa cũ để không làm hỏng code gọi hiện tại;
        thống kê chi tiết nằm trong ``last_mail_stats`` và
        ``last_mail_errors``.
        """
        self._bag_full = False
        self.last_mail_errors = []
        self.last_mail_stats = {
            "claimed": 0,
            "deleted": 0,
            "failed": 0,
            "kept": 0,
            "bag_full": 0,
            "bag_full_mail_ids": [],
            "rewards": [],
            "mail_results": [],
        }
        self.last_mail_rewards = []
        mails = self.request_mail_list()
        result = {"claimed": 0, "deleted": 0, "failed": 0}
        deleted_ids = set()
        for mail in mails:
            mail_id = mail["mail_id"]
            title = mail.get("title", f"id={mail_id}")
            mail_result = {
                "mail_id": mail_id,
                "sender": mail.get("sender", ""),
                "title": title,
                "is_read": mail.get("is_read", False),
                "is_received": mail.get("is_received", False),
                "has_attachments": mail.get("has_attachments", False),
                "claimed": False,
                "deleted": False,
                "kept": False,
                "failed": False,
                "bag_full": False,
                "content": "",
                "items": [],
            }
            self.last_mail_stats["mail_results"].append(mail_result)
            try:
                has_attachments = mail.get("has_attachments", False)
                read_detail_loaded = False
                # Thư mới chưa đọc sẽ trả payload đầy đủ gồm toàn bộ phần
                # thưởng. Với thư đã đọc, chỉ đọc lại khi caller bật
                # include_read_mail_details; cờ is_received vẫn lấy từ mail
                # list để quyết định claim/xóa.
                if not mail["is_read"]:
                    self.read_mail(mail_id)
                    data = self._wait_mail_packet(self.MAIL_READ, mail_id)
                    if data is None:
                        raise RuntimeError("Không nhận được phản hồi đọc thư")
                    detail = self._parse_mail_read(data)
                    mail_result["content"] = detail.get("content", "")
                    mail_result["items"] = [
                        dict(item) for item in detail.get("items", [])
                    ]
                    if has_attachments or detail.get("has_attachments"):
                        self._record_mail_reward(detail)
                        read_detail_loaded = True
                elif include_read_mail_details and has_attachments:
                    detail_record = self._try_record_read_mail_reward(mail_id)
                    if detail_record is not None:
                        mail_result["content"] = detail_record.get("content", "")
                        mail_result["items"] = [
                            dict(item) for item in detail_record.get("items", [])
                        ]
                        read_detail_loaded = True

                claimed_success = False

                if has_attachments:
                    if not mail.get("is_received", False):
                        # Sau khi một lần nhận báo đầy túi, giữ lại tất cả
                        # thư còn quà phía sau để người dùng xử lý sau.
                        if self._bag_full:
                            print(f"  ⚠️ Hành trang đầy, bỏ qua thư {mail_id} ({title}) -> GIỮ LẠI THƯ")
                            self.last_mail_stats["bag_full"] += 1
                            self.last_mail_stats["bag_full_mail_ids"].append(mail_id)
                            self.last_mail_stats["kept"] += 1
                            mail_result["bag_full"] = True
                            mail_result["kept"] = True
                            continue

                        self.claim_mail_attachment(mail_id)
                        data = self._wait_mail_packet(self.MAIL_CLAIM_ATTACHMENTS,
                                                      mail_id, timeout=15)
                        parsed = self._parse_mail_action_result(data) if data else None
                        if (parsed and parsed[0] == self.MAIL_CLAIM_ATTACHMENTS
                                and parsed[1] == mail_id and parsed[2] is True):
                            claimed_success = True
                            mail_result["claimed"] = True
                            result["claimed"] += 1
                            print(f"  ✅ Đã nhận quà thư {mail_id} ({title})")
                        else:
                            if self._bag_full:
                                print(f"  ⚠️ Hành trang đầy! Chưa nhận được quà thư {mail_id} ({title}) -> GIỮ LẠI THƯ, KHÔNG XÓA")
                                self.last_mail_stats["bag_full"] += 1
                                self.last_mail_stats["bag_full_mail_ids"].append(mail_id)
                                mail_result["bag_full"] = True
                            else:
                                print(f"  ⚠️ Nhận quà thư {mail_id} ({title}) không thành công -> GIỮ LẠI THƯ, KHÔNG XÓA")
                                result["failed"] += 1
                                self.last_mail_errors.append(
                                    f"Thư {mail_id} ({title}) nhận quà không thành công"
                                )
                            self.last_mail_stats["kept"] += 1
                            mail_result["kept"] = True
                            mail_result["failed"] = not self._bag_full
                            continue
                    else:
                        # Server đánh dấu đã nhận từ trước: không còn quà để
                        # mất, nên xử lý xóa ở bước bên dưới.
                        action = "XÓA THƯ" if delete_after_claim else "GIỮ LẠI THƯ"
                        print(f"  ℹ️ Thư {mail_id} ({title}) đã nhận từ trước -> {action}")

                if (include_read_mail_details and has_attachments
                        and mail["is_read"] and not read_detail_loaded
                        and (claimed_success or mail.get("is_received", False))):
                    detail_record = self._try_record_read_mail_reward(mail_id)
                    if detail_record is not None:
                        mail_result["content"] = detail_record.get("content", "")
                        mail_result["items"] = [
                            dict(item) for item in detail_record.get("items", [])
                        ]

                # Chỉ xóa thư không còn quà hoặc vừa nhận quà thành công.
                if delete_after_claim:
                    can_delete = (
                        not has_attachments
                        or mail.get("is_received", False)
                        or claimed_success
                    )
                    if can_delete:
                        self.delete_mail(mail_id)
                        data = self._wait_mail_packet(self.MAIL_DELETE, mail_id)
                        expected = (self.MAIL_DELETE, mail_id, True)
                        if data is None or self._parse_mail_action_result(data) != expected:
                            raise RuntimeError("Xóa thư chưa thành công")
                        deleted_ids.add(mail_id)
                        mail_result["deleted"] = True
                    else:
                        self.last_mail_stats["kept"] += 1
                        mail_result["kept"] = True
                else:
                    self.last_mail_stats["kept"] += 1
                    mail_result["kept"] = True
            except (RuntimeError, ValueError, EOFError, OSError) as exc:
                result["failed"] += 1
                self.last_mail_stats["kept"] += 1
                self.last_mail_errors.append(f"Thư {mail_id} ({title}): {exc}")
                mail_result["failed"] = True
                mail_result["kept"] = True
                print(f"  ❌ Thư {mail_id}: {exc}")

        # Xác minh thư đã xóa không còn xuất hiện trong danh sách server.
        if deleted_ids:
            remaining_ids = {mail["mail_id"] for mail in self.request_mail_list()}
            result["deleted"] = len(deleted_ids - remaining_ids)
            result["failed"] += len(deleted_ids & remaining_ids)
            self.last_mail_stats["kept"] += len(deleted_ids & remaining_ids)
            for mail_id in sorted(deleted_ids & remaining_ids):
                for mail_result in self.last_mail_stats["mail_results"]:
                    if mail_result["mail_id"] == mail_id:
                        mail_result["deleted"] = False
                        mail_result["failed"] = True
                        mail_result["kept"] = True
                        break
                self.last_mail_errors.append(f"Thư {mail_id} vẫn còn sau yêu cầu xóa")
                print(f"  ❌ Thư {mail_id} vẫn còn sau yêu cầu xóa")
        self.last_mail_stats.update(result)
        self.last_mail_stats["rewards"] = list(self.last_mail_rewards)
        print(f"  Kết quả: nhận={result['claimed']}, xóa={result['deleted']}, "
              f"giữ lại={self.last_mail_stats['kept']}, "
              f"chật rương={self.last_mail_stats['bag_full']}, "
              f"lỗi={result['failed']}")
        return result

    def listen_all(self, duration=10):
        """Lắng nghe tất cả packet trong duration giây"""
        print(f"\n👂 Lắng nghe packets {duration}s...")
        pkts = self.recv_all(timeout=duration, max_pkts=200)
        print(f"Nhận được {len(pkts)} packet(s):")
        for cmd, data in pkts:
            _log_packet("  ← ", cmd, data)
        return pkts


# ─── Helper: log packet ─────────────────────

def _log_packet(prefix, cmd, data):
    """In thông tin packet ra màn hình"""
    if cmd == -26:
        try:
            print(f"{prefix}Server: {NSOReader(data).read_utf()}")
            return
        except EOFError:
            pass
    print(f"{prefix}cmd={cmd:4d}  len={len(data):5d}  hex={data[:32].hex()}", end="")
    # Thử parse nếu data có thể là UTF string
    if len(data) >= 3:
        try:
            r = NSOReader(data)
            sub = r.read_byte()
            print(f"  sub={sub}", end="")
            if r.remaining() >= 2:
                try:
                    s = r.read_utf()
                    if s and all(32 <= ord(c) < 127 or ord(c) > 127 for c in s):
                        print(f"  str={repr(s[:60])}", end="")
                except: pass
        except: pass
    print()


# ─── Main ────────────────────────────────────

def read_accounts(csv_path):
    """Cùng định dạng hoatdong.py: username,password, chấp nhận BOM/header."""
    accounts = []
    with csv_path.open("r", encoding="utf-8-sig", newline="") as handle:
        for line, row in enumerate(csv.reader(handle), start=1):
            if not row or not any(cell.strip() for cell in row):
                continue
            if len(row) < 2:
                raise ValueError(f"Dòng {line} thiếu username/password")
            username, password = row[0].strip(), row[1].strip()
            if username.lower() == "username" and password.lower() == "password":
                continue
            if not username or not password:
                raise ValueError(f"Dòng {line} thiếu username/password")
            accounts.append((username, password))
    return accounts


def build_parser():
    parser = argparse.ArgumentParser(description="Tự động nhận quà và xóa thư NSO từ CSV")
    parser.add_argument("csv_file", nargs="?", type=Path, default=DEFAULT_CSV)
    parser.add_argument("--host", default=HOST)
    parser.add_argument("--port", type=int, default=PORT)
    parser.add_argument("--character-index", type=int,
                        help="Chỉ xử lý nhân vật ở index này; mặc định: tất cả")
    parser.add_argument("--max-characters", type=int, default=0,
                        help="Giới hạn số nhân vật mỗi tài khoản; 0: tất cả")
    parser.add_argument("--character-delay", type=float, default=DEFAULT_DELAY)
    parser.add_argument("--account-delay", type=float, default=DEFAULT_DELAY)
    return parser


def process_account(args, username, password):
    totals = {"characters": 0, "claimed": 0, "deleted": 0, "failed": 0}
    client = NSOMailClient(args.host, args.port)
    try:
        if not client.connect() or not client.login(username, password):
            raise RuntimeError("Không kết nối/đăng nhập được")
        names = list(client.characters)
        indexes = list(range(len(names)))
        if args.character_index is not None:
            indexes = [args.character_index] if args.character_index < len(names) else []
        if args.max_characters:
            indexes = indexes[:args.max_characters]
        if not indexes:
            raise RuntimeError("Không có nhân vật phù hợp")
        for position, index in enumerate(indexes):
            try:
                if position:
                    time.sleep(args.character_delay)
                    client = NSOMailClient(args.host, args.port)
                    if not client.connect() or not client.login(username, password):
                        raise RuntimeError("Không đăng nhập lại được")
                # Tìm theo tên để không xử lý nhầm nếu danh sách đổi thứ tự.
                name = names[index]
                print(f"  Nhân vật [{index}] {name}")
                if name not in client.characters:
                    raise RuntimeError("Nhân vật không còn trong danh sách")
                if not client.select_character(client.characters.index(name)):
                    raise RuntimeError("Không vào được nhân vật")
                totals["characters"] += 1
                client.recv_all(timeout=3)
                result = client.receive_all_mail(delete_after_claim=True)
                for key, count in result.items():
                    totals[key] += count
            except Exception as exc:
                print(f"  ❌ Nhân vật [{index}] {names[index]}: {exc}")
                totals["failed"] += 1
            finally:
                client.disconnect()
    except Exception as exc:
        print(f"  ❌ Tài khoản {username}: {exc}")
        totals["failed"] += 1
    finally:
        if client.sock is not None:
            client.disconnect()
    return totals


def main(argv=None):
    args = build_parser().parse_args(argv)
    if (args.character_index is not None and args.character_index < 0
            or args.max_characters < 0
            or args.character_delay < 0 or args.account_delay < 0):
        print("❌ Index, giới hạn nhân vật và thời gian chờ không được âm")
        return 2
    try:
        accounts = read_accounts(args.csv_file)
        if not accounts:
            raise ValueError("CSV không có tài khoản")
    except (OSError, ValueError, csv.Error) as exc:
        print(f"❌ Không đọc được CSV: {exc}")
        return 2
    print(f"NSO MAIL: {len(accounts)} tài khoản — tự động nhận quà và xóa thư")
    totals = {"characters": 0, "claimed": 0, "deleted": 0, "failed": 0}
    retry_accounts = []
    try:
        for number, (username, password) in enumerate(accounts, start=1):
            if number > 1:
                time.sleep(args.account_delay)
            print(f"\n[{number}/{len(accounts)}] Tài khoản {username}")
            result = process_account(args, username, password)
            for key, count in result.items():
                totals[key] += count
            if result["failed"]:
                retry_accounts.append(username)
    except KeyboardInterrupt:
        print("\nĐã dừng theo yêu cầu")
        return 130
    print(f"\nTỔNG KẾT: nhân vật={totals['characters']}, nhận={totals['claimed']}, "
          f"xóa={totals['deleted']}, lỗi={totals['failed']}")
    if retry_accounts:
        print("TÀI KHOẢN CẦN CHẠY LẠI: " + ", ".join(retry_accounts))
    return 1 if totals["failed"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
