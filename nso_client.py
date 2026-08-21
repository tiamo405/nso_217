#!/usr/bin/env python3
"""
NSO Game Client - Python Implementation
Kết nối đến NSO game server và thực hiện các thao tác như login, chọn nhân vật, v.v.
"""

import socket
import struct
import time
import random
from io import BytesIO


class NSOMessage:
    """Class để tạo và đọc binary messages theo protocol NSO"""

    def __init__(self, command_byte):
        self.command = command_byte
        self.buffer = BytesIO()

    def write_byte(self, value):
        """Ghi 1 byte"""
        self.buffer.write(struct.pack('b', value))

    def write_short(self, value):
        """Ghi 2 bytes (short)"""
        self.buffer.write(struct.pack('>h', value))

    def write_int(self, value):
        """Ghi 4 bytes (int)"""
        self.buffer.write(struct.pack('>i', value))

    def write_long(self, value):
        """Ghi 8 bytes (long)"""
        self.buffer.write(struct.pack('>q', value))

    def write_utf(self, text):
        """Ghi UTF string (2 bytes length + string data)"""
        utf_bytes = text.encode('utf-8')
        self.write_short(len(utf_bytes))
        self.buffer.write(utf_bytes)

    def write_boolean(self, value):
        """Ghi boolean (1 byte: 0 hoặc 1)"""
        self.write_byte(1 if value else 0)

    def get_data(self):
        """Lấy data đã write"""
        return self.buffer.getvalue()

    def to_packet(self, encrypt_func=None):
        """
        Chuyển thành packet hoàn chỉnh: [command][length][data]
        encrypt_func: function để encrypt nếu có
        """
        data = self.get_data()

        # Nếu có encryption function, encrypt data
        if encrypt_func:
            data = encrypt_func(data)

        # Tạo packet: command (1 byte) + length (2 bytes) + data
        packet = BytesIO()
        packet.write(struct.pack('b', self.command))
        packet.write(struct.pack('>h', len(data)))
        packet.write(data)

        return packet.getvalue()


class NSOMessageReader:
    """Class để đọc binary messages từ server"""

    def __init__(self, data):
        self.buffer = BytesIO(data)

    def read_byte(self):
        """Đọc 1 byte"""
        data = self.buffer.read(1)
        if not data:
            raise EOFError("No more data")
        return struct.unpack('b', data)[0]

    def read_unsigned_byte(self):
        """Đọc 1 byte không dấu"""
        data = self.buffer.read(1)
        if not data:
            raise EOFError("No more data")
        return struct.unpack('B', data)[0]

    def read_short(self):
        """Đọc 2 bytes (short)"""
        data = self.buffer.read(2)
        if len(data) < 2:
            raise EOFError("Not enough data")
        return struct.unpack('>h', data)[0]

    def read_int(self):
        """Đọc 4 bytes (int)"""
        data = self.buffer.read(4)
        if len(data) < 4:
            raise EOFError("Not enough data")
        return struct.unpack('>i', data)[0]

    def read_long(self):
        """Đọc 8 bytes (long)"""
        data = self.buffer.read(8)
        if len(data) < 8:
            raise EOFError("Not enough data")
        return struct.unpack('>q', data)[0]

    def read_utf(self):
        """Đọc UTF string (2 bytes length + string data)"""
        length = self.read_short()
        if length < 0:
            return ""
        data = self.buffer.read(length)
        if len(data) < length:
            raise EOFError("Not enough data for string")
        return data.decode('utf-8')

    def read_boolean(self):
        """Đọc boolean"""
        return self.read_byte() != 0

    def available(self):
        """Số bytes còn lại"""
        current = self.buffer.tell()
        self.buffer.seek(0, 2)  # Seek to end
        end = self.buffer.tell()
        self.buffer.seek(current)  # Seek back
        return end - current


