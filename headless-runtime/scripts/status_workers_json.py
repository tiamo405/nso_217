#!/usr/bin/env python3
"""Return headless worker status as stable JSON for the web controller."""

from __future__ import annotations

import json
import os
import subprocess
import sys
import time
from datetime import datetime, timezone
from pathlib import Path


MAX_LOG_SCAN_BYTES = 4 * 1024 * 1024


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

    if "HeadlessMain" not in cmdline or str(worker_dir) not in cmdline:
        return None, "ERROR"
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


def last_matching_line(log_file: Path, marker: str) -> str | None:
    try:
        with log_file.open("rb") as stream:
            stream.seek(0, os.SEEK_END)
            size = stream.tell()
            start = max(0, size - MAX_LOG_SCAN_BYTES)
            stream.seek(start)
            content = stream.read()
    except OSError:
        return None
    lines = content.splitlines()
    if start > 0 and lines:
        lines = lines[1:]
    marker_bytes = marker.encode("utf-8")
    for line in reversed(lines):
        if marker_bytes in line:
            return line.decode("utf-8", errors="replace")
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
    done = (worker_dir / "home" / "worker.done").is_file()
    first_pass_done = (worker_dir / "home" / "worker.first-pass.done").is_file()

    if done:
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

    return {
        "name": worker_dir.name,
        "pid": pid,
        "state": state,
        "run_pass": 2 if first_pass_done else 1,
        "run_pass_total": 2,
        "cpu_percent": cpu,
        "rss_mb": rss_mb,
        "elapsed": elapsed,
        "accounts": count_accounts(worker_dir / "account.csv"),
        "last_auto_log": last_matching_line(stdout_log, "AUTO NVHN"),
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
