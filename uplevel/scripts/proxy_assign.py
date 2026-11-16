#!/usr/bin/env python3
"""Gán proxy SOCKS5 cho từng worker uplevel (chia đều theo khối liên tiếp).

- proxy file rỗng / không tồn tại → mọi worker chạy mạng của máy.
- có P proxy, W worker → mạng của máy được tính như thêm 1 "proxy" (phần cuối), tức W chia
  đều cho P+1 phần: mỗi phần W//(P+1) worker liên tiếp, W%(P+1) phần đầu nhận thêm 1.
  VD 50 worker / 2 proxy: worker-01..17 = proxy 1, worker-18..34 = proxy 2, worker-35..50 = máy.
  10 / 4: 2+2+2+2 + 2 máy.  3 / 5: proxy 1..3 mỗi cái 1, proxy 4..5 và máy không dùng.
  NSO_PROXY_USE_LOCAL=0 (hoặc --no-local) → không dùng mạng máy, chỉ chia cho P proxy như cũ.
Kết quả ghi vào <worker>/.proxy (host:port:user:pass); worker chạy mạng máy thì file bị xoá.
start-workers đọc file này và truyền env NSO_SOCKS5_PROXY cho JVM (Connector.java mở SOCKS5).

Khi start (mặc định có probe): proxy được dùng bị lỗi → thoát mã 1, không chạy bằng IP máy.
"""

from __future__ import annotations

import argparse
import os
import socket
import sys
from pathlib import Path

DEFAULT_TARGET = "180.93.114.122:14444"
TIMEOUT = 8.0
LOCAL = None  # phần chia cho mạng của máy (không ghi .proxy)


def use_local_default() -> bool:
    """Mặc định có chia worker cho mạng máy khi đã có proxy; NSO_PROXY_USE_LOCAL=0 để tắt."""
    return os.environ.get("NSO_PROXY_USE_LOCAL", "1").strip().lower() not in ("0", "false", "no")


class ProxyError(RuntimeError):
    pass


def recv_exact(sock: socket.socket, size: int) -> bytes:
    data = bytearray()
    while len(data) < size:
        chunk = sock.recv(size - len(data))
        if not chunk:
            raise ProxyError("proxy đóng kết nối")
        data.extend(chunk)
    return bytes(data)


def load_proxies(path: Path) -> list[str]:
    try:
        lines = path.read_text(encoding="utf-8-sig").splitlines()
    except OSError as exc:
        raise ProxyError(f"không đọc được file proxy: {path}") from exc
    proxies: list[str] = []
    for number, raw in enumerate(lines, 1):
        value = raw.strip()
        if not value or value.startswith("#"):
            continue
        parts = value.split(":", 3)
        if len(parts) != 4 or not all(parts) or not parts[1].isdigit() \
                or not 1 <= int(parts[1]) <= 65535:
            raise ProxyError(f"{path.name} dòng {number} không đúng host:port:user:password")
        if value in proxies:
            raise ProxyError(f"{path.name} dòng {number} trùng proxy")
        proxies.append(value)
    return proxies


def masked(value: str) -> str:
    host, port, user, _ = value.split(":", 3)
    return f"{host}:{port}:{user}:***"


def probe(value: str, target_host: str, target_port: int) -> None:
    host, port, user, password = value.split(":", 3)
    u, p, t = user.encode(), password.encode(), target_host.encode()
    if len(u) > 255 or len(p) > 255:
        raise ProxyError("user/password quá dài")
    with socket.create_connection((host, int(port)), TIMEOUT) as sock:
        sock.settimeout(TIMEOUT)
        sock.sendall(bytes((5, 1, 2)))
        if recv_exact(sock, 2) != b"\x05\x02":
            raise ProxyError("không hỗ trợ xác thực user/password")
        sock.sendall(bytes((1, len(u))) + u + bytes((len(p),)) + p)
        if recv_exact(sock, 2) != b"\x01\x00":
            raise ProxyError("sai user/password")
        sock.sendall(bytes((5, 1, 0, 3, len(t))) + t + target_port.to_bytes(2, "big"))
        response = recv_exact(sock, 4)
        if response[1] != 0:
            raise ProxyError(f"CONNECT tới server game lỗi mã {response[1]}")


