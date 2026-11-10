"""Web monitor (chỉ đọc) cho worker uplevel/ và uplevel35/.

Không điều khiển worker. Mỗi request đọc:
  - <runtime>/workers/worker-XX/home/status.properties  (UplevelStatus.java ghi mỗi 5 giây)
  - bot.pid / loop.pid + /proc  (tiến trình còn sống, CPU, RAM)
  - stdout.log                  (mtime; fallback dòng STATE khi chưa có status.properties)
  - account.csv + home/uplevel-progress.properties (tài khoản thứ mấy / tổng)

Chạy: python3 -m uplevel_monitor.server   (chỉ dùng thư viện chuẩn)
Biến môi trường: UPLEVEL_MONITOR_HOST (mặc định 127.0.0.1), UPLEVEL_MONITOR_PORT (8090),
UPLEVEL_WORKERS_DIR, UPLEVEL35_WORKERS_DIR.
"""
from __future__ import annotations

import json
import os
import re
import threading
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import parse_qs, urlparse

REPO_DIR = Path(__file__).resolve().parents[1]
STATIC_DIR = Path(__file__).resolve().with_name("static")
WORKER_RE = re.compile(r"^worker-\d{1,4}$")
CLK_TCK = os.sysconf("SC_CLK_TCK") if hasattr(os, "sysconf") else 100
NUM_CPUS = os.cpu_count() or 1

RUNTIMES: dict[str, Path] = {
    "uplevel": Path(os.environ.get("UPLEVEL_WORKERS_DIR", REPO_DIR / "uplevel" / "workers")),
    "uplevel35": Path(os.environ.get("UPLEVEL35_WORKERS_DIR", REPO_DIR / "uplevel35" / "workers")),
}
# Tiền tố dòng STATE dùng khi worker chạy bản build cũ chưa ghi status.properties.
STATE_PREFIX = {"uplevel": "UPLEVEL STATE ", "uplevel35": "UPLEVEL35 STATE "}

_cpu_lock = threading.Lock()
_cpu_samples: dict[int, tuple[float, float]] = {}  # pid -> (cpu_seconds, wall_time)


# ---------------------------------------------------------------- /proc helpers

def _read(path: Path) -> str | None:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return None


def _pid_from_file(path: Path, worker_dir: Path) -> int | None:
    """PID trong file còn sống và đúng là tiến trình của worker này (cmdline chứa <worker>/home)."""
    raw = (_read(path) or "").strip()
    if not raw.isdigit():
        return None
    pid = int(raw)
    try:
        cmdline = Path(f"/proc/{pid}/cmdline").read_bytes().replace(b"\0", b" ").decode(errors="replace")
    except OSError:
        return None
    return pid if f"{worker_dir}/home" in cmdline else None


def _proc_cpu_seconds(pid: int) -> tuple[float, float] | None:
    """(utime+stime giây, thời điểm process start tính bằng giây từ boot)."""
    stat = _read(Path(f"/proc/{pid}/stat"))
    if not stat:
        return None
    # comm có thể chứa dấu cách -> tách sau dấu ')' cuối.
    fields = stat[stat.rfind(")") + 2:].split()
    try:
        cpu = (int(fields[11]) + int(fields[12])) / CLK_TCK
        start = int(fields[19]) / CLK_TCK
    except (IndexError, ValueError):
        return None
    return cpu, start


def _uptime() -> float:
    try:
        return float((_read(Path("/proc/uptime")) or "0").split()[0])
    except ValueError:
        return 0.0


def cpu_percent(pid: int) -> float | None:
    """% của MỘT core giữa hai lần gọi; lần đầu lấy trung bình từ lúc process khởi động."""
    sample = _proc_cpu_seconds(pid)
    if sample is None:
        return None
    cpu, start = sample
    now = time.monotonic()
    with _cpu_lock:
        prev = _cpu_samples.get(pid)
        _cpu_samples[pid] = (cpu, now)
    if prev and now - prev[1] >= 0.5:
        return max(0.0, (cpu - prev[0]) / (now - prev[1]) * 100)
    elapsed = _uptime() - start
    return cpu / elapsed * 100 if elapsed > 0 else None


