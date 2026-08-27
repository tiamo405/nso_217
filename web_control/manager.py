from __future__ import annotations

import asyncio
import csv
import io
import json
import os
import re
import signal
import subprocess
from collections import deque
from pathlib import Path
from typing import Any

from .config import Settings


WORKER_RE = re.compile(r"^worker-([0-9]+)$")


class ControlError(RuntimeError):
    pass


class HeadlessManager:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.control_lock = asyncio.Lock()
        self._supervisor_process: subprocess.Popen[bytes] | None = None
        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)

    @property
    def state_file(self) -> Path:
        return self.settings.runtime_dir / "state.json"

    @property
    def supervisor_log(self) -> Path:
        return self.settings.runtime_dir / "supervisor.log"

    @property
    def supervisor_pid_file(self) -> Path:
        return self.settings.workers_dir / "supervisor.pid"

    def _script(self, name: str) -> Path:
        path = (self.settings.scripts_dir / name).resolve()
        if path.parent != self.settings.scripts_dir.resolve() or not path.is_file():
            raise ControlError(f"Không tìm thấy script: {name}")
        return path

    async def _capture(self, *args: str, timeout: int | None = None) -> tuple[int, str]:
        try:
            process = await asyncio.create_subprocess_exec(
                *args,
                cwd=self.settings.repo_dir,
                env=self.settings.command_env(),
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.STDOUT,
            )
        except OSError as exc:
            raise ControlError(f"Không chạy được {Path(args[0]).name}: {exc}") from exc
        try:
            stdout, _ = await asyncio.wait_for(
                process.communicate(), timeout=timeout or self.settings.command_timeout
            )
        except TimeoutError:
            process.kill()
            await process.wait()
            raise ControlError(f"Lệnh quá thời gian: {Path(args[0]).name}")
        return process.returncode or 0, stdout.decode("utf-8", errors="replace")

    async def status(self) -> dict[str, Any]:
        code, output = await self._capture(str(self._script("status-workers.sh")), "--json")
        if code != 0:
            raise ControlError(output.strip() or "Không đọc được trạng thái worker")
        try:
            data = json.loads(output)
        except json.JSONDecodeError as exc:
            raise ControlError("status-workers.sh trả về JSON không hợp lệ") from exc
        data["supervisor"] = self.supervisor_status()
        return data

    def _read_pid(self, path: Path) -> int | None:
        try:
            raw = path.read_text(encoding="utf-8").strip()
        except OSError:
            return None
        return int(raw) if raw.isdigit() else None

    def _supervisor_pid_is_valid(self, pid: int) -> bool:
        try:
            os.kill(pid, 0)
            raw_args = Path(f"/proc/{pid}/cmdline").read_bytes().split(b"\0")
            process_cwd = Path(f"/proc/{pid}/cwd").resolve()
        except OSError:
            return False
        expected_script = (self.settings.scripts_dir / "supervise-workers.sh").resolve()
        for raw_arg in raw_args:
            if not raw_arg:
                continue
            arg = Path(raw_arg.decode("utf-8", errors="replace"))
            if arg.name != "supervise-workers.sh":
                continue
            candidate = arg if arg.is_absolute() else process_cwd / arg
            try:
                if candidate.resolve() == expected_script:
                    return True
            except OSError:
                continue
        return False

    def desired_supervisor(self) -> bool:
        try:
            state = json.loads(self.state_file.read_text(encoding="utf-8"))
            return state.get("supervisor_desired") is True
        except (OSError, json.JSONDecodeError):
            return False

    def _set_desired_supervisor(self, desired: bool) -> None:
        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(
            json.dumps({"supervisor_desired": desired}, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
        os.chmod(temporary, 0o600)
        temporary.replace(self.state_file)

    def supervisor_status(self) -> dict[str, Any]:
        pid = self._read_pid(self.supervisor_pid_file)
        running = pid is not None and self._supervisor_pid_is_valid(pid)
        stale = self.supervisor_pid_file.exists() and not running
        return {
            "running": running,
            "pid": pid if running else None,
            "stale_pid": stale,
            "desired": self.desired_supervisor(),
            "log": str(self.supervisor_log),
        }

    async def start_supervisor(self, *, remember: bool = True) -> dict[str, Any]:
        async with self.control_lock:
            return await self._start_supervisor_unlocked(remember=remember)

    async def _start_supervisor_unlocked(self, *, remember: bool) -> dict[str, Any]:
        current = self.supervisor_status()
        if current["running"]:
            if remember:
                self._set_desired_supervisor(True)
                current["desired"] = True
            return current

        if current["stale_pid"]:
            self.supervisor_pid_file.unlink(missing_ok=True)
        if not any(self.settings.workers_dir.glob("worker-*")):
            raise ControlError("Chưa có worker. Hãy build trước.")
        if not (self.settings.headless_dir / "build" / "classes" / "HeadlessMain.class").is_file():
            raise ControlError("Chưa có HeadlessMain.class. Hãy build trước.")

        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
        log_stream = self.supervisor_log.open("ab", buffering=0)
        try:
            try:
                self._supervisor_process = subprocess.Popen(
                    [str(self._script("supervise-workers.sh"))],
                    cwd=self.settings.repo_dir,
                    env=self.settings.command_env(),
                    stdin=subprocess.DEVNULL,
                    stdout=log_stream,
                    stderr=subprocess.STDOUT,
                    start_new_session=True,
                    close_fds=True,
                )
            except OSError as exc:
                raise ControlError(f"Không chạy được supervisor: {exc}") from exc
        finally:
            log_stream.close()

        await asyncio.sleep(0.5)
        result = self.supervisor_status()
        if not result["running"]:
            if self._supervisor_process is not None:
                if self._supervisor_process.poll() is None:
                    self._supervisor_process.terminate()
                try:
                    self._supervisor_process.wait(timeout=2)
                except subprocess.TimeoutExpired:
                    self._supervisor_process.kill()
                    self._supervisor_process.wait(timeout=2)
                self._supervisor_process = None
            raise ControlError(f"Supervisor khởi động lỗi. Xem {self.supervisor_log}")
        if remember:
            self._set_desired_supervisor(True)
            result["desired"] = True
        return result

    async def stop_supervisor(self, *, remember: bool = True) -> dict[str, Any]:
        async with self.control_lock:
            return await self._stop_supervisor_unlocked(remember=remember)

    async def _stop_supervisor_unlocked(self, *, remember: bool) -> dict[str, Any]:
        if remember:
            self._set_desired_supervisor(False)
        current = self.supervisor_status()
        if current["running"]:
            pid = int(current["pid"])
            try:
                os.kill(pid, signal.SIGTERM)
            except ProcessLookupError:
                self.supervisor_pid_file.unlink(missing_ok=True)
            for _ in range(300):
                if not self._supervisor_pid_is_valid(pid):
                    break
                await asyncio.sleep(0.1)
            else:
                raise ControlError("Supervisor không dừng sau 30 giây")
            if (
                self._supervisor_process is not None
                and self._supervisor_process.pid == pid
            ):
                try:
                    self._supervisor_process.wait(timeout=2)
                except subprocess.TimeoutExpired:
                    self._supervisor_process.kill()
                    self._supervisor_process.wait(timeout=2)
                self._supervisor_process = None
        else:
            code, output = await self._capture(str(self._script("stop-workers.sh")), timeout=20)
            if code != 0:
                raise ControlError(output.strip() or "Không dừng được worker")
        if self.supervisor_pid_file.exists() and not self._supervisor_pid_is_valid(
            self._read_pid(self.supervisor_pid_file) or -1
        ):
            self.supervisor_pid_file.unlink(missing_ok=True)
        return self.supervisor_status()

    def worker_number(self, worker_name: str) -> str:
        match = WORKER_RE.fullmatch(worker_name)
        if not match:
            raise ControlError("Tên worker không hợp lệ")
        number = str(int(match.group(1)))
        worker_dir = (self.settings.workers_dir / f"worker-{int(number):02d}").resolve()
        if worker_dir.parent != self.settings.workers_dir or not worker_dir.is_dir():
            raise ControlError("Không tìm thấy worker")
        return number

    async def restart_worker(self, worker_name: str) -> str:
        number = self.worker_number(worker_name)
        code, output = await self._capture(
            str(self._script("restart-workers.sh")), number, timeout=30
        )
        if code != 0:
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
            raise ControlError("account.csv phải dùng UTF-8") from exc
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
            raise ControlError("Mỗi dòng account phải có username và password")

        buffer = io.StringIO(newline="")
        writer = csv.writer(buffer, lineterminator="\n")
        writer.writerow(["username", "password"])
        writer.writerows((row[0].strip(), row[1].strip()) for row in accounts)
        normalized = buffer.getvalue()
        temporary = self.settings.account_csv.with_suffix(".csv.tmp")
        self.settings.account_csv.parent.mkdir(parents=True, exist_ok=True)
        temporary.write_text(normalized, encoding="utf-8", newline="")
        os.chmod(temporary, 0o600)
        temporary.replace(self.settings.account_csv)
        return len(accounts)

    def account_summary(self) -> dict[str, Any]:
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
            # The dashboard remains available so the user can inspect/build.
            return
