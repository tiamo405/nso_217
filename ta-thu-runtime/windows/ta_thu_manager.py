"""Native cross-platform worker manager for the Tà Thú runtime.

This is the Windows counterpart of ta-thu-runtime/scripts/*.sh.  It keeps the
same marker and command contract, but uses Python/Java/taskkill instead of
assuming a POSIX shell is installed on Windows Server.
"""

from __future__ import annotations

import argparse
import csv
import json
import os
import shutil
import signal
import subprocess
import sys
import time
from collections import deque
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Iterable


SCRIPT_DIR = Path(__file__).resolve().parent
TA_THU_DIR = SCRIPT_DIR.parent
REPO_DIR = TA_THU_DIR.parent
WORKERS_DIR = Path(
    os.environ.get("TA_THU_WORKERS_DIR", TA_THU_DIR / "workers")
).resolve()
CLASSES_DIR = Path(
    os.environ.get("TA_THU_CLASSES_DIR", TA_THU_DIR / "build" / "classes")
).resolve()
ACCOUNT_CSV = Path(
    os.environ.get("TA_THU_ACCOUNT_CSV", REPO_DIR / "account.csv")
).resolve()
STATE_DIR = Path(
    os.environ.get("TA_THU_STATE_DIR", TA_THU_DIR / "ta-thu-state")
).resolve()
JAVA_BIN = os.environ.get("JAVA_BIN", "java")
JAVA_XMS = os.environ.get("JAVA_XMS", "8m")
JAVA_XMX = os.environ.get("JAVA_XMX", "48m")
START_DELAY = 10
CHECK_INTERVAL = 20
STALE_LOG_SECONDS = int(os.environ.get("STALE_LOG_SECONDS", "300"))
REPEATED_STATUS_LIMIT = int(os.environ.get("REPEATED_STATUS_LIMIT", "5"))
PID_FILE = WORKERS_DIR / "supervisor.pid"


def _norm(value: str | Path) -> str:
    return str(value).replace("\\", "/").casefold()


def _creationflags() -> int:
    if os.name == "nt":
        return subprocess.CREATE_NO_WINDOW | subprocess.DETACHED_PROCESS
    return 0


def _process_cmdline(pid: int) -> list[str]:
    try:
        import psutil  # type: ignore

        return psutil.Process(pid).cmdline()
    except ImportError:
        if os.name != "nt":
            try:
                return [part.decode("utf-8", errors="replace") for part in Path(f"/proc/{pid}/cmdline").read_bytes().split(b"\0") if part]
            except OSError:
                return []
        return []
    except Exception:
        return []


def _process_ids() -> Iterable[int]:
    try:
        import psutil  # type: ignore

        return [int(proc.pid) for proc in psutil.process_iter()]
    except ImportError:
        return []
    except Exception:
        return []


def is_pid_running(pid: int) -> bool:
    if pid <= 0:
        return False
    try:
        import psutil  # type: ignore

        process = psutil.Process(pid)
        return process.is_running() and process.status() != psutil.STATUS_ZOMBIE
    except ImportError:
        try:
            os.kill(pid, 0)
            return True
        except OSError:
            return False
    except Exception:
        return False