class NSOClient:
    """NSO Game Client - kết nối và tương tác với game server"""

    # Command bytes
    CMD_NOT_LOGIN = -29
    CMD_NOT_MAP = -28
    CMD_SUB_COMMAND = -30
    CMD_KEY_EXCHANGE = -27

    # Sub-commands
    CMD_SET_CLIENT_TYPE = -125
    CMD_LOGIN = -127
    CMD_SELECT_CHAR = -126
    CMD_REGISTER = -122

    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.socket = None
        self.connected = False

        # Encryption key (sẽ nhận từ server)
        self.key = None
        self.key_pos_write = 0
        self.key_pos_read = 0
        self.characters = []
        self._pending_updates = set()

    def _recv_exact(self, size):
        """Read exactly size bytes or raise when the peer closes the socket."""
        data = bytearray()
        while len(data) < size:
            chunk = self.socket.recv(size - len(data))
            if not chunk:
                raise ConnectionError(
                    f"Socket closed while reading {size} bytes "
                    f"(received {len(data)})"
                )
            data.extend(chunk)
        return bytes(data)

    def connect(self):
        """Kết nối đến game server"""
        print(f"🔌 Đang kết nối đến {self.host}:{self.port}...")

        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.settimeout(10)
            self.socket.connect((self.host, self.port))

            print("✅ Đã kết nối!")
            self.connected = True

            # Gửi key exchange request
            self._request_encryption_key()

            return True

        except Exception as e:
            print(f"❌ Lỗi kết nối: {e}")
            self.connected = False
            return False

    def disconnect(self):
        """Ngắt kết nối"""
        if self.socket:
            self.socket.close()
            self.socket = None
        self.connected = False
        print("🔌 Đã ngắt kết nối")

    def _request_encryption_key(self):
        """Gửi request để lấy encryption key từ server"""
        print("🔑 Đang request encryption key...")

        try:
            # Gửi command -27 (KEY_EXCHANGE) với empty data
            packet = struct.pack('b', self.CMD_KEY_EXCHANGE) + struct.pack('>h', 0)
            self.socket.sendall(packet)

            # ĐỌC response từ server (quan trọng!)
            print("📥 Đang đợi key từ server...")

            # Đọc command byte
            cmd_data = self._recv_exact(1)
            if cmd_data:
                command = struct.unpack('b', cmd_data)[0]

                if command != self.CMD_KEY_EXCHANGE:
                    raise ValueError(f"Unexpected key-exchange command: {command}")

                # Đọc length
                len_data = self._recv_exact(2)
                if len(len_data) == 2:
                    length = struct.unpack('>H', len_data)[0]

                    # Đọc key data
                    key_data = self._recv_exact(length)

                    if key_data:
                        # Java protocol: payload = [key length][encoded key].
                        key_length = key_data[0]
                        if len(key_data) != key_length + 1:
                            raise ValueError(
                                f"Invalid key payload: declared={key_length}, "
                                f"actual={len(key_data) - 1}"
                            )

                        self.key = list(key_data[1:])

                        # MessageCollector decodes the key cumulatively before use.
                        for index in range(len(self.key) - 1):
                            self.key[index + 1] ^= self.key[index]

                        self.key_pos_write = 0
                        self.key_pos_read = 0
                        print(f"✅ Đã nhận encryption key ({len(self.key)} bytes)")
                    else:
                        print("⚠️  Key data rỗng - không sử dụng encryption")
                        self.key = None
                else:
                    print("⚠️  Không đọc được length - không sử dụng encryption")
                    self.key = None
            else:
                print("⚠️  Không nhận được response - không sử dụng encryption")
                self.key = None

        except Exception as e:
            print(f"⚠️  Lỗi key exchange: {e} - không sử dụng encryption")
            self.key = None

    def _encrypt_byte(self, byte_val):
        """Encrypt 1 byte với XOR key"""
        if not self.key:
            return byte_val

        encrypted = (self.key[self.key_pos_write] ^ byte_val) & 0xFF
        self.key_pos_write = (self.key_pos_write + 1) % len(self.key)
        return encrypted

    def _decrypt_byte(self, byte_val):
        """Decrypt 1 byte với XOR key"""
        if not self.key:
            return byte_val

        decrypted = (self.key[self.key_pos_read] ^ byte_val) & 0xFF
        self.key_pos_read = (self.key_pos_read + 1) % len(self.key)
        return decrypted

    def send_message(self, message):
        """Gửi message đến server"""
        if not self.connected:
            print("❌ Chưa kết nối đến server!")
            return False

        try:
            packet = message.to_packet()

            # Nếu có encryption key, encrypt toàn bộ packet
            if self.key:
                encrypted_packet = bytearray()
                for byte in packet:
                    encrypted_byte = self._encrypt_byte(byte)
                    encrypted_packet.append(encrypted_byte)
                packet = bytes(encrypted_packet)

            self.socket.sendall(packet)
            return True
        except Exception as e:
            print(f"❌ Lỗi gửi message: {e}")
            return False

    def receive_message(self):
        """Nhận message từ server"""
        if not self.connected:
            return None

        try:
            # Đọc command byte
            cmd_data = self._recv_exact(1)
            if not cmd_data:
                print("⚠️ Không nhận được data từ socket")
                return None

            print(f"🔍 Nhận raw cmd byte: {cmd_data.hex()}")

            # Decrypt nếu có key
            command_byte = cmd_data[0]
            if self.key:
                command = self._decrypt_byte(command_byte)
                if command >= 128:
                    command -= 256
                print(f"🔍 Command sau decrypt: {command}")
            else:
                command = struct.unpack('b', cmd_data)[0]
                print(f"🔍 Command (không decrypt): {command}")

            # Command -32 is the Java protocol's large-packet wrapper:
            # [encrypted -32][encrypted real command][encrypted uint32 length].
            if command == -32:
                real_command = self._decrypt_byte(self._recv_exact(1)[0])
                command = real_command - 256 if real_command >= 128 else real_command
                length_bytes = [
                    self._decrypt_byte(value)
                    for value in self._recv_exact(4)
                ]
                length = int.from_bytes(bytes(length_bytes), "big")
            else:
                len_data = self._recv_exact(2)
                if self.key:
                    len_byte1 = self._decrypt_byte(len_data[0])
                    len_byte2 = self._decrypt_byte(len_data[1])
                    length = ((len_byte1 & 0xFF) << 8) | (len_byte2 & 0xFF)
                else:
                    length = struct.unpack('>H', len_data)[0]

            # Đọc data
            data = self._recv_exact(length)

            # Decrypt data nếu có key
            if self.key:
                decrypted_data = bytearray()
                for byte in data:
                    decrypted_byte = self._decrypt_byte(byte)
                    decrypted_data.append(decrypted_byte)
                data = bytes(decrypted_data)

            return {
                'command': command,
                'length': length,
                'data': data
            }

        except socket.timeout:
            return None
        except Exception as e:
            print(f"❌ Lỗi nhận message: {e}")
            return None

    def set_client_type(self):
        """Gửi thông tin client type (cần gọi trước login)"""
        print("📱 Đang gửi client info...")

        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_SET_CLIENT_TYPE)

        # Client info
        msg.write_byte(1)           # CLIENT_TYPE
        msg.write_byte(1)           # zoomLevel
        msg.write_boolean(True)     # isGPRS
        msg.write_int(480)          # screen width
        msg.write_int(800)          # screen height
        msg.write_boolean(True)     # isQwerty
        msg.write_boolean(True)     # isTouch
        msg.write_utf("Nokia6300/2.0 (06.01) Profile/MIDP-2.0 Configuration/CLDC-1.1")
        msg.write_byte(0)
        msg.write_int(0)
        msg.write_byte(0)           # languageID (0 = Vietnamese)
        msg.write_int(0)            # userProvider
        msg.write_utf("0")          # clientAgent from agent.txt

        # Gửi và KHÔNG đợi response (theo Java code)
        return self.send_message(msg)

    def _send_not_map(self, sub_command):
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(sub_command)
        return self.send_message(msg)

    def login(self, username, password, version="2.1.7"):
        """
        Đăng nhập vào game

        Args:
            username: Tên đăng nhập
            password: Mật khẩu
            version: Phiên bản game (mặc định "2.1.7")

        Returns:
            True nếu gửi thành công, False nếu thất bại
        """
        print(f"🔐 Đang đăng nhập với user: {username}...")

        # Gửi client type trước và đọc response
        if not self.set_client_type():
            print("❌ Gửi client type thất bại")
            return False

        # Tạo login message
        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_LOGIN)

        msg.write_utf(username)
        msg.write_utf(password)
        msg.write_utf(version)
        msg.write_utf("")           # empty field
        msg.write_utf("")           # empty field
        msg.write_utf("".join(str(random.randint(0, 8)) for _ in range(12)))
        msg.write_byte(0)           # serverID
        msg.write_utf("VALID_CLIENT_KEY")  # auth key

        if self.send_message(msg):
            print("📤 Đã gửi login request")

            # Đợi response
            print("⏳ Đang đợi response từ server...")
            # Login may be preceded by housekeeping packets (for example -43,
            # the server clock). Keep reading until a decisive response arrives.
            while True:
                response = self.receive_message()
                if not response:
                    print("❌ Không nhận được response từ server")
                    return False

                print(
                    f"📥 Nhận response: command={response['command']}, "
                    f"length={response['length']}"
                )
                result = self._parse_login_response(response)
                if result is not None:
                    return result

        return False

    def _parse_login_response(self, response):
        """Parse login response từ server"""
        try:
            reader = NSOMessageReader(response['data'])

            # Server clock packet; the Java Controller intentionally ignores it.
            if response['command'] == -43:
                server_time = reader.read_long()
                print(f"🕒 Server time: {server_time}")
                return None

            # General server notification. During login this is an error message.
            if response['command'] == -26:
                message = reader.read_utf()
                print(f"❌ Server từ chối đăng nhập: {message}")
                return False

            # NOT_MAP packets drive the post-login data synchronization.
            if response['command'] == self.CMD_NOT_MAP:
                sub_command = reader.read_byte()

                if sub_command == -123:
                    versions = [reader.read_byte() for _ in range(4)]
                    print(
                        "📊 Phiên bản server: "
                        f"data={versions[0]}, map={versions[1]}, "
                        f"skill={versions[2]}, item={versions[3]}"
                    )

                    # A fresh Python client has no RMS cache, so request all
                    # datasets just like the Java client does on first launch.
                    self._pending_updates = {-122, -121, -120, -119}
                    for update_command in (-122, -121, -120, -119):
                        if not self._send_not_map(update_command):
                            return False
                    return None

                if sub_command in (-122, -121, -120, -119):
                    labels = {
                        -122: "data",
                        -121: "map",
                        -120: "skill",
                        -119: "item",
                    }
                    print(f"✅ Đã nhận dữ liệu {labels[sub_command]}")
                    self._pending_updates.discard(sub_command)
                    if not self._pending_updates:
                        print("📲 Đồng bộ hoàn tất, gửi clientOk")
                        if not self._send_not_map(-101):
                            return False
                    return None

                if sub_command == -126:
                    count = reader.read_byte()
                    self.characters = []
                    for _ in range(count):
                        gender = reader.read_byte()
                        name = reader.read_utf()
                        school = reader.read_utf()
                        level = reader.read_unsigned_byte()
                        # head, weapon, body and leg part IDs
                        reader.read_short()
                        reader.read_short()
                        reader.read_short()
                        reader.read_short()
                        self.characters.append(name)
                        print(
                            f"👤 Nhân vật: {name} - cấp {level} - "
                            f"phái {school} - giới tính {gender}"
                        )
                    if self.characters:
                        print("✅ Đăng nhập thành công và đã nhận danh sách nhân vật")
                    return bool(self.characters)

                print(f"ℹ️  NOT_MAP sub-command chưa xử lý: {sub_command}")
                return None

            print(f"ℹ️  Packet đăng nhập chưa xử lý: command={response['command']}")
            return None

        except Exception as e:
            print(f"⚠️  Không parse được response: {e}")
            return False

    def select_character(self, char_index):
        """
        Chọn nhân vật

        Args:
            char_index: Index của nhân vật (0, 1, 2...)

        Returns:
            True nếu gửi thành công
        """
        print(f"👤 Đang chọn nhân vật index: {char_index}...")

        if char_index < 0 or char_index >= len(self.characters):
            print(f"❌ Index nhân vật không hợp lệ: {char_index}")
            return False

        character_name = self.characters[char_index]
        msg = NSOMessage(self.CMD_NOT_MAP)
        msg.write_byte(self.CMD_SELECT_CHAR)
        msg.write_utf(character_name)

        if self.send_message(msg):
            print(f"📤 Đã gửi chọn nhân vật: {character_name}")

            # Đợi response
            response = self.receive_message()
            if response:
                print(f"📥 Nhận response: command={response['command']}")
                return True

        return False

    def register(self, username, password):
        """
        Đăng ký tài khoản mới

        Args:
            username: Tên đăng nhập
            password: Mật khẩu

        Returns:
            True nếu gửi thành công
        """
        print(f"📝 Đang đăng ký tài khoản: {username}...")

        msg = NSOMessage(self.CMD_NOT_LOGIN)
        msg.write_byte(self.CMD_REGISTER)

        msg.write_utf(username)
        msg.write_utf(password)
        msg.write_utf("")  # empty field

        if self.send_message(msg):
            print("📤 Đã gửi register request")

            # Đợi response
            response = self.receive_message()
            if response:
                print(f"📥 Nhận response: command={response['command']}")
                return True

        return False


def main():
    """Demo usage của NSO Client"""
    print("=" * 60)
    print("NSO Game Client - Python Implementation")
    print("=" * 60)
    print()

    # Thông tin server (thay đổi theo server thực tế)
    HOST = "Nsm1.ninjasm.net"  # Localhost hoặc IP server
    PORT = 14444        # Port mặc định

    # Tạo client
    client = NSOClient(HOST, PORT)

    try:
        # 1. Kết nối
        if not client.connect():
            print("❌ Không thể kết nối đến server")
            return

        print()

        # 2. Login
        username = "luongclone991"
        password = "ngan2021"

        if client.login(username, password):
            print()

            # 3. Chọn nhân vật (ví dụ chọn nhân vật đầu tiên)
            client.select_character(0)
            print()

            # 4. Các thao tác khác có thể thêm vào đây
            # Ví dụ: chat, move, attack, etc.

        # 5. Ngắt kết nối
        time.sleep(2)
        client.disconnect()

    except KeyboardInterrupt:
        print("\n⚠️  Bị ngắt bởi user")
        client.disconnect()
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        client.disconnect()


if __name__ == "__main__":
    main()
