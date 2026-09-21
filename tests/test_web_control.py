from __future__ import annotations

import asyncio
import json
import subprocess
import stat
import tempfile
import unittest
from dataclasses import replace
from datetime import datetime, timedelta
from pathlib import Path
from unittest.mock import AsyncMock, patch

from httpx import ASGITransport, AsyncClient

from web_control.app import create_app
from web_control.config import Settings
from web_control.jobs import BuildJob
from web_control.manager import ControlError, HeadlessManager
from web_control.scheduler import TZ_VN


def write_script(path: Path, body: str) -> None:
    path.write_text("#!/usr/bin/env bash\nset -euo pipefail\n" + body, encoding="utf-8")
    path.chmod(path.stat().st_mode | stat.S_IXUSR)


class WebControlTest(unittest.IsolatedAsyncioTestCase):
    def setUp(self) -> None:
        self.temporary = tempfile.TemporaryDirectory(prefix="nso-web-test-")
        tmp_path = Path(self.temporary.name)
        repo = tmp_path / "repo"
        headless = repo / "headless-runtime"
        scripts = headless / "scripts"
        workers = headless / "workers"
        runtime = headless / "run" / "web-control"
        scripts.mkdir(parents=True)
        worker = workers / "worker-01"
        (worker / "home").mkdir(parents=True)
        (worker / "account.csv").write_text(
            "username,password\nold,secret\n", encoding="utf-8"
        )
        (worker / "stdout.log").write_text("AUTO NVHN STATUS: test\n", encoding="utf-8")
        (headless / "build" / "classes").mkdir(parents=True)
        (headless / "build" / "classes" / "HeadlessMain.class").touch()
        (headless / "build" / "classes" / "OptimizedMain.class").touch()

        status_payload = {
            "workers_dir": str(workers),
            "workers": [
                {
                    "name": "worker-01",
                    "pid": None,
                    "state": "STOPPED",
                    "char_name": "fmgmza",
                    "cpu_percent": None,
                    "rss_mb": None,
                    "elapsed": None,
                    "accounts": 1,
                    "last_auto_log": "AUTO NVHN STATUS: test",
                    "stdout_log": str(worker / "stdout.log"),
                    "error_log": str(worker / "java-errors.log"),
                }
            ],
            "totals": {"running": 0, "stopped": 1, "done": 0, "total": 1},
        }
        write_script(
            scripts / "status-workers.sh",
            "if [[ ${1:-} != --json ]]; then exit 2; fi\n"
            f"printf '%s\\n' '{json.dumps(status_payload)}'\n",
        )
        write_script(
            scripts / "supervise-workers.sh",
            'mkdir -p "$HEADLESS_WORKERS_DIR"\n'
            'printf "%s\\n" "$*" >"$HEADLESS_WORKERS_DIR/supervisor.args"\n'
            'printf "%s\\n" "${PERIODIC_RESTART_SECONDS:-}" >"$HEADLESS_WORKERS_DIR/supervisor.periodic"\n'
            'printf "%s\\n" "$$" >"$HEADLESS_WORKERS_DIR/supervisor.pid"\n'
            'cleanup() { rm -f "$HEADLESS_WORKERS_DIR/supervisor.pid"; exit 0; }\n'
            "trap cleanup INT TERM\n"
            "while true; do sleep 0.2; done\n",
        )
        write_script(scripts / "stop-workers.sh", "exit 0\n")
        write_script(
            scripts / "start-workers.sh",
            'number="${3:-${1:-1}}"\nprintf "started worker-%02d\\n" "$number"\n',
        )
        write_script(
            scripts / "restart-workers.sh", 'printf "restarted worker-%02d\\n" "$1"\n'
        )
        write_script(
            scripts / "build-workers.sh",
            'count="$1"\n'
            'mkdir -p "$PWD/headless-runtime/build/classes"\n'
            'touch "$PWD/headless-runtime/build/classes/OptimizedMain.class"\n'
            'touch "$PWD/headless-runtime/build/classes/HeadlessMain.class"\n'
            'find "$HEADLESS_WORKERS_DIR" -mindepth 1 -maxdepth 1 -type d -name "worker-*" -exec rm -rf -- {} +\n'
            'for ((i=1; i<=count; i++)); do mkdir -p "$(printf "$HEADLESS_WORKERS_DIR/worker-%02d/home" "$i")"; done\n'
            'printf "built %s workers\\n" "$count"\n',
        )

        self.account_csv = repo / "account.csv"
        ta_thu = repo / "ta-thu-runtime"
        (ta_thu / "scripts").mkdir(parents=True)
        (ta_thu / "workers").mkdir(parents=True)
        write_script(ta_thu / "scripts" / "stop-workers.sh", "exit 0\n")
        self.settings = Settings(
            repo_dir=repo,
            headless_dir=headless,
            scripts_dir=scripts,
            workers_dir=workers,
            account_csv=self.account_csv,
            runtime_dir=runtime,
            ta_thu_dir=ta_thu,
            command_timeout=5,
        )

    async def asyncTearDown(self) -> None:
        manager = HeadlessManager(self.settings)
        status = manager.supervisor_status()
        if status["running"]:
            await manager.stop_supervisor()
        self.temporary.cleanup()

    async def test_open_status_upload_and_worker_validation(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            static_dir = Path(__file__).resolve().parents[1] / "web_control" / "static"
            self.assertIn(
                "NSO Headless Control",
                (static_dir / "index.html").read_text(encoding="utf-8"),
            )
            self.assertIn(
                "refreshStatus", (static_dir / "app.js").read_text(encoding="utf-8")
            )
            self.assertEqual((await client.get("/docs")).status_code, 404)
            self.assertEqual((await client.get("/health")).json(), {"ok": True})

            status_response = await client.get("/api/status")
            self.assertEqual(status_response.status_code, 200)
            self.assertEqual(status_response.json()["workers"][0]["name"], "worker-01")
            self.assertEqual(status_response.json()["workers"][0]["char_name"], "fmgmza")
            self.assertEqual(status_response.json()["supervisor"]["periodic_restart_hours"], 3)
            self.assertEqual(status_response.json()["supervisor"]["worker_start_delay_seconds"], 30)

            # Check index.html table header and buttons
            index_html = (static_dir / "index.html").read_text(encoding="utf-8")
            self.assertIn("<th>Nhân vật</th>", index_html)
            self.assertIn('id="build-button"', index_html)
            self.assertIn('id="run-button"', index_html)
            self.assertIn('id="periodic-restart-hours"', index_html)
            self.assertIn('id="worker-start-delay-seconds"', index_html)

            invalid_csv = await client.post(
                "/api/accounts/upload",
                content=b"bad,header\nuser,password\n",
                headers={"Content-Type": "text/csv"},
            )
            self.assertEqual(invalid_csv.status_code, 400)

            valid_csv = await client.post(
                "/api/accounts/upload",
                content=b'username,password\nuser1,"p,a,s,s"\nuser2,secret2\n',
                headers={"Content-Type": "text/csv"},
            )
            self.assertEqual(valid_csv.status_code, 200)
            self.assertEqual(valid_csv.json(), {"count": 2})
            self.assertEqual(stat.S_IMODE(self.account_csv.stat().st_mode), 0o600)
            self.assertIn("p,a,s,s", self.account_csv.read_text(encoding="utf-8"))

            traversal = await client.post("/api/workers/..%2F..%2Fetc/restart")
            self.assertIn(traversal.status_code, {400, 404})

    async def test_account_upload_limit_is_enforced_while_streaming(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/api/accounts/upload",
                content=b"x" * (self.settings.max_upload_bytes + 1),
            )
            self.assertEqual(response.status_code, 413)
            self.assertFalse(self.account_csv.exists())

    async def test_build_job_is_streamed_and_creates_workers(self) -> None:
        self.account_csv.write_text(
            "username,password\nuser1,secret1\nuser2,secret2\n", encoding="utf-8"
        )
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/api/build",
                json={"worker_count": 2, "start_after_build": False},
            )
            self.assertEqual(response.status_code, 200, response.text)
            job_id = response.json()["id"]

            result = None
            for _ in range(100):
                result = (await client.get(f"/api/jobs/{job_id}")).json()
                if result["status"] in {"succeeded", "failed"}:
                    break
                await asyncio.sleep(0.02)
            self.assertIsNotNone(result)
            self.assertEqual(result["status"], "succeeded", result)
            self.assertTrue(any("built 2 workers" in line for line in result["output"]))
            self.assertEqual(len(list(self.settings.workers_dir.glob("worker-*"))), 2)

    async def test_mutating_actions_are_blocked_during_build(self) -> None:
        app = create_app(self.settings)
        app.state.jobs.jobs["active-test"] = BuildJob(
            id="active-test", worker_count=1, start_after_build=False
        )
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post("/api/workers/worker-01/restart")
            self.assertEqual(response.status_code, 400)
            self.assertIn("đang chạy", response.json()["detail"])

    async def test_build_timeout_fails_job_and_reaps_process(self) -> None:
        self.account_csv.write_text("username,password\nuser,secret\n", encoding="utf-8")
        write_script(
            self.settings.scripts_dir / "build-workers.sh",
            "sleep 5\nprintf 'should not finish\\n'\n",
        )
        timeout_settings = replace(self.settings, build_timeout=0.1)
        transport = ASGITransport(app=create_app(timeout_settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/api/build",
                json={"worker_count": 1, "start_after_build": False},
            )
            job_id = response.json()["id"]
            result = None
            for _ in range(100):
                result = (await client.get(f"/api/jobs/{job_id}")).json()
                if result["status"] in {"succeeded", "failed"}:
                    break
                await asyncio.sleep(0.02)
            self.assertIsNotNone(result)
            self.assertEqual(result["status"], "failed")
            self.assertIn("vượt quá", result["error"])

    async def test_supervisor_start_and_stop(self) -> None:
        manager = HeadlessManager(self.settings)
        started = await manager.start_supervisor()
        self.assertTrue(started["running"])
        self.assertTrue(manager.desired_supervisor())
        self.assertIn("--delay 30", (self.settings.workers_dir / "supervisor.args").read_text())
        self.assertEqual(
            (self.settings.workers_dir / "supervisor.periodic").read_text().strip(),
            "10800",
        )
        stopped = await manager.stop_supervisor()
        self.assertFalse(stopped["running"])
        self.assertFalse(manager.desired_supervisor())

    async def test_supervisor_settings_are_saved_and_used_on_next_start(self) -> None:
        app = create_app(self.settings)
        transport = ASGITransport(app=app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.post(
                "/api/supervisor/settings",
                json={
                    "periodic_restart_hours": 8,
                    "worker_start_delay_seconds": 47,
                },
            )
            self.assertEqual(response.status_code, 200, response.text)
            data = response.json()
            self.assertEqual(data["periodic_restart_hours"], 8)
            self.assertEqual(data["worker_start_delay_seconds"], 47)
            self.assertFalse(data["requires_restart"])

        manager = app.state.manager
        try:
            started = await manager.start_supervisor()
            self.assertTrue(started["running"])
            self.assertIn(
                "--delay 47",
                (self.settings.workers_dir / "supervisor.args").read_text(),
            )
            self.assertEqual(
                (self.settings.workers_dir / "supervisor.periodic").read_text().strip(),
                "28800",
            )
        finally:
            await manager.stop_supervisor()

    async def test_completed_start_does_not_trigger_ta_thu(self) -> None:
        home = self.settings.workers_dir / "worker-01" / "home"
        (home / "worker.done").touch()
        (home / "worker.first-pass.done").touch()
        app = create_app(self.settings)
        manager = app.state.manager
        scheduler = app.state.scheduler
        manager._set_desired_supervisor(True)
        with patch.object(manager, "stop_ta_thu", new_callable=AsyncMock), patch.object(
            manager, "start_ta_thu", new_callable=AsyncMock
        ) as start_ta_thu:
            async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
                response = await client.post("/api/supervisor/start")
            self.assertEqual(response.status_code, 400, response.text)
            self.assertIn("Build rồi Run", response.text)
            self.assertIsNone(manager._supervisor_process)
            self.assertFalse(manager.desired_supervisor())
            await scheduler._check_auto_ta_thu()
            start_ta_thu.assert_not_awaited()

    async def test_first_pass_done_can_still_start(self) -> None:
        (self.settings.workers_dir / "worker-01" / "home" / "worker.done").touch()
        manager = HeadlessManager(self.settings)
        try:
            self.assertTrue((await manager.start_supervisor())["running"])
        finally:
            await manager.stop_supervisor()

    async def test_start_updates_phase_only_after_success(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.current_phase = "ta_thu"
        with patch.object(app.state.manager, "stop_ta_thu", new_callable=AsyncMock), patch.object(
            app.state.manager, "start_supervisor", new_callable=AsyncMock
        ) as start:
            async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as client:
                start.side_effect = ControlError("startup failed")
                self.assertEqual((await client.post("/api/supervisor/start")).status_code, 400)
                self.assertEqual(scheduler.current_phase, "ta_thu")
                start.side_effect = None
                start.return_value = {"running": True}
                self.assertEqual((await client.post("/api/supervisor/start")).status_code, 200)
        self.assertEqual(json.loads(scheduler.state_file.read_text())["current_phase"], "nvhn")

    async def test_auto_ta_thu_requires_active_nvhn_and_successful_start(self) -> None:
        app = create_app(self.settings)
        manager = app.state.manager
        scheduler = app.state.scheduler
        completed = {"totals": {"total": 1, "done": 1}, "supervisor": {"running": False}}
        with patch.object(manager, "status", new_callable=AsyncMock, return_value=completed), patch.object(
            manager, "start_ta_thu", new_callable=AsyncMock, return_value=False
        ) as start:
            await scheduler._check_auto_ta_thu()
            start.assert_not_awaited()
            manager._set_desired_supervisor(True)
            await scheduler._check_auto_ta_thu()
            start.assert_awaited_once()
            self.assertEqual(scheduler.current_phase, "nvhn")
            start.return_value = True
            await scheduler._check_auto_ta_thu()
            self.assertEqual(scheduler.current_phase, "ta_thu")

    async def test_worker_pause_start_and_restart_actions(self) -> None:
        marker = self.settings.workers_dir / "worker-01" / ".paused"
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            stopped = await client.post("/api/workers/worker-01/stop")
            self.assertEqual(stopped.status_code, 200, stopped.text)
            self.assertTrue(marker.is_file())
            self.assertEqual(stat.S_IMODE(marker.stat().st_mode), 0o600)

            started = await client.post("/api/workers/worker-01/start")
            self.assertEqual(started.status_code, 200, started.text)
            self.assertFalse(marker.exists())
            self.assertIn("started worker-01", started.json()["output"])

            marker.touch()
            restarted = await client.post("/api/workers/worker-01/restart")
            self.assertEqual(restarted.status_code, 200, restarted.text)
            self.assertFalse(marker.exists())
            self.assertIn("restarted worker-01", restarted.json()["output"])

    async def test_schedule_api_configuration(self) -> None:
        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            # Lấy trạng thái schedule ban đầu
            res = await client.get("/api/schedule")
            self.assertEqual(res.status_code, 200)
            initial = res.json()
            self.assertFalse(initial["enabled"])
            self.assertEqual(initial["timezone"], "GMT+7")

            # Cấu hình giờ chạy đầu tiên + chu kỳ lặp và bật auto Tà Thú.
            update_res = await client.post(
                "/api/schedule",
                json={
                    "enabled": True,
                    "start_time": "01:00",
                    "repeat_hours": 6,
                    "worker_count": 15,
                    "worker_start_delay_seconds": 47,
                    "auto_ta_thu": True,
                },
            )
            self.assertEqual(update_res.status_code, 200, update_res.text)
            data = update_res.json()
            self.assertTrue(data["enabled"])
            self.assertEqual(data["schedule_type"], "start_then_repeat")
            self.assertEqual(data["start_time"], "01:00")
            self.assertEqual(data["repeat_hours"], 6)
            self.assertEqual(data["worker_count"], 15)
            self.assertEqual(data["worker_start_delay_seconds"], 47)
            self.assertTrue(data["auto_ta_thu"])
            self.assertIsNotNone(data["next_run_at"])

            # Đổi cả hai giá trị trong cùng một lịch.
            interval_res = await client.post(
                "/api/schedule",
                json={
                    "enabled": True,
                    "start_time": "02:30",
                    "repeat_hours": 4,
                    "worker_count": 20,
                    "worker_start_delay_seconds": 12,
                    "auto_ta_thu": False,
                },
            )
            self.assertEqual(interval_res.status_code, 200)
            data_interval = interval_res.json()
            self.assertEqual(data_interval["schedule_type"], "start_then_repeat")
            self.assertEqual(data_interval["start_time"], "02:30")
            self.assertEqual(data_interval["repeat_hours"], 4)
            self.assertEqual(data_interval["worker_count"], 20)
            self.assertEqual(data_interval["worker_start_delay_seconds"], 12)
            self.assertFalse(data_interval["auto_ta_thu"])

            # Cập nhật tham số sai định dạng
            bad_res = await client.post(
                "/api/schedule",
                json={
                    "enabled": True,
                    "start_time": "99:99",
                    "repeat_hours": 4,
                    "worker_count": 10,
                },
            )
            self.assertEqual(bad_res.status_code, 400)

    async def test_schedule_runs_at_start_then_repeats_from_trigger(self) -> None:
        app = create_app(self.settings)
        scheduler = app.state.scheduler
        scheduler.enabled = True
        scheduler.start_time = "01:00"
        scheduler.repeat_hours = 3
        trigger_time = datetime.now(TZ_VN).replace(microsecond=0)
        scheduler.next_run_at = (trigger_time - timedelta(seconds=1)).isoformat()

        with patch.object(app.state.manager, "stop_ta_thu", new_callable=AsyncMock), patch.object(
            app.state.jobs, "active_job", return_value=None
        ), patch.object(app.state.jobs, "create", new_callable=AsyncMock) as create:
            await scheduler._trigger_scheduled_run(trigger_time)

        create.assert_awaited_once_with(
            worker_count=scheduler.worker_count,
            start_after_build=True,
            server=scheduler.server,
        )
        self.assertEqual(scheduler.last_run_at, trigger_time.isoformat())
        next_run = datetime.fromisoformat(scheduler.next_run_at)
        self.assertEqual(next_run - trigger_time, timedelta(hours=3))

        # Một instance mới đọc lại đúng lịch đang chờ, không quay về giờ mặc định.
        restored = create_app(self.settings).state.scheduler.get_state()
        self.assertEqual(restored["start_time"], "01:00")
        self.assertEqual(restored["repeat_hours"], 3)
        self.assertEqual(restored["next_run_at"], scheduler.next_run_at)

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

    async def test_stop_ta_thu_supervisor_endpoint(self) -> None:
        ta_thu_dir = self.settings.ta_thu_dir
        ta_thu_scripts = ta_thu_dir / "scripts"
        ta_thu_scripts.mkdir(parents=True, exist_ok=True)
        write_script(ta_thu_scripts / "stop-workers.sh", "exit 0\n")

        transport = ASGITransport(app=create_app(self.settings))
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            res = await client.post("/api/ta-thu/supervisor/stop")
            self.assertEqual(res.status_code, 200)
            data = res.json()
            self.assertTrue(data["ok"])
            self.assertFalse(data["supervisor"]["running"])

    async def test_relative_manual_supervisor_is_recognized(self) -> None:
        relative_script = Path("headless-runtime/scripts/supervise-workers.sh")
        process = subprocess.Popen(
            [str(relative_script)],
            cwd=self.settings.repo_dir,
            env=self.settings.command_env(),
            stdin=subprocess.DEVNULL,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            start_new_session=True,
        )
        manager = HeadlessManager(self.settings)
        try:
            for _ in range(50):
                if manager.supervisor_status()["running"]:
                    break
                await asyncio.sleep(0.02)
            self.assertTrue(manager.supervisor_status()["running"])
            stopped = await manager.stop_supervisor()
            self.assertFalse(stopped["running"])
        finally:
            if process.poll() is None:
                process.kill()
            process.wait(timeout=2)

    def test_invalid_worker_name_never_reaches_shell(self) -> None:
        manager = HeadlessManager(self.settings)
        with self.assertRaises(ControlError):
            manager.worker_number("../../etc")


if __name__ == "__main__":
    unittest.main()