def kill_pid(pid: int) -> bool:
    if not is_pid_running(pid):
        return False
    if os.name == "nt":
        return subprocess.run(
            ["taskkill", "/F", "/T", "/PID", str(pid)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        ).returncode == 0
    try:
        os.kill(pid, signal.SIGTERM)
    except OSError:
        return False
    deadline = time.monotonic() + 5
    while time.monotonic() < deadline and is_pid_running(pid):
        time.sleep(0.1)
    if is_pid_running(pid):
        try:
            os.kill(pid, signal.SIGKILL)
        except OSError:
            pass
    return not is_pid_running(pid)


def is_expected_worker_process(pid: int, worker_dir: Path) -> bool:
    command = " ".join(_process_cmdline(pid))
    return (
        "HeadlessMain" in command
        and "-Dnso.runtime=ta-thu" in command
        and _norm(worker_dir) in _norm(command)
    )


def is_expected_supervisor_process(pid: int) -> bool:
    command = _norm(" ".join(_process_cmdline(pid)))
    return _norm(SCRIPT_DIR / "ta_thu_manager.py") in command and " supervise" in f" {command}"


def read_pid(path: Path) -> int | None:
    try:
        value = path.read_text(encoding="utf-8").strip()
        return int(value) if value.isdigit() else None
    except (OSError, ValueError):
        return None


def worker_dirs() -> list[Path]:
    return sorted(path for path in WORKERS_DIR.glob("worker-*") if path.is_dir())


def parse_workers(values: list[str]) -> set[int]:
    result: set[int] = set()
    for value in values:
        value = value.removeprefix("worker-")
        if not value.isdigit() or int(value) <= 0:
            raise RuntimeError(f"Worker không hợp lệ: {value}")
        result.add(int(value))
    return result


def selected_worker_dirs(values: list[str]) -> list[Path]:
    wanted = parse_workers(values)
    paths = worker_dirs()
    if not wanted:
        return paths
    return [path for path in paths if int(path.name.removeprefix("worker-")) in wanted]


def run_build() -> None:
    builder = SCRIPT_DIR / "build_ta_thu.py"
    command = [sys.executable, str(builder)]
    result = subprocess.run(command, cwd=REPO_DIR, env=os.environ.copy())
    if result.returncode != 0:
        raise RuntimeError(f"Build Tà Thú thất bại với exit code {result.returncode}")


def load_accounts() -> tuple[list[str], list[list[str]]]:
    if not ACCOUNT_CSV.is_file():
        raise RuntimeError(f"Không tìm thấy account CSV: {ACCOUNT_CSV}")
    with ACCOUNT_CSV.open("r", encoding="utf-8-sig", newline="") as stream:
        rows = list(csv.reader(stream))
    if not rows or len(rows[0]) < 2:
        raise RuntimeError("account.csv không có header hợp lệ")
    accounts = [row for row in rows[1:] if any(cell.strip() for cell in row)]
    if not accounts:
        raise RuntimeError("account.csv không có tài khoản")
    if any(len(row) < 2 or not row[0].strip() or not row[1].strip() for row in accounts):
        raise RuntimeError("Mỗi dòng account phải gồm username và password")
    return rows[0], accounts


def build_workers(count: int) -> None:
    if count < 1:
        raise RuntimeError("Số worker phải là số nguyên dương")
    header, accounts = load_accounts()
    if count > len(accounts):
        raise RuntimeError(f"Có {len(accounts)} tài khoản nhưng yêu cầu {count} worker")

    for worker in worker_dirs():
        pid = read_pid(worker / "bot.pid")
        if pid is not None and is_expected_worker_process(pid, worker):
            raise RuntimeError(f"{worker.name} vẫn đang chạy. Hãy stop trước khi build lại")

    run_build()
    WORKERS_DIR.parent.mkdir(parents=True, exist_ok=True)
    staging = Path(__import__("tempfile").mkdtemp(prefix=".workers-build.", dir=WORKERS_DIR.parent))
    backup = WORKERS_DIR.parent / f".workers-old.{os.getpid()}"
    try:
        base, extra = divmod(len(accounts), count)
        offset = 0
        for index in range(1, count + 1):
            amount = base + (1 if index <= extra else 0)
            worker = staging / f"worker-{index:02d}"
            (worker / "home").mkdir(parents=True)
            with (worker / "account.csv").open("w", encoding="utf-8", newline="") as stream:
                writer = csv.writer(stream, lineterminator="\n")
                writer.writerow(header)
                writer.writerows(accounts[offset:offset + amount])
            offset += amount
            print(f"Đã tạo {worker.name}: {amount} tài khoản", flush=True)

        if backup.exists():
            shutil.rmtree(backup)
        if WORKERS_DIR.exists():
            shutil.move(str(WORKERS_DIR), str(backup))
        shutil.move(str(staging), str(WORKERS_DIR))
        staging = Path()
        if backup.exists():
            shutil.rmtree(backup)
        print(f"Hoàn tất: {len(accounts)} tài khoản / {count} Tà Thú worker", flush=True)
    finally:
        if staging != Path() and staging.exists():
            shutil.rmtree(staging, ignore_errors=True)
        if backup.exists():
            shutil.rmtree(backup, ignore_errors=True)


def java_command(worker: Path) -> list[str]:
    classpath = os.pathsep.join((str(worker), str(CLASSES_DIR)))
    server = os.environ.get("NSO_SERVER", "tk")
    return [
        JAVA_BIN,
        f"-Xms{JAVA_XMS}",
        f"-Xmx{JAVA_XMX}",
        "-XX:+UseSerialGC",
        "-XX:MinHeapFreeRatio=5",
        "-XX:MaxHeapFreeRatio=10",
        "-Djava.awt.headless=true",
        "-Dmicroedition.platform=NSOHeadless",
        "-Dnso.runtime=ta-thu",
        f"-Dnso.server={server}",
        f"-Dta.thu.stage={os.environ.get('TA_THU_STAGE', 'full')}",
        f"-Dta.thu.state.dir={STATE_DIR}",
        f"-Duser.home={worker / 'home'}",
        "-cp",
        classpath,
        "HeadlessMain",
    ]


def start_workers(values: list[str], delay: int) -> int:
    if not (CLASSES_DIR / "HeadlessMain.class").is_file():
        raise RuntimeError("Chưa có Tà Thú classes. Hãy build trước")
    paths = selected_worker_dirs(values)
    if not paths:
        raise RuntimeError("Chưa có Tà Thú worker nào. Hãy build trước")
    started = running = completed = failed = 0
    for worker in paths:
        if (worker / ".paused").is_file():
            print(f"{worker.name} đang tạm dừng, bỏ qua")
            continue
        if (worker / "worker.done").is_file() or (worker / "home" / "worker.done").is_file():
            print(f"{worker.name} đã hoàn tất, bỏ qua")
            completed += 1
            continue
        pid_file = worker / "bot.pid"
        pid = read_pid(pid_file)
        if pid is not None and is_expected_worker_process(pid, worker):
            print(f"{worker.name} đang chạy (PID {pid})")
            running += 1
            continue
        pid_file.unlink(missing_ok=True)
        (worker / "home").mkdir(parents=True, exist_ok=True)
        now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        with (worker / "stdout.log").open("a", encoding="utf-8") as stream:
            stream.write(f"\n===== START TA THU {now} PASS 1/1 =====\n")
        with (worker / "java-errors.log").open("a", encoding="utf-8") as stream:
            stream.write(f"\n===== START TA THU {now} PASS 1/1 =====\n")
        try:
            with (worker / "stdout.log").open("a", encoding="utf-8") as output, (worker / "java-errors.log").open("a", encoding="utf-8") as errors:
                process = subprocess.Popen(
                    java_command(worker),
                    cwd=str(worker),
                    env=os.environ.copy(),
                    stdout=output,
                    stderr=errors,
                    creationflags=_creationflags(),
                )
            pid_file.write_text(str(process.pid), encoding="utf-8")
            time.sleep(0.3)
            if is_expected_worker_process(process.pid, worker):
                print(f"{worker.name} đã khởi động (PID {process.pid})")
                started += 1
            else:
                pid_file.unlink(missing_ok=True)
                print(f"{worker.name} khởi động thất bại", file=sys.stderr)
                failed += 1
        except OSError as exc:
            print(f"{worker.name} lỗi khởi động: {exc}", file=sys.stderr)
            failed += 1
        if delay:
            time.sleep(delay)
    print(f"Kết quả: mới={started}, đang chạy={running}, done={completed}, lỗi={failed}", flush=True)
    return 0 if failed == 0 else 1


def stop_workers(values: list[str]) -> int:
    paths = selected_worker_dirs(values)
    if not values:
        supervisor = read_pid(PID_FILE)
        if supervisor is not None and is_expected_supervisor_process(supervisor):
            kill_pid(supervisor)
        PID_FILE.unlink(missing_ok=True)
        # Catch a supervisor that lost its PID file.
        for candidate in _process_ids():
            if is_expected_supervisor_process(candidate):
                kill_pid(candidate)
    stopped = 0
    for worker in paths:
        pid_file = worker / "bot.pid"
        pid = read_pid(pid_file)
        if pid is not None and is_expected_worker_process(pid, worker):
            if kill_pid(pid):
                stopped += 1
                print(f"Đã dừng {worker.name} (PID {pid})")
        pid_file.unlink(missing_ok=True)
    # Catch the small window between Java Popen and bot.pid creation.
    for candidate in _process_ids():
        for worker in paths:
            if is_expected_worker_process(candidate, worker):
                if kill_pid(candidate):
                    stopped += 1
                break
    print(f"Đã dừng {stopped} Tà Thú worker", flush=True)
    return 0


def _log_time(path: Path) -> tuple[str | None, int | None]:
    try:
        modified = path.stat().st_mtime
    except OSError:
        return None, None
    return (
        datetime.fromtimestamp(modified, timezone.utc).astimezone().isoformat(timespec="seconds"),
        max(0, int(time.time() - modified)),
    )


def _last_log(path: Path, marker: str) -> str | None:
    try:
        with path.open("r", encoding="utf-8", errors="replace") as stream:
            lines = deque(stream, maxlen=200)
    except OSError:
        return None
    for line in reversed(lines):
        if marker in line:
            return line.strip()
    return None


def status_dict() -> dict[str, Any]:
    workers: list[dict[str, Any]] = []
    for worker in worker_dirs():
        pid_file = worker / "bot.pid"
        pid = read_pid(pid_file)
        if pid is not None and not is_expected_worker_process(pid, worker):
            pid = None
        paused = (worker / ".paused").is_file()
        done = (worker / "worker.done").is_file() or (worker / "home" / "worker.done").is_file()
        state = "PAUSED" if paused else "DONE" if done else "RUNNING" if pid is not None else "STOPPED"
        log_at, log_age = _log_time(worker / "stdout.log")
        try:
            with (worker / "account.csv").open("r", encoding="utf-8-sig") as stream:
                accounts = max(0, sum(1 for _ in stream) - 1)
        except OSError:
            accounts = 0
        cpu = rss = elapsed = None
        if pid is not None:
            try:
                import psutil  # type: ignore
                process = psutil.Process(pid)
                cpu = round(process.cpu_percent(interval=None), 1)
                rss = round(process.memory_info().rss / 1024 / 1024, 1)
                elapsed = time.strftime("%H:%M:%S", time.gmtime(max(0, time.time() - process.create_time())))
            except Exception:
                pass
        workers.append({
            "name": worker.name,
            "pid": pid,
            "state": state,
            "paused": paused,
            "run_pass": 1,
            "run_pass_total": 1,
            "cpu_percent": cpu,
            "rss_mb": rss,
            "elapsed": elapsed,
            "accounts": accounts,
            "last_auto_log": _last_log(worker / "stdout.log", "AUTO TA THU"),
            "last_log_at": log_at,
            "last_log_age_seconds": log_age,
            "stdout_log": str(worker / "stdout.log"),
            "error_log": str(worker / "java-errors.log"),
        })
    totals = {
        "running": sum(item["state"] == "RUNNING" for item in workers),
        "stopped": sum(item["state"] == "STOPPED" for item in workers),
        "paused": sum(item["state"] == "PAUSED" for item in workers),
        "done": sum(item["state"] == "DONE" for item in workers),
        "total": len(workers),
    }
    return {"workers_dir": str(WORKERS_DIR), "workers": workers, "totals": totals}


def stale_reason(worker: Path) -> str | None:
    if STALE_LOG_SECONDS > 0:
        _at, age = _log_time(worker / "stdout.log")
        if age is not None and age >= STALE_LOG_SECONDS:
            return f"stdout.log im lặng {age}s"
    if REPEATED_STATUS_LIMIT > 0:
        try:
            with (worker / "stdout.log").open("r", encoding="utf-8", errors="replace") as stream:
                lines = [line.strip() for line in stream if "AUTO TA THU" in line][-20:]
        except OSError:
            lines = []
        if len(lines) >= REPEATED_STATUS_LIMIT and len(set(lines[-REPEATED_STATUS_LIMIT:])) == 1:
            return f"AUTO TA THU lặp {REPEATED_STATUS_LIMIT} lần: {lines[-1]}"
    return None


def restart_worker(worker: Path) -> None:
    pid = read_pid(worker / "bot.pid")
    if pid is not None and is_expected_worker_process(pid, worker):
        kill_pid(pid)
    (worker / "bot.pid").unlink(missing_ok=True)
    start_workers([worker.name], 0)


def supervise(values: list[str], delay: int, interval: int) -> int:
    WORKERS_DIR.mkdir(parents=True, exist_ok=True)
    old = read_pid(PID_FILE)
    if old is not None and is_pid_running(old):
        print(f"Tà Thú Supervisor đã chạy (PID {old})", file=sys.stderr)
        return 1
    PID_FILE.write_text(str(os.getpid()), encoding="utf-8")
    print(f"Tà Thú Supervisor đang chạy (PID {os.getpid()})", flush=True)
    try:
        while True:
            paths = selected_worker_dirs(values)
            if paths and all(
                (worker / "worker.done").is_file() or (worker / "home" / "worker.done").is_file()
                for worker in paths
            ):
                print("Tất cả Tà Thú worker đã hoàn tất. Supervisor kết thúc.", flush=True)
                break
            start_workers(values, delay)
            for worker in paths:
                if (worker / ".paused").is_file() or (worker / "worker.done").is_file() or (worker / "home" / "worker.done").is_file():
                    continue
                pid = read_pid(worker / "bot.pid")
                if pid is None or not is_expected_worker_process(pid, worker):
                    continue
                reason = stale_reason(worker)
                if reason:
                    print(f"{worker.name} cần restart: {reason}", flush=True)
                    restart_worker(worker)
            time.sleep(interval)
    except KeyboardInterrupt:
        print("Tà Thú Supervisor nhận lệnh dừng.", flush=True)
    finally:
        PID_FILE.unlink(missing_ok=True)
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)
    for name in ("start", "stop", "restart"):
        command = sub.add_parser(name)
        command.add_argument("workers", nargs="*")
        if name != "stop":
            command.add_argument("--delay", type=int, default=START_DELAY)
    sub.add_parser("status").add_argument("--json", action="store_true")
    build = sub.add_parser("build-workers")
    build.add_argument("count", type=int)
    supervise_parser = sub.add_parser("supervise")
    supervise_parser.add_argument("workers", nargs="*")
    supervise_parser.add_argument("--delay", type=int, default=START_DELAY)
    supervise_parser.add_argument("--interval", type=int, default=CHECK_INTERVAL)
    sub.add_parser("build")
    args = parser.parse_args()
    try:
        if args.command == "build":
            run_build()
            return 0
        if args.command == "build-workers":
            build_workers(args.count)
            return 0
        if args.command == "status":
            payload = status_dict()
            if args.json:
                print(json.dumps(payload, ensure_ascii=False, separators=(",", ":")))
            else:
                for worker in payload["workers"]:
                    print(f"{worker['name']:<12} {worker['state']:<8} PID={worker['pid'] or '-'}")
                print(json.dumps(payload["totals"], ensure_ascii=False))
            return 0
        if args.command == "stop":
            return stop_workers(args.workers)
        if args.command == "restart":
            stop_workers(args.workers)
            return start_workers(args.workers, args.delay)
        if args.command == "start":
            return start_workers(args.workers, args.delay)
        if args.command == "supervise":
            if args.delay < 0 or args.interval < 1:
                raise RuntimeError("delay phải >= 0 và interval phải > 0")
            return supervise(args.workers, args.delay, args.interval)
    except Exception as exc:
        print(f"[!] {exc}", file=sys.stderr)
        return 1
    return 2


if __name__ == "__main__":
    raise SystemExit(main())
