from __future__ import annotations

import asyncio
import json
from contextlib import asynccontextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import AsyncIterator, Literal, Optional

from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import FileResponse, JSONResponse, StreamingResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from .config import Settings
from .jobs import BuildJobManager
from .manager import ControlError, HeadlessManager
from .scheduler import ScheduleManager


class BuildRequest(BaseModel):
    worker_count: int = Field(ge=1, le=500)
    start_after_build: bool = True
    server: Literal["ninjamobile", "ninjamobileSV4", "tk"] = "tk"


class SupervisorRequest(BaseModel):
    server: Literal["ninjamobile", "ninjamobileSV4", "tk"] = "tk"
    periodic_restart_hours: Optional[int] = Field(default=None, ge=0, le=168)
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)


class SupervisorSettingsRequest(BaseModel):
    periodic_restart_hours: int = Field(default=3, ge=0, le=168)
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)


class ScheduleRequest(BaseModel):
    enabled: bool
    # Cấu hình mới: chạy lần đầu tại start_time, sau đó lặp mỗi repeat_hours.
    start_time: Optional[str] = None
    repeat_hours: Optional[int] = Field(default=None, ge=1, le=72)
    # Đọc request cũ để người dùng không mất cấu hình khi frontend chưa được
    # refresh đồng thời với backend.
    mode: Optional[Literal["daily", "interval", "start_then_repeat"]] = None
    daily_time: Optional[str] = None
    interval_hours: Optional[int] = Field(default=None, ge=1, le=72)
    worker_count: int = 10
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)
    auto_ta_thu: bool = True
    server: Literal["ninjamobile", "ninjamobileSV4", "tk"] = "tk"


