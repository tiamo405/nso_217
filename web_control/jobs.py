from __future__ import annotations

import asyncio
import os
import signal
import uuid
from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Any

from .manager import ControlError, HeadlessManager


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


@dataclass
class BuildJob:
    id: str
    worker_count: int
    start_after_build: bool
    status: str = "queued"
    created_at: str = field(default_factory=utc_now)
    started_at: str | None = None
    finished_at: str | None = None
    output: list[str] = field(default_factory=list)
    error: str | None = None
    version: int = 0
    changed: asyncio.Condition = field(default_factory=asyncio.Condition, repr=False)

    def public(self) -> dict[str, Any]:
        return {
            "id": self.id,
            "worker_count": self.worker_count,
            "start_after_build": self.start_after_build,
            "status": self.status,
            "created_at": self.created_at,
            "started_at": self.started_at,
            "finished_at": self.finished_at,
            "error": self.error,
            "version": self.version,
        }

    async def append(self, line: str) -> None:
        self.output.append(line.rstrip("\r\n"))
        if len(self.output) > 5000:
            del self.output[:1000]
        async with self.changed:
            self.version += 1
            self.changed.notify_all()

    async def touch(self) -> None:
        async with self.changed:
            self.version += 1
            self.changed.notify_all()


class BuildJobManager:
    def __init__(self, manager: HeadlessManager):
        self.manager = manager
        self.jobs: dict[str, BuildJob] = {}
        self.build_lock = asyncio.Lock()
        self.tasks: set[asyncio.Task[None]] = set()

    def get(self, job_id: str) -> BuildJob:
        try:
            return self.jobs[job_id]
        except KeyError as exc:
            raise ControlError("Không tìm thấy build job") from exc

    def active_job(self) -> BuildJob | None:
        return next(
            (job for job in self.jobs.values() if job.status in {"queued", "running"}),
            None,
        )

    async def create(self, worker_count: int, start_after_build: bool) -> BuildJob:
        if worker_count < 1 or worker_count > 500:
            raise ControlError("Số worker phải từ 1 đến 500")
        account_count = int(self.manager.account_summary()["count"])
        if account_count == 0:
            raise ControlError("Chưa có account.csv")
        if worker_count > account_count:
            raise ControlError(f"Có {account_count} account nhưng yêu cầu {worker_count} worker")
        active = self.active_job()
        if active is not None:
            raise ControlError(f"Build {active.id} đang chạy")

        job = BuildJob(
            id=uuid.uuid4().hex[:12],
            worker_count=worker_count,
            start_after_build=start_after_build,
        )
        self.jobs[job.id] = job
        task = asyncio.create_task(self._run(job))
        self.tasks.add(task)
        task.add_done_callback(self.tasks.discard)
        return job

    async def _run(self, job: BuildJob) -> None:
        async with self.build_lock:
            job.status = "running"
            job.started_at = utc_now()
            await job.touch()
            was_desired = self.manager.desired_supervisor()
            was_running = self.manager.supervisor_status()["running"]
            should_restart = job.start_after_build or was_desired or was_running
            try:
                await job.append("Đang dừng supervisor và worker...")
                await self.manager.stop_supervisor(remember=False)
                await job.append(f"Đang build headless và chia {job.worker_count} worker...")
                env = self.manager.settings.command_env()
                env["BUILD_HEADLESS"] = "1"
                process = await asyncio.create_subprocess_exec(
                    str(self.manager._script("build-workers.sh")),
                    str(job.worker_count),
                    cwd=self.manager.settings.repo_dir,
                    env=env,
                    stdout=asyncio.subprocess.PIPE,
                    stderr=asyncio.subprocess.STDOUT,
                    start_new_session=True,
                )
                assert process.stdout is not None
                try:
                    return_code = await asyncio.wait_for(
                        self._stream_process(job, process),
                        timeout=self.manager.settings.build_timeout,
                    )
                except TimeoutError:
                    await self._terminate_process_group(process)
                    raise ControlError(
                        f"Build vượt quá {self.manager.settings.build_timeout} giây"
                    )
                if return_code != 0:
                    raise ControlError(f"Build thất bại với exit code {return_code}")

                main_class = self.manager.settings.headless_dir / "build" / "classes" / "HeadlessMain.class"
                if not main_class.is_file():
                    raise ControlError("Build xong nhưng thiếu HeadlessMain.class")
                worker_dirs = list(self.manager.settings.workers_dir.glob("worker-*"))
                if len(worker_dirs) != job.worker_count:
                    raise ControlError(
                        f"Build tạo {len(worker_dirs)}/{job.worker_count} worker"
                    )
                await job.append("Build và kiểm tra worker thành công.")
                if should_restart:
                    await job.append("Đang khởi động supervisor...")
                    await self.manager.start_supervisor(remember=True)
                else:
                    self.manager._set_desired_supervisor(False)
                job.status = "succeeded"
            except Exception as exc:
                job.status = "failed"
                job.error = str(exc)
                await job.append(f"LỖI: {exc}")
                if was_desired or was_running:
                    try:
                        await job.append("Đang phục hồi supervisor với runtime cũ...")
                        await self.manager.start_supervisor(remember=True)
                    except Exception as restart_exc:
                        await job.append(f"Không thể phục hồi supervisor: {restart_exc}")
            finally:
                job.finished_at = utc_now()
                await job.touch()

    @staticmethod
    async def _stream_process(
        job: BuildJob, process: asyncio.subprocess.Process
    ) -> int:
        assert process.stdout is not None
        while line := await process.stdout.readline():
            await job.append(line.decode("utf-8", errors="replace"))
        return await process.wait()

    @staticmethod
    async def _terminate_process_group(process: asyncio.subprocess.Process) -> None:
        if process.returncode is not None:
            return
        try:
            os.killpg(process.pid, signal.SIGTERM)
        except ProcessLookupError:
            return
        try:
            await asyncio.wait_for(process.wait(), timeout=5)
        except TimeoutError:
            try:
                os.killpg(process.pid, signal.SIGKILL)
            except ProcessLookupError:
                return
            await process.wait()
