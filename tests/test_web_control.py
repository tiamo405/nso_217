from __future__ import annotations

import asyncio
import json
import subprocess
import stat
import tempfile
import unittest
from dataclasses import replace
from pathlib import Path

from httpx import ASGITransport, AsyncClient

from web_control.app import create_app
from web_control.config import Settings
from web_control.jobs import BuildJob
from web_control.manager import ControlError, HeadlessManager


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

        status_payload = {
            "workers_dir": str(workers),
            "workers": [
                {
                    "name": "worker-01",
                    "pid": None,
                    "state": "STOPPED",
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
            'touch "$PWD/headless-runtime/build/classes/HeadlessMain.class"\n'
            'find "$HEADLESS_WORKERS_DIR" -mindepth 1 -maxdepth 1 -type d -name "worker-*" -exec rm -rf -- {} +\n'
            'for ((i=1; i<=count; i++)); do mkdir -p "$(printf "$HEADLESS_WORKERS_DIR/worker-%02d/home" "$i")"; done\n'
            'printf "built %s workers\\n" "$count"\n',
        )

        self.account_csv = repo / "account.csv"
        self.settings = Settings(
            repo_dir=repo,
            headless_dir=headless,
            scripts_dir=scripts,
            workers_dir=workers,
            account_csv=self.account_csv,
            runtime_dir=runtime,
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
        stopped = await manager.stop_supervisor()
        self.assertFalse(stopped["running"])
        self.assertFalse(manager.desired_supervisor())

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
