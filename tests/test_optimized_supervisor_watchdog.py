from __future__ import annotations

import os
import shlex
import shutil
import stat
import subprocess
import sys
import tempfile
import time
import unittest
from pathlib import Path


REPO_DIR = Path(__file__).resolve().parents[1]


def write_script(path: Path, body: str) -> None:
    path.write_text("#!/usr/bin/env bash\nset -euo pipefail\n" + body, encoding="utf-8")
    path.chmod(path.stat().st_mode | stat.S_IXUSR)


class OptimizedSupervisorWatchdogTest(unittest.TestCase):
    def test_windows_stop_kills_untracked_worker_process(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-optimized-win-stop-") as temporary:
            root = Path(temporary)
            runtime = root / "optimized-runtime"
            windows = runtime / "windows"
            worker = runtime / "workers" / "worker-01"
            (runtime / "src").mkdir(parents=True)
            (runtime / "src" / "OptimizedMain.java").write_text("", encoding="utf-8")
            (runtime / "overrides").mkdir()
            (root / "src" / "map").mkdir(parents=True)
            (worker / "home").mkdir(parents=True)
            windows.mkdir(parents=True)
            win_manager = windows / "win_manager.py"
            shutil.copy2(REPO_DIR / "optimized-runtime/windows/win_manager.py", win_manager)

            process_title = f"OptimizedMain {worker}"
            worker_process = subprocess.Popen(
                ["bash", "-c", f"exec -a {shlex.quote(process_title)} sleep 60"],
                stdin=subprocess.DEVNULL,
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            )
            try:
                result = subprocess.run(
                    [sys.executable, str(win_manager), "stop"],
                    cwd=root,
                    env=os.environ.copy(),
                    capture_output=True,
                    text=True,
                    timeout=5,
                )
                self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                worker_process.wait(timeout=3)
                self.assertIn("worker mo coi", result.stdout)
            finally:
                if worker_process.poll() is None:
                    worker_process.kill()
                worker_process.wait(timeout=2)

    def test_ubuntu_stop_kills_orphan_supervisor_without_pid_file(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-optimized-stop-") as temporary:
            root = Path(temporary)
            runtime = root / "optimized-runtime"
            scripts = runtime / "scripts"
            workers = runtime / "workers"
            (workers / "worker-01" / "home").mkdir(parents=True)
            scripts.mkdir(parents=True)

            for name in ("supervise-workers.sh", "stop-workers.sh"):
                shutil.copy2(REPO_DIR / "optimized-runtime/scripts" / name, scripts / name)
            write_script(scripts / "start-workers.sh", "sleep 0.1\n")

            env = {
                **os.environ,
                "OPTIMIZED_WORKERS_DIR": str(workers),
                "CHECK_INTERVAL": "1",
                "START_DELAY": "0",
            }
            supervisor = subprocess.Popen(
                [str(scripts / "supervise-workers.sh")],
                cwd=root,
                env=env,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            try:
                deadline = time.time() + 3
                pid_file = workers / "supervisor.pid"
                while time.time() < deadline and not pid_file.is_file():
                    time.sleep(0.05)
                self.assertTrue(pid_file.is_file(), "Supervisor chưa tạo supervisor.pid")
                pid_file.unlink()

                result = subprocess.run(
                    [str(scripts / "stop-workers.sh")],
                    cwd=root,
                    env=env,
                    capture_output=True,
                    text=True,
                    timeout=5,
                )
                self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                supervisor.wait(timeout=3)
                supervisor.communicate(timeout=2)
                self.assertEqual(supervisor.returncode, 0)
            finally:
                if supervisor.poll() is None:
                    supervisor.kill()
                supervisor.wait(timeout=2)

    def test_ubuntu_supervisor_restarts_worker_with_stale_stdout(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-optimized-stale-") as temporary:
            root = Path(temporary)
            runtime = root / "optimized-runtime"
            scripts = runtime / "scripts"
            workers = runtime / "workers"
            worker = workers / "worker-01"
            scripts.mkdir(parents=True)
            (worker / "home").mkdir(parents=True)

            shutil.copy2(
                REPO_DIR / "optimized-runtime/scripts/supervise-workers.sh",
                scripts / "supervise-workers.sh",
            )
            marker = root / "restart-marker.txt"
            write_script(
                scripts / "start-workers.sh",
                'if [[ "$*" == *"worker-01"* ]]; then printf "%s\\n" "$*" >>"$TEST_RESTART_MARKER"; fi\n',
            )

            process_title = f"OptimizedMain {worker}"
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

            env = {
                **os.environ,
                "OPTIMIZED_WORKERS_DIR": str(workers),
                "TEST_RESTART_MARKER": str(marker),
                "STALE_LOG_SECONDS": "1",
                "CHECK_INTERVAL": "1",
                "START_DELAY": "0",
            }
            supervisor = subprocess.Popen(
                [str(scripts / "supervise-workers.sh")],
                cwd=root,
                env=env,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            try:
                deadline = time.time() + 6
                while time.time() < deadline:
                    if marker.is_file() and "worker-01" in marker.read_text(encoding="utf-8"):
                        break
                    time.sleep(0.05)
                else:
                    self.fail("Ubuntu supervisor không restart worker có stdout.log cũ")
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

            self.assertIn("log im lặng đủ 1s", output)
            self.assertIn("stdout.log không đổi", output)
            self.assertEqual(
                supervisor.returncode,
                0,
                "Ubuntu supervisor phải thoát hẳn khi nhận lệnh Stop",
            )

    def test_windows_supervisor_restarts_worker_with_stale_stdout(self) -> None:
        with tempfile.TemporaryDirectory(prefix="nso-optimized-win-stale-") as temporary:
            root = Path(temporary)
            runtime = root / "optimized-runtime"
            windows = runtime / "windows"
            workers = runtime / "workers"
            worker = workers / "worker-01"
            (runtime / "src").mkdir(parents=True)
            (runtime / "src" / "OptimizedMain.java").write_text("", encoding="utf-8")
            (runtime / "overrides").mkdir()
            (root / "src").mkdir()
            (runtime / "build" / "classes").mkdir(parents=True)
            (runtime / "build" / "classes" / "OptimizedMain.class").touch()
            (worker / "home").mkdir(parents=True)
            windows.mkdir(parents=True)
            win_manager = windows / "win_manager.py"
            shutil.copy2(REPO_DIR / "optimized-runtime/windows/win_manager.py", win_manager)

            bin_dir = root / "bin"
            bin_dir.mkdir()
            marker = root / "restart-marker.txt"
            write_script(
                bin_dir / "java",
                'printf "%s\\n" "$*" >>"$TEST_RESTART_MARKER"\nexec sleep 60\n',
            )

            sleeper = subprocess.Popen(
                ["bash", "-c", f"exec -a {shlex.quote(f'OptimizedMain {worker}')} sleep 60"],
                stdin=subprocess.DEVNULL,
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            )
            (worker / "bot.pid").write_text(f"{sleeper.pid}\n", encoding="utf-8")
            log_file = worker / "stdout.log"
            log_file.write_text("AUTO NVHN STATUS: old\n", encoding="utf-8")
            old_time = time.time() - 10
            os.utime(log_file, (old_time, old_time))

            env = {
                **os.environ,
                "PATH": f"{bin_dir}{os.pathsep}{os.environ.get('PATH', '')}",
                "TEST_RESTART_MARKER": str(marker),
                "STALE_LOG_SECONDS": "1",
            }
            supervisor = subprocess.Popen(
                [
                    sys.executable,
                    str(win_manager),
                    "supervise",
                    "--delay",
                    "0",
                    "--interval",
                    "1",
                    "1",
                ],
                cwd=root,
                env=env,
                stdin=subprocess.DEVNULL,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
            )
            restarted_pid = None
            failure_message = None
            try:
                deadline = time.time() + 8
                while time.time() < deadline:
                    if marker.is_file() and marker.read_text(encoding="utf-8").strip():
                        restarted_pid = int((worker / "bot.pid").read_text(encoding="utf-8"))
                        break
                    time.sleep(0.05)
                else:
                    failure_message = "Windows supervisor không restart worker có stdout.log cũ"
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
                if restarted_pid is not None:
                    try:
                        os.kill(restarted_pid, 9)
                    except OSError:
                        pass

            if failure_message is not None:
                self.fail(failure_message + "\n" + output)
            self.assertIn("log im lặng đủ 1s", output)
            self.assertIn("stdout.log không đổi", output)


if __name__ == "__main__":
    unittest.main()
