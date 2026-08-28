from __future__ import annotations

import json
import os
import shlex
import shutil
import stat
import subprocess
import tempfile
import time
import unittest
from pathlib import Path


REPO_DIR = Path(__file__).resolve().parents[1]


def write_script(path: Path, body: str) -> None:
    path.write_text("#!/usr/bin/env bash\nset -euo pipefail\n" + body, encoding="utf-8")
    path.chmod(path.stat().st_mode | stat.S_IXUSR)


class SupervisorWatchdogTest(unittest.TestCase):
    def test_paused_worker_is_reported_and_not_started(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-paused-worker-") as temporary:
            headless = Path(temporary) / "headless-runtime"
            workers = headless / "workers"
            worker = workers / "worker-01"
            (headless / "build" / "classes").mkdir(parents=True)
            (worker / "home").mkdir(parents=True)
            (worker / ".paused").touch()
            (worker / "account.csv").write_text(
                "username,password\nuser,secret\n", encoding="utf-8"
            )

            env = os.environ.copy()
            env.update(
                {
                    "HEADLESS_WORKERS_DIR": str(workers),
                    "HEADLESS_CLASSES_DIR": str(headless / "build" / "classes"),
                    "JAVA_BIN": "/bin/false",
                }
            )
            start = subprocess.run(
                [str(REPO_DIR / "headless-runtime/scripts/start-workers.sh"), "--delay", "0"],
                cwd=REPO_DIR,
                env=env,
                check=True,
                capture_output=True,
                text=True,
                timeout=5,
            )
            self.assertIn("đang tạm dừng", start.stdout)
            self.assertFalse((worker / "bot.pid").exists())

            status = subprocess.run(
                [str(REPO_DIR / "headless-runtime/scripts/status-workers.sh"), "--json"],
                cwd=REPO_DIR,
                env=env,
                check=True,
                capture_output=True,
                text=True,
                timeout=5,
            )
            payload = json.loads(status.stdout)
            self.assertEqual(payload["workers"][0]["state"], "PAUSED")
            self.assertTrue(payload["workers"][0]["paused"])
            self.assertEqual(payload["totals"]["paused"], 1)

    def test_status_json_contains_last_log_time(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-status-time-") as temporary:
            workers = Path(temporary) / "workers"
            worker = workers / "worker-01"
            (worker / "home").mkdir(parents=True)
            (worker / "account.csv").write_text(
                "username,password\nuser,secret\n", encoding="utf-8"
            )
            log_file = worker / "stdout.log"
            log_file.write_text("AUTO NVHN STATUS: timestamp-test\n", encoding="utf-8")
            (worker / "home" / "worker.first-pass.done").touch()
            modified_at = time.time() - 12
            os.utime(log_file, (modified_at, modified_at))

            env = os.environ.copy()
            env["HEADLESS_WORKERS_DIR"] = str(workers)
            result = subprocess.run(
                [str(REPO_DIR / "headless-runtime/scripts/status-workers.sh"), "--json"],
                cwd=REPO_DIR,
                env=env,
                check=True,
                capture_output=True,
                text=True,
                timeout=5,
            )
            payload = json.loads(result.stdout)
            status = payload["workers"][0]
            self.assertIsNotNone(status["last_log_at"])
            self.assertGreaterEqual(status["last_log_age_seconds"], 10)
            self.assertIn("timestamp-test", status["last_auto_log"])
            self.assertEqual(status["run_pass"], 2)
            self.assertEqual(status["run_pass_total"], 2)

    def test_first_done_marker_is_promoted_to_second_pass_once(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-second-pass-") as temporary:
            headless = Path(temporary) / "headless-runtime"
            scripts = headless / "scripts"
            workers = headless / "workers"
            worker = workers / "worker-01"
            scripts.mkdir(parents=True)
            (worker / "home").mkdir(parents=True)
            (worker / "home" / "worker.done").touch()

            shutil.copy2(
                REPO_DIR / "headless-runtime/scripts/supervise-workers.sh",
                scripts / "supervise-workers.sh",
            )
            write_script(scripts / "start-workers.sh", "exit 0\n")
            write_script(scripts / "stop-workers.sh", "exit 0\n")

            env = os.environ.copy()
            env.update(
                {
                    "HEADLESS_WORKERS_DIR": str(workers),
                    "CHECK_INTERVAL": "1",
                    "START_DELAY": "0",
                    "STALE_LOG_SECONDS": "0",
                }
            )
            supervisor = subprocess.Popen(
                [str(scripts / "supervise-workers.sh")],
                cwd=Path(temporary),
                env=env,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            output = ""
            try:
                first_pass = worker / "home" / "worker.first-pass.done"
                deadline = time.time() + 4
                while time.time() < deadline and not first_pass.is_file():
                    time.sleep(0.05)
                self.assertTrue(first_pass.is_file())
                self.assertFalse((worker / "home" / "worker.done").exists())

                # Simulate Java completing pass 2. Supervisor must retain this
                # final marker instead of scheduling a third pass.
                (worker / "home" / "worker.done").touch()
                time.sleep(1.2)
                self.assertTrue(first_pass.is_file())
                self.assertTrue((worker / "home" / "worker.done").is_file())
            finally:
                if supervisor.poll() is None:
                    supervisor.terminate()
                try:
                    output, _ = supervisor.communicate(timeout=4)
                except subprocess.TimeoutExpired:
                    supervisor.kill()
                    output, _ = supervisor.communicate(timeout=2)

            self.assertIn("đã xong lượt 1/2", output)

    def test_stale_stdout_restarts_running_worker(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-stale-watchdog-") as temporary:
            headless = Path(temporary) / "headless-runtime"
            scripts = headless / "scripts"
            workers = headless / "workers"
            worker = workers / "worker-01"
            scripts.mkdir(parents=True)
            (worker / "home").mkdir(parents=True)

            shutil.copy2(
                REPO_DIR / "headless-runtime/scripts/supervise-workers.sh",
                scripts / "supervise-workers.sh",
            )
            marker = Path(temporary) / "restart-marker.txt"
            write_script(
                scripts / "start-workers.sh",
                'if (( $# > 0 )); then printf "%s\\n" "$*" >>"$TEST_RESTART_MARKER"; fi\n',
            )
            write_script(scripts / "stop-workers.sh", "exit 0\n")

            process_title = f"HeadlessMain {worker}"
            sleeper = subprocess.Popen(
                ["bash", "-c", f"exec -a {shlex.quote(process_title)} sleep 60"],
                stdin=subprocess.DEVNULL,
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            )
            (worker / "bot.pid").write_text(f"{sleeper.pid}\n", encoding="utf-8")
            log_file = worker / "stdout.log"
            log_file.write_text("AUTO NVHN STATUS: old\n", encoding="utf-8")
            old_time = time.time() - 10
            os.utime(log_file, (old_time, old_time))

            env = os.environ.copy()
            env.update(
                {
                    "HEADLESS_WORKERS_DIR": str(workers),
                    "TEST_RESTART_MARKER": str(marker),
                    "STALE_LOG_SECONDS": "1",
                    "CHECK_INTERVAL": "1",
                    "REPEATED_STATUS_LIMIT": "999",
                    "START_DELAY": "0",
                }
            )
            supervisor = subprocess.Popen(
                [str(scripts / "supervise-workers.sh")],
                cwd=Path(temporary),
                env=env,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            output = ""
            try:
                deadline = time.time() + 6
                while time.time() < deadline:
                    if marker.is_file() and "worker-01" in marker.read_text(encoding="utf-8"):
                        break
                    time.sleep(0.05)
                else:
                    self.fail("Supervisor không restart worker có stdout.log cũ")

                sleeper.wait(timeout=3)
            finally:
                if supervisor.poll() is None:
                    supervisor.terminate()
                try:
                    output, _ = supervisor.communicate(timeout=4)
                except subprocess.TimeoutExpired:
                    supervisor.kill()
                    output, _ = supervisor.communicate(timeout=2)
                if sleeper.poll() is None:
                    sleeper.kill()
                    sleeper.wait(timeout=2)

            self.assertIn("không có log mới", output)
            self.assertIn("ngưỡng 1s", output)


if __name__ == "__main__":
    unittest.main()
