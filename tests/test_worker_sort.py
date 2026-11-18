"""Danh sách worker phải xếp theo số (worker-9 < worker-10 < worker-100), không theo chuỗi."""
from __future__ import annotations

import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
NAMES = ["worker-100", "worker-20", "worker-01", "worker-119", "worker-10", "worker-9", "worker-02"]
EXPECTED = ["worker-01", "worker-02", "worker-9", "worker-10", "worker-20", "worker-100", "worker-119"]


def make_workers(root: Path) -> Path:
    for name in NAMES:
        (root / name / "home").mkdir(parents=True)
    return root


class StatusJsonSortTest(unittest.TestCase):
    def _names(self, script: Path) -> list[str]:
        with tempfile.TemporaryDirectory() as tmp:
            out = subprocess.run([sys.executable, str(script), str(make_workers(Path(tmp)))],
                                 check=True, capture_output=True, text=True).stdout
        return [w["name"] for w in json.loads(out)["workers"]]

    def test_optimized(self) -> None:
        self.assertEqual(self._names(REPO / "optimized-runtime/scripts/status_workers_json.py"), EXPECTED)

    def test_ta_thu(self) -> None:
        self.assertEqual(self._names(REPO / "ta-thu-runtime/scripts/status_workers_json.py"), EXPECTED)

    def test_headless(self) -> None:
        self.assertEqual(self._names(REPO / "headless-runtime/scripts/status_workers_json.py"), EXPECTED)


class UplevelMonitorSortTest(unittest.TestCase):
    def test_collect_numeric_order(self) -> None:
        from uplevel_monitor import server

        with tempfile.TemporaryDirectory() as tmp:
            root = make_workers(Path(tmp) / "uplevel")
            original = server.RUNTIMES
            server.RUNTIMES = {"uplevel": root}
            try:
                names = [w["worker"] for w in server.collect()["workers"]]
            finally:
                server.RUNTIMES = original
        self.assertEqual(names, EXPECTED)


if __name__ == "__main__":
    unittest.main()
