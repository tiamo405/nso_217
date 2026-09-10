from __future__ import annotations

import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest


SCRIPTS = Path(__file__).resolve().parents[1] / "ta-thu-runtime" / "scripts"


class TaThuCompletionTest(unittest.TestCase):
    def test_completed_workers_are_not_relaunched(self) -> None:
        for marker_path in ("worker.done", "home/worker.done"):
            with self.subTest(marker=marker_path), tempfile.TemporaryDirectory() as temporary:
                root = Path(temporary)
                worker = root / "workers" / "worker-01"
                (worker / "home").mkdir(parents=True)
                (root / "classes").mkdir()
                (worker / marker_path).write_text("completed\n")
                # A launch is always a failure; no game server is contacted.
                env = {
                    **os.environ,
                    "TA_THU_WORKERS_DIR": str(worker.parent),
                    "TA_THU_CLASSES_DIR": str(root / "classes"),
                    "JAVA_BIN": "/bin/false",
                    "CHECK_INTERVAL": "1",
                    "STALE_LOG_SECONDS": "1",
                }

                def run(script: str, *args: str) -> str:
                    return subprocess.run(
                        [str(SCRIPTS / script), *args], env=env,
                        check=True, capture_output=True, text=True, timeout=5,
                    ).stdout

                self.assertIn("hoàn tất=1", run("start-workers.sh", "--delay", "0"))
                self.assertIn("DONE", run("status-workers.sh"))
                status = json.loads(run("status-workers.sh", "--json"))
                self.assertEqual(status["workers"][0]["state"], "DONE")
                self.assertEqual(status["totals"]["done"], 1)

                supervisor = subprocess.Popen(
                    [str(SCRIPTS / "supervise-workers.sh"), "--delay", "0", "1"],
                    env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                    text=True,
                )
                try:
                    try:
                        output, _ = supervisor.communicate(timeout=2.5)
                        self.fail(f"Supervisor exited unexpectedly: {output}")
                    except subprocess.TimeoutExpired:
                        supervisor.terminate()
                        output, _ = supervisor.communicate(timeout=5)
                finally:
                    if supervisor.poll() is None:
                        supervisor.kill()
                        supervisor.communicate(timeout=5)

                self.assertGreaterEqual(output.count("hoàn tất=1"), 2)
                self.assertNotIn("đang restart", output)
                self.assertFalse((worker / "stdout.log").exists())
                self.assertFalse((worker / "bot.pid").exists())
                self.assertEqual((worker / marker_path).read_text(), "completed\n")


if __name__ == "__main__":
    unittest.main()
