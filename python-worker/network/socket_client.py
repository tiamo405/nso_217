"""
NSO Network Socket Client
Handles connection, packet framing, rolling XOR encryption/decryption, and thread-safe messaging.
"""
import socket
import struct
import threading
import time
import logging
from typing import Callable, Optional
from .encryption import RollingXOR
from .message import NSOMessage
from .constants import CMD_KEY_REQUEST

logger = logging.getLogger("NSOSocket")


class NSOSocketClient:
    def __init__(self, host: str, port: int, timeout: float = 12.0):
        self.host = host
        self.port = port
        self.timeout = timeout
        self.sock: Optional[socket.socket] = None
        self.cipher = RollingXOR()
        self.connected = False
        self.is_running = False
        self._recv_thread: Optional[threading.Thread] = None
        self.on_message_callback: Optional[Callable[[NSOMessage], None]] = None
        self.on_disconnected_callback: Optional[Callable[[], None]] = None
        self._lock = threading.Lock()

    def connect(self) -> bool:
        """Establishes TCP connection and performs key exchange."""
        self.disconnect()
        try:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.sock.settimeout(self.timeout)
            self.sock.connect((self.host, self.port))
            self.connected = True
            self.is_running = True
            self.cipher.reset()

            # Start message receiver thread
            self._recv_thread = threading.Thread(target=self._recv_loop, daemon=True)
            self._recv_thread.start()

            # Request encryption key
            key_msg = NSOMessage(CMD_KEY_REQUEST)
            self.send_message(key_msg)

            # Wait for key exchange to complete
            deadline = time.time() + 8.0
            while not self.cipher.is_ready and time.time() < deadline:
                time.sleep(0.05)

            if not self.cipher.is_ready:
                logger.error("Key exchange timeout")
                self.disconnect()
                return False

            return True
        except Exception as e:
            logger.error(f"Connection failed to {self.host}:{self.port} - {e}")
            self.disconnect()
            return False

    def disconnect(self):
        with self._lock:
            self.is_running = False
            self.connected = False
            self.cipher.reset()
            if self.sock:
                try:
                    self.sock.shutdown(socket.SHUT_RDWR)
                except Exception:
                    pass
                try:
                    self.sock.close()
                except Exception:
                    pass
                self.sock = None

    def send_message(self, msg: NSOMessage) -> bool:
        if not self.connected or not self.sock:
            return False
        with self._lock:
            try:
                data = msg.get_data()
                cmd = msg.command

                # Encrypt command byte
                if self.cipher.is_ready:
                    cmd_byte = self.cipher.encrypt_byte(cmd)
                else:
                    cmd_byte = cmd & 0xFF

                packet = bytearray()
                packet.append(cmd_byte)

                if data:
                    length = len(data)
                    if cmd != -31 and self.cipher.is_ready:
                        packet.append(self.cipher.encrypt_byte((length >> 8) & 0xFF))
                        packet.append(self.cipher.encrypt_byte(length & 0xFF))
                        packet.extend(self.cipher.encrypt_data(data))
                    else:
                        packet.extend(struct.pack('>H', length))
                        if self.cipher.is_ready:
                            packet.extend(self.cipher.encrypt_data(data))
                        else:
                            packet.extend(data)
                else:
                    if cmd != -31 and self.cipher.is_ready:
                        packet.append(self.cipher.encrypt_byte(0))
                        packet.append(self.cipher.encrypt_byte(0))
                    else:
                        packet.extend(b'\x00\x00')

                self.sock.sendall(packet)
                return True
            except Exception as e:
                logger.error(f"Failed to send message cmd={msg.command}: {e}")
                return False

    def _recv_exact(self, size: int) -> Optional[bytes]:
        buf = bytearray()
        while len(buf) < size and self.is_running:
            try:
                chunk = self.sock.recv(size - len(buf))
                if not chunk:
                    return None
                buf.extend(chunk)
            except socket.timeout:
                continue
            except Exception:
                return None
        return bytes(buf) if len(buf) == size else None

    def _recv_loop(self):
        while self.is_running and self.sock:
            try:
                raw_cmd = self._recv_exact(1)
                if not raw_cmd:
                    break
                cmd_byte = struct.unpack('b', raw_cmd)[0]
                if self.cipher.is_ready:
                    cmd_byte = struct.unpack('b', bytes([self.cipher.decrypt_byte(cmd_byte)]))[0]

                # Length parsing (handle -32 for 4-byte length or standard 2-byte)
                if cmd_byte == -32:
                    raw_sub = self._recv_exact(1)
                    if not raw_sub:
                        break
                    sub_cmd = struct.unpack('b', raw_sub)[0]
                    if self.cipher.is_ready:
                        sub_cmd = struct.unpack('b', bytes([self.cipher.decrypt_byte(sub_cmd)]))[0]
                    cmd_byte = sub_cmd

                    len_bytes = self._recv_exact(4)
                    if not len_bytes:
                        break
                    if self.cipher.is_ready:
                        d0 = self.cipher.decrypt_byte(len_bytes[0])
                        d1 = self.cipher.decrypt_byte(len_bytes[1])
                        d2 = self.cipher.decrypt_byte(len_bytes[2])
                        d3 = self.cipher.decrypt_byte(len_bytes[3])
                        msg_len = (d0 << 24) | (d1 << 16) | (d2 << 8) | d3
                    else:
                        msg_len = struct.unpack('>I', len_bytes)[0]
                elif self.cipher.is_ready:
                    len_bytes = self._recv_exact(2)
                    if not len_bytes:
                        break
                    d0 = self.cipher.decrypt_byte(len_bytes[0])
                    d1 = self.cipher.decrypt_byte(len_bytes[1])
                    msg_len = (d0 << 8) | d1
                else:
                    len_bytes = self._recv_exact(2)
                    if not len_bytes:
                        break
                    msg_len = struct.unpack('>H', len_bytes)[0]

                # Read body payload
                body_data = b""
                if msg_len > 0:
                    raw_body = self._recv_exact(msg_len)
                    if not raw_body:
                        break
                    if self.cipher.is_ready:
                        body_data = self.cipher.decrypt_data(raw_body)
                    else:
                        body_data = raw_body

                msg = NSOMessage(cmd_byte, body_data)

                # Process key exchange message (-27)
                if cmd_byte == CMD_KEY_REQUEST:
                    r = msg.reader()
                    key_len = r.read_byte()
                    raw_key = r.read_bytes(key_len)
                    self.cipher.setup_key(raw_key)
                    logger.debug(f"Received encryption key len={key_len}")
                    continue

                if self.on_message_callback:
                    try:
                        self.on_message_callback(msg)
                    except Exception as ex:
                        logger.error(f"Error handling message cmd={cmd_byte}: {ex}")

            except Exception as e:
                if self.is_running:
                    logger.info(f"AUTO SOCKET: mất kết nối (Recv loop exception: {e})")
                break

        self.disconnect()
        if self.on_disconnected_callback:
            try:
                self.on_disconnected_callback()
            except Exception:
                pass

