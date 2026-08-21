#!/usr/bin/env python3
"""
NSO Mail Client - Test nhận thư và quà hàng ngày
Dựa trên nso_client.py, thêm chức năng:
  - Probe tìm mail command bytes
  - Nhận thư (MailList, MailRead, MailClaimAttachment, MailDelete)
  - Nhận quà hàng ngày (rewardPB cmd -82, rewardCT cmd -79)
  - Log toàn bộ raw packets để debug

Cách dùng:
  python3 mail_client.py
"""

import socket
import struct
import time
import random
import sys
from io import BytesIO


# ─────────────────────────────────────────────
#  Config - SỬA ĐÂY
# ─────────────────────────────────────────────
HOST     = "Nsm1.ninjasm.net"
PORT     = 14444
USERNAME = "luongclone001"
PASSWORD = "ngan2021"
CHAR_IDX = 0          # index nhân vật muốn vào (0, 1, 2)
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
        self.write_short(len(b))
        self.buffer.write(b)

    def get_data(self):
        return self.buffer.getvalue()

    def to_raw_packet(self):
        """[command:1][length:2][data:n]"""
        data = self.get_data()
        pkt = BytesIO()
        pkt.write(struct.pack('b', self.command))
        pkt.write(struct.pack('>h', len(data)))
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
        length = self.read_short()
        if length <= 0: return ""
        return self.buf.read(length).decode('utf-8', errors='replace')

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

    # NOT_LOGIN sub-commands
    CMD_SET_CLIENT  = -125
    CMD_LOGIN       = -127
    CMD_SELECT_CHAR = -126
    CMD_REGISTER    = -122

    # NOT_MAP sub-commands (gửi lên server)
    CMD_REWARD_PB   = -82   # nhận quà hàng ngày (Phần Bổ)
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
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.settimeout(15)
            self.sock.connect((self.host, self.port))
            self.connected = True
            print("✅ Đã kết nối")
            self._key_exchange()
            return True
        except Exception as e:
            print(f"❌ Lỗi kết nối: {e}")
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
            print(f"⚠️  Unexpected cmd {cmd}")
            return

        len_b = self._recv_exact(2)
        length = struct.unpack('>H', len_b)[0]
        payload = self._recv_exact(length)

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
        pkt = msg.to_raw_packet()
        if self.key:
            pkt = bytes(self._enc(b) for b in pkt)
        self.sock.sendall(pkt)

    def recv_packet(self, timeout=10):
        """Nhận 1 packet, trả về (cmd, data_bytes)"""
        self.sock.settimeout(timeout)
        try:
            cmd_raw = self._recv_exact(1)[0]
            cmd = self._dec(cmd_raw)
            if cmd >= 128: cmd -= 256

            if cmd == -32:
                # Large packet: [real_cmd:1][len:4]
                real_cmd_raw = self._recv_exact(1)[0]
                cmd = self._dec(real_cmd_raw)
                if cmd >= 128: cmd -= 256
                len_bytes = [self._dec(b) for b in self._recv_exact(4)]
                length = int.from_bytes(bytes(len_bytes), 'big')
            else:
                lb = self._recv_exact(2)
                b1, b2 = self._dec(lb[0]), self._dec(lb[1])
                length = ((b1 & 0xFF) << 8) | (b2 & 0xFF)

            data = bytearray(self._recv_exact(length)) if length > 0 else bytearray()
            if self.key:
                data = bytearray(self._dec(b) for b in data)

            return cmd, bytes(data)

        except socket.timeout:
            return None, None
        except Exception as e:
            print(f"❌ recv error: {e}")
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
        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_SET_CLIENT)
        msg.write_byte(1)           # CLIENT_TYPE
        msg.write_byte(1)           # zoomLevel
        msg.write_boolean(True)     # isGPRS
        msg.write_int(480)          # width
        msg.write_int(800)          # height
        msg.write_boolean(True)     # isQwerty
        msg.write_boolean(True)     # isTouch
        msg.write_utf("Nokia6300/2.0 (06.01) Profile/MIDP-2.0 Configuration/CLDC-1.1")
        msg.write_byte(0)
        msg.write_int(0)
        msg.write_byte(0)           # languageID
        msg.write_int(0)
        msg.write_utf("0")
        self.send_raw(msg)

    def _send_not_map(self, sub_cmd):
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(sub_cmd)
        self.send_raw(msg)

    def login(self, username, password, version="2.1.7"):
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
        msg.write_utf("VALID_CLIENT_KEY")
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
                        gender = r.read_byte()
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
        if idx >= len(self.characters):
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
            if cmd is None: continue
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
        """Gửi yêu cầu nhận quà hàng ngày (rewardPB, cmd NOT_MAP -82)"""
        print("\n🎁 Gửi request nhận quà hàng ngày (rewardPB -82)...")
        self._send_not_map(self.CMD_REWARD_PB)

    def request_reward_ct(self):
        """Gửi yêu cầu nhận quà chiến trường (rewardCT, cmd NOT_MAP -79)"""
        print("\n🏆 Gửi request nhận quà chiến trường (rewardCT -79)...")
        self._send_not_map(self.CMD_REWARD_CT)

    # ─── Mail probe ──────────────────────────

    # Command bytes cần thử cho mail (NOT_MAP sub-commands chưa biết)
    # Dựa trên Unity metadata: MailList, MailRead, MailClaim, MailDelete
    # Các slot trống trong gameAE: -110, -107, -106, -105, -104, -103,
    #   -102, -101 (clientOk), -100, -94, -92, -91, -89, -87, -85,
    #   -82 (reward), -79 (reward), -78, -76, -75, -74, ...
    MAIL_PROBE_CMDS = [
        -110, -109, -108, -107, -106, -105, -104, -103,
        -100, -96, -94, -93, -92, -91, -90, -89,
        -88, -87, -86, -85, -84, -83, -81, -80,
        -78, -77, -76, -75, -74, -73, -71, -70,
        -69, -68, -67, -66, -65, -64, -63,
    ]

    def probe_mail_command(self, sub_cmd):
        """Thử gửi 1 sub-command và log response"""
        print(f"\n🔍 Probe NOT_MAP sub-cmd: {sub_cmd}")
        self._send_not_map(sub_cmd)
        pkts = self.recv_all(timeout=2)
        for cmd, data in pkts:
            _log_packet("  ← ", cmd, data)
        return pkts

    def probe_all_mail_commands(self):
        """Probe tất cả cmd có thể là mail để tìm đúng byte"""
        print("\n" + "=" * 60)
        print("🔍 PROBE TÌM MAIL COMMAND BYTES")
        print("=" * 60)
        results = {}
        for sc in self.MAIL_PROBE_CMDS:
            pkts = self.probe_mail_command(sc)
            if pkts:
                results[sc] = pkts
            time.sleep(0.3)
        print("\n📊 Tóm tắt: các cmd có response:")
        for sc, pkts in results.items():
            cmds = [p[0] for p in pkts]
            print(f"  sub-cmd {sc:4d} → response cmds: {cmds}")
        return results

    # ─── Mail actions (sau khi biết cmd) ─────

    def request_mail_list(self, mail_cmd):
        """Gửi yêu cầu danh sách thư với mail_cmd đã biết"""
        print(f"\n📬 Yêu cầu danh sách thư (cmd {mail_cmd})...")
        self._send_not_map(mail_cmd)
        pkts = self.recv_all(timeout=5)
        print(f"  Nhận {len(pkts)} packet(s):")
        for cmd, data in pkts:
            _log_packet("  ← ", cmd, data)
        return pkts

    def claim_mail_attachment(self, mail_cmd, mail_id=None):
        """
        Gửi yêu cầu nhận quà đính kèm thư.
        mail_id: int ID của thư, hoặc None để gửi không có param
        """
        print(f"\n📦 Claim mail attachment (cmd {mail_cmd}, id={mail_id})...")
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(mail_cmd)
        if mail_id is not None:
            msg.write_int(mail_id)
        self.send_raw(msg)
        pkts = self.recv_all(timeout=5)
        for cmd, data in pkts:
            _log_packet("  ← ", cmd, data)
        return pkts

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

