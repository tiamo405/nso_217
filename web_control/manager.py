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

from server_config import DEFAULT_SERVER, normalize_server

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

    async def _capture(
        self, *args: str, timeout: int | None = None, server: str | None = None
    ) -> tuple[int, str]:
        try:
            process = await asyncio.create_subprocess_exec(
                *args,
                cwd=self.settings.repo_dir,
                env=self.settings.command_env(server),
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
        return self._read_state().get("supervisor_desired") is True

    def _read_state(self) -> dict[str, Any]:
        try:
            data = json.loads(self.state_file.read_text(encoding="utf-8"))
            return data if isinstance(data, dict) else {}
        except (OSError, json.JSONDecodeError):
            return {}

    def _write_state(self, data: dict[str, Any]) -> None:
        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(
            json.dumps(data, ensure_ascii=False) + "\n", encoding="utf-8"
        )
        os.chmod(temporary, 0o600)
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

    def supervisor_status(self) -> dict[str, Any]:
        pid = self._read_pid(self.supervisor_pid_file)
        running = pid is not None and self._supervisor_pid_is_valid(pid)
        stale = self.supervisor_pid_file.exists() and not running
        return {
            "running": running,
            "pid": pid if running else None,
            "stale_pid": stale,
            "desired": self.desired_supervisor(),
            "server": self.selected_server(),
            "log": str(self.supervisor_log),
        }

    async def start_supervisor(
        self, *, remember: bool = True, server: str | None = None
    ) -> dict[str, Any]:
        async with self.control_lock:
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
        self, *, remember: bool, server: str | None = None
    ) -> dict[str, Any]:
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
        workers = [path for path in self.settings.workers_dir.glob("worker-*") if path.is_dir()]
        if not workers:
            raise ControlError("Chưa có worker. Hãy build trước.")
        if all(
            (worker / "home" / "worker.done").is_file()
            and (worker / "home" / "worker.first-pass.done").is_file()
            for worker in workers
        ):
            raise ControlError(
                "Tất cả worker NVHN đã hoàn thành 2/2 lượt. "
                "Start/Run chỉ chạy tiếp tiến độ cũ. Hãy nhấn Build rồi Run để chạy lại từ đầu."
            )
        has_main = (
            (self.settings.headless_dir / "build" / "classes" / "OptimizedMain.class").is_file()
            or (self.settings.headless_dir / "build" / "classes" / "HeadlessMain.class").is_file()
        )
        if not has_main:
            raise ControlError("Chưa có OptimizedMain.class (hoặc HeadlessMain.class). Hãy build trước.")

        self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
        log_stream = self.supervisor_log.open("ab", buffering=0)
        try:
            try:
                self._supervisor_process = subprocess.Popen(
                    [str(self._script("supervise-workers.sh"))],
                    cwd=self.settings.repo_dir,
                    env=self.settings.command_env(selected_server),
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
                self._signal_supervisor(pid, signal.SIGTERM)
            except ProcessLookupError:
                self.supervisor_pid_file.unlink(missing_ok=True)
            for _ in range(50):
                if not self._supervisor_pid_is_valid(pid):
                    break
                await asyncio.sleep(0.1)
            else:
                try:
                    self._signal_supervisor(pid, signal.SIGKILL)
                except ProcessLookupError:
                    pass
                for _ in range(20):
                    if not self._supervisor_pid_is_valid(pid):
                        break
                    await asyncio.sleep(0.1)
                else:
                    raise ControlError("Không thể kill Supervisor sau khi gửi SIGTERM/SIGKILL")
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
        code, output = await self._capture(str(self._script("stop-workers.sh")), timeout=20)
        if code != 0:
            raise ControlError(output.strip() or "Không dừng được worker")
        if self.supervisor_pid_file.exists() and not self._supervisor_pid_is_valid(
            self._read_pid(self.supervisor_pid_file) or -1
        ):
            self.supervisor_pid_file.unlink(missing_ok=True)
        return self.supervisor_status()

    @staticmethod
    def _signal_supervisor(pid: int, signum: int) -> None:
        """Signal the web-owned Supervisor session and its launcher children."""
        try:
            process_group = os.getpgid(pid)
        except ProcessLookupError:
            raise
        if process_group == pid:
            os.killpg(process_group, signum)
        else:
            # A manually launched Supervisor may share the web process group;
            # never signal that whole group.
            os.kill(pid, signum)

    def worker_number(self, worker_name: str) -> str:
        match = WORKER_RE.fullmatch(worker_name)
        if not match:
            raise ControlError("Tên worker không hợp lệ")
        number = str(int(match.group(1)))
        worker_dir = (self.settings.workers_dir / f"worker-{int(number):02d}").resolve()
        if worker_dir.parent != self.settings.workers_dir or not worker_dir.is_dir():
            raise ControlError("Không tìm thấy worker")
        return number

    def worker_pause_marker(self, worker_name: str) -> tuple[str, Path]:
        number = self.worker_number(worker_name)
        marker = self.settings.workers_dir / f"worker-{int(number):02d}" / ".paused"
        return number, marker

    async def stop_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            pause_marker.touch(mode=0o600, exist_ok=True)
            os.chmod(pause_marker, 0o600)
            code, output = await self._capture(
                str(self._script("stop-workers.sh")), number, timeout=30
            )
            if code != 0:
                raise ControlError(output.strip() or "Dừng worker thất bại")
            return output.strip()

    async def start_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            was_paused = pause_marker.is_file()
            pause_marker.unlink(missing_ok=True)
            code, output = await self._capture(
                str(self._script("start-workers.sh")),
                "--delay",
                "0",
                number,
                timeout=30,
                server=self.selected_server(),
            )
            if code != 0:
                if was_paused:
                    pause_marker.touch(mode=0o600, exist_ok=True)
                    os.chmod(pause_marker, 0o600)
                raise ControlError(output.strip() or "Khởi động worker thất bại")
            return output.strip()

    async def restart_worker(self, worker_name: str) -> str:
        async with self.control_lock:
            number, pause_marker = self.worker_pause_marker(worker_name)
            was_paused = pause_marker.is_file()
            pause_marker.unlink(missing_ok=True)
            code, output = await self._capture(
                str(self._script("restart-workers.sh")),
                number,
                timeout=30,
                server=self.selected_server(),
            )
            if code != 0:
                if was_paused:
                    pause_marker.touch(mode=0o600, exist_ok=True)
                    os.chmod(pause_marker, 0o600)
                raise ControlError(output.strip() or "Restart worker thất bại")
            return output.strip()

    def log_path(self, worker_name: str, kind: str, runtime: str = "auto") -> Path:
        number = self.worker_number(worker_name)
        filename = {"stdout": "stdout.log", "error": "java-errors.log"}.get(kind)
        if filename is None:
            raise ControlError("Loại log không hợp lệ")

        # Nếu runtime chỉ định là ta_thu hoặc ta-thu supervisor đang chạy thì đọc từ ta-thu-runtime
        if runtime == "ta_thu" or (runtime == "auto" and self.ta_thu_supervisor_status()["running"]):
            ta_thu_path = self.settings.ta_thu_dir / "workers" / f"worker-{int(number):02d}" / filename
            if ta_thu_path.is_file():
                return ta_thu_path

        return self.settings.workers_dir / f"worker-{int(number):02d}" / filename

    def tail_log(self, worker_name: str, kind: str, lines: int = 200, runtime: str = "auto") -> str:
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

    # ==================== Tà Thú Management ====================

    @property
    def ta_thu_supervisor_pid_file(self) -> Path:
        return self.settings.ta_thu_dir / "workers" / "supervisor.pid"

    @property
    def ta_thu_supervisor_log(self) -> Path:
        return self.settings.runtime_dir / "ta-thu-supervisor.log"

    def ta_thu_supervisor_status(self) -> dict[str, Any]:
        pid = self._read_pid(self.ta_thu_supervisor_pid_file)
        running = False
        if pid is not None:
            try:
                os.kill(pid, 0)
                raw_args = Path(f"/proc/{pid}/cmdline").read_bytes().split(b"\0")
                for raw_arg in raw_args:
                    if b"ta-thu-runtime/scripts/supervise-workers.sh" in raw_arg or raw_arg.endswith(b"supervise-workers.sh"):
                        running = True
                        break
            except OSError:
                running = False

        # Quét kiểm tra dự phòng nếu supervisor đang chạy nhưng file PID bị xóa
        if not running:
            try:
                for proc_dir in Path("/proc").iterdir():
                    if not proc_dir.name.isdigit():
                        continue
                    try:
                        cmdline = (proc_dir / "cmdline").read_bytes()
                        if b"ta-thu-runtime/scripts/supervise-workers.sh" in cmdline:
                            cand_pid = int(proc_dir.name)
                            if cand_pid != os.getpid():
                                pid = cand_pid
                                running = True
                                break
                    except (OSError, ValueError):
                        continue
            except Exception:
                pass

        return {
            "running": running,
            "pid": pid if running else None,
        }

    async def start_ta_thu(self, worker_count: int = 10) -> bool:
        async with self.control_lock:
            status = self.ta_thu_supervisor_status()
            if status["running"]:
                return True

            # 1. Build workers Tà Thú
            build_script = self.settings.ta_thu_dir / "scripts" / "build-workers.sh"
            if not build_script.is_file():
                return False

            code, output = await self._capture(
                str(build_script),
                str(worker_count),
                timeout=300,
            )
            if code != 0:
                return False

            # 2. Start supervisor Tà Thú
            sup_script = self.settings.ta_thu_dir / "scripts" / "supervise-workers.sh"
            if not sup_script.is_file():
                return False

            self.settings.runtime_dir.mkdir(parents=True, exist_ok=True)
            log_stream = self.ta_thu_supervisor_log.open("ab", buffering=0)
            try:
                subprocess.Popen(
                    [str(sup_script)],
                    cwd=self.settings.repo_dir,
                    env=self.settings.command_env(),
                    stdin=subprocess.DEVNULL,
                    stdout=log_stream,
                    stderr=subprocess.STDOUT,
                    start_new_session=True,
                    close_fds=True,
                )
            except OSError:
                return False
            finally:
                log_stream.close()

            await asyncio.sleep(1)
            return self.ta_thu_supervisor_status()["running"]

    async def stop_ta_thu(self) -> bool:
        async with self.control_lock:
            # 1. Tìm PID của Tà Thú supervisor trước khi gọi script dừng
            status = self.ta_thu_supervisor_status()
            sup_pid = status["pid"] if status["running"] else None

            # 2. Chạy stop-workers.sh
            stop_script = self.settings.ta_thu_dir / "scripts" / "stop-workers.sh"
            if stop_script.is_file():
                await self._capture(str(stop_script), timeout=30)

            # 3. Đảm bảo supervisor đã bị kill
            if sup_pid is not None:
                try:
                    os.kill(sup_pid, 15)  # SIGTERM
                    for _ in range(15):
                        await asyncio.sleep(0.1)
                        os.kill(sup_pid, 0)
                    os.kill(sup_pid, 9)   # SIGKILL nếu còn sống
                except OSError:
                    pass

            # 4. Quét dọn bất kỳ supervisor tà thú nào còn sót lại
            try:
                for proc_dir in Path("/proc").iterdir():
                    if not proc_dir.name.isdigit():
                        continue
                    try:
                        cmdline = (proc_dir / "cmdline").read_bytes()
                        if b"ta-thu-runtime/scripts/supervise-workers.sh" in cmdline:
                            other_pid = int(proc_dir.name)
                            if other_pid != os.getpid():
                                try:
                                    os.kill(other_pid, 9)
                                except OSError:
                                    pass
                    except (OSError, ValueError):
                        continue
            except Exception:
                pass

            if self.ta_thu_supervisor_pid_file.exists():
                self.ta_thu_supervisor_pid_file.unlink(missing_ok=True)
            return True
