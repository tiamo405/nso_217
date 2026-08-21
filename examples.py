#!/usr/bin/env python3
"""
Ví dụ sử dụng NSO Client
Các kịch bản thực tế khi sử dụng client
"""

from nso_client import NSOClient
import time


def example_1_simple_login():
    """Ví dụ 1: Login đơn giản"""
    print("=" * 60)
    print("VÍ DỤ 1: LOGIN ĐƠN GIẢN")
    print("=" * 60)
    print()

    # Khởi tạo client
    client = NSOClient("127.0.0.1", 14444)

    # Kết nối và login
    if client.connect():
        client.login("myusername", "mypassword")
        time.sleep(2)
        client.disconnect()


def example_2_login_and_select_char():
    """Ví dụ 2: Login và chọn nhân vật"""
    print("=" * 60)
    print("VÍ DỤ 2: LOGIN VÀ CHỌN NHÂN VẬT")
    print("=" * 60)
    print()

    client = NSOClient("game.server.com", 14444)

    if client.connect():
        # Login
        if client.login("user123", "pass123"):
            time.sleep(1)

            # Chọn nhân vật đầu tiên (index 0)
            client.select_character(0)
            time.sleep(2)

        client.disconnect()


def example_3_register_new_account():
    """Ví dụ 3: Đăng ký tài khoản mới"""
    print("=" * 60)
    print("VÍ DỤ 3: ĐĂNG KÝ TÀI KHOẢN MỚI")
    print("=" * 60)
    print()

    client = NSOClient("127.0.0.1", 14444)

    if client.connect():
        # Đăng ký tài khoản mới
        client.register("newuser", "newpass123")
        time.sleep(2)
        client.disconnect()


def example_4_auto_reconnect():
    """Ví dụ 4: Tự động reconnect khi mất kết nối"""
    print("=" * 60)
    print("VÍ DỤ 4: AUTO RECONNECT")
    print("=" * 60)
    print()

    client = NSOClient("127.0.0.1", 14444)

    max_retries = 3
    retry_count = 0

    while retry_count < max_retries:
        try:
            if client.connect():
                if client.login("user", "pass"):
                    print("✅ Đã login thành công!")
                    # Làm các thao tác game ở đây
                    time.sleep(5)
                break
        except Exception as e:
            print(f"❌ Lỗi: {e}")
            retry_count += 1
            if retry_count < max_retries:
                print(f"🔄 Thử lại lần {retry_count}/{max_retries}...")
                time.sleep(3)
        finally:
            client.disconnect()

    if retry_count >= max_retries:
        print("❌ Không thể kết nối sau nhiều lần thử")


def example_5_multiple_servers():
    """Ví dụ 5: Thử kết nối nhiều server"""
    print("=" * 60)
    print("VÍ DỤ 5: THỬ NHIỀU SERVER")
    print("=" * 60)
    print()

    servers = [
        ("server1.game.com", 14444),
        ("server2.game.com", 14444),
        ("127.0.0.1", 14444),
    ]

    username = "testuser"
    password = "testpass"

    for host, port in servers:
        print(f"\n🔍 Đang thử server: {host}:{port}")
        client = NSOClient(host, port)

        if client.connect():
            if client.login(username, password):
                print(f"✅ Đã login vào {host}:{port}")
                time.sleep(2)
                client.disconnect()
                break  # Dừng lại khi login thành công
            else:
                client.disconnect()
        else:
            print(f"❌ Không thể kết nối đến {host}:{port}")


if __name__ == "__main__":
    print("NSO CLIENT - CÁC VÍ DỤ SỬ DỤNG")
    print()
    print("Chọn ví dụ để chạy:")
    print("1. Login đơn giản")
    print("2. Login và chọn nhân vật")
    print("3. Đăng ký tài khoản mới")
    print("4. Auto reconnect")
    print("5. Thử nhiều server")
    print()

    choice = input("Nhập số (1-5): ").strip()

    if choice == "1":
        example_1_simple_login()
    elif choice == "2":
        example_2_login_and_select_char()
    elif choice == "3":
        example_3_register_new_account()
    elif choice == "4":
        example_4_auto_reconnect()
    elif choice == "5":
        example_5_multiple_servers()
    else:
        print("❌ Lựa chọn không hợp lệ")