def worker_number(worker_dir: Path) -> int:
    try:
        return int(worker_dir.name[len("worker-"):])
    except ValueError as exc:
        raise ProxyError(f"tên worker không hợp lệ: {worker_dir.name}") from exc


def clear(workers_dir: Path) -> None:
    for worker_dir in workers_dir.glob("worker-*"):
        (worker_dir / ".proxy").unlink(missing_ok=True)


def assign(workers_dir: Path, proxy_file: Path, target: str = DEFAULT_TARGET,
           check: bool = True, out=sys.stdout,
           use_local: bool | None = None) -> dict[str, str | None]:
    """Gán proxy cho mọi worker trong workers_dir. Trả về {worker_name: proxy|None}."""
    target_host, _, target_port = target.rpartition(":")
    if not target_host or not target_port.isdigit():
        # Tên server cũ (tk, ninjamobile...) bị client bỏ qua → dùng server mặc định.
        target_host, _, target_port = DEFAULT_TARGET.rpartition(":")

    workers = sorted((d for d in workers_dir.glob("worker-*") if d.is_dir()), key=worker_number)
    proxies = load_proxies(proxy_file) if proxy_file.is_file() else []
    if not proxies:
        clear(workers_dir)
        print(f"Proxy: {proxy_file.name} trống → {len(workers)} worker chạy mạng của máy", file=out)
        return {w.name: None for w in workers}

    if use_local is None:
        use_local = use_local_default()
    count = len(workers)
    # Khối liên tiếp: các proxy rồi tới mạng máy (nếu bật); W//S worker mỗi phần,
    # W%S phần đầu nhận thêm 1 worker dư (S = số phần).
    slots: list[str | None] = list(proxies) + ([LOCAL] if use_local else [])
    base, extra = divmod(count, len(slots))
    order: list[str | None] = []
    for index, value in enumerate(slots):
        order += [value] * (base + (1 if index < extra else 0))
    plan = {w.name: order[i] for i, w in enumerate(workers)}
    used = [p for p in proxies if p in plan.values()]

    if check:
        errors = []
        for value in used:
            try:
                probe(value, target_host, int(target_port))
            except (OSError, ProxyError) as exc:
                errors.append(f"{masked(value)}: {exc}")
        if errors:
            raise ProxyError("proxy không hoạt động:\n  " + "\n  ".join(errors))

    for worker in workers:
        path = worker / ".proxy"
        value = plan[worker.name]
        if value is LOCAL:
            path.unlink(missing_ok=True)
            continue
        path.write_text(value + "\n", encoding="utf-8")
        try:
            path.chmod(0o600)
        except OSError:
            pass

    local_note = " + mạng máy" if use_local else ""
    print(f"Proxy: {count} worker chia đều cho {len(used)}/{len(proxies)} proxy{local_note}",
          file=out)
    for number, value in enumerate(slots, 1):
        label = "mạng máy" if value is LOCAL else f"proxy {number} {masked(value)}"
        names = [n for n, v in plan.items() if v == value]
        if not names:
            print(f"  {label}: không dùng (ít worker hơn số phần chia)", file=out)
            continue
        span = names[0] if len(names) == 1 else f"{names[0]}..{names[-1]}"
        print(f"  {label}: {span} ({len(names)} worker)", file=out)
    return plan


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--workers-dir", required=True)
    parser.add_argument("--proxy-file", required=True)
    parser.add_argument("--no-check", action="store_true",
                        help="chỉ gán, không probe proxy (dùng lúc build-workers)")
    parser.add_argument("--server", default=DEFAULT_TARGET)
    parser.add_argument("--clear", action="store_true", help="xoá mọi .proxy (tắt proxy)")
    parser.add_argument("--no-local", action="store_true",
                        help="không chia worker cho mạng máy (giống NSO_PROXY_USE_LOCAL=0)")
    args = parser.parse_args()
    workers_dir = Path(args.workers_dir)
    try:
        if args.clear:
            clear(workers_dir)
            print("Proxy: tắt (mọi worker chạy IP máy)")
            return 0
        assign(workers_dir, Path(args.proxy_file), args.server, check=not args.no_check,
               use_local=False if args.no_local else None)
        return 0
    except ProxyError as exc:
        print(f"Proxy lỗi: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
