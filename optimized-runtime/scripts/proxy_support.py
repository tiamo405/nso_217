#!/usr/bin/env python3
"""Kiểm tra proxy SOCKS5 và chia đều proxy cho worker NVHN (giống uplevel).

- --group-size 0: tắt proxy, xoá toàn bộ gán proxy.
- --group-size > 0: bật proxy. Giá trị cụ thể không còn ý nghĩa (giữ để tương
  thích env NSO_PROXY_WORKERS_PER_PROXY cũ). Có P proxy hoạt động và W worker →
  mạng của máy được tính như thêm 1 phần (phần cuối): W chia đều cho P+1 phần,
  mỗi phần W//(P+1) worker liên tiếp, W%(P+1) phần đầu nhận thêm 1.
  VD 50 worker / 2 proxy: worker-01..17 = proxy 1, worker-18..34 = proxy 2,
  worker-35..50 = mạng máy (.proxy ghi "direct").
  NSO_PROXY_USE_LOCAL=0 → không dùng mạng máy, chỉ chia cho P proxy như cũ.
"""

from __future__ import annotations

import argparse
import os
import socket
import sys
from pathlib import Path


SOCKS_VERSION = 5
DEFAULT_TARGET_HOST = "180.93.114.122"
DEFAULT_TARGET_PORT = 14444
DEFAULT_TIMEOUT = 8.0
# Giá trị trong <worker>/.proxy: worker này chạy bằng mạng của máy (không SOCKS5).
DIRECT = "direct"
LOCAL_SLOT: dict[str, object] = {"value": DIRECT}


def use_local_default() -> bool:
    """Mặc định chia thêm worker cho mạng máy khi có proxy; NSO_PROXY_USE_LOCAL=0 để tắt."""
    return os.environ.get("NSO_PROXY_USE_LOCAL", "1").strip().lower() not in ("0", "false", "no")


def is_direct(value: str | None) -> bool:
    return (value or "").strip().lower() == DIRECT


def read_assignment(worker_dir: Path) -> str:
    try:
        return (worker_dir / ".proxy").read_text(encoding="utf-8").strip()
    except OSError:
        return ""


def has_real_proxy(worker_dir: Path) -> bool:
    value = read_assignment(worker_dir)
    return bool(value) and not is_direct(value)


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


def _process_command(pid: int) -> str | None:
    """Command line của PID trên Linux (/proc) hoặc Windows (psutil)."""
    try:
        return Path(f"/proc/{pid}/cmdline").read_bytes().replace(b"\0", b" ").decode(
            "utf-8", errors="replace"
        )
    except OSError:
        pass
    try:
        import psutil  # type: ignore

        return " ".join(psutil.Process(pid).cmdline())
    except Exception:
        return None


def worker_is_running(worker_dir: Path) -> bool:
    pid_file = worker_dir / "bot.pid"
    try:
        pid = int(pid_file.read_text(encoding="utf-8").strip())
    except (OSError, ValueError):
        return False
    command = _process_command(pid)
    if not command:
        return False
    norm = lambda value: str(value).replace("\\", "/").casefold()  # noqa: E731
    return "optimizedmain" in norm(command) and norm(worker_dir) in norm(command)


def active_workers(workers_dir: Path) -> list[Path]:
    workers = []
    for worker_dir in sorted(workers_dir.glob("worker-*")):
        if not worker_dir.is_dir():
            continue
        if (worker_dir / ".paused").is_file():
            continue
        if (worker_dir / "home" / "worker.done").is_file() or (worker_dir / "worker.done").is_file():
            continue
        if (worker_dir / "home" / "worker.first-pass.done").is_file():
            continue
        workers.append(worker_dir)
    return workers


def clear_assignments(workers_dir: Path) -> None:
    for worker_dir in workers_dir.glob("worker-*"):
        (worker_dir / ".proxy").unlink(missing_ok=True)
    (workers_dir / ".proxy-group-size").unlink(missing_ok=True)


def all_workers(workers_dir: Path) -> list[Path]:
    """Mọi worker (kể cả đã xong/tạm dừng) để phép chia luôn ổn định theo số thứ tự."""
    return sorted(
        (path for path in workers_dir.glob("worker-*") if path.is_dir()), key=worker_number
    )


def even_plan(workers: list[Path], proxies: list[dict[str, object]]) -> dict[str, dict[str, object]]:
    base, extra = divmod(len(workers), len(proxies))
    order: list[dict[str, object]] = []
    for index, proxy in enumerate(proxies):
        order += [proxy] * (base + (1 if index < extra else 0))
    return {worker.name: order[index] for index, worker in enumerate(workers)}


