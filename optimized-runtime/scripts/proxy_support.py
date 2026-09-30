#!/usr/bin/env python3
"""Validate SOCKS5 proxies and assign one proxy to each worker group."""

from __future__ import annotations

import argparse
import os
import socket
import sys
from pathlib import Path


SOCKS_VERSION = 5
DEFAULT_TARGET_HOST = "Nsotk1.nsotk.online"
DEFAULT_TARGET_PORT = 14444
DEFAULT_TIMEOUT = 8.0


class ProxyError(RuntimeError):
    pass


def read_exact(stream: socket.socket, size: int) -> bytes:
    data = bytearray()
    while len(data) < size:
        chunk = stream.recv(size - len(data))
        if not chunk:
            raise ProxyError("proxy đóng kết nối")
        data.extend(chunk)
    return bytes(data)


def parse_proxy(raw: str, line_number: int) -> dict[str, object]:
    value = raw.strip()
    parts = value.split(":", 3)
    if len(parts) != 4 or any(not part for part in parts):
        raise ProxyError(f"dòng {line_number} không đúng host:port:user:password")
    host, port_text, username, password = parts
    try:
        port = int(port_text)
    except ValueError as exc:
        raise ProxyError(f"dòng {line_number} có port không hợp lệ") from exc
    if not 1 <= port <= 65535:
        raise ProxyError(f"dòng {line_number} có port ngoài phạm vi")
    return {
        "value": value,
        "host": host,
        "port": port,
        "username": username,
        "password": password,
        "endpoint": f"{host.lower()}:{port}",
    }


def load_proxies(path: Path) -> list[dict[str, object]]:
    try:
        lines = path.read_text(encoding="utf-8-sig").splitlines()
    except OSError as exc:
        raise ProxyError(f"không đọc được file proxy: {path}") from exc

    proxies: list[dict[str, object]] = []
    endpoints: set[str] = set()
    for line_number, raw in enumerate(lines, 1):
        if not raw.strip() or raw.lstrip().startswith("#"):
            continue
        proxy = parse_proxy(raw, line_number)
        endpoint = str(proxy["endpoint"])
        if endpoint in endpoints:
            raise ProxyError(f"proxy trùng endpoint {endpoint}")
        endpoints.add(endpoint)
        proxies.append(proxy)
    return proxies


def probe(proxy: dict[str, object], target_host: str, target_port: int) -> None:
    username = str(proxy["username"]).encode("utf-8")
    password = str(proxy["password"]).encode("utf-8")
    if len(username) > 255 or len(password) > 255:
        raise ProxyError("username/password SOCKS5 quá dài")
    target = target_host.encode("idna")
    if len(target) > 255:
        raise ProxyError("hostname TK quá dài")

    with socket.create_connection(
        (str(proxy["host"]), int(proxy["port"])), DEFAULT_TIMEOUT
    ) as connection:
        connection.settimeout(DEFAULT_TIMEOUT)
        connection.sendall(bytes((SOCKS_VERSION, 2, 0, 2)))
        if read_exact(connection, 2) != bytes((SOCKS_VERSION, 2)):
            raise ProxyError("không nhận SOCKS5 username/password")

        auth = bytes((1, len(username))) + username + bytes((len(password),)) + password
        connection.sendall(auth)
        if read_exact(connection, 2) != bytes((1, 0)):
            raise ProxyError("SOCKS5 xác thực thất bại")

        request = (
            bytes((SOCKS_VERSION, 1, 0, 3, len(target)))
            + target
            + target_port.to_bytes(2, "big")
        )
        connection.sendall(request)
        response = read_exact(connection, 4)
        if response[0] != SOCKS_VERSION or response[1] != 0:
            raise ProxyError(f"SOCKS5 CONNECT thất bại mã {response[1]}")

        address_type = response[3]
        if address_type == 1:
            read_exact(connection, 4 + 2)
        elif address_type == 3:
            read_exact(connection, read_exact(connection, 1)[0] + 2)
        elif address_type == 4:
            read_exact(connection, 16 + 2)
        else:
            raise ProxyError("SOCKS5 trả về địa chỉ không hợp lệ")


def masked(proxy: dict[str, object]) -> str:
    return f"{proxy['host']}:{proxy['port']}:{proxy['username']}:***"


def worker_number(worker_dir: Path) -> int:
    try:
        return int(worker_dir.name[len("worker-"):])
    except ValueError as exc:
        raise ProxyError(f"worker không hợp lệ: {worker_dir.name}") from exc


def worker_is_running(worker_dir: Path) -> bool:
    pid_file = worker_dir / "bot.pid"
    try:
        pid = int(pid_file.read_text(encoding="utf-8").strip())
        os.kill(pid, 0)
    except (OSError, ValueError):
        return False
    try:
        command = Path(f"/proc/{pid}/cmdline").read_bytes().replace(b"\0", b" ").decode(
            "utf-8", errors="replace"
        )
    except OSError:
        return False
    return "OptimizedMain" in command and str(worker_dir) in command


