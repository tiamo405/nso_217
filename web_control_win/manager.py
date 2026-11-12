from __future__ import annotations

import asyncio
import csv
import ctypes
import io
import json
import os
import re
import shutil
import subprocess
import sys
import time
from collections import deque
from pathlib import Path
from typing import Any, Dict, Optional, Tuple

from server_config import DEFAULT_SERVER, normalize_server

from web_control.common import (  # noqa: F401  (re-export cho test/scheduler)
    DEFAULT_PERIODIC_RESTART_HOURS,
    DEFAULT_WORKER_START_DELAY_SECONDS,
    EMPTY_TOTALS,
    MAX_PERIODIC_RESTART_HOURS,
    MAX_WORKER_START_DELAY_SECONDS,
    ControlError,
    ManagerCommon,
)
from web_control.logparse import SummaryCache

from .config import Settings

WORKER_RE = re.compile(r"^worker-([0-9]+)$")
LOG_KINDS = {"stdout": "stdout.log", "error": "java-errors.log"}


def _creationflags_for_windows() -> int:
    if os.name == "nt":
        return subprocess.CREATE_NO_WINDOW | subprocess.DETACHED_PROCESS
    return 0


def is_pid_running(pid: int) -> bool:
    """Kiểm tra tiến trình còn chạy hay không đa nền tảng (ưu tiên psutil / Win32 API)."""
    if pid <= 0:
        return False

    try:
        import psutil  # type: ignore

        if psutil.pid_exists(pid):
            p = psutil.Process(pid)
            return p.is_running() and p.status() != psutil.STATUS_ZOMBIE
        return False
    except ImportError:
        pass

    if os.name == "nt":
        PROCESS_QUERY_LIMITED_INFORMATION = 0x1000
        SYNCHRONIZE = 0x00100000
        kernel32 = ctypes.windll.kernel32
        handle = kernel32.OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION | SYNCHRONIZE, False, pid)
        if handle:
            exit_code = ctypes.c_ulong()
            kernel32.GetExitCodeProcess(handle, ctypes.byref(exit_code))
            kernel32.CloseHandle(handle)
            return exit_code.value == 259  # STILL_ACTIVE
        return False

    try:
        os.kill(pid, 0)
        return True
    except (OSError, ValueError):
        return False


