from __future__ import annotations

import asyncio
import json
import os
import stat
import tempfile
import unittest
from datetime import datetime, timedelta
from pathlib import Path
from unittest.mock import AsyncMock, patch

from httpx import ASGITransport, AsyncClient

from web_control_win.app import create_app
from web_control_win.config import Settings
from web_control_win.manager import ControlError, WindowsHeadlessManager
from web_control_win.scheduler import TZ_VN


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
            self.assertIn("ta_thu", data)
            self.assertEqual(data["schedule"]["current_phase"], "nvhn")
            self.assertEqual(data["supervisor"]["periodic_restart_hours"], 3)
            self.assertEqual(data["supervisor"]["worker_start_delay_seconds"], 30)

    async def test_supervisor_periodic_restart_settings(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            res = await client.post(
                "/api/supervisor/settings",
                json={
                    "periodic_restart_hours": 3,
                    "worker_start_delay_seconds": 47,
                },
            )
            self.assertEqual(res.status_code, 200)
            self.assertEqual(res.json()["periodic_restart_hours"], 3)
            self.assertEqual(res.json()["worker_start_delay_seconds"], 47)
            self.assertFalse(res.json()["requires_restart"])

            disabled = await client.post(
                "/api/supervisor/settings",
                json={
                    "periodic_restart_hours": 0,
                    "worker_start_delay_seconds": 0,
                },
            )
            self.assertEqual(disabled.status_code, 200)
            self.assertEqual(disabled.json()["periodic_restart_hours"], 0)
            self.assertEqual(disabled.json()["worker_start_delay_seconds"], 0)

            invalid = await client.post(
                "/api/supervisor/settings",
                json={
                    "periodic_restart_hours": 169,
                    "worker_start_delay_seconds": 3601,
                },
            )
            self.assertEqual(invalid.status_code, 422)

        saved = json.loads((self.settings.web_runtime_dir / "state.json").read_text())
        self.assertEqual(saved["periodic_restart_hours"], 0)
        self.assertEqual(saved["worker_start_delay_seconds"], 0)

    async def test_completed_worker_cannot_start_again_without_build(self) -> None:
        (self.settings.workers_dir / "worker-01" / "home" / "worker.done").touch()
        manager = WindowsHeadlessManager(self.settings)
        with self.assertRaises(ControlError) as context:
            await manager.start_supervisor()
        self.assertIn("Hãy bấm Build rồi Run", str(context.exception))

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
                    "start_time": "02:00",
                    "repeat_hours": 8,
                    "worker_count": 15,
                    "worker_start_delay_seconds": 47,
                },
            )
            self.assertEqual(res.status_code, 200)
            data = res.json()
            self.assertTrue(data["enabled"])
            self.assertEqual(data["schedule_type"], "start_then_repeat")
            self.assertEqual(data["start_time"], "02:00")
            self.assertEqual(data["repeat_hours"], 8)
            self.assertEqual(data["worker_count"], 15)
            self.assertEqual(data["worker_start_delay_seconds"], 47)

        saved = json.loads((self.settings.web_runtime_dir / "schedule.json").read_text())
        self.assertEqual(saved["worker_count"], 15)
        self.assertEqual(saved["worker_start_delay_seconds"], 47)
        manager_state = json.loads(
            (self.settings.web_runtime_dir / "state.json").read_text(encoding="utf-8")
        )
        self.assertEqual(manager_state["worker_start_delay_seconds"], 47)
        # Một app mới phải đọc lại lịch đã lưu, kể cả sau khi tắt lịch.
        async with AsyncClient(
            transport=ASGITransport(app=create_app(self.settings)), base_url="http://test"
        ) as client:
            restored = (await client.get("/api/schedule")).json()
            for key in ("enabled", "schedule_type", "start_time", "repeat_hours", "worker_count"):
                self.assertEqual(restored[key], data[key])
            restored["enabled"] = False
            self.assertEqual((await client.post("/api/schedule", json=restored)).status_code, 200)
        restored = create_app(self.settings).state.scheduler.get_state()
        self.assertFalse(restored["enabled"])
        self.assertEqual(restored["worker_count"], 15)

    async def test_schedule_runs_at_start_then_repeats_from_trigger(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.enabled = True
        scheduler.start_time = "01:00"
        scheduler.repeat_hours = 3
        trigger_time = datetime.now(TZ_VN).replace(microsecond=0)
        scheduler.next_run_at = (trigger_time - timedelta(seconds=1)).isoformat()

        with patch.object(app.state.jobs, "active_job", return_value=None), patch.object(
            app.state.jobs, "create", new_callable=AsyncMock
        ) as create:
            await scheduler._trigger_scheduled_run()

        create.assert_awaited_once_with(
            worker_count=scheduler.worker_count,
            start_after_build=True,
            server=scheduler.server,
        )
        last_run = datetime.fromisoformat(scheduler.last_run_at)
        next_run = datetime.fromisoformat(scheduler.next_run_at)
        self.assertEqual(next_run - last_run, timedelta(hours=3))

        restored = create_app(self.settings).state.scheduler.get_state()
        self.assertEqual(restored["repeat_hours"], 3)
        self.assertEqual(restored["next_run_at"], scheduler.next_run_at)

    async def test_schedule_stops_ta_thu_before_starting_nvhn_cycle(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.enabled = True
        scheduler.repeat_hours = 3
        scheduler.next_run_at = datetime.now(TZ_VN).isoformat(timespec="seconds")
        with patch.object(app.state.manager, "stop_ta_thu", new_callable=AsyncMock) as stop_ta_thu, patch.object(
            app.state.jobs, "active_job", return_value=None
        ), patch.object(app.state.jobs, "create", new_callable=AsyncMock) as create:
            await scheduler._trigger_scheduled_run()

        stop_ta_thu.assert_awaited_once()
        create.assert_awaited_once_with(
            worker_count=scheduler.worker_count,
            start_after_build=True,
            server=scheduler.server,
        )
        self.assertEqual(scheduler.current_phase, "nvhn")

    async def test_auto_ta_thu_starts_after_all_nvhn_workers_done(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.auto_ta_thu = True
        scheduler.current_phase = "nvhn"
        app.state.manager._set_desired_supervisor(True)
        with patch.object(
            app.state.manager,
            "status",
            new_callable=AsyncMock,
            return_value={
                "totals": {"total": 1, "done": 1},
                "supervisor": {"running": False},
            },
        ), patch.object(
            app.state.manager,
            "start_ta_thu",
            new_callable=AsyncMock,
            return_value=True,
        ) as start_ta_thu:
            await scheduler._check_auto_ta_thu()

        start_ta_thu.assert_awaited_once_with(worker_count=scheduler.worker_count)
        self.assertEqual(scheduler.current_phase, "ta_thu")

    async def test_schedule_does_not_skip_when_build_is_active(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.enabled = True
        scheduler.next_run_at = (
            datetime.now(TZ_VN) - timedelta(minutes=1)
        ).isoformat(timespec="seconds")
        with patch.object(app.state.jobs, "active_job", return_value=object()), patch.object(
            app.state.jobs, "create", new_callable=AsyncMock
        ) as create:
            await scheduler._trigger_scheduled_run()
        create.assert_not_awaited()
        self.assertGreater(datetime.fromisoformat(scheduler.next_run_at), datetime.now(TZ_VN))

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

    async def test_stop_all_also_stops_ta_thu(self) -> None:
        app = create_app(self.settings)
        with patch.object(app.state.manager, "stop_ta_thu", new_callable=AsyncMock) as stop_ta_thu, patch.object(
            app.state.manager,
            "stop_supervisor",
            new_callable=AsyncMock,
            return_value={"running": False},
        ) as stop_nvhn:
            transport = ASGITransport(app=app)
            async with AsyncClient(transport=transport, base_url="http://test") as client:
                response = await client.post("/api/supervisor/stop")
        self.assertEqual(response.status_code, 200)
        stop_ta_thu.assert_awaited_once()
        stop_nvhn.assert_awaited_once()


if __name__ == "__main__":
    unittest.main()
