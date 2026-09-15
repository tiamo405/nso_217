from __future__ import annotations

import asyncio
import json
import os
import stat
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from httpx import ASGITransport, AsyncClient

from web_control_win.app import create_app
from web_control_win.config import Settings
from web_control_win.manager import WindowsHeadlessManager


class WebControlWinTest(unittest.IsolatedAsyncioTestCase):
    def setUp(self) -> None:
        self.temporary = tempfile.TemporaryDirectory(prefix="nso-web-win-test-")
        tmp_path = Path(self.temporary.name)
        repo = tmp_path / "repo"
        runtime = repo / "optimized-runtime"
        windows_dir = runtime / "windows"
        workers = runtime / "workers"
        web_runtime = runtime / "run" / "web-control-win"

        windows_dir.mkdir(parents=True)
        workers.mkdir(parents=True)
        web_runtime.mkdir(parents=True)

        worker = workers / "worker-01"
        (worker / "home").mkdir(parents=True)
        (worker / "account.csv").write_text("username,password\nacc1,pass1\n", encoding="utf-8")
        (worker / "stdout.log").write_text("AUTO NVHN STATUS: nv=ninja_pro\n", encoding="utf-8")

        build_classes = runtime / "build" / "classes"
        build_classes.mkdir(parents=True)
        (build_classes / "OptimizedMain.class").touch()

        # Giả lập file win_manager.py
        self.win_manager = windows_dir / "win_manager.py"
        status_payload = {
            "workers_dir": str(workers),
            "workers": [
                {
                    "name": "worker-01",
                    "pid": None,
                    "state": "STOPPED",
                    "char_name": "ninja_pro",
                    "cpu_percent": None,
                    "rss_mb": None,
                    "elapsed": None,
                    "accounts": 1,
                    "last_auto_log": "AUTO NVHN STATUS: nv=ninja_pro",
                    "stdout_log": str(worker / "stdout.log"),
                    "error_log": str(worker / "java-errors.log"),
                }
            ],
            "totals": {"running": 0, "stopped": 1, "done": 0, "total": 1},
        }

        # Mock script phản hồi các lệnh của win_manager.py
        raw_json = json.dumps(status_payload)
        win_manager_code = f'''import sys
if len(sys.argv) > 1:
    cmd = sys.argv[1]
    if cmd == 'status':
        print({repr(raw_json)})
        sys.exit(0)
    elif cmd in ('stop', 'start', 'restart'):
        print(f"done {{cmd}}")
        sys.exit(0)
    elif cmd == 'build-workers':
        print("done build-workers")
        sys.exit(0)
sys.exit(0)
'''
        self.win_manager.write_text(win_manager_code, encoding="utf-8")

        self.account_csv = repo / "account.csv"
        self.account_csv.write_text("username,password\nu1,p1\nu2,p2\n", encoding="utf-8")

        self.settings = Settings(
            repo_dir=repo,
            runtime_dir=runtime,
            workers_dir=workers,
            win_manager_py=self.win_manager,
            account_csv=self.account_csv,
            web_runtime_dir=web_runtime,
            command_timeout=5,
        )

    def tearDown(self) -> None:
        self.temporary.cleanup()

    async def test_health_and_status(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            res = await client.get("/health")
            self.assertEqual(res.status_code, 200)
            self.assertEqual(res.json()["ok"], True)

            status_res = await client.get("/api/status")
            self.assertEqual(status_res.status_code, 200)
            data = status_res.json()
            self.assertIn("workers", data)
            self.assertEqual(data["totals"]["total"], 1)
            self.assertEqual(data["account"]["count"], 2)
            self.assertFalse(data["supervisor"]["running"])

    async def test_account_upload(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            valid_csv = "username,password\nuserA,passA\nuserB,passB\nuserC,passC\n"
            res = await client.post(
                "/api/account",
                content=valid_csv.encode("utf-8"),
                headers={"Content-Type": "text/csv"},
            )
            self.assertEqual(res.status_code, 200)
            self.assertEqual(res.json()["count"], 3)

            # Upload sai header
            bad_res = await client.post(
                "/api/account",
                content=b"user,pass\na,b\n",
                headers={"Content-Type": "text/csv"},
            )
            self.assertEqual(bad_res.status_code, 400)

    async def test_schedule_configuration(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            res = await client.post(
                "/api/schedule",
                json={
                    "enabled": True,
                    "mode": "interval",
                    "daily_time": "02:00",
                    "interval_hours": 8,
                    "worker_count": 15,
                },
            )
            self.assertEqual(res.status_code, 200)
            data = res.json()
            self.assertTrue(data["enabled"])
            self.assertEqual(data["mode"], "interval")
            self.assertEqual(data["interval_hours"], 8)
            self.assertEqual(data["worker_count"], 15)

    async def test_worker_actions(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            stop_res = await client.post("/api/workers/worker-01/stop")
            self.assertEqual(stop_res.status_code, 200)

            start_res = await client.post("/api/workers/worker-01/start")
            self.assertEqual(start_res.status_code, 200)

            restart_res = await client.post("/api/workers/worker-01/restart")
            self.assertEqual(restart_res.status_code, 200)

            log_res = await client.get("/api/workers/worker-01/logs?kind=stdout")
            self.assertEqual(log_res.status_code, 200)
            self.assertIn("ninja_pro", log_res.json()["content"])


if __name__ == "__main__":
    unittest.main()