def rss_mb(pid: int) -> float | None:
    for line in (_read(Path(f"/proc/{pid}/status")) or "").splitlines():
        if line.startswith("VmRSS:"):
            try:
                return int(line.split()[1]) / 1024
            except (IndexError, ValueError):
                return None
    return None


if os.name == "nt":  # Windows không có /proc: thay 3 hàm trên bằng bản dùng psutil / Win32 API.
    from uplevel_monitor.winproc import cpu_percent, pid_from_file as _pid_from_file, rss_mb  # noqa: E402,F811


# ---------------------------------------------------------------- file parsing

def parse_properties(text: str | None) -> dict[str, str]:
    out: dict[str, str] = {}
    for line in (text or "").splitlines():
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        out[key.strip()] = value.strip()
    return out


def tail_bytes(path: Path, limit: int) -> bytes:
    try:
        with path.open("rb") as fh:
            fh.seek(0, os.SEEK_END)
            size = fh.tell()
            fh.seek(max(0, size - limit))
            return fh.read()
    except OSError:
        return b""


def last_state_line(log: Path, prefix: str) -> dict[str, str]:
    """Fallback: tách key=value đơn giản của dòng STATE cuối cùng trong 256 KB cuối log."""
    text = tail_bytes(log, 256 * 1024).decode("utf-8", errors="replace")
    idx = text.rfind(prefix)
    if idx < 0:
        return {}
    line = text[idx + len(prefix):].split("\n", 1)[0]
    out: dict[str, str] = {}
    for key, value in re.findall(r"(\w+)=([^\s\[\],]+)", line):
        out.setdefault(key, value)  # giữ giá trị đầu (hp= của nhân vật, không phải của quái)
    exp = out.get("exp", "")
    if "/" in exp:
        out["exp"], out["expNeed"] = exp.split("/", 1)
    return out


def account_names(csv_path: Path) -> list[str] | None:
    """Danh sách username trong account.csv của worker (không trả password)."""
    text = _read(csv_path)
    if text is None:
        return None
    names = [r.split(",", 1)[0].strip() for r in text.splitlines() if r.strip() and "," in r]
    if names and names[0].lower() in {"username", "account"}:
        names = names[1:]
    return names


def _to_int(value: str | None) -> int | None:
    try:
        return int(value) if value not in (None, "", "-") else None
    except ValueError:
        return None


# ---------------------------------------------------------------- worker snapshot

def worker_snapshot(runtime: str, worker_dir: Path) -> dict[str, object]:
    home = worker_dir / "home"
    log = worker_dir / "stdout.log"
    java_pid = _pid_from_file(worker_dir / "bot.pid", worker_dir)
    loop_pid = _pid_from_file(worker_dir / "loop.pid", worker_dir)
    done = (home / "worker.done").exists()

    if java_pid:
        state = "running"
    elif loop_pid:
        state = "switching"  # worker-loop.sh đang chuyển sang JVM của nhân vật kế tiếp
    elif done:
        state = "done"
    else:
        state = "stopped"

    status = parse_properties(_read(home / "status.properties"))
    source = "status" if status else "log"
    if not status:
        status = last_state_line(log, STATE_PREFIX[runtime])
    # status.properties của JVM trước (handoff mỗi nhân vật) -> JVM mới còn đang đăng nhập.
    stale_pid = bool(java_pid and status.get("pid") and status.get("pid") != str(java_pid))

    progress = parse_properties(_read(home / "uplevel-progress.properties"))
    acc_index = _to_int(progress.get("accountIndex"))
    accounts = account_names(worker_dir / "account.csv")
    account = status.get("account") or progress.get("account") or "-"
    if acc_index is None and accounts and account in accounts:
        acc_index = accounts.index(account)

    try:
        log_mtime = log.stat().st_mtime
    except OSError:
        log_mtime = None

    cpu = cpu_percent(java_pid) if java_pid else None
    rss = rss_mb(java_pid) if java_pid else None
    updated_ms = _to_int(status.get("updatedAt"))
    progress_ms = _to_int(status.get("progressAt"))
    return {
        "runtime": runtime,
        "worker": worker_dir.name,
        "state": state,
        "pid": java_pid,
        "cpu": round(cpu, 1) if cpu is not None else None,
        "rss_mb": round(rss, 1) if rss is not None else None,
        "source": source,
        "phase": "login" if stale_pid else status.get("phase", "-"),
        "account": account,
        "account_index": acc_index + 1 if acc_index is not None else None,
        "account_total": len(accounts) if accounts is not None else None,
        "name": status.get("name", "-"),
        "level": _to_int(status.get("level")),
        "exp": _to_int(status.get("exp")),
        "exp_need": _to_int(status.get("expNeed")),
        "bag_free": _to_int(status.get("bagFree")),
        "bag_total": _to_int(status.get("bagTotal")),
        "luong": _to_int(status.get("luong")),
        "xu": _to_int(status.get("xu")),
        "yen": _to_int(status.get("yen")),
        "map": status.get("map", "-"),
        "map_name": status.get("mapName", ""),
        "task": status.get("task", "-"),
        "hp": status.get("hp", "-"),
        "status_ts": status.get("ts"),
        "updated_at": updated_ms / 1000 if updated_ms else None,
        "progress_at": progress_ms / 1000 if progress_ms else None,
        "log_mtime": log_mtime,
    }