def active_workers(workers_dir: Path) -> list[Path]:
    workers = []
    for worker_dir in sorted(workers_dir.glob("worker-*")):
        if not worker_dir.is_dir():
            continue
        if (worker_dir / ".paused").is_file():
            continue
        if (worker_dir / "home" / "worker.done").is_file():
            continue
        if (worker_dir / "home" / "worker.first-pass.done").is_file():
            continue
        workers.append(worker_dir)
    return workers


def clear_assignments(workers_dir: Path) -> None:
    for worker_dir in workers_dir.glob("worker-*"):
        (worker_dir / ".proxy").unlink(missing_ok=True)
    (workers_dir / ".proxy-group-size").unlink(missing_ok=True)


def assign(args: argparse.Namespace) -> int:
    workers_dir = Path(args.workers_dir).resolve()
    proxy_file = Path(args.proxy_file).resolve()
    workers = active_workers(workers_dir)
    running = [worker for worker in workers if worker_is_running(worker)]

    if args.server != "tk" or args.group_size == 0:
        if running and any((worker / ".proxy").is_file() for worker in running):
            raise ProxyError("hãy Stop toàn bộ worker trước khi tắt proxy")
        clear_assignments(workers_dir)
        print("Proxy: disabled")
        return 0

    if not workers:
        clear_assignments(workers_dir)
        print("Proxy: không có worker cần chạy")
        return 0

    marker = workers_dir / ".proxy-group-size"
    old_group_size = marker.read_text(encoding="utf-8").strip() if marker.is_file() else ""
    if old_group_size != str(args.group_size) and running:
        raise ProxyError("đã đổi số worker/proxy; hãy Stop toàn bộ worker rồi Start lại")
    if not running:
        clear_assignments(workers_dir)

    try:
        configured = load_proxies(proxy_file)
    except ProxyError as exc:
        raise ProxyError(str(exc)) from exc

    valid: dict[str, dict[str, object]] = {}
    for proxy in configured:
        try:
            probe(proxy, args.target_host, args.target_port)
        except (OSError, ProxyError) as exc:
            print(f"Proxy lỗi {masked(proxy)}: {exc}", file=sys.stderr)
            continue
        valid[str(proxy["value"])] = proxy

    groups: dict[int, list[Path]] = {}
    for worker in workers:
        group = (worker_number(worker) - 1) // args.group_size
        groups.setdefault(group, []).append(worker)

    assignments: dict[int, dict[str, object]] = {}
    used_endpoints: set[str] = set()
    for group, group_workers in sorted(groups.items()):
        values = {
            (worker / ".proxy").read_text(encoding="utf-8").strip()
            for worker in group_workers
            if (worker / ".proxy").is_file()
        }
        if len(values) > 1:
            raise ProxyError(f"group {group + 1} có nhiều proxy khác nhau")
        if values:
            value = values.pop()
            proxy = valid.get(value)
            if proxy is None:
                raise ProxyError(f"proxy đã gán cho group {group + 1} không hoạt động")
        else:
            proxy = next(
                (candidate for candidate in valid.values()
                 if str(candidate["endpoint"]) not in used_endpoints),
                None,
            )
            if proxy is None:
                raise ProxyError(
                    f"chỉ có {len(valid)} proxy hoạt động, cần {len(groups)} proxy"
                )
        endpoint = str(proxy["endpoint"])
        if endpoint in used_endpoints:
            raise ProxyError(f"proxy {endpoint} đang bị gán cho nhiều group")
        used_endpoints.add(endpoint)
        assignments[group] = proxy

    for group, group_workers in groups.items():
        value = str(assignments[group]["value"])
        for worker in group_workers:
            path = worker / ".proxy"
            path.write_text(value + "\n", encoding="utf-8")
            path.chmod(0o600)
    marker.write_text(str(args.group_size) + "\n", encoding="utf-8")
    marker.chmod(0o600)

    print(f"Proxy: {len(assignments)} group, {len(valid)}/{len(configured)} hoạt động")
    for group, proxy in sorted(assignments.items()):
        print(f"  group {group + 1}: {masked(proxy)}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--workers-dir", required=True)
    parser.add_argument("--proxy-file", required=True)
    parser.add_argument("--server", default="tk")
    parser.add_argument("--group-size", type=int, required=True)
    parser.add_argument("--target-host", default=DEFAULT_TARGET_HOST)
    parser.add_argument("--target-port", type=int, default=DEFAULT_TARGET_PORT)
    args = parser.parse_args()
    if args.group_size < 0:
        parser.error("--group-size phải >= 0")
    try:
        return assign(args)
    except ProxyError as exc:
        print(f"Proxy preflight thất bại: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
