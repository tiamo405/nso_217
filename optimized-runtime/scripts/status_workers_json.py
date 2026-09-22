#!/usr/bin/env python3
"""Return optimized worker status as stable JSON for the web controller."""

from __future__ import annotations

import json
import os
import re
import subprocess
import sys
import time
from datetime import datetime, timezone
from pathlib import Path


MAX_LOG_SCAN_BYTES = 4 * 1024 * 1024

RE_CHAR_STATUS = re.compile(r"AUTO NVHN STATUS:.*?nv=([a-zA-Z0-9_]+)")
RE_CHAR_CHOOSE = re.compile(r"AUTO NVHN: (?:chọn|chuẩn bị) nhân vật ([a-zA-Z0-9_]+)")


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8", errors="replace").strip()
    except OSError:
        return ""


def classify_pid(pid_file: Path, worker_dir: Path) -> tuple[int | None, str | None]:
    raw_pid = read_text(pid_file)
    if not raw_pid.isdigit():
        return None, "STALE_PID" if raw_pid else None
    pid = int(raw_pid)
    try:
        os.kill(pid, 0)
    except (OSError, ValueError):
        return None, "STALE_PID"

    try:
        cmdline = Path(f"/proc/{pid}/cmdline").read_bytes().replace(b"\0", b" ").decode(
            "utf-8", errors="replace"
        )
    except OSError:
        return None, "STALE_PID"

    if ("OptimizedMain" not in cmdline and "HeadlessMain" not in cmdline) or str(worker_dir) not in cmdline:
        # bot.pid may remain after a forced stop, or its number may have been
        # reused by another process. It is not a worker error and must never
        # be reported as a live worker to the dashboard.
        return None, "STALE_PID"
    return pid, "RUNNING"


def process_stats(pid: int) -> tuple[float | None, float | None, str | None]:
    try:
        result = subprocess.run(
            ["ps", "-o", "pcpu=,rss=,etime=", "-p", str(pid)],
            check=True,
            capture_output=True,
            text=True,
            timeout=2,
            env={**os.environ, "LC_ALL": "C"},
        )
        values = result.stdout.strip().split(None, 2)
        if len(values) != 3:
            return None, None, None
        cpu, rss_kb, elapsed = values
        return float(cpu), round(int(rss_kb) / 1024, 1), elapsed
    except (OSError, ValueError, subprocess.SubprocessError):
        return None, None, None


def count_accounts(csv_file: Path) -> int | None:
    try:
        lines = csv_file.read_text(encoding="utf-8-sig", errors="replace").splitlines()
    except OSError:
        return None
    return sum(1 for line in lines[1:] if line.strip())


def get_log_lines_tail(log_file: Path) -> list[str]:
    try:
        with log_file.open("rb") as stream:
            stream.seek(0, os.SEEK_END)
            size = stream.tell()
            start = max(0, size - MAX_LOG_SCAN_BYTES)
            stream.seek(start)
            content = stream.read()
    except OSError:
        return []
    lines = content.splitlines()
    if start > 0 and lines:
        lines = lines[1:]
    return [line.decode("utf-8", errors="replace") for line in lines]


def last_matching_line(lines: list[str], marker: str) -> str | None:
    for line in reversed(lines):
        if marker in line:
            return line
    return None


def extract_char_name(lines: list[str]) -> str | None:
    for line in reversed(lines):
        match_status = RE_CHAR_STATUS.search(line)
        if match_status:
            return match_status.group(1)
        match_choose = RE_CHAR_CHOOSE.search(line)
        if match_choose:
            return match_choose.group(1)
    return None


def log_time(log_file: Path) -> tuple[str | None, int | None]:
    try:
        modified_at = log_file.stat().st_mtime
    except OSError:
        return None, None
    timestamp = datetime.fromtimestamp(modified_at, timezone.utc).astimezone()
    age_seconds = max(0, int(time.time() - modified_at))
    return timestamp.isoformat(timespec="seconds"), age_seconds


def worker_status(worker_dir: Path) -> dict[str, object]:
    pid_file = worker_dir / "bot.pid"
    pid, pid_state = classify_pid(pid_file, worker_dir)
    paused = (worker_dir / ".paused").is_file()
    done_marker = worker_dir / "home" / "worker.done"
    legacy_done_marker = worker_dir / "home" / "worker.first-pass.done"
    done = done_marker.is_file() or legacy_done_marker.is_file()

    if paused:
        state = "PAUSED"
    elif done:
        state = "DONE"
    elif pid_state is not None:
        state = pid_state
    else:
        state = "STOPPED"

    cpu = rss_mb = elapsed = None
    if pid is not None:
        cpu, rss_mb, elapsed = process_stats(pid)

    stdout_log = worker_dir / "stdout.log"
    last_log_at, last_log_age_seconds = log_time(stdout_log)
    lines = get_log_lines_tail(stdout_log) if stdout_log.is_file() else []

    return {
        "name": worker_dir.name,
        "pid": pid,
        "state": state,
        "char_name": extract_char_name(lines),
        "paused": paused,
        "run_pass": 1,
        "run_pass_total": 1,
        "cpu_percent": cpu,
        "rss_mb": rss_mb,
        "elapsed": elapsed,
        "accounts": count_accounts(worker_dir / "account.csv"),
        "last_auto_log": last_matching_line(lines, "AUTO NVHN"),
        "last_log_at": last_log_at,
        "last_log_age_seconds": last_log_age_seconds,
        "stdout_log": str(stdout_log),
        "error_log": str(worker_dir / "java-errors.log"),
    }


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: status_workers_json.py WORKERS_DIR", file=sys.stderr)
        return 2

    workers_dir = Path(sys.argv[1]).resolve()
    workers = [worker_status(path) for path in sorted(workers_dir.glob("worker-*")) if path.is_dir()]
    totals = {
        "running": sum(worker["state"] == "RUNNING" for worker in workers),
        "stopped": sum(worker["state"] in {"STOPPED", "STALE_PID"} for worker in workers),
        "paused": sum(worker["state"] == "PAUSED" for worker in workers),
        "done": sum(worker["state"] == "DONE" for worker in workers),
        "error": sum(worker["state"] == "ERROR" for worker in workers),
        "total": len(workers),
    }
    json.dump(
        {"workers_dir": str(workers_dir), "workers": workers, "totals": totals},
        sys.stdout,
        ensure_ascii=False,
        separators=(",", ":"),
    )
    sys.stdout.write("\n")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