def assign(args: argparse.Namespace) -> int:
    workers_dir = Path(args.workers_dir).resolve()
    proxy_file = Path(args.proxy_file).resolve()
    workers = all_workers(workers_dir)
    pending = {worker.name for worker in active_workers(workers_dir)}
    running = {worker.name for worker in workers if worker_is_running(worker)}

    if args.group_size == 0:
        if any(has_real_proxy(workers_dir / name) for name in running):
            raise ProxyError("hãy Stop toàn bộ worker trước khi tắt proxy")
        clear_assignments(workers_dir)
        print("Proxy: tắt, worker chạy mạng của máy")
        return 0

    if not pending:
        print("Proxy: không có worker cần chạy")
        return 0

    configured = load_proxies(proxy_file) if proxy_file.is_file() else []
    if not configured:
        # Giống uplevel: file proxy rỗng / không có → chạy mạng của máy.
        if any(has_real_proxy(workers_dir / name) for name in running):
            raise ProxyError("hãy Stop toàn bộ worker trước khi bỏ proxy")
        clear_assignments(workers_dir)
        marker = workers_dir / ".proxy-group-size"
        marker.write_text("even:0\n", encoding="utf-8")
        print(f"Proxy: {proxy_file.name} trống → {len(workers)} worker chạy mạng của máy")
        return 0
    valid: list[dict[str, object]] = []
    for proxy in configured:
        try:
            probe(proxy, args.target_host, args.target_port)
        except (OSError, ProxyError) as exc:
            print(f"Proxy lỗi, bỏ qua {masked(proxy)}: {exc}", file=sys.stderr)
            continue
        valid.append(proxy)
    if not valid:
        raise ProxyError(f"không có proxy nào hoạt động trong {proxy_file.name}")

    use_local = getattr(args, "use_local", None)
    if use_local is None:
        use_local = use_local_default()
    slots = valid + ([LOCAL_SLOT] if use_local else [])
    plan = even_plan(workers, slots)
    for worker in workers:
        path = worker / ".proxy"
        wanted = str(plan[worker.name]["value"])
        if worker.name in running:
            # Không đổi proxy của worker đang chạy; nếu lệch thì cần Stop/Start toàn bộ.
            current = path.read_text(encoding="utf-8").strip() if path.is_file() else ""
            if current and current != wanted:
                print(f"  {worker.name} đang chạy với proxy cũ; Stop rồi Start để chia lại",
                      file=sys.stderr)
            continue
        path.write_text(wanted + "\n", encoding="utf-8")
        path.chmod(0o600)
    marker = workers_dir / ".proxy-group-size"
    marker.write_text(marker_value(len(valid), use_local) + "\n", encoding="utf-8")
    marker.chmod(0o600)

    used = [proxy for proxy in valid if proxy in plan.values()]
    local_note = " + mạng máy" if use_local else ""
    print(f"Proxy: {len(workers)} worker chia đều cho {len(used)} proxy{local_note} "
          f"({len(valid)}/{len(configured)} proxy hoạt động)")
    for number, proxy in enumerate(slots, 1):
        label = "mạng máy" if proxy is LOCAL_SLOT else f"proxy {number} {masked(proxy)}"
        names = [name for name, value in plan.items() if value is proxy]
        if not names:
            print(f"  {label}: không dùng (ít worker hơn số phần chia)")
            continue
        span = names[0] if len(names) == 1 else f"{names[0]}..{names[-1]}"
        print(f"  {label}: {span} ({len(names)} worker)")
    return 0


def marker_value(proxy_count: int, use_local: bool) -> str:
    """Nội dung workers/.proxy-group-size; start-workers so sánh để biết cần chia lại."""
    return f"even:{proxy_count}+local" if use_local and proxy_count > 0 else f"even:{proxy_count}"


def prepare(
    workers_dir: Path,
    proxy_file: Path,
    enabled: bool,
    target_host: str = DEFAULT_TARGET_HOST,
    target_port: int = DEFAULT_TARGET_PORT,
) -> None:
    """Dùng từ Python (manager Windows): chia proxy trước khi start worker."""
    assign(argparse.Namespace(
        workers_dir=str(workers_dir), proxy_file=str(proxy_file),
        group_size=1 if enabled else 0, target_host=target_host, target_port=target_port,
    ))


def launch_env(worker_dir: Path, enabled: bool) -> dict[str, str]:
    """Env proxy cho JVM của một worker (Connector.java đọc NSO_SOCKS5_PROXY).

    Trả về {} khi tắt proxy, file proxy rỗng (marker even:0) hoặc worker được chia
    cho mạng máy (.proxy = "direct") → mạng của máy.
    """
    if not enabled:
        return {}
    value = read_assignment(worker_dir)
    if is_direct(value):
        return {}
    if value:
        return {"NSO_PROXY_REQUIRED": "1", "NSO_SOCKS5_PROXY": value}
    marker = worker_dir.parent / ".proxy-group-size"
    if marker.is_file() and marker.read_text(encoding="utf-8").strip() == "even:0":
        return {}
    raise ProxyError(f"{worker_dir.name} thiếu proxy assignment; không khởi động")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--workers-dir", required=True)
    parser.add_argument("--proxy-file", required=True)
    # Giữ để tương thích lệnh cũ; chỉ còn 1 server nên giá trị bị bỏ qua.
    parser.add_argument("--server", default="")
    parser.add_argument("--group-size", type=int, required=True)
    parser.add_argument("--target-host", default=DEFAULT_TARGET_HOST)
    parser.add_argument("--target-port", type=int, default=DEFAULT_TARGET_PORT)
    parser.add_argument("--no-local", dest="use_local", action="store_false", default=None,
                        help="không chia worker cho mạng máy (giống NSO_PROXY_USE_LOCAL=0)")
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
