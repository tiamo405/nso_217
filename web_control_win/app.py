from __future__ import annotations

import asyncio
import json
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Any, AsyncIterator, Dict, Literal, Optional

from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import FileResponse, JSONResponse, StreamingResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from .config import Settings
from .jobs import WindowsBuildJobManager
from .manager import ControlError, WindowsHeadlessManager
from .scheduler import WindowsScheduleManager


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
    enabled: bool = False
    # Cấu hình mới: chạy lần đầu tại start_time, sau đó lặp mỗi repeat_hours.
    start_time: Optional[str] = None
    repeat_hours: Optional[int] = Field(default=None, ge=1, le=72)
    # Alias đọc request cũ để không làm hỏng client/schedule cũ.
    mode: Optional[Literal["daily", "interval", "start_then_repeat"]] = None
    daily_time: Optional[str] = None
    interval_hours: Optional[int] = Field(default=None, ge=1, le=72)
    worker_count: Optional[int] = 10
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)
    auto_ta_thu: bool = True
    server: Literal["ninjamobile", "ninjamobileSV4", "tk"] = "tk"


def create_app(settings: Optional[Settings] = None) -> FastAPI:
    settings = settings or Settings.from_env()
    manager = WindowsHeadlessManager(settings)
    jobs = WindowsBuildJobManager(manager)
    scheduler = WindowsScheduleManager(settings.web_runtime_dir, jobs, manager)
    static_dir = Path(__file__).with_name("static")

    @asynccontextmanager
    async def lifespan(_app: FastAPI):
        # Khi dashboard khởi động lại trong pha Tà Thú, không được tự dựng
        # lại Supervisor NVHN chỉ vì state supervisor_desired vẫn còn true.
        if not (scheduler.auto_ta_thu and scheduler.current_phase == "ta_thu"):
            await manager.reconcile()
        await scheduler.start_loop()
        yield
        await scheduler.stop_loop()

    app = FastAPI(
        title="NSO Optimized Control (Windows)",
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
    async def health() -> Dict[str, Any]:
        return {"ok": True, "os": "windows"}

    @app.get("/api/status")
    async def status() -> Dict[str, Any]:
        data = await manager.status()
        data["server"] = manager.selected_server()
        data["account"] = manager.account_summary()
        data["schedule"] = scheduler.get_state()
        data["current_phase"] = scheduler.current_phase
        data["ta_thu"] = await manager.ta_thu_status()
        active = jobs.active_job()
        data["active_job"] = active.public() if active else None
        return data

    @app.get("/api/schedule")
    async def get_schedule() -> Dict[str, Any]:
        return scheduler.get_state()

    @app.post("/api/schedule")
    async def update_schedule(body: ScheduleRequest) -> Dict[str, Any]:
        return scheduler.update_config(
            enabled=body.enabled,
            mode=body.mode,
            start_time=body.start_time or body.daily_time or "01:00",
            repeat_hours=(
                body.repeat_hours
                if body.repeat_hours is not None
                else (body.interval_hours if body.interval_hours is not None else 6)
            ),
            worker_count=body.worker_count if body.worker_count is not None else 10,
            worker_start_delay_seconds=body.worker_start_delay_seconds,
            auto_ta_thu=body.auto_ta_thu,
            server=body.server,
        )

    @app.post("/api/supervisor/start")
    async def start_supervisor(body: Optional[SupervisorRequest] = None) -> Dict[str, Any]:
        async with scheduler.transition_lock:
            require_idle()
            selected_server = body.server if body is not None else manager.selected_server()
            periodic_hours = body.periodic_restart_hours if body is not None else None
            start_delay = body.worker_start_delay_seconds if body is not None else None
            if not await manager.stop_ta_thu():
                raise ControlError("Không dừng được toàn bộ Tà Thú trước khi Start NVHN")
            result = await manager.start_supervisor(
                server=selected_server,
                periodic_restart_hours=periodic_hours,
                worker_start_delay_seconds=start_delay,
            )
            scheduler.worker_start_delay_seconds = manager.worker_start_delay_seconds()
            scheduler.current_phase = "nvhn"
            scheduler._save()
            return result

    @app.post("/api/supervisor/settings")
    async def update_supervisor_settings(body: SupervisorSettingsRequest) -> Dict[str, Any]:
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
    async def stop_supervisor() -> Dict[str, Any]:
        async with scheduler.transition_lock:
            require_idle()
            if not await manager.stop_ta_thu():
                raise ControlError("Không dừng được toàn bộ Tà Thú")
            return await manager.stop_supervisor()

    @app.get("/api/ta-thu/status")
    async def ta_thu_status() -> Dict[str, Any]:
        return await manager.ta_thu_status()

    @app.post("/api/ta-thu/supervisor/stop")
    async def stop_ta_thu_supervisor() -> Dict[str, Any]:
        require_idle()
        stopped = await manager.stop_ta_thu()
        return {"ok": stopped, "supervisor": manager.ta_thu_supervisor_status()}

    @app.get("/api/ta-thu/workers/{worker_name}/logs")
    async def ta_thu_worker_logs(
        worker_name: str, kind: Literal["stdout", "error"] = "stdout", lines: int = 200
    ) -> Dict[str, str]:
        return {"content": manager.tail_ta_thu_log(worker_name, kind, lines=lines)}

    @app.post("/api/workers/{worker_name}/restart")
    async def restart_worker(worker_name: str) -> Dict[str, str]:
        require_idle()
        return {"output": await manager.restart_worker(worker_name)}

    @app.post("/api/workers/{worker_name}/stop")
    async def stop_worker(worker_name: str) -> Dict[str, str]:
        require_idle()
        return {"output": await manager.stop_worker(worker_name)}

    @app.post("/api/workers/{worker_name}/start")
    async def start_worker(worker_name: str) -> Dict[str, str]:
        require_idle()
        return {"output": await manager.start_worker(worker_name)}

    @app.get("/api/workers/{worker_name}/logs")
    async def worker_logs(
        worker_name: str, kind: Literal["stdout", "error"] = "stdout", lines: int = 200
    ) -> Dict[str, str]:
        return {"content": manager.tail_log(worker_name, kind, lines=lines)}

    @app.post("/api/account")
    async def upload_account(request: Request) -> Dict[str, Any]:
        require_idle()
        body = await request.body()
        count = manager.validate_and_store_account_csv(body)
        return {"ok": True, "count": count}

    @app.post("/api/build")
    async def build(body: BuildRequest) -> Dict[str, Any]:
        async with scheduler.transition_lock:
            require_idle()
            if not await manager.stop_ta_thu():
                raise ControlError("Không dừng được toàn bộ Tà Thú trước khi Build NVHN")
            scheduler.current_phase = "nvhn"
            scheduler._save()
            job = await jobs.create(
                worker_count=body.worker_count,
                start_after_build=body.start_after_build,
                server=body.server,
            )
            return {"job": job.public()}

    @app.get("/api/build/{job_id}")
    async def get_build(job_id: str) -> Dict[str, Any]:
        job = jobs.get(job_id)
        return {"job": job.public()}

    @app.get("/api/build/{job_id}/events")
    async def build_events(job_id: str) -> StreamingResponse:
        job = jobs.get(job_id)

        async def generator() -> AsyncIterator[str]:
            last_idx = 0
            while True:
                lines = job.output[last_idx:]
                for line in lines:
                    yield f"event: log\ndata: {json.dumps({'line': line})}\n\n"
                last_idx = len(job.output)
                payload = json.dumps(job.public())
                yield f"event: status\ndata: {payload}\n\n"
                if job.status in {"succeeded", "failed"}:
                    break
                async with job.changed:
                    await job.changed.wait()

        return StreamingResponse(
            generator(),
            media_type="text/event-stream",
            headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
        )

    return app


app = create_app()