def create_app(settings: Settings | None = None) -> FastAPI:
    settings = settings or Settings.from_env()
    manager = HeadlessManager(settings)
    jobs = BuildJobManager(manager)
    scheduler = ScheduleManager(settings.runtime_dir, jobs, manager)
    static_dir = Path(__file__).with_name("static")

    @asynccontextmanager
    async def lifespan(_app: FastAPI):
        await manager.reconcile()
        await scheduler.start_loop()
        yield
        await scheduler.stop_loop()

    app = FastAPI(
        title="NSO Headless Control",
        version="0.1.0",
        lifespan=lifespan,
        docs_url=None,
        redoc_url=None,
        openapi_url=None,
    )
    app.state.settings = settings
    app.state.manager = manager
    app.state.jobs = jobs
    app.state.scheduler = scheduler
    app.mount("/static", StaticFiles(directory=static_dir), name="static")

    @app.exception_handler(ControlError)
    async def control_error_handler(_request: Request, exc: ControlError) -> JSONResponse:
        return JSONResponse(status_code=400, content={"detail": str(exc)})

    def require_idle() -> None:
        active = jobs.active_job()
        if active is not None:
            raise ControlError(f"Build {active.id} đang chạy; hãy chờ build hoàn tất")

    @app.get("/", include_in_schema=False)
    async def index() -> FileResponse:
        return FileResponse(static_dir / "index.html")

    @app.get("/health")
    async def health() -> dict[str, object]:
        return {"ok": True}

    @app.get("/api/status")
    async def status() -> dict[str, object]:
        data = await manager.status()
        data["server"] = manager.selected_server()
        data["account"] = manager.account_summary()
        data["schedule"] = scheduler.get_state()
        data["current_phase"] = scheduler.current_phase
        data["ta_thu"] = manager.ta_thu_supervisor_status()
        active = jobs.active_job()
        data["active_job"] = active.public() if active else None
        return data

    @app.get("/api/ta-thu/status")
    async def ta_thu_status() -> dict[str, object]:
        status_script = settings.ta_thu_dir / "scripts" / "status-workers.sh"
        if not status_script.is_file():
            return {"workers": [], "totals": {"running": 0, "stopped": 0, "done": 0, "total": 0}}
        code, output = await manager._capture(str(status_script), "--json")
        if code != 0:
            return {"workers": [], "totals": {"running": 0, "stopped": 0, "done": 0, "total": 0}}
        try:
            data = json.loads(output)
            data["supervisor"] = manager.ta_thu_supervisor_status()
            return data
        except Exception:
            return {"workers": [], "totals": {"running": 0, "stopped": 0, "done": 0, "total": 0}}

    @app.get("/api/schedule")
    async def get_schedule() -> dict[str, object]:
        return scheduler.get_state()

    @app.post("/api/schedule")
    async def update_schedule(body: ScheduleRequest) -> dict[str, object]:
        return scheduler.update_config(
            enabled=body.enabled,
            mode=body.mode,
            start_time=body.start_time or body.daily_time or "01:00",
            repeat_hours=(
                body.repeat_hours
                if body.repeat_hours is not None
                else (body.interval_hours if body.interval_hours is not None else 6)
            ),
            worker_count=body.worker_count,
            worker_start_delay_seconds=body.worker_start_delay_seconds,
            auto_ta_thu=body.auto_ta_thu,
            server=body.server,
        )

    @app.post("/api/supervisor/start")
    async def start_supervisor(body: SupervisorRequest | None = None) -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            try:
                await manager.stop_ta_thu()
                selected_server = body.server if body is not None else manager.selected_server()
                result = await manager.start_supervisor(
                    server=selected_server,
                    periodic_restart_hours=(
                        body.periodic_restart_hours if body is not None else None
                    ),
                    worker_start_delay_seconds=(
                        body.worker_start_delay_seconds if body is not None else None
                    ),
                )
                scheduler.worker_start_delay_seconds = manager.worker_start_delay_seconds()
            except Exception:
                # Không dùng tiến độ cũ để tự chuyển Tà Thú sau Start thất bại.
                manager._set_desired_supervisor(False)
                raise
            scheduler.current_phase = "nvhn"
            scheduler._save()
            return result

    @app.post("/api/supervisor/settings")
    async def update_supervisor_settings(
        body: SupervisorSettingsRequest,
    ) -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            was_running = manager.supervisor_status()["running"]
            manager.set_periodic_restart_hours(body.periodic_restart_hours)
            if body.worker_start_delay_seconds is not None:
                manager.set_worker_start_delay_seconds(body.worker_start_delay_seconds)
                scheduler.worker_start_delay_seconds = manager.worker_start_delay_seconds()
                scheduler._save()
            result = manager.supervisor_status()
            result["requires_restart"] = was_running
            return result

    @app.post("/api/supervisor/stop")
    async def stop_supervisor() -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            # Dừng cả supervisor NVHN và Tà Thú
            await manager.stop_ta_thu()
            return await manager.stop_supervisor()

    @app.post("/api/ta-thu/supervisor/stop")
    async def stop_ta_thu_supervisor() -> dict[str, object]:
        require_idle()
        stopped = await manager.stop_ta_thu()
        return {"ok": stopped, "supervisor": manager.ta_thu_supervisor_status()}

    @app.post("/api/workers/{worker_name}/restart")
    async def restart_worker(worker_name: str) -> dict[str, str]:
        require_idle()
        return {"output": await manager.restart_worker(worker_name)}

    @app.post("/api/workers/{worker_name}/stop")
    async def stop_worker(worker_name: str) -> dict[str, str]:
        require_idle()
        return {"output": await manager.stop_worker(worker_name)}

    @app.post("/api/workers/{worker_name}/start")
    async def start_worker(worker_name: str) -> dict[str, str]:
        require_idle()
        return {"output": await manager.start_worker(worker_name)}

    @app.get("/api/workers/{worker_name}/logs")
    async def worker_logs(
        worker_name: str,
        kind: Literal["stdout", "error"] = "stdout",
        lines: int = 200,
        runtime: str = "auto",
    ) -> dict[str, str]:
        return {"content": manager.tail_log(worker_name, kind, lines, runtime=runtime)}

    @app.get("/api/workers/{worker_name}/logs/stream")
    async def worker_log_stream(
        worker_name: str,
        request: Request,
        kind: Literal["stdout", "error"] = "stdout",
        runtime: str = "auto",
    ) -> StreamingResponse:
        path = manager.log_path(worker_name, kind, runtime=runtime)

        async def stream() -> AsyncIterator[str]:
            position = 0
            initial = manager.tail_log(worker_name, kind, 100, runtime=runtime)
            if initial:
                try:
                    modified_at = datetime.fromtimestamp(
                        path.stat().st_mtime, timezone.utc
                    ).astimezone().isoformat(timespec="seconds")
                except OSError:
                    modified_at = datetime.now(timezone.utc).isoformat(timespec="seconds")
                payload = {"text": initial, "timestamp": modified_at, "initial": True}
                yield f"event: log\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"
            if path.exists():
                position = path.stat().st_size
            while True:
                if await request.is_disconnected():
                    return
                try:
                    size = path.stat().st_size
                    if size < position:
                        position = 0
                    if size > position:
                        with path.open("rb") as log_stream:
                            log_stream.seek(position)
                            chunk = log_stream.read()
                        position += len(chunk)
                        text = chunk.decode("utf-8", errors="replace")
                        timestamp = datetime.fromtimestamp(
                            path.stat().st_mtime, timezone.utc
                        ).astimezone().isoformat(timespec="seconds")
                        payload = {"text": text, "timestamp": timestamp, "initial": False}
                        yield f"event: log\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"
                    else:
                        yield ": keepalive\n\n"
                except OSError:
                    yield ": waiting-for-log\n\n"
                await asyncio.sleep(1)

        return StreamingResponse(
            stream(),
            media_type="text/event-stream",
            headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
        )

    @app.post("/api/accounts/upload")
    async def upload_accounts(request: Request) -> dict[str, int]:
        require_idle()
        content_length = request.headers.get("content-length")
        if content_length:
            try:
                if int(content_length) > settings.max_upload_bytes:
                    raise HTTPException(status_code=413, detail="File account vượt quá giới hạn 2 MB")
            except ValueError:
                raise HTTPException(status_code=400, detail="Content-Length không hợp lệ")
        content = bytearray()
        async for chunk in request.stream():
            content.extend(chunk)
            if len(content) > settings.max_upload_bytes:
                raise HTTPException(status_code=413, detail="File account vượt quá giới hạn 2 MB")
        return {"count": manager.validate_and_store_account_csv(bytes(content))}

    @app.post("/api/build")
    async def build(body: BuildRequest) -> dict[str, object]:
        job = await jobs.create(body.worker_count, body.start_after_build, body.server)
        return job.public()

    @app.get("/api/jobs/{job_id}")
    async def build_job(job_id: str) -> dict[str, object]:
        job = jobs.get(job_id)
        result = job.public()
        result["output"] = job.output
        return result

    @app.get("/api/jobs/{job_id}/events")
    async def build_events(job_id: str) -> StreamingResponse:
        job = jobs.get(job_id)

        async def stream() -> AsyncIterator[str]:
            cursor = 0
            while True:
                while cursor < len(job.output):
                    line = job.output[cursor]
                    cursor += 1
                    yield f"event: output\ndata: {json.dumps(line, ensure_ascii=False)}\n\n"
                yield f"event: status\ndata: {json.dumps(job.public(), ensure_ascii=False)}\n\n"
                if job.status in {"succeeded", "failed"}:
                    return
                version = job.version
                try:
                    async with job.changed:
                        await asyncio.wait_for(
                            job.changed.wait_for(lambda: job.version != version), timeout=15
                        )
                except TimeoutError:
                    yield ": keepalive\n\n"

        return StreamingResponse(
            stream(),
            media_type="text/event-stream",
            headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
        )

    return app


app = create_app()
