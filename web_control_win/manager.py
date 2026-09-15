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

from .config import Settings

WORKER_RE = re.compile(r"^worker-([0-9]+)$")


class ControlError(RuntimeError):
    pass


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
            ["taskkill", "/F", "/PID", str(pid)],
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


class WindowsHeadlessManager:
    """Quản lý runtime và worker trên Windows thông qua win_manager.py và Windows APIs."""

    def __init__(self, settings: Settings):
        self.settings = settings
        self.control_lock = asyncio.Lock()
        self._supervisor_process: Optional[subprocess.Popen[bytes]] = None
        self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)

    @property
    def state_file(self) -> Path:
        return self.settings.web_runtime_dir / "state.json"

    @property
    def supervisor_log(self) -> Path:
        return self.settings.web_runtime_dir / "supervisor.log"

    @property
    def supervisor_pid_file(self) -> Path:
        return self.settings.workers_dir / "supervisor.pid"

    async def _run_win_manager(self, *args: str, timeout: Optional[int] = None) -> Tuple[int, str]:
        """Thực thi lệnh win_manager.py bất đồng bộ."""
        cmd = [sys.executable, str(self.settings.win_manager_py), *args]
        try:
            process = await asyncio.create_subprocess_exec(
                *cmd,
                cwd=self.settings.repo_dir,
                env=self.settings.command_env(),
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.STDOUT,
            )
        except OSError as exc:
            raise ControlError(f"Không thực thi được win_manager.py: {exc}") from exc

        try:
            stdout, _ = await asyncio.wait_for(
                process.communicate(), timeout=timeout or self.settings.command_timeout
            )
        except TimeoutError:
            process.kill()
            await process.wait()
            raise ControlError(f"Lệnh win_manager {args[0] if args else ''} quá thời gian")

        return process.returncode or 0, stdout.decode("utf-8", errors="replace")

    def _read_pid(self, path: Path) -> Optional[int]:
        try:
            raw = path.read_text(encoding="utf-8").strip()
        except OSError:
            return None
        return int(raw) if raw.isdigit() else None

    def desired_supervisor(self) -> bool:
        try:
            state = json.loads(self.state_file.read_text(encoding="utf-8"))
            return state.get("supervisor_desired") is True
        except (OSError, json.JSONDecodeError):
            return False

    def _set_desired_supervisor(self, desired: bool) -> None:
        self.settings.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(
            json.dumps({"supervisor_desired": desired}, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
        temporary.replace(self.state_file)

    def supervisor_status(self) -> Dict[str, Any]:
        pid = self._read_pid(self.supervisor_pid_file)
        running = pid is not None and is_pid_running(pid)
        stale = self.supervisor_pid_file.exists() and not running
        return {
            "running": running,
            "pid": pid if running else None,
            "stale_pid": stale,
            "desired": self.desired_supervisor(),
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
        return data

    async def start_supervisor(self, *, remember: bool = True) -> Dict[str, Any]:
        async with self.control_lock:
            return await self._start_supervisor_unlocked(remember=remember)

    async def _start_supervisor_unlocked(self, *, remember: bool) -> Dict[str, Any]:
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
            "--delay", "30",
            "--interval", "20",
        ]

        try:
            try:
                self._supervisor_process = subprocess.Popen(
                    cmd,
                    cwd=self.settings.repo_dir,
                    env=self.settings.command_env(),
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
            code, output = await self._run_win_manager("start", "--delay", "0", number, timeout=30)
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
            code, output = await self._run_win_manager("restart", number, timeout=30)
            if code != 0:
                if was_paused:
                    pause_marker.touch(exist_ok=True)
                raise ControlError(output.strip() or "Restart worker thất bại")
            return output.strip()

    def log_path(self, worker_name: str, kind: str) -> Path:
        number = self.worker_number(worker_name)
        filename = {"stdout": "stdout.log", "error": "java-errors.log"}.get(kind)
        if filename is None:
            raise ControlError("Loại log không hợp lệ")
        return self.settings.workers_dir / f"worker-{int(number):02d}" / filename

    def tail_log(self, worker_name: str, kind: str, lines: int = 200) -> str:
        path = self.log_path(worker_name, kind)
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