def collect() -> dict[str, object]:
    now = time.time()
    workers = []
    for runtime, workers_dir in RUNTIMES.items():
        if not workers_dir.is_dir():
            continue
        for worker_dir in sorted(workers_dir.iterdir()):
            if worker_dir.is_dir() and WORKER_RE.match(worker_dir.name):
                workers.append(worker_snapshot(runtime, worker_dir))
    # Dọn mẫu CPU của PID đã chết.
    with _cpu_lock:
        for pid in [p for p in _cpu_samples if not Path(f"/proc/{p}").exists()]:
            _cpu_samples.pop(pid, None)
    return {"now": now, "cpus": NUM_CPUS, "workers": workers}


def read_log(runtime: str, worker: str, kind: str, lines: int) -> str:
    if runtime not in RUNTIMES or not WORKER_RE.match(worker):
        raise ValueError("runtime/worker không hợp lệ")
    name = "java-errors.log" if kind == "error" else "stdout.log"
    data = tail_bytes(RUNTIMES[runtime] / worker / name, 512 * 1024).decode("utf-8", errors="replace")
    return "\n".join(data.splitlines()[-max(1, min(lines, 2000)):])


# ---------------------------------------------------------------- HTTP

class Handler(BaseHTTPRequestHandler):
    server_version = "UplevelMonitor/1.0"

    def log_message(self, fmt: str, *args: object) -> None:  # yên lặng, chỉ ghi lỗi
        pass

    def _send(self, code: int, body: bytes, ctype: str) -> None:
        self.send_response(code)
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(body)

    def _json(self, data: object, code: int = 200) -> None:
        self._send(code, json.dumps(data, ensure_ascii=False).encode(), "application/json; charset=utf-8")

    def do_GET(self) -> None:  # noqa: N802
        url = urlparse(self.path)
        query = {k: v[-1] for k, v in parse_qs(url.query).items()}
        try:
            if url.path in ("/", "/index.html"):
                self._send(200, (STATIC_DIR / "index.html").read_bytes(), "text/html; charset=utf-8")
            elif url.path == "/api/workers":
                self._json(collect())
            elif url.path == "/api/log":
                text = read_log(query.get("runtime", ""), query.get("worker", ""),
                                query.get("kind", "stdout"), int(query.get("lines", "300")))
                self._json({"content": text})
            elif url.path == "/favicon.ico":
                self._send(204, b"", "image/x-icon")
            elif url.path == "/health":
                self._json({"ok": True})
            else:
                self._json({"detail": "not found"}, 404)
        except ValueError as exc:
            self._json({"detail": str(exc)}, 400)


def main() -> None:
    host = os.environ.get("UPLEVEL_MONITOR_HOST", "0.0.0.0")
    port = int(os.environ.get("UPLEVEL_MONITOR_PORT", "8190"))
    httpd = ThreadingHTTPServer((host, port), Handler)
    print(f"Uplevel monitor: http://{host}:{port}  (workers: "
          + ", ".join(f"{k}={v}" for k, v in RUNTIMES.items()) + ")", flush=True)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass


if __name__ == "__main__":
    main()
