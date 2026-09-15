from __future__ import annotations

import asyncio
import json
import logging
from datetime import datetime, time, timedelta, timezone
from pathlib import Path
from typing import Any, Literal

from .jobs import BuildJobManager
from .manager import ControlError, HeadlessManager

logger = logging.getLogger(__name__)

# Múi giờ Việt Nam GMT+7
TZ_VN = timezone(timedelta(hours=7), name="GMT+7")


class ScheduleManager:
    def __init__(self, runtime_dir: Path, jobs: BuildJobManager, manager: HeadlessManager | None = None):
        self.runtime_dir = runtime_dir
        self.jobs = jobs
        self.manager = manager
        self.state_file = runtime_dir / "schedule.json"

        # Cấu hình mặc định
        self.enabled: bool = False
        self.mode: Literal["daily", "interval"] = "daily"
        self.daily_time: str = "01:00"  # HH:MM
        self.interval_hours: int = 6     # 1 - 72 giờ
        self.worker_count: int = 10
        self.auto_ta_thu: bool = True    # Tự động chạy Tà Thú sau khi NVHN xong
        self.current_phase: Literal["nvhn", "ta_thu"] = "nvhn"
        self.last_run_at: str | None = None
        self.next_run_at: str | None = None

        self.transition_lock = asyncio.Lock()
        self._task: asyncio.Task[None] | None = None
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
            self.auto_ta_thu = bool(data.get("auto_ta_thu", True))
            self.current_phase = data.get("current_phase", "nvhn")
            self.last_run_at = data.get("last_run_at")
            self.next_run_at = data.get("next_run_at")
        except Exception:
            pass

        # Tính toán lại nếu chưa có next_run_at hoặc đã quá hạn khi khởi động
        self._update_next_run()

    def _save(self) -> None:
        self.runtime_dir.mkdir(parents=True, exist_ok=True)
        data = {
            "enabled": self.enabled,
            "mode": self.mode,
            "daily_time": self.daily_time,
            "interval_hours": self.interval_hours,
            "worker_count": self.worker_count,
            "auto_ta_thu": self.auto_ta_thu,
            "current_phase": self.current_phase,
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
            if self.last_run_at:
                try:
                    last_dt = datetime.fromisoformat(self.last_run_at).astimezone(TZ_VN)
                    next_dt = last_dt + timedelta(hours=self.interval_hours)
                    if next_dt <= now:
                        next_dt = now + timedelta(hours=self.interval_hours)
                except Exception:
                    next_dt = now + timedelta(hours=self.interval_hours)
            else:
                next_dt = now + timedelta(hours=self.interval_hours)
            self.next_run_at = next_dt.isoformat(timespec="seconds")

    def get_state(self) -> dict[str, Any]:
        now = datetime.now(TZ_VN)
        return {
            "enabled": self.enabled,
            "mode": self.mode,
            "daily_time": self.daily_time,
            "interval_hours": self.interval_hours,
            "worker_count": self.worker_count,
            "auto_ta_thu": self.auto_ta_thu,
            "current_phase": self.current_phase,
            "last_run_at": self.last_run_at,
            "next_run_at": self.next_run_at,
            "current_time": now.isoformat(timespec="seconds"),
            "timezone": "GMT+7",
        }

    def update_config(
        self,
        enabled: bool,
        mode: Literal["daily", "interval"],
        daily_time: str,
        interval_hours: int,
        worker_count: int,
        auto_ta_thu: bool = True,
    ) -> dict[str, Any]:
        if mode not in {"daily", "interval"}:
            raise ControlError("Chế độ hẹn giờ phải là daily hoặc interval")
        if mode == "daily":
            parts = daily_time.strip().split(":")
            if len(parts) != 2 or not parts[0].isdigit() or not parts[1].isdigit():
                raise ControlError("Giờ hẹn phải theo định dạng HH:MM (vd 01:00)")
            hour, minute = int(parts[0]), int(parts[1])
            if not (0 <= hour <= 23 and 0 <= minute <= 59):
                raise ControlError("Giờ hẹn không hợp lệ (00:00 - 23:59)")
            self.daily_time = f"{hour:02d}:{minute:02d}"
        else:
            if interval_hours < 1 or interval_hours > 72:
                raise ControlError("Chu kỳ lặp lại phải từ 1 đến 72 giờ")
            self.interval_hours = interval_hours

        if worker_count < 1 or worker_count > 500:
            raise ControlError("Số worker phải từ 1 đến 500")

        self.enabled = bool(enabled)
        self.mode = mode
        self.worker_count = worker_count
        self.auto_ta_thu = bool(auto_ta_thu)
        self._update_next_run()
        self._save()
        return self.get_state()

    async def start_loop(self) -> None:
        if self._task is None or self._task.done():
            self._task = asyncio.create_task(self._loop())

    async def stop_loop(self) -> None:
        if self._task and not self._task.done():
            self._task.cancel()
            try:
                await self._task
            except asyncio.CancelledError:
                pass
            self._task = None

    async def _loop(self) -> None:
        while True:
            try:
                await asyncio.sleep(15)

                now = datetime.now(TZ_VN)

                # =========================================================================
                # 1. KIỂM TRA LỊCH HẸN GIỜ: ƯU TIÊN NGẮT TÀ THÚ VÀ BUILD & RUN LẠI NVHN
                # =========================================================================
                if self.enabled and self.next_run_at:
                    try:
                        target_dt = datetime.fromisoformat(self.next_run_at).astimezone(TZ_VN)
                    except Exception:
                        self._update_next_run()
                        self._save()
                        target_dt = None

                    if target_dt and now >= target_dt:
                        if self.jobs.active_job() is not None:
                            logger.warning("Đến giờ hẹn Build & Run nhưng có build job đang chạy...")
                            await asyncio.sleep(10)
                            continue

                        logger.info("Đến giờ hẹn Build & Run! Ngắt toàn bộ Tà Thú và ưu tiên chạy lại NVHN...")
                        if self.manager is not None:
                            # Dừng toàn bộ tiến trình Tà Thú trước
                            await self.manager.stop_ta_thu()
                        self.current_phase = "nvhn"
                        self._save()

                        try:
                            await self.jobs.create(
                                worker_count=self.worker_count,
                                start_after_build=True,
                            )
                            self.last_run_at = now.isoformat(timespec="seconds")
                        except Exception as exc:
                            logger.error("Lỗi khi tự động kích hoạt Build & Run: %s", exc)

                        # Tính toán mốc chạy tiếp theo
                        self._update_next_run()
                        self._save()
                        continue

                # =========================================================================
                # 2. KIỂM TRA AUTO TÀ THÚ: NẾU NVHN ĐÃ XONG 2/2 LƯỢT THÌ TỰ ĐỘNG CHẠY TÀ THÚ
                # =========================================================================
                await self._check_auto_ta_thu()

            except asyncio.CancelledError:
                break
            except Exception as exc:
                logger.error("Lỗi trong scheduler loop: %s", exc)
                await asyncio.sleep(15)

    async def _check_auto_ta_thu(self) -> None:
        async with self.transition_lock:
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
            if (
                total_workers > 0
                and totals.get("done", 0) == total_workers
                and not nvhn_status.get("supervisor", {}).get("running")
            ):
                logger.info("Tất cả %s worker NVHN đã hoàn thành! Khởi chạy Tà Thú...", total_workers)
                if await self.manager.start_ta_thu(worker_count=self.worker_count):
                    self.current_phase = "ta_thu"
                    self._save()
                    logger.info("Đã khởi chạy thành công Auto Tà Thú.")
                else:
                    logger.warning("Khởi chạy Auto Tà Thú không thành công.")
