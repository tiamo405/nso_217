from __future__ import annotations

import asyncio
import json
from contextlib import asynccontextmanager
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, AsyncIterator, Literal, Optional

from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import FileResponse, JSONResponse, StreamingResponse
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field

from .config import Settings
from .jobs import BuildJobManager
from .logparse import (
    level_at_least,
    parse_line,
    parse_lines,
    read_new_lines,
    read_tail_lines,
)
from .manager import RUNTIMES, ControlError, HeadlessManager
from .scheduler import ScheduleManager

Runtime = Literal["nvhn", "ta_thu"]
LogKind = Literal["stdout", "error", "supervisor"]


class BuildRequest(BaseModel):
    worker_count: int = Field(ge=1, le=500)
    start_after_build: bool = True
    # Chỉ còn 1 server game; giữ field để tương thích client cũ, giá trị bị bỏ qua.
    server: Optional[str] = None
    proxy_workers_per_proxy: Optional[int] = Field(default=None, ge=0, le=500)


class SupervisorRequest(BaseModel):
    # Chỉ còn 1 server game; giữ field để tương thích client cũ, giá trị bị bỏ qua.
    server: Optional[str] = None
    periodic_restart_hours: Optional[int] = Field(default=None, ge=0, le=168)
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)
    proxy_workers_per_proxy: Optional[int] = Field(default=None, ge=0, le=500)


class SupervisorSettingsRequest(BaseModel):
    periodic_restart_hours: int = Field(default=0, ge=0, le=168)
    worker_start_delay_seconds: Optional[int] = Field(default=None, ge=0, le=3600)
    proxy_workers_per_proxy: Optional[int] = Field(default=None, ge=0, le=500)


class TaThuStartRequest(BaseModel):
    worker_count: Optional[int] = Field(default=None, ge=1, le=500)
    # True: chia lại worker Tà Thú từ account.csv; False: dùng worker hiện có.
    rebuild: Optional[bool] = None


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
    proxy_workers_per_proxy: Optional[int] = Field(default=None, ge=0, le=500)
    auto_ta_thu: bool = True
    # Chỉ còn 1 server game; giữ field để tương thích client cũ, giá trị bị bỏ qua.
    server: Optional[str] = None


