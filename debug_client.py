#!/usr/bin/env python3
"""
NSO Client Debug Version
Thêm logging chi tiết để debug connection issue
"""

import socket
import struct
import time
from nso_client import NSOClient, NSOMessage

class NSOClientDebug(NSOClient):
    """Debug version với detailed logging"""

    def send_message(self, message):
        """Gửi message với logging"""
        if not self.connected:
            print("❌ Chưa kết nối đến server!")
            return False

        try:
            packet = message.to_packet()
            print(f"\n📤 SENDING MESSAGE:")
            print(f"   Packet size: {len(packet)} bytes")
            print(f"   Raw (first 50 bytes): {packet[:50].hex()}")

            # Encrypt if have key
            if self.key:
                print(f"   Encrypting with key (len={len(self.key)})")
                encrypted_packet = bytearray()
                for byte in packet:
                    encrypted_byte = self._encrypt_byte(byte)
                    encrypted_packet.append(encrypted_byte)
                packet = bytes(encrypted_packet)
                print(f"   Encrypted (first 50 bytes): {packet[:50].hex()}")

            self.socket.send(packet)
            print(f"   ✅ Sent successfully")
            return True
        except Exception as e:
            print(f"❌ Lỗi gửi message: {e}")
            return False

    def receive_message(self):
        """Nhận message với logging"""
        if not self.connected:
            return None

        print(f"\n📥 RECEIVING MESSAGE:")
        try:
            # Check if data available
            print(f"   Waiting for data (timeout={self.socket.gettimeout()}s)...")

            # Read command byte
            cmd_data = self.socket.recv(1)
            if not cmd_data:
                print("   ⚠️  No data received (empty read)")
                return None

            print(f"   Raw cmd byte: {cmd_data.hex()}")

            # Decrypt if have key
            command_byte = cmd_data[0]
            if self.key:
                command = self._decrypt_byte(command_byte)
                print(f"   Decrypted command: {command}")
            else:
                command = struct.unpack('b', cmd_data)[0]
                print(f"   Command (no decrypt): {command}")

            # Read length
            len_data = self.socket.recv(2)
            if len(len_data) < 2:
                print(f"   ⚠️  Incomplete length data: {len(len_data)} bytes")
                return None

            print(f"   Raw length bytes: {len_data.hex()}")

            # Decrypt length if have key
            if self.key:
                len_byte1 = self._decrypt_byte(len_data[0])
                len_byte2 = self._decrypt_byte(len_data[1])
                length = ((len_byte1 & 0xFF) << 8) | (len_byte2 & 0xFF)
                print(f"   Decrypted length: {length}")
            else:
                length = struct.unpack('>h', len_data)[0]
                print(f"   Length (no decrypt): {length}")

            # Read data
            print(f"   Reading {length} bytes of data...")
            data = b''
            while len(data) < length:
                chunk = self.socket.recv(length - len(data))
                if not chunk:
                    break
                data += chunk

            print(f"   Raw data (first 50 bytes): {data[:50].hex()}")

            # Decrypt data if have key
            if self.key:
                decrypted_data = bytearray()
                for byte in data:
                    decrypted_byte = self._decrypt_byte(byte)
                    decrypted_data.append(decrypted_byte)
                data = bytes(decrypted_data)
                print(f"   Decrypted data (first 50 bytes): {data[:50].hex()}")

            print(f"   ✅ Received complete message")
            return {
                'command': command,
                'length': length,
                'data': data
            }

        except socket.timeout:
            print("   ⏱️  Socket timeout - no data received")
            return None
        except Exception as e:
            print(f"   ❌ Error: {e}")
            import traceback
            traceback.print_exc()
            return None


def test_login():
    """Test login với debug logging"""
    print("=" * 70)
    print("NSO CLIENT DEBUG TEST")
    print("=" * 70)

    HOST = "Nsm1.ninjasm.net"
    PORT = 14444
    USERNAME = "luongclone991"
    PASSWORD = "your_password_here"  # Replace

    client = NSOClientDebug(HOST, PORT)

    try:
        # Connect
        if not client.connect():
            print("\n❌ Connection failed")
            return

        print("\n" + "=" * 70)
        print("TESTING LOGIN FLOW")
        print("=" * 70)

        # Send client info
        print("\n>>> Sending client info...")
        if not client.set_client_type():
            print("❌ Failed to send client info")
            return

        # Small delay
        time.sleep(0.2)

        # Try to read any response after client info
        print("\n>>> Checking for response after client info...")
        client.socket.settimeout(2)  # Short timeout
        resp = client.receive_message()
        if resp:
            print(f"   📬 Got response! Command: {resp['command']}")
        else:
            print(f"   ℹ️  No response after client info (expected)")

        # Reset timeout
        client.socket.settimeout(10)

        # Send login
        print("\n>>> Sending login request...")
        msg = NSOMessage(client.CMD_NOT_LOGIN)
        msg.write_byte(client.CMD_LOGIN)
        msg.write_utf(USERNAME)
        msg.write_utf(PASSWORD)
        msg.write_utf("217")
        msg.write_utf("")
        msg.write_utf("")
        msg.write_utf("random123")
        msg.write_byte(0)
        msg.write_utf("VALID_CLIENT_KEY")

        if not client.send_message(msg):
            print("❌ Failed to send login")
            return

        # Wait for login response
        print("\n>>> Waiting for login response...")
        response = client.receive_message()

        if response:
            print(f"\n✅ GOT RESPONSE!")
            print(f"   Command: {response['command']}")
            print(f"   Data length: {response['length']}")
            print(f"   Data: {response['data'][:100].hex()}")
        else:
            print(f"\n❌ NO RESPONSE")

        client.disconnect()

    except Exception as e:
        print(f"\n❌ Exception: {e}")
        import traceback
        traceback.print_exc()
        client.disconnect()


if __name__ == "__main__":
    test_login()
