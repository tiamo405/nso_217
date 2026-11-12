"""Lịch NVHN -> Tà Thú trên Windows: dùng chung logic với Ubuntu (web_control.scheduler)."""

from __future__ import annotations

from web_control.scheduler import TZ_VN, ScheduleManager  # noqa: F401


class WindowsScheduleManager(ScheduleManager):
    """Giữ tên cũ; toàn bộ logic nằm ở web_control.scheduler.ScheduleManager."""