def kill_pid(pid: int) -> bool:
    """Dừng tiến trình theo PID đa nền tảng."""
    if not is_pid_running(pid):
        return False

    if os.name == "nt":
        res = subprocess.run(
            ["taskkill", "/F", "/T", "/PID", str(pid)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        return res.returncode == 0
    else:
        try:
            os.kill(pid, 9)
            return True
        except OSError:
            return False


def supervisor_pid_is_valid(pid: int, manager_path: Path) -> bool:
    """Verify a supervisor PID belongs to this runtime before killing it."""
    if not is_pid_running(pid):
        return False
    try:
        import psutil  # type: ignore

        args = psutil.Process(pid).cmdline()
    except ImportError:
        if os.name == "nt":
            # Without psutil Windows cannot expose command-line ownership
            # portably; keep compatibility with the PID-file contract.
            return True
        args = []
    except Exception:
        return False

    if not args and os.name != "nt":
        try:
            raw = Path(f"/proc/{pid}/cmdline").read_bytes()
            args = [part.decode("utf-8", errors="replace") for part in raw.split(b"\0") if part]
        except OSError:
            return False
    command = " ".join(args).replace("\\", "/").casefold()
    expected = str(manager_path.resolve()).replace("\\", "/").casefold()
    return expected in command and " supervise" in f" {command}"


class WindowsHeadlessManager(ManagerCommon):
    """Quản lý runtime và worker trên Windows thông qua win_manager.py và Windows APIs."""

    def __init__(self, settings: Settings):
        self.settings = settings
        self.control_lock = asyncio.Lock()
        self._supervisor_process: Optional[subprocess.Popen[bytes]] = None
        self._ta_thu_supervisor_process: Optional[subprocess.Popen[bytes]] = None
        self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        self.log_summaries = SummaryCache()

    @property
    def web_runtime_dir(self) -> Path:
        return self.settings.web_runtime_dir

    @property
    def ta_thu_workers_dir(self) -> Path:
        return self.settings.ta_thu_workers_dir

    def _command_env(self, server: Optional[str] = None,
                     extra_env: Optional[Dict[str, str]] = None) -> Dict[str, str]:
        env = self.settings.command_env(server)
        env["NSO_PROXY_WORKERS_PER_PROXY"] = str(self.proxy_workers_per_proxy())
        if extra_env:
            env.update(extra_env)
        return env

    async def _run_python(self, script: Path, args: Tuple[str, ...], *, timeout: Optional[int],
                          env: Dict[str, str], label: str) -> Tuple[int, str]:
        """Chạy manager Python, gom output qua file tạm (không giữ PIPE với tiến trình java con)."""
        import tempfile

        with tempfile.TemporaryFile() as output:
            try:
                process = await asyncio.create_subprocess_exec(
                    sys.executable, str(script), *args,
                    cwd=self.settings.repo_dir,
                    env=env,
                    stdin=asyncio.subprocess.DEVNULL,
                    stdout=output,
                    stderr=asyncio.subprocess.STDOUT,
                )
            except OSError as exc:
                raise ControlError(f"Không thực thi được {label}: {exc}") from exc
            try:
                await asyncio.wait_for(process.wait(), timeout=timeout or self.settings.command_timeout)
            except asyncio.TimeoutError:
                if os.name == "nt":
                    subprocess.run(["taskkill", "/F", "/T", "/PID", str(process.pid)],
                                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
                else:
                    try:
                        process.kill()
                    except ProcessLookupError:
                        pass
                await process.wait()
                raise ControlError(f"Lệnh {label} {args[0] if args else ''} quá thời gian")
            output.seek(0)
            data = output.read()
        return process.returncode or 0, data.decode("utf-8", errors="replace")

    @property
    def state_file(self) -> Path:
        return self.settings.web_runtime_dir / "state.json"

    @property
    def supervisor_log(self) -> Path:
        return self.settings.web_runtime_dir / "supervisor.log"

    @property
    def supervisor_pid_file(self) -> Path:
        return self.settings.workers_dir / "supervisor.pid"

    async def _run_win_manager(
        self, *args: str, timeout: Optional[int] = None, server: Optional[str] = None,
        extra_env: Optional[Dict[str, str]] = None,
    ) -> Tuple[int, str]:
        """Thực thi lệnh win_manager.py bất đồng bộ."""
        return await self._run_python(
            self.settings.win_manager_py, args, timeout=timeout,
            env=self._command_env(server, extra_env), label="win_manager.py",
        )

    async def _run_ta_thu_manager(
        self, *args: str, timeout: Optional[int] = None,
        extra_env: Optional[Dict[str, str]] = None,
    ) -> Tuple[int, str]:
        """Run the native cross-platform Tà Thú controller."""
        manager_py = self.settings.ta_thu_manager_py
        if not manager_py.is_file():
            raise ControlError(f"Không tìm thấy Tà Thú manager: {manager_py}")
        return await self._run_python(
            manager_py, args, timeout=timeout,
            env=self._command_env(self.selected_server(), extra_env), label="Tà Thú manager",
        )

    def _read_pid(self, path: Path) -> Optional[int]:
        try:
            raw = path.read_text(encoding="utf-8").strip()
        except OSError:
            return None
        return int(raw) if raw.isdigit() else None

    def desired_supervisor(self) -> bool:
        return self._read_state().get("supervisor_desired") is True

    def _read_state(self) -> Dict[str, Any]:
        try:
            data = json.loads(self.state_file.read_text(encoding="utf-8"))
            return data if isinstance(data, dict) else {}
        except (OSError, json.JSONDecodeError):
            return {}

    def _write_state(self, data: Dict[str, Any]) -> None:
        self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(
            json.dumps(data, ensure_ascii=False) + "\n", encoding="utf-8"
        )
        temporary.replace(self.state_file)

    def selected_server(self) -> str:
        state = self._read_state()
        try:
            return normalize_server(state.get("server"))
        except (AttributeError, ValueError):
            try:
                return normalize_server(os.environ.get("NSO_SERVER"))
            except ValueError:
                return DEFAULT_SERVER

    def set_server(self, server: str) -> str:
        try:
            selected = normalize_server(server)
        except ValueError as exc:
            raise ControlError(str(exc)) from exc
        state = self._read_state()
        state["server"] = selected
        self._write_state(state)
        return selected

    def _set_desired_supervisor(self, desired: bool) -> None:
        state = self._read_state()
        state["supervisor_desired"] = desired
        state.setdefault("server", self.selected_server())
        self._write_state(state)

    def supervisor_status(self) -> Dict[str, Any]:
        pid = self._read_pid(self.supervisor_pid_file)
        running = pid is not None and supervisor_pid_is_valid(pid, self.settings.win_manager_py)
        stale = self.supervisor_pid_file.exists() and not running
        return {
            "running": running,
            "pid": pid if running else None,
            "stale_pid": stale,
            "desired": self.desired_supervisor(),
            "server": self.selected_server(),
            "periodic_restart_hours": self.periodic_restart_hours(),
            "worker_start_delay_seconds": self.worker_start_delay_seconds(),
            "proxy_workers_per_proxy": self.proxy_workers_per_proxy(),
            "proxy_enabled": self.proxy_workers_per_proxy() > 0,
            "proxy_count": self.proxy_count(),
            "log": str(self.supervisor_log),
        }

    async def status(self) -> Dict[str, Any]:
        """Lấy toàn bộ thông tin trạng thái worker & supervisor từ win_manager.py."""
        code, output = await self._run_win_manager("status", "--json")
        if code != 0:
            raise ControlError(output.strip() or "Không đọc được trạng thái worker từ win_manager")

        try:
            data = json.loads(output)
        except json.JSONDecodeError as exc:
            raise ControlError("win_manager status trả về JSON không hợp lệ") from exc

        data["supervisor"] = self.supervisor_status()
        self.enrich_workers(data.get("workers") or [])
        return data

    async def start_supervisor(
        self,
        *,
        remember: bool = True,
        server: Optional[str] = None,
        periodic_restart_hours: Optional[int] = None,
        worker_start_delay_seconds: Optional[int] = None,
        proxy_workers_per_proxy: Optional[int] = None,
    ) -> Dict[str, Any]:
        async with self.control_lock:
            if periodic_restart_hours is not None:
                self.set_periodic_restart_hours(periodic_restart_hours)
            if worker_start_delay_seconds is not None:
                self.set_worker_start_delay_seconds(worker_start_delay_seconds)
            if proxy_workers_per_proxy is not None:
                self.set_proxy_workers_per_proxy(proxy_workers_per_proxy)
            current = self.supervisor_status()
            requested_server = (
                self.selected_server() if server is None else self.set_server(server)
            )
            if current["running"] and current["server"] != requested_server:
                await self._stop_supervisor_unlocked(remember=False)
            return await self._start_supervisor_unlocked(
                remember=remember, server=requested_server
            )

    async def _start_supervisor_unlocked(
        self, *, remember: bool, server: Optional[str] = None
    ) -> Dict[str, Any]:
        selected_server = (
            self.selected_server() if server is None else self.set_server(server)
        )
        current = self.supervisor_status()
        if current["running"]:
            if remember:
                self._set_desired_supervisor(True)
                current["desired"] = True
            return current

        if current["stale_pid"]:
            self.supervisor_pid_file.unlink(missing_ok=True)

        if not any(self.settings.workers_dir.glob("worker-*")):
            raise ControlError("Chưa có worker nào. Hãy bấm Build trước.")

        workers = [path for path in self.settings.workers_dir.glob("worker-*") if path.is_dir()]
        if workers and all(
            (worker / "home" / "worker.done").is_file()
            or (worker / "home" / "worker.first-pass.done").is_file()
            for worker in workers
        ):
            raise ControlError(
                "Tất cả worker NVHN đã hoàn thành. "
                "Hãy bấm Build rồi Run để chạy lại từ đầu."
            )

        main_class = self.settings.runtime_dir / "build" / "classes" / "OptimizedMain.class"
        if not main_class.is_file():
            raise ControlError("Chưa có OptimizedMain.class. Hãy bấm Build trước.")

        self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        log_stream = self.supervisor_log.open("ab", buffering=0)

        creationflags = 0
        if os.name == "nt":
            creationflags = subprocess.CREATE_NO_WINDOW | subprocess.DETACHED_PROCESS

        cmd = [
            sys.executable,
            str(self.settings.win_manager_py),
            "supervise",
            "--delay", str(self.worker_start_delay_seconds()),
            "--interval", "20",
        ]
        supervisor_env = self._command_env(selected_server)
        supervisor_env["PERIODIC_RESTART_SECONDS"] = str(
            self.periodic_restart_hours() * 60 * 60
        )

        try:
            try:
                self._supervisor_process = subprocess.Popen(
                    cmd,
                    cwd=self.settings.repo_dir,
                    env=supervisor_env,
                    stdin=subprocess.DEVNULL,
                    stdout=log_stream,
                    stderr=subprocess.STDOUT,
                    creationflags=creationflags,
                )
            except OSError as exc:
                raise ControlError(f"Không khởi động được Supervisor: {exc}") from exc
        finally:
            log_stream.close()

        # Đợi một chút để supervisor tạo file pid hoặc chạy
        for _ in range(15):
            await asyncio.sleep(0.2)
            if self.supervisor_status()["running"]:
                break

        result = self.supervisor_status()
        if not result["running"]:
            if self._supervisor_process is not None:
                if self._supervisor_process.poll() is None:
                    self._supervisor_process.terminate()
                try:
                    self._supervisor_process.wait(timeout=2)
                except subprocess.TimeoutExpired:
                    self._supervisor_process.kill()
                self._supervisor_process = None
            raise ControlError(f"Supervisor khởi động thất bại. Kiểm tra {self.supervisor_log}")

        if remember:
            self._set_desired_supervisor(True)
            result["desired"] = True
        return result

    async def stop_supervisor(self, *, remember: bool = True) -> Dict[str, Any]:
        async with self.control_lock:
            return await self._stop_supervisor_unlocked(remember=remember)

    async def _stop_supervisor_unlocked(self, *, remember: bool) -> Dict[str, Any]:
        if remember:
            self._set_desired_supervisor(False)

        current = self.supervisor_status()
        if current["running"] and current["pid"]:
            pid = int(current["pid"])
            kill_pid(pid)
            for _ in range(30):
                if not is_pid_running(pid):
                    break
                await asyncio.sleep(0.1)
            else:
                raise ControlError("Supervisor không dừng sau thời gian chờ")

        # Gọi win_manager stop để dừng toàn bộ worker con
        code, output = await self._run_win_manager("stop", timeout=30)
        if code != 0:
            raise ControlError(output.strip() or "Dừng workers thất bại")

        self.supervisor_pid_file.unlink(missing_ok=True)
        return self.supervisor_status()

    @property
    def ta_thu_supervisor_pid_file(self) -> Path:
        return self.settings.ta_thu_workers_dir / "supervisor.pid"

    @property
    def ta_thu_supervisor_log(self) -> Path:
        return self.settings.web_runtime_dir / "ta-thu-supervisor.log"

    def ta_thu_supervisor_status(self) -> Dict[str, Any]:
        pid = self._read_pid(self.ta_thu_supervisor_pid_file)
        running = pid is not None and is_pid_running(pid)
        if running and not supervisor_pid_is_valid(pid, self.settings.ta_thu_manager_py):
            running = False
        stale = self.ta_thu_supervisor_pid_file.exists() and not running
        return {
            "running": running,
            "pid": pid if running else None,
            "stale_pid": stale,
            "log": str(self.ta_thu_supervisor_log),
        }

    async def ta_thu_status(self) -> Dict[str, Any]:
        data: Dict[str, Any] = {
            "workers_dir": str(self.settings.ta_thu_workers_dir),
            "workers": [],
            "totals": dict(EMPTY_TOTALS),
        }
        if self.settings.ta_thu_manager_py.is_file():
            code, output = await self._run_ta_thu_manager("status", "--json")
            if code == 0:
                try:
                    parsed = json.loads(output)
                    if isinstance(parsed, dict):
                        data.update(parsed)
                except json.JSONDecodeError:
                    pass
        data["totals"] = {**EMPTY_TOTALS, **(data.get("totals") or {})}
        self.enrich_workers(data.get("workers") or [])
        data["supervisor"] = self.ta_thu_supervisor_status()
        return data

    async def start_ta_thu(
        self,
        worker_count: int = 10,
        server: Optional[str] = None,
        nvhn_workers_dir: Path | None = None,
        rebuild_workers: bool = True,
    ) -> bool:
        async with self.control_lock:
            if self.ta_thu_supervisor_status()["running"]:
                self.set_desired_ta_thu(True)
                return True
            if server is not None:
                self.set_server(server)
            self.settings.ta_thu_root.mkdir(parents=True, exist_ok=True)
            has_workers = any(p.is_dir() for p in self.settings.ta_thu_workers_dir.glob("worker-*"))
            if rebuild_workers or not has_workers:
                code, output = await self._run_ta_thu_manager(
                    "build-workers", str(worker_count), timeout=self.settings.build_timeout
                )
                self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
                with self.ta_thu_supervisor_log.open("ab") as stream:
                    stream.write(output.encode("utf-8", errors="replace"))
                    if output and not output.endswith("\n"):
                        stream.write(b"\n")
                if code != 0:
                    return False

            pid_file = self.ta_thu_supervisor_pid_file
            if pid_file.exists() and not self.ta_thu_supervisor_status()["running"]:
                pid_file.unlink(missing_ok=True)
            log_stream = self.ta_thu_supervisor_log.open("ab", buffering=0)
            command = [
                sys.executable,
                str(self.settings.ta_thu_manager_py),
                "supervise",
                "--delay", str(self.worker_start_delay_seconds()),
                "--interval", "20",
            ]
            environment = self._command_env(self.selected_server())
            if nvhn_workers_dir is not None:
                environment["TA_THU_NVHN_WORKERS_DIR"] = str(nvhn_workers_dir)
            try:
                self._ta_thu_supervisor_process = subprocess.Popen(
                    command,
                    cwd=self.settings.repo_dir,
                    env=environment,
                    stdin=subprocess.DEVNULL,
                    stdout=log_stream,
                    stderr=subprocess.STDOUT,
                    creationflags=_creationflags_for_windows(),
                )
            except OSError:
                log_stream.close()
                return False
            finally:
                log_stream.close()

            for _ in range(20):
                await asyncio.sleep(0.2)
                if self.ta_thu_supervisor_status()["running"]:
                    self.set_desired_ta_thu(True)
                    return True
            if self._ta_thu_supervisor_process.poll() is None:
                kill_pid(self._ta_thu_supervisor_process.pid)
            self._ta_thu_supervisor_process = None
            return False

    async def stop_ta_thu(self, *, remember: bool = True) -> bool:
        if remember:
            # Ghi trước để scheduler không tự khôi phục Tà Thú trong lúc dừng.
            self.set_desired_ta_thu(False)
        async with self.control_lock:
            status = self.ta_thu_supervisor_status()
            pid = status["pid"]
            if pid is not None:
                kill_pid(int(pid))
                for _ in range(30):
                    if not is_pid_running(int(pid)):
                        break
                    await asyncio.sleep(0.1)
            self._ta_thu_supervisor_process = None
            try:
                code, output = await self._run_ta_thu_manager("stop", timeout=30)
            except ControlError:
                code, output = 0, ""
            if output:
                self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
                with self.ta_thu_supervisor_log.open("ab") as stream:
                    stream.write(output.encode("utf-8", errors="replace"))
                    if not output.endswith("\n"):
                        stream.write(b"\n")
            self.ta_thu_supervisor_pid_file.unlink(missing_ok=True)
            if self.ta_thu_supervisor_status()["running"]:
                return False
            return code == 0

    def ta_thu_worker_number(self, worker_name: str, *, must_exist: bool = True) -> int:
        match = WORKER_RE.fullmatch(worker_name)
        if not match:
            raise ControlError("Tên Tà Thú worker không hợp lệ")
        number = int(match.group(1))
        worker_dir = (self.settings.ta_thu_workers_dir / f"worker-{number:02d}").resolve()
        if worker_dir.parent != self.settings.ta_thu_workers_dir.resolve() or (
            must_exist and not worker_dir.is_dir()
        ):
            raise ControlError("Không tìm thấy Tà Thú worker")
        return number

    async def ta_thu_worker_action(self, worker_name: str, action: str) -> str:
        """Start / Stop / Restart một worker Tà Thú (marker .paused giống NVHN)."""
        if action not in ("start", "stop", "restart"):
            raise ControlError("Thao tác không hợp lệ")
        async with self.control_lock:
            number = str(self.ta_thu_worker_number(worker_name))
            marker = self.settings.ta_thu_workers_dir / f"worker-{int(number):02d}" / ".paused"
            was_paused = marker.is_file()
            if action == "stop":
                marker.touch(exist_ok=True)
                code, output = await self._run_ta_thu_manager("stop", number, timeout=30)
            else:
                marker.unlink(missing_ok=True)
                code, output = await self._run_ta_thu_manager(
                    action, "--delay", "0", number, timeout=45
                )
                if code != 0 and was_paused:
                    marker.touch(exist_ok=True)
            if code != 0:
                raise ControlError(output.strip() or f"Tà Thú {action} {worker_name} thất bại")
            return output.strip()

    def ta_thu_log_path(self, worker_name: str, kind: str) -> Path:
        number = self.ta_thu_worker_number(worker_name, must_exist=False)
        filename = {"stdout": "stdout.log", "error": "java-errors.log"}.get(kind)
        if filename is None:
            raise ControlError("Loại log không hợp lệ")
        return self.settings.ta_thu_workers_dir / f"worker-{number:02d}" / filename

    def tail_ta_thu_log(self, worker_name: str, kind: str, lines: int = 200) -> str:
        path = self.ta_thu_log_path(worker_name, kind)
        if not path.is_file():
            return ""
        with path.open("r", encoding="utf-8", errors="replace") as stream:
            return "".join(deque(stream, maxlen=max(1, min(lines, 2000))))

    def worker_number(self, worker_name: str) -> str:
        match = WORKER_RE.fullmatch(worker_name)
        if not match:
            raise ControlError("Tên worker không hợp lệ")
        number = str(int(match.group(1)))
        worker_dir = (self.settings.workers_dir / f"worker-{int(number):02d}").resolve()
        if worker_dir.parent != self.settings.workers_dir.resolve() or not worker_dir.is_dir():
            raise ControlError("Không tìm thấy worker")
        return number

    def worker_pause_marker(self, worker_name: str) -> Tuple[str, Path]:
        number = self.worker_number(worker_name)
        marker = self.settings.workers_dir / f"worker-{int(number):02d}" / ".paused"
        return number, marker

    async def stop_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            pause_marker.touch(exist_ok=True)
            code, output = await self._run_win_manager("stop", number, timeout=30)
            if code != 0:
                raise ControlError(output.strip() or "Dừng worker thất bại")
            return output.strip()

    async def start_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            was_paused = pause_marker.is_file()
            pause_marker.unlink(missing_ok=True)
            code, output = await self._run_win_manager(
                "start", "--delay", "0", number, timeout=45, server=self.selected_server()
            )
            if code != 0:
                if was_paused:
                    pause_marker.touch(exist_ok=True)
                raise ControlError(output.strip() or "Khởi động worker thất bại")
            return output.strip()

    async def restart_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            was_paused = pause_marker.is_file()
            pause_marker.unlink(missing_ok=True)
            code, output = await self._run_win_manager(
                "restart", "--delay", "0", number, timeout=45, server=self.selected_server()
            )
            if code != 0:
                if was_paused:
                    pause_marker.touch(exist_ok=True)
                raise ControlError(output.strip() or "Restart worker thất bại")
            return output.strip()

    def log_path(self, worker_name: str, kind: str, runtime: str = "nvhn") -> Path:
        if kind == "supervisor":
            return self.ta_thu_supervisor_log if runtime == "ta_thu" else self.supervisor_log
        if runtime == "ta_thu":
            return self.ta_thu_log_path(worker_name, kind)
        number = self.worker_number(worker_name)
        filename = LOG_KINDS.get(kind)
        if filename is None:
            raise ControlError("Loại log không hợp lệ")
        return self.settings.workers_dir / f"worker-{int(number):02d}" / filename

    def tail_log(self, worker_name: str, kind: str, lines: int = 200, runtime: str = "nvhn") -> str:
        path = self.log_path(worker_name, kind, runtime=runtime)
        if not path.is_file():
            return ""
        with path.open("r", encoding="utf-8", errors="replace") as stream:
            return "".join(deque(stream, maxlen=max(1, min(lines, 2000))))

    def validate_and_store_account_csv(self, content: bytes) -> int:
        if len(content) > self.settings.max_upload_bytes:
            raise ControlError("File account vượt quá giới hạn 2 MB")
        try:
            text = content.decode("utf-8-sig")
        except UnicodeDecodeError as exc:
            raise ControlError("account.csv phải dùng bảng mã UTF-8") from exc

        try:
            rows = list(csv.reader(text.splitlines(), strict=True))
        except csv.Error as exc:
            raise ControlError(f"account.csv không hợp lệ: {exc}") from exc

        if not rows or [cell.strip().lower() for cell in rows[0]] != ["username", "password"]:
            raise ControlError("Header bắt buộc là username,password")

        accounts = [row for row in rows[1:] if any(cell.strip() for cell in row)]
        if not accounts:
            raise ControlError("account.csv không có tài khoản")

        if any(len(row) != 2 or not row[0].strip() or not row[1].strip() for row in accounts):
            raise ControlError("Mỗi dòng account phải gồm username và password")

        buffer = io.StringIO(newline="")
        writer = csv.writer(buffer, lineterminator="\n")
        writer.writerow(["username", "password"])
        writer.writerows((row[0].strip(), row[1].strip()) for row in accounts)
        normalized = buffer.getvalue()

        temporary = self.settings.account_csv.with_suffix(".csv.tmp")
        self.settings.account_csv.parent.mkdir(parents=True, exist_ok=True)
        temporary.write_text(normalized, encoding="utf-8", newline="")
        temporary.replace(self.settings.account_csv)
        return len(accounts)

    def account_summary(self) -> Dict[str, Any]:
        if not self.settings.account_csv.is_file():
            return {"configured": False, "count": 0}
        try:
            with self.settings.account_csv.open("r", encoding="utf-8-sig", newline="") as stream:
                rows = list(csv.reader(stream, strict=True))
        except (OSError, csv.Error):
            return {"configured": False, "count": 0}

        count = sum(1 for row in rows[1:] if any(cell.strip() for cell in row))
        return {"configured": True, "count": count}

    async def reconcile(self) -> None:
        if not self.desired_supervisor() or self.supervisor_status()["running"]:
            return
        try:
            await self.start_supervisor(remember=False)
        except ControlError:
            return