def create_app(
    settings: Settings | None = None,
    *,
    manager: Any = None,
    jobs: Any = None,
    scheduler: Any = None,
) -> FastAPI:
    """Tạo app dashboard.

    Ubuntu: create_app(settings). Windows (web_control_win) truyền manager/jobs
    riêng nhưng dùng chung toàn bộ API, scheduler, parser log và giao diện.
    """
    if manager is None:
        manager = HeadlessManager(settings or Settings.from_env())
    settings = manager.settings
    jobs = jobs or BuildJobManager(manager)
    scheduler = scheduler or ScheduleManager(manager.web_runtime_dir, jobs, manager)
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
        data["ta_thu"]["desired"] = manager.desired_ta_thu()
        data["ta_thu_runtime"] = await manager.ta_thu_status()
        active = jobs.active_job()
        data["active_job"] = active.public() if active else None
        return data

    @app.get("/api/ta-thu/status")
    async def ta_thu_status() -> dict[str, object]:
        return await manager.ta_thu_status()

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
            proxy_workers_per_proxy=body.proxy_workers_per_proxy,
            auto_ta_thu=body.auto_ta_thu,
            server=body.server,
        )

    @app.post("/api/supervisor/start")
    async def start_supervisor(body: SupervisorRequest | None = None) -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            try:
                if not await manager.stop_ta_thu():
                    raise ControlError("Không dừng được toàn bộ Tà Thú trước khi Start NVHN")
                selected_server = body.server if body is not None else manager.selected_server()
                result = await manager.start_supervisor(
                    server=selected_server,
                    periodic_restart_hours=(
                        body.periodic_restart_hours if body is not None else None
                    ),
                    worker_start_delay_seconds=(
                        body.worker_start_delay_seconds if body is not None else None
                    ),
                    proxy_workers_per_proxy=(
                        body.proxy_workers_per_proxy if body is not None else None
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
            if body.proxy_workers_per_proxy is not None:
                manager.set_proxy_workers_per_proxy(body.proxy_workers_per_proxy)
            result = manager.supervisor_status()
            result["requires_restart"] = was_running
            return result

    @app.post("/api/supervisor/stop")
    async def stop_supervisor() -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            # Dừng cả supervisor NVHN và Tà Thú
            if not await manager.stop_ta_thu():
                raise ControlError("Không dừng được toàn bộ Tà Thú")
            return await manager.stop_supervisor()

    @app.post("/api/nvhn/supervisor/stop")
    async def stop_nvhn_supervisor() -> dict[str, object]:
        """Chỉ dừng NVHN (supervisor + worker), không đụng Tà Thú."""
        async with scheduler.transition_lock:
            require_idle()
            return await manager.stop_supervisor()

    @app.post("/api/ta-thu/supervisor/start")
    async def start_ta_thu_supervisor(body: TaThuStartRequest | None = None) -> dict[str, object]:
        body = body or TaThuStartRequest()
        async with scheduler.transition_lock:
            require_idle()
            if manager.supervisor_status()["running"]:
                raise ControlError(
                    "NVHN đang chạy cùng account; hãy Stop NVHN trước khi chạy Tà Thú"
                )
            workers_root = manager.ta_thu_workers_dir
            existing = sorted(p for p in workers_root.glob("worker-*") if p.is_dir())
            rebuild = body.rebuild if body.rebuild is not None else not existing
            worker_count = (
                body.worker_count
                or (len(existing) if existing and not rebuild else None)
                or scheduler.worker_count
            )
            started = await manager.start_ta_thu(
                worker_count=worker_count,
                rebuild_workers=rebuild,
                nvhn_workers_dir=None,
            )
            if not started:
                raise ControlError(
                    f"Không khởi động được Tà Thú. Xem log supervisor Tà Thú ({manager.ta_thu_supervisor_log})"
                )
            scheduler.current_phase = "ta_thu"
            scheduler._save()
            return {"ok": True, "supervisor": manager.ta_thu_supervisor_status()}

    @app.post("/api/ta-thu/supervisor/stop")
    async def stop_ta_thu_supervisor() -> dict[str, object]:
        async with scheduler.transition_lock:
            require_idle()
            stopped = await manager.stop_ta_thu()
            # Giữ pha ta_thu + ta_thu_desired=False: scheduler không tự bật lại,
            # và cũng không coi NVHN xong là tín hiệu chạy Tà Thú lần nữa.
            scheduler.current_phase = "ta_thu"
            scheduler._save()
            return {"ok": stopped, "supervisor": manager.ta_thu_supervisor_status()}

    @app.post("/api/ta-thu/workers/{worker_name}/{action}")
    async def ta_thu_worker_action(
        worker_name: str, action: Literal["start", "stop", "restart"]
    ) -> dict[str, str]:
        require_idle()
        return {"output": await manager.ta_thu_worker_action(worker_name, action)}

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

    def resolve_log(runtime: str, worker: str, kind: str) -> Path:
        if runtime not in RUNTIMES:
            raise ControlError("Runtime không hợp lệ")
        return manager.log_path(worker, kind, runtime=runtime)

    def file_mtime(path: Path) -> str | None:
        try:
            return datetime.fromtimestamp(path.stat().st_mtime, timezone.utc).astimezone().isoformat(
                timespec="seconds"
            )
        except OSError:
            return None

    def filter_entries(
        entries: list[dict[str, object]], level: str, query: str
    ) -> list[dict[str, object]]:
        needle = query.strip().lower()
        result = []
        for entry in entries:
            if entry["kind"] != "session" and not level_at_least(str(entry["level"]), level):
                continue
            if needle and needle not in str(entry["msg"]).lower():
                continue
            result.append(entry)
        return result

    @app.get("/api/logs")
    async def read_logs(
        worker: str = "worker-01",
        runtime: Runtime = "nvhn",
        kind: LogKind = "stdout",
        lines: int = 500,
        level: Literal["debug", "info", "success", "warn", "error"] = "debug",
        q: str = "",
    ) -> dict[str, object]:
        """Đọc log có cấu trúc; có lọc theo level và chuỗi tìm kiếm."""
        path = resolve_log(runtime, worker, kind)
        lines = max(1, min(lines, 5000))
        # Khi tìm kiếm thì quét rộng hơn rồi mới cắt theo số dòng yêu cầu.
        scan = 20000 if q.strip() or level != "debug" else lines
        raw, size = read_tail_lines(path, scan, max_bytes=4 * 1024 * 1024)
        entries = filter_entries(parse_lines(raw), level, q)[-lines:]
        return {"lines": entries, "offset": size, "modified_at": file_mtime(path), "path": path.name}

    @app.get("/api/logs/download")
    async def download_log(
        worker: str = "worker-01",
        runtime: Runtime = "nvhn",
        kind: LogKind = "stdout",
    ) -> FileResponse:
        path = resolve_log(runtime, worker, kind)
        if not path.is_file():
            raise HTTPException(status_code=404, detail="Chưa có file log")
        label = "supervisor" if kind == "supervisor" else worker
        return FileResponse(
            path,
            media_type="text/plain; charset=utf-8",
            filename=f"{runtime}-{label}-{path.name}",
        )

    @app.get("/api/logs/stream")
    async def stream_logs(
        request: Request,
        worker: str = "worker-01",
        runtime: Runtime = "nvhn",
        kind: LogKind = "stdout",
        tail: int = 400,
        offset: int = -1,
    ) -> StreamingResponse:
        """SSE log theo dòng.

        event `lines`: {lines:[entry], offset, initial, modified_at}; SSE id là
        byte offset đã đọc, trình duyệt gửi lại qua Last-Event-ID khi kết nối
        lại nên không mất hay lặp dòng. event `reset`: file bị cắt/xoay vòng.
        """
        path = resolve_log(runtime, worker, kind)
        tail = max(0, min(tail, 3000))
        last_event_id = request.headers.get("last-event-id", "")
        if last_event_id.isdigit():
            offset = int(last_event_id)

        def sse(event: str, payload: dict[str, object], event_id: int | None = None) -> str:
            head = f"id: {event_id}\n" if event_id is not None else ""
            return f"{head}event: {event}\ndata: {json.dumps(payload, ensure_ascii=False)}\n\n"

        async def stream() -> AsyncIterator[str]:
            position = offset
            if position < 0:
                raw, position = read_tail_lines(path, tail) if tail else ([], 0)
                if not tail:
                    try:
                        position = path.stat().st_size
                    except OSError:
                        position = 0
                yield sse(
                    "lines",
                    {"lines": parse_lines(raw), "offset": position, "initial": True,
                     "modified_at": file_mtime(path), "path": path.name},
                    position,
                )
            else:
                yield sse("lines", {"lines": [], "offset": position, "initial": True,
                                    "modified_at": file_mtime(path), "path": path.name}, position)
            idle = 0.0
            while True:
                if await request.is_disconnected():
                    return
                raw, position, reset = read_new_lines(path, position)
                if reset:
                    yield sse("reset", {"offset": 0})
                if raw:
                    now = datetime.now().strftime("%H:%M:%S")
                    entries = []
                    for line in raw:
                        if not line.strip():
                            continue
                        entry = parse_line(line)
                        if entry["ts"] is None:
                            # Dòng không có prefix giờ: dùng giờ server nhận được.
                            entry["ts"] = now
                            entry["ts_estimated"] = True
                        entries.append(entry)
                    if entries:
                        yield sse(
                            "lines",
                            {"lines": entries, "offset": position, "initial": False,
                             "modified_at": file_mtime(path)},
                            position,
                        )
                    idle = 0.0
                else:
                    idle += 0.5
                    if idle >= 15:
                        idle = 0.0
                        yield ": keepalive\n\n"
                    await asyncio.sleep(0.5)

        return StreamingResponse(
            stream(),
            media_type="text/event-stream",
            headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
        )

    @app.get("/api/workers/{worker_name}/logs")
    async def worker_logs(
        worker_name: str,
        kind: Literal["stdout", "error"] = "stdout",
        lines: int = 200,
        runtime: str = "auto",
    ) -> dict[str, str]:
        """Giữ cho client cũ: trả text thô."""
        return {"content": manager.tail_log(worker_name, kind, lines, runtime=runtime)}

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
        job = await jobs.create(
            body.worker_count,
            body.start_after_build,
            body.server,
            body.proxy_workers_per_proxy,
        )
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
                except asyncio.TimeoutError:
                    yield ": keepalive\n\n"

        return StreamingResponse(
            stream(),
            media_type="text/event-stream",
            headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"},
        )

    return app


app = create_app()
