from __future__ import annotations

import asyncio
import json
import logging
from datetime import datetime, time, timedelta, timezone
from pathlib import Path
from typing import Any, Dict, Literal, Optional

from server_config import DEFAULT_SERVER, normalize_server

from .jobs import WindowsBuildJobManager
from .manager import (
    DEFAULT_WORKER_START_DELAY_SECONDS,
    ControlError,
    WindowsHeadlessManager,
)

logger = logging.getLogger(__name__)

# Múi giờ Việt Nam GMT+7
TZ_VN = timezone(timedelta(hours=7), name="GMT+7")


class WindowsScheduleManager:
    """Quản lý chu kỳ NVHN -> Tà Thú trên Windows Server."""

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
        # Một lịch gồm hai mốc liên tiếp: chạy lần đầu tại start_time, sau đó
        # build & run lại mỗi repeat_hours. Các tên cũ được đọc để tương
        # thích với schedule.json đã tồn tại.
        self.start_time: str = "01:00"  # HH:MM, GMT+7
        self.repeat_hours: int = 6       # 1 - 72 giờ
        self.worker_count: int = 10
        self.server: str = manager.selected_server() if manager is not None else DEFAULT_SERVER
        self.auto_ta_thu: bool = True
        self.worker_start_delay_seconds: int = (
            manager.worker_start_delay_seconds()
            if manager is not None
            else DEFAULT_WORKER_START_DELAY_SECONDS
        )
        self.current_phase: Literal["nvhn", "ta_thu"] = "nvhn"
        self.last_run_at: Optional[str] = None
        self.next_run_at: Optional[str] = None

        self.transition_lock = asyncio.Lock()
        self._task: Optional[asyncio.Task[None]] = None
        self._load()

    def _load(self) -> None:
        if not self.state_file.is_file():
            self._update_next_run()
            return
        loaded = False
        try:
            data = json.loads(self.state_file.read_text(encoding="utf-8"))
            self.enabled = bool(data.get("enabled", False))
            self.start_time = str(data.get("start_time", data.get("daily_time", "01:00")))
            self.repeat_hours = max(
                1,
                min(
                    int(data.get("repeat_hours", data.get("interval_hours", 6))),
                    72,
                ),
            )
            self.worker_count = max(1, min(int(data.get("worker_count", 10)), 500))
            try:
                self.server = normalize_server(data.get("server"))
            except ValueError:
                pass
            self.auto_ta_thu = bool(data.get("auto_ta_thu", True))
            raw_delay = data.get(
                "worker_start_delay_seconds", self.worker_start_delay_seconds
            )
            try:
                self.worker_start_delay_seconds = max(0, min(int(raw_delay), 3600))
            except (TypeError, ValueError):
                self.worker_start_delay_seconds = DEFAULT_WORKER_START_DELAY_SECONDS
            if self.manager is not None:
                self.manager.set_worker_start_delay_seconds(self.worker_start_delay_seconds)
            phase = data.get("current_phase", "nvhn")
            self.current_phase = phase if phase in {"nvhn", "ta_thu"} else "nvhn"
            self.last_run_at = data.get("last_run_at")
            self.next_run_at = data.get("next_run_at")
            loaded = True
        except Exception:
            pass

        # Giữ next_run_at đã lưu khi reload web/restart service.
        if not self.enabled:
            self.next_run_at = None
        else:
            try:
                self._parse_time(self.start_time)
            except ValueError:
                self.start_time = "01:00"
            if not self._valid_timestamp(self.next_run_at):
                self._update_next_run()

        if loaded and (
            "start_time" not in data
            or "repeat_hours" not in data
            or "worker_start_delay_seconds" not in data
        ):
            self._save()

    def _save(self) -> None:
        self.web_runtime_dir.mkdir(parents=True, exist_ok=True)
        data = {
            "enabled": self.enabled,
            "schedule_type": "start_then_repeat",
            "start_time": self.start_time,
            "repeat_hours": self.repeat_hours,
            # Alias chỉ để client cũ không bị lỗi khi đọc state.
            "mode": "start_then_repeat",
            "daily_time": self.start_time,
            "interval_hours": self.repeat_hours,
            "worker_count": self.worker_count,
            "worker_start_delay_seconds": self.worker_start_delay_seconds,
            "server": self.server,
            "auto_ta_thu": self.auto_ta_thu,
            "current_phase": self.current_phase,
            "last_run_at": self.last_run_at,
            "next_run_at": self.next_run_at,
        }
        temporary = self.state_file.with_suffix(".tmp")
        temporary.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        temporary.replace(self.state_file)

    @staticmethod
    def _valid_timestamp(value: Optional[str]) -> bool:
        if not value:
            return False
        try:
            datetime.fromisoformat(value)
            return True
        except (TypeError, ValueError):
            return False

    @staticmethod
    def _parse_time(value: str) -> time:
        parts = value.strip().split(":")
        if len(parts) != 2 or not parts[0].isdigit() or not parts[1].isdigit():
            raise ValueError("Giờ chạy phải theo định dạng HH:MM (vd 12:30)")
        hour, minute = int(parts[0]), int(parts[1])
        if not (0 <= hour <= 23 and 0 <= minute <= 59):
            raise ValueError("Giờ chạy không hợp lệ (00:00 - 23:59)")
        return time(hour=hour, minute=minute)

    def _update_next_run(self) -> None:
        if not self.enabled:
            self.next_run_at = None
            return

        now = datetime.now(TZ_VN)
        if self.last_run_at:
            try:
                last_dt = datetime.fromisoformat(self.last_run_at)
                if last_dt.tzinfo is None:
                    last_dt = last_dt.replace(tzinfo=TZ_VN)
                else:
                    last_dt = last_dt.astimezone(TZ_VN)
                next_dt = last_dt + timedelta(hours=self.repeat_hours)
            except (TypeError, ValueError):
                self.last_run_at = None
                next_dt = None
        else:
            next_dt = None

        if next_dt is None:
            target_time = self._parse_time(self.start_time)
            next_dt = datetime.combine(now.date(), target_time, tzinfo=TZ_VN)
            if next_dt < now:
                next_dt += timedelta(days=1)
        self.next_run_at = next_dt.isoformat(timespec="seconds")

    def get_state(self) -> Dict[str, Any]:
        return {
            "enabled": self.enabled,
            "schedule_type": "start_then_repeat",
            "start_time": self.start_time,
            "repeat_hours": self.repeat_hours,
            # Alias cho client cũ trong giai đoạn chuyển đổi.
            "mode": "start_then_repeat",
            "daily_time": self.start_time,
            "interval_hours": self.repeat_hours,
            "worker_count": self.worker_count,
            "worker_start_delay_seconds": self.worker_start_delay_seconds,
            "server": self.server,
            "auto_ta_thu": self.auto_ta_thu,
            "current_phase": self.current_phase,
            "last_run_at": self.last_run_at,
            "next_run_at": self.next_run_at,
        }

    def update_config(
        self,
        enabled: bool,
        mode: str | None = None,
        daily_time: str | None = None,
        interval_hours: int | None = None,
        worker_count: int = 10,
        auto_ta_thu: bool = True,
        server: str = DEFAULT_SERVER,
        *,
        start_time: str | None = None,
        repeat_hours: int | None = None,
        worker_start_delay_seconds: int | None = None,
    ) -> Dict[str, Any]:
        selected_start_time = start_time if start_time is not None else daily_time
        selected_repeat_hours = repeat_hours if repeat_hours is not None else interval_hours
        if selected_start_time is None:
            selected_start_time = self.start_time
        if selected_repeat_hours is None:
            selected_repeat_hours = self.repeat_hours
        try:
            parsed_time = self._parse_time(selected_start_time)
        except ValueError as exc:
            raise ControlError(str(exc)) from exc
        if selected_repeat_hours < 1 or selected_repeat_hours > 72:
            raise ControlError("Chu kỳ lặp lại phải từ 1 đến 72 giờ")

        if worker_start_delay_seconds is not None:
            if worker_start_delay_seconds < 0 or worker_start_delay_seconds > 3600:
                raise ControlError("Giãn cách khởi động worker phải từ 0 đến 3600 giây")
            self.worker_start_delay_seconds = int(worker_start_delay_seconds)
            if self.manager is not None:
                self.manager.set_worker_start_delay_seconds(self.worker_start_delay_seconds)

        self.enabled = enabled
        self.start_time = f"{parsed_time.hour:02d}:{parsed_time.minute:02d}"
        self.repeat_hours = int(selected_repeat_hours)
        self.worker_count = max(1, min(worker_count, 500))
        self.auto_ta_thu = bool(auto_ta_thu)
        self.last_run_at = None
        try:
            self.server = normalize_server(server)
        except ValueError as exc:
            raise ControlError(str(exc)) from exc
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
                now = datetime.now(TZ_VN)
                if self.enabled and self.next_run_at:
                    try:
                        target = datetime.fromisoformat(self.next_run_at)
                        if target.tzinfo is None:
                            target = target.replace(tzinfo=TZ_VN)
                        else:
                            target = target.astimezone(TZ_VN)
                    except Exception:
                        self._update_next_run()
                        self._save()
                        target = None

                    if target and now >= target:
                        logger.info("Đã đến giờ hẹn chu kỳ NVHN -> Tà Thú: %s", self.next_run_at)
                        await self._trigger_scheduled_run(now)
                        continue

                await self._check_auto_ta_thu()
            except asyncio.CancelledError:
                break
            except Exception as e:
                logger.error("Lỗi trong vòng lặp scheduler: %s", e)

    async def _trigger_scheduled_run(self, now: datetime | None = None) -> None:
        async with self.transition_lock:
            await self._trigger_scheduled_run_unlocked(now)

    async def _trigger_scheduled_run_unlocked(self, now: datetime | None = None) -> None:
        now = now or datetime.now(TZ_VN)
        if self.jobs.active_job() is not None:
            logger.warning("Đến giờ hẹn Build & Run nhưng có build job đang chạy; sẽ thử lại sau.")
            self.next_run_at = (
                datetime.now(TZ_VN) + timedelta(seconds=30)
            ).isoformat(timespec="seconds")
            self._save()
            return

        try:
            logger.info(
                "Đến lịch: dừng Tà Thú/NVHN rồi build & run lại NVHN với %d workers...",
                self.worker_count,
            )
            if self.manager is not None:
                if not await self.manager.stop_ta_thu():
                    raise ControlError("Không dừng được toàn bộ Tà Thú trước khi build NVHN")
            self.current_phase = "nvhn"
            await self.jobs.create(
                worker_count=self.worker_count,
                start_after_build=True,
                server=self.server,
            )
            self.last_run_at = now.isoformat(timespec="seconds")
            self._update_next_run()
            self._save()
        except Exception as exc:
            logger.error("Lỗi khi tự động kích hoạt Build & Run: %s", exc)
            self.next_run_at = (
                datetime.now(TZ_VN) + timedelta(seconds=30)
            ).isoformat(timespec="seconds")
            self._save()

    async def _check_auto_ta_thu(self) -> None:
        async with self.transition_lock:
            await self._check_auto_ta_thu_unlocked()

    async def _check_auto_ta_thu_unlocked(self) -> None:
        if (
            not self.auto_ta_thu
            or self.current_phase != "nvhn"
            or self.manager is None
            or not self.manager.desired_supervisor()
            or self.jobs.active_job() is not None
        ):
            return

        nvhn_status = await self.manager.status()
        totals = nvhn_status.get("totals", {})
        total_workers = totals.get("total", 0)
        supervisor = nvhn_status.get("supervisor", {})
        if (
            total_workers > 0
            and totals.get("done", 0) == total_workers
            and not supervisor.get("running")
        ):
            logger.info(
                "Tất cả %s worker NVHN đã hoàn thành; build và chạy Tà Thú...",
                total_workers,
            )
            if await self.manager.start_ta_thu(worker_count=self.worker_count):
                self.current_phase = "ta_thu"
                self._save()
                logger.info("Đã khởi chạy thành công Auto Tà Thú trên Windows.")
            else:
                logger.warning("Khởi chạy Auto Tà Thú trên Windows không thành công.")
