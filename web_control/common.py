"""Phần dùng chung giữa manager Ubuntu (HeadlessManager) và Windows.

Lớp con phải cung cấp: settings.repo_dir, _read_state(), _write_state(), và
self.log_summaries (logparse.SummaryCache).
"""

from __future__ import annotations

import os
from pathlib import Path
from typing import Any

DEFAULT_PERIODIC_RESTART_HOURS = 0  # chạy lại từ đầu do lịch "Lặp lại sau" đảm nhiệm
MAX_PERIODIC_RESTART_HOURS = 168
DEFAULT_WORKER_START_DELAY_SECONDS = 30
MAX_WORKER_START_DELAY_SECONDS = 3600
# >0 = bật proxy (chia đều worker cho proxy-tk.txt, kiểu uplevel); 0 = tắt.
DEFAULT_PROXY_WORKERS_PER_PROXY = 6
MAX_PROXY_WORKERS_PER_PROXY = 500
EMPTY_TOTALS = {"running": 0, "stopped": 0, "paused": 0, "done": 0, "error": 0, "total": 0}


class ControlError(RuntimeError):
    pass


class ManagerCommon:
    @property
    def proxy_file(self) -> Path:
        return Path(os.environ.get("PROXY_TK_FILE", self.settings.repo_dir / "proxy-tk.txt"))

    def periodic_restart_hours(self) -> int:
        """Configured per-worker periodic restart interval; zero disables it."""
        raw = self._read_state().get(
            "periodic_restart_hours", DEFAULT_PERIODIC_RESTART_HOURS
        )
        try:
            value = int(raw)
        except (TypeError, ValueError):
            value = DEFAULT_PERIODIC_RESTART_HOURS
        return max(0, min(value, MAX_PERIODIC_RESTART_HOURS))

    def set_periodic_restart_hours(self, hours: int) -> int:
        """Persist the periodic restart interval in whole hours."""
        try:
            value = int(hours)
        except (TypeError, ValueError) as exc:
            raise ControlError("Số giờ restart định kỳ không hợp lệ") from exc
        if value < 0 or value > MAX_PERIODIC_RESTART_HOURS:
            raise ControlError(
                f"Số giờ restart định kỳ phải từ 0 đến {MAX_PERIODIC_RESTART_HOURS}"
            )
        state = self._read_state()
        state["periodic_restart_hours"] = value
        self._write_state(state)
        return value

    def worker_start_delay_seconds(self) -> int:
        """Configured delay between starting consecutive workers."""
        raw = self._read_state().get(
            "worker_start_delay_seconds", DEFAULT_WORKER_START_DELAY_SECONDS
        )
        try:
            value = int(raw)
        except (TypeError, ValueError):
            value = DEFAULT_WORKER_START_DELAY_SECONDS
        return max(0, min(value, MAX_WORKER_START_DELAY_SECONDS))

    def set_worker_start_delay_seconds(self, seconds: int) -> int:
        """Persist the delay between consecutive worker starts."""
        try:
            value = int(seconds)
        except (TypeError, ValueError) as exc:
            raise ControlError("Giãn cách khởi động worker không hợp lệ") from exc
        if value < 0 or value > MAX_WORKER_START_DELAY_SECONDS:
            raise ControlError(
                "Giãn cách khởi động worker phải từ "
                f"0 đến {MAX_WORKER_START_DELAY_SECONDS} giây"
            )
        state = self._read_state()
        state["worker_start_delay_seconds"] = value
        self._write_state(state)
        return value

    def proxy_workers_per_proxy(self) -> int:
        raw = self._read_state().get(
            "proxy_workers_per_proxy", DEFAULT_PROXY_WORKERS_PER_PROXY
        )
        try:
            value = int(raw)
        except (TypeError, ValueError):
            value = DEFAULT_PROXY_WORKERS_PER_PROXY
        return max(0, min(value, MAX_PROXY_WORKERS_PER_PROXY))

    def set_proxy_workers_per_proxy(self, count: int) -> int:
        try:
            value = int(count)
        except (TypeError, ValueError) as exc:
            raise ControlError("Số worker dùng chung proxy không hợp lệ") from exc
        if value < 0 or value > MAX_PROXY_WORKERS_PER_PROXY:
            raise ControlError(
                "Số worker dùng chung proxy phải từ "
                f"0 đến {MAX_PROXY_WORKERS_PER_PROXY}"
            )
        state = self._read_state()
        state["proxy_workers_per_proxy"] = value
        self._write_state(state)
        return value

    def proxy_count(self) -> int:
        try:
            lines = self.proxy_file.read_text(encoding="utf-8-sig").splitlines()
        except OSError:
            return 0
        return sum(1 for line in lines if line.strip() and not line.strip().startswith("#"))

    def desired_ta_thu(self) -> bool:
        state = self._read_state()
        if "ta_thu_desired" not in state:
            # State cũ chưa có cờ này: giữ hành vi cũ (theo cờ NVHN).
            return state.get("supervisor_desired") is True
        return state.get("ta_thu_desired") is True

    def set_desired_ta_thu(self, desired: bool) -> None:
        state = self._read_state()
        state["ta_thu_desired"] = desired
        self._write_state(state)

    def enrich_workers(self, workers: list[dict[str, Any]]) -> None:
        """Gắn tóm tắt log (sự kiện cuối, trận đánh, số lần chết...) vào worker."""
        for worker in workers:
            proxy = worker.get("proxy")
            if proxy:
                # Không đưa user/password proxy ra trình duyệt.
                worker["proxy"] = ":".join(str(proxy).split(":")[:2])
            stdout_log = worker.get("stdout_log")
            summary = self.log_summaries.get(Path(stdout_log)) if stdout_log else None
            if not summary:
                continue
            worker["char_name"] = worker.get("char_name") or summary["char_name"]
            worker["last_event"] = summary["last_event"]
            worker["fight"] = summary["fight"]
            worker["nvhn_status"] = summary["status"]
            worker["account"] = summary["account"]
            for key in ("kills", "lost", "deaths", "pk", "warnings", "errors"):
                worker[key] = summary[key]

