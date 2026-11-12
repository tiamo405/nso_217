"""Dashboard Windows: dùng chung API, scheduler, parser log và giao diện với Ubuntu.

Chỉ phần điều khiển tiến trình là riêng (WindowsHeadlessManager gọi
optimized-runtime/windows/win_manager.py và ta-thu-runtime/windows/ta_thu_manager.py,
WindowsBuildJobManager build qua win_manager.py).
"""

from __future__ import annotations

from typing import Optional

from fastapi import FastAPI

from web_control.app import create_app as create_shared_app

from .config import Settings
from .jobs import WindowsBuildJobManager
from .manager import ControlError, WindowsHeadlessManager  # noqa: F401
from .scheduler import WindowsScheduleManager


def create_app(settings: Optional[Settings] = None) -> FastAPI:
    settings = settings or Settings.from_env()
    manager = WindowsHeadlessManager(settings)
    jobs = WindowsBuildJobManager(manager)
    scheduler = WindowsScheduleManager(settings.web_runtime_dir, jobs, manager)
    return create_shared_app(manager=manager, jobs=jobs, scheduler=scheduler)


app = create_app()
