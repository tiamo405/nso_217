from __future__ import annotations

import asyncio
import json
from contextlib import asynccontextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import AsyncIterator, Literal

from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import FileResponse, JSONResponse, StreamingResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from .config import Settings
from .jobs import BuildJobManager
from .manager import ControlError, HeadlessManager


class BuildRequest(BaseModel):
    worker_count: int = Field(ge=1, le=500)
    start_after_build: bool = True


def create_app(settings: Settings | None = None) -> FastAPI:
    settings = settings or Settings.from_env()
    manager = HeadlessManager(settings)
    jobs = BuildJobManager(manager)
    static_dir = Path(__file__).with_name("static")

    @asynccontextmanager
    async def lifespan(_app: FastAPI):
        await manager.reconcile()
        yield

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
        data["account"] = manager.account_summary()
        active = jobs.active_job()
        data["active_job"] = active.public() if active else None
        return data

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

    @app.get("/api/workers/{worker_name}/logs")
    async def worker_logs(
        worker_name: str,
        kind: Literal["stdout", "error"] = "stdout",
        lines: int = 200,
    ) -> dict[str, str]:
        return {"content": manager.tail_log(worker_name, kind, lines)}

    @app.get("/api/workers/{worker_name}/logs/stream")
    async def worker_log_stream(
        worker_name: str,
        request: Request,
        kind: Literal["stdout", "error"] = "stdout",
    ) -> StreamingResponse:
        path = manager.log_path(worker_name, kind)

        async def stream() -> AsyncIterator[str]:
            position = 0
            initial = manager.tail_log(worker_name, kind, 100)
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
        job = await jobs.create(body.worker_count, body.start_after_build)
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
