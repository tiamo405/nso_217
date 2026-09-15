from __future__ import annotations

import asyncio
import json
from contextlib import asynccontextmanager
from pathlib import Path
from typing import AsyncIterator, Literal

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


class ScheduleRequest(BaseModel):
    enabled: bool
    mode: Literal["daily", "interval"]
    daily_time: str = "01:00"
    interval_hours: int = 6
    worker_count: int = 10


def create_app(settings: Settings | None = None) -> FastAPI:
    settings = settings or Settings.from_env()
    manager = WindowsHeadlessManager(settings)
    jobs = WindowsBuildJobManager(manager)
    scheduler = WindowsScheduleManager(settings.web_runtime_dir, jobs, manager)
    static_dir = Path(__file__).with_name("static")

    @asynccontextmanager
    async def lifespan(_app: FastAPI):
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
    async def health() -> dict[str, object]:
        return {"ok": True, "os": "windows"}

    @app.get("/api/status")
    async def status() -> dict[str, object]:
        data = await manager.status()
        data["account"] = manager.account_summary()
        data["schedule"] = scheduler.get_state()
        active = jobs.active_job()
        data["active_job"] = active.public() if active else None
        return data

    @app.get("/api/schedule")
    async def get_schedule() -> dict[str, object]:
        return scheduler.get_state()

    @app.post("/api/schedule")
    async def update_schedule(body: ScheduleRequest) -> dict[str, object]:
        return scheduler.update_config(
            enabled=body.enabled,
            mode=body.mode,
            daily_time=body.daily_time,
            interval_hours=body.interval_hours,
            worker_count=body.worker_count,
        )

    @app.post("/api/supervisor/start")
    async def start_supervisor() -> dict[str, object]:
        require_idle()
        return await manager.start_supervisor()

    @app.post("/api/supervisor/stop")
    async def stop_supervisor() -> dict[str, object]:
        require_idle()
        return await manager.stop_supervisor()

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
        worker_name: str, kind: Literal["stdout", "error"] = "stdout", lines: int = 200
    ) -> dict[str, str]:
        return {"content": manager.tail_log(worker_name, kind, lines=lines)}

    @app.post("/api/account")
    async def upload_account(request: Request) -> dict[str, object]:
        require_idle()
        body = await request.body()
        count = manager.validate_and_store_account_csv(body)
        return {"ok": True, "count": count}

    @app.post("/api/build")
    async def build(body: BuildRequest) -> dict[str, object]:
        require_idle()
        job = await jobs.create(
            worker_count=body.worker_count,
            start_after_build=body.start_after_build,
        )
        return {"job": job.public()}

    @app.get("/api/build/{job_id}")
    async def get_build(job_id: str) -> dict[str, object]:
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
