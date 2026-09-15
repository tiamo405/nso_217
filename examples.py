#!/usr/bin/env python3
"""Test đăng nhập một tài khoản vào server NSO."""

from hoatdong import NSOActivityClient


# Sửa trực tiếp thông tin test tại đây.
HOST = "Nsotk1.nsotk.online"
PORT = 14444
USERNAME = "namtk0000"
PASSWORD = "000111"


def main():
    print(f"Đang kết nối {HOST}:{PORT}...")
    print(f"Tài khoản: {USERNAME}")

    client = NSOActivityClient(HOST, PORT)
    try:
        if not client.connect():
            print("❌ Không thể kết nối hoặc key exchange thất bại")
            return 1

        print("✅ Kết nối và key exchange thành công")
        if not client.login(USERNAME, PASSWORD):
            print("❌ Login thất bại")
            return 1

        print(f"✅ LOGIN THÀNH CÔNG: {len(client.characters)} nhân vật")
        for index, (name, level, school) in enumerate(client.characters):
            print(f"  [{index}] {name} - level {level} - {school}")
        return 0
    finally:
        client.disconnect()


if __name__ == "__main__":
    raise SystemExit(main())
