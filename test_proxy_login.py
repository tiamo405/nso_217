#!/usr/bin/env python3
"""Test TẤT CẢ proxy SOCKS5 trong proxy-tk.txt trước khi build worker.

Với từng proxy: kiểm tra định dạng → IP ra ngoài → kết nối server game + login thử 1 tài khoản.
Cuối cùng in danh sách proxy lỗi (kèm số dòng) để xoá khỏi file rồi mới chạy build-workers.

Dùng:
    python3 test_proxy_login.py                   # acc đầu trong account-as20.csv
    python3 test_proxy_login.py acctest2 000111   # chỉ định tài khoản login thử
    python3 test_proxy_login.py --no-login        # chỉ kiểm tra kết nối, không login
    python3 test_proxy_login.py --file khac.txt   # file proxy khác

Mã thoát: 0 = mọi proxy OK, 1 = có proxy lỗi (hoặc file rỗng).
Các proxy được test lần lượt (không song song) vì cùng login một tài khoản.
"""

import csv
import socket
import sys
from pathlib import Path

from hoatdong import NSOActivityClient

HOST = "180.93.114.122"
PORT = 14444
TIMEOUT = 15
REPO_DIR = Path(__file__).resolve().parent
PROXY_FILE = REPO_DIR / "proxy-tk.txt"
ACCOUNT_CSV = REPO_DIR / "account-as20.csv"
USENAME = "acctest1"
PASSWORD = "000111"


def read_proxy_lines(path: Path):
    """Trả về [(số dòng, chuỗi gốc, (host, port, user, pass) | None, lỗi định dạng | None)]."""
    result, seen = [], {}
    for number, raw in enumerate(path.read_text(encoding="utf-8-sig").splitlines(), 1):
        value = raw.strip()
        if not value or value.startswith("#"):
            continue
        parts = value.split(":", 3)
        if len(parts) != 4 or not all(parts):
            result.append((number, value, None, "sai định dạng host:port:user:password"))
        elif not parts[1].isdigit() or not 1 <= int(parts[1]) <= 65535:
            result.append((number, value, None, "port không hợp lệ"))
        elif value in seen:
            result.append((number, value, None, f"trùng với dòng {seen[value]}"))
        else:
            seen[value] = number
            result.append((number, value, (parts[0], int(parts[1]), parts[2], parts[3]), None))
    return result


def masked(value: str) -> str:
    parts = value.split(":", 3)
    return ":".join(parts[:3]) + ":***" if len(parts) == 4 else value


SOCKS5_ERRORS = {
    1: "lỗi chung của proxy", 2: "proxy từ chối (sai user/pass hoặc IP chưa được phép)",
    3: "mạng không tới được", 4: "host không tới được", 5: "server từ chối kết nối",
    6: "hết TTL", 7: "lệnh không hỗ trợ", 8: "kiểu địa chỉ không hỗ trợ",
}


def recv_exact(sock, size):
    data = bytearray()
    while len(data) < size:
        chunk = sock.recv(size - len(data))
        if not chunk:
            raise ConnectionError("proxy đóng kết nối")
        data.extend(chunk)
    return bytes(data)


def socks5_connect(proxy, target_host, target_port, timeout=TIMEOUT):
    """Mở socket tới target qua SOCKS5 user/pass (giống Connector.java)."""
    p_host, p_port, user, password = proxy
    sock = socket.create_connection((p_host, p_port), timeout)
    try:
        sock.settimeout(timeout)
        sock.sendall(bytes((5, 1, 2)))
        if recv_exact(sock, 2) != b"\x05\x02":
            raise ConnectionError("proxy không nhận xác thực user/pass")
        u, pw = user.encode(), password.encode()
        sock.sendall(bytes((1, len(u))) + u + bytes((len(pw),)) + pw)
        if recv_exact(sock, 2) != b"\x01\x00":
            raise ConnectionError("SOCKS5 sai user/pass")
        t = target_host.encode()
        sock.sendall(bytes((5, 1, 0, 3, len(t))) + t + target_port.to_bytes(2, "big"))
        resp = recv_exact(sock, 4)
        if resp[1] != 0:
            raise ConnectionError(f"SOCKS5 lỗi mã {resp[1]}: {SOCKS5_ERRORS.get(resp[1], 'không rõ')}")
        atyp = resp[3]
        recv_exact(sock, 6 if atyp == 1 else 18 if atyp == 4 else recv_exact(sock, 1)[0] + 2)
        return sock
    except BaseException:
        sock.close()
        raise


