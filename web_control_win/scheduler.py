from __future__ import annotations

import asyncio
import json
import logging
from datetime import datetime, time, timedelta, timezone
from pathlib import Path
from typing import Any, Dict, Literal, Optional

from .jobs import WindowsBuildJobManager
from .manager import ControlError, WindowsHeadlessManager

logger = logging.getLogger(__name__)

# Múi giờ Việt Nam GMT+7
TZ_VN = timezone(timedelta(hours=7), name="GMT+7")


class WindowsScheduleManager:
    """Quản lý hẹn giờ tự động Build & Run NVHN trên Windows Server."""

    def __init__(
        self,
        web_runtime_dir: Path,
        jobs: WindowsBuildJobManager,
        manager: Optional[WindowsHeadlessManager] = None,
    ):
        self.web_runtime_dir = web_runtime_dir
        self.jobs = jobs
        self.manager = manager
        self.state_file = web_runtime_dir / "schedule.json"

        # Cấu hình mặc định
        self.enabled: bool = False
        self.mode: Literal["daily", "interval"] = "daily"
        self.daily_time: str = "01:00"  # HH:MM
        self.interval_hours: int = 6     # 1 - 72 giờ
        self.worker_count: int = 10
        self.last_run_at: Optional[str] = None
        self.next_run_at: Optional[str] = None

        self._task: Optional[asyncio.Task[None]] = None
        self._load()

    def _load(self) -> None:
        if not self.state_file.is_file():
            self._update_next_run()
            return
        try:
            data = json.loads(self.state_file.read_text(encoding="utf-8"))
            self.enabled = bool(data.get("enabled", False))
            self.mode = data.get("mode", "daily") if data.get("mode") in {"daily", "interval"} else "daily"
            self.daily_time = str(data.get("daily_time", "01:00"))
            self.interval_hours = max(1, min(int(data.get("interval_hours", 6)), 72))
            self.worker_count = max(1, min(int(data.get("worker_count", 10)), 500))
            self.last_run_at = data.get("last_run_at")
            self.next_run_at = data.get("next_run_at")
        except Exception:
            pass

        self._update_next_run()

    def _save(self) -> None:
        self.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        data = {
            "enabled": self.enabled,
            "mode": self.mode,
            "daily_time": self.daily_time,
            "interval_hours": self.interval_hours,
            "worker_count": self.worker_count,
            "last_run_at": self.last_run_at,
            "next_run_at": self.next_run_at,
        }
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        temporary.replace(self.state_file)

    def _update_next_run(self) -> None:
        if not self.enabled:
            self.next_run_at = None
            return

        now = datetime.now(TZ_VN)
        if self.mode == "daily":
            try:
                parts = self.daily_time.strip().split(":")
                hour = int(parts[0])
                minute = int(parts[1]) if len(parts) > 1 else 0
                target_time = time(hour=hour, minute=minute)
            except Exception:
                target_time = time(hour=1, minute=0)
                self.daily_time = "01:00"

            today_target = datetime.combine(now.date(), target_time, tzinfo=TZ_VN)
            if now < today_target:
                next_dt = today_target
            else:
                next_dt = today_target + timedelta(days=1)
            self.next_run_at = next_dt.isoformat(timespec="seconds")
        elif self.mode == "interval":
            delta = timedelta(hours=self.interval_hours)
            if self.last_run_at:
                try:
                    last_dt = datetime.fromisoformat(self.last_run_at)
                    if last_dt.tzinfo is None:
                        last_dt = last_dt.replace(tzinfo=TZ_VN)
                    next_dt = last_dt + delta
                    if next_dt <= now:
                        next_dt = now + timedelta(seconds=10)
                except Exception:
                    next_dt = now + delta
            else:
                next_dt = now + delta
            self.next_run_at = next_dt.isoformat(timespec="seconds")

    def get_state(self) -> Dict[str, Any]:
        return {
            "enabled": self.enabled,
            "mode": self.mode,
            "daily_time": self.daily_time,
            "interval_hours": self.interval_hours,
            "worker_count": self.worker_count,
            "last_run_at": self.last_run_at,
            "next_run_at": self.next_run_at,
        }

    def update_config(
        self,
        enabled: bool,
        mode: Literal["daily", "interval"],
        daily_time: str,
        interval_hours: int,
        worker_count: int,
    ) -> Dict[str, Any]:
        self.enabled = enabled
        self.mode = mode
        self.daily_time = daily_time
        self.interval_hours = max(1, min(interval_hours, 72))
        self.worker_count = max(1, min(worker_count, 500))
        self._update_next_run()
        self._save()
        return self.get_state()

    async def start_loop(self) -> None:
        if self._task is None or self._task.done():
            self._task = asyncio.create_task(self._scheduler_loop())

    async def stop_loop(self) -> None:
        if self._task is not None:
            self._task.cancel()
            try:
                await self._task
            except asyncio.CancelledError:
                pass
            self._task = None

    async def _scheduler_loop(self) -> None:
        while True:
            try:
                await asyncio.sleep(10)
                if not self.enabled or not self.next_run_at:
                    continue

                now = datetime.now(TZ_VN)
                try:
                    target = datetime.fromisoformat(self.next_run_at)
                    if target.tzinfo is None:
                        target = target.replace(tzinfo=TZ_VN)
                except Exception:
                    self._update_next_run()
                    self._save()
                    continue

                if now >= target:
                    logger.info("Đã đến giờ hẹn tự động NVHN: %s", self.next_run_at)
                    await self._trigger_scheduled_run()
            except asyncio.CancelledError:
                break
            except Exception as e:
                logger.error("Lỗi trong vòng lặp scheduler: %s", e)

    async def _trigger_scheduled_run(self) -> None:
        now_iso = datetime.now(TZ_VN).isoformat(timespec="seconds")
        self.last_run_at = now_iso
        self._update_next_run()
        self._save()

        if self.jobs.active_job() is not None:
            logger.warning("Bỏ qua lịch hẹn vì đang có build job khác đang chạy.")
            return

        try:
            logger.info("Khởi động tự động build & run NVHN với %d workers...", self.worker_count)
            await self.jobs.create(
                worker_count=self.worker_count,
                start_after_build=True,
            )
        except Exception as exc:
            logger.error("Lỗi khi tự động kích hoạt Build & Run: %s", exc)