def main():
    print("=" * 60)
    print("  NSO MAIL CLIENT - Test nhận thư & quà")
    print("=" * 60)

    client = NSOMailClient(HOST, PORT)

    try:
        # 1. Kết nối
        if not client.connect():
            return

        # 2. Login
        if not client.login(USERNAME, PASSWORD):
            return

        # 3. Chọn nhân vật
        if not client.select_character(CHAR_IDX):
            # Nếu không vào được game, vẫn thử probe ở màn chọn nhân vật
            print("⚠️  Không vào được game, thử probe từ màn chọn nhân vật")

        # Drain packet còn lại sau khi vào map
        print("\n⏳ Drain packets ban đầu...")
        pkts = client.recv_all(timeout=3)
        print(f"  Drain {len(pkts)} packets")
        for cmd, data in pkts:
            _log_packet("  [init] ", cmd, data)

        print("\n" + "=" * 60)
        print("  MENU TEST")
        print("=" * 60)
        print("1. Thử nhận quà hàng ngày (rewardPB -82)")
        print("2. Thử nhận quà chiến trường (rewardCT -79)")
        print("3. Probe tất cả mail command bytes (tự động)")
        print("4. Probe 1 command cụ thể")
        print("5. Lắng nghe packets 15 giây")
        print("6. Thoát")
        print()

        while True:
            try:
                choice = input("Chọn (1-6): ").strip()
            except (EOFError, KeyboardInterrupt):
                break

            if choice == "1":
                client.request_reward_pb()
                pkts = client.recv_all(timeout=5)
                print(f"  Nhận {len(pkts)} packet(s):")
                for cmd, data in pkts:
                    _log_packet("  ← ", cmd, data)

            elif choice == "2":
                client.request_reward_ct()
                pkts = client.recv_all(timeout=5)
                print(f"  Nhận {len(pkts)} packet(s):")
                for cmd, data in pkts:
                    _log_packet("  ← ", cmd, data)

            elif choice == "3":
                results = client.probe_all_mail_commands()
                # Gợi ý cmd nào có vẻ là mail
                print("\n💡 Gợi ý: xem hex data để xác định mail command")

            elif choice == "4":
                try:
                    sc = int(input("Nhập sub-command (vd: -110): ").strip())
                    client.probe_mail_command(sc)
                except ValueError:
                    print("❌ Số không hợp lệ")

            elif choice == "5":
                client.listen_all(duration=15)

            elif choice == "6":
                break
            else:
                print("❓ Chọn 1-6")

    except KeyboardInterrupt:
        print("\n⚠️  Bị ngắt")
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        import traceback; traceback.print_exc()
    finally:
        client.disconnect()


if __name__ == "__main__":
    main()