def public_ip(proxy):
    try:
        sock = socks5_connect(proxy, "api.ipify.org", 80, 10) if proxy else \
            socket.create_connection(("api.ipify.org", 80), 10)
        with sock:
            sock.sendall(b"GET / HTTP/1.0\r\nHost: api.ipify.org\r\n\r\n")
            data = b""
            while chunk := sock.recv(4096):
                data += chunk
        return data.split(b"\r\n\r\n", 1)[-1].decode().strip()
    except Exception as exc:
        return f"? ({exc})"


class ProxyClient(NSOActivityClient):
    def __init__(self, host, port, proxy):
        super().__init__(host, port)
        self.proxy = proxy
        self.connect_error = None

    def connect(self) -> bool:
        if not self.proxy:
            return super().connect()
        try:
            self.sock = socks5_connect(self.proxy, self.host, self.port)
            self._exchange_key()
            return bool(self.key)
        except Exception as exc:
            self.connect_error = str(exc) or type(exc).__name__
            self.disconnect()
            return False


def test_one(proxy, username, password, do_login):
    """Trả về (ok, mô tả)."""
    client = ProxyClient(HOST, PORT, proxy)
    try:
        if not client.connect():
            return False, f"không kết nối được server game: {client.connect_error or 'key exchange lỗi'}"
        if not do_login:
            return True, "kết nối + key exchange OK"
        if not client.login(username, password):
            return False, f"login {username} thất bại"
        names = ", ".join(f"{n} lv{lv}" for n, lv, _ in client.characters) or "không có nhân vật"
        return True, f"login {username} OK ({names})"
    except Exception as exc:
        return False, f"lỗi: {exc}"
    finally:
        client.disconnect()


def main():
    args = sys.argv[1:]
    do_login = "--no-login" not in args
    proxy_file = PROXY_FILE
    
    username, password = USENAME, PASSWORD

    if not proxy_file.is_file():
        print(f"Không có file {proxy_file} → worker sẽ chạy mạng của máy.")
        return 1
    entries = read_proxy_lines(proxy_file)
    if not entries:
        print(f"{proxy_file.name} trống → worker sẽ chạy mạng của máy.")
        return 1

    print(f"IP máy: {public_ip(None)}")
    print(f"Test {len(entries)} proxy trong {proxy_file.name} → server {HOST}:{PORT}"
          + (f", login thử {username}" if do_login else ", không login"))
    print("-" * 70)

    bad, good_ips = [], {}
    for index, (number, value, proxy, format_error) in enumerate(entries, 1):
        label = f"[{index}/{len(entries)}] dòng {number}: {masked(value)}"
        if format_error:
            print(f"❌ {label}\n     {format_error}")
            bad.append((number, value, format_error))
            continue
        ok, detail = test_one(proxy, username, password, do_login)
        if ok:
            ip = public_ip(proxy)
            print(f"✅ {label}\n     IP ra ngoài: {ip} | {detail}")
        else:
            ip = "?"
            print(f"❌ {label}\n     {detail}")
        if ok:
            if not ip.startswith("?"):
                good_ips.setdefault(ip, []).append(number)
        else:
            bad.append((number, value, detail))

    print("-" * 70)
    print(f"Kết quả: {len(entries) - len(bad)}/{len(entries)} proxy OK, {len(bad)} lỗi")
    for ip, lines in good_ips.items():
        if len(lines) > 1:
            print(f"⚠️  Các dòng {', '.join(map(str, lines))} cùng IP ra ngoài {ip} "
                  f"→ server tính chung giới hạn acc/IP, nên chỉ giữ 1 dòng.")
    if bad:
        print(f"\nXoá các dòng sau khỏi {proxy_file.name} rồi mới chạy build-workers:")
        for number, value, reason in bad:
            print(f"  dòng {number}: {masked(value)}  ← {reason}")
        return 1
    print("Tất cả proxy hoạt động, có thể chạy build-workers.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
