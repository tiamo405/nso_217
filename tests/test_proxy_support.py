import sys
import tempfile
import unittest
from argparse import Namespace
from pathlib import Path
from unittest.mock import patch


SCRIPT_DIR = Path(__file__).resolve().parents[1] / "optimized-runtime" / "scripts"
sys.path.insert(0, str(SCRIPT_DIR))

import proxy_support


PROXIES = [
    "200.229.24.3:10132:luong123:luong123",
    "206.125.175.52:14390:luong123:luong123",
    "149.19.197.184:49081:luong123:luong123",
]


class ProxySupportTest(unittest.TestCase):
    def make_workers(self, root: Path, count: int) -> Path:
        workers = root / "workers"
        workers.mkdir()
        for index in range(1, count + 1):
            (workers / f"worker-{index:02d}" / "home").mkdir(parents=True)
        return workers

    def make_args(self, root: Path, workers: Path, group_size: int) -> Namespace:
        proxy_file = root / "proxy-tk.txt"
        proxy_file.write_text("\n".join(PROXIES) + "\n", encoding="utf-8")
        return Namespace(
            workers_dir=str(workers),
            proxy_file=str(proxy_file),
            server="tk",
            group_size=group_size,
            target_host="Nsotk1.nsotk.online",
            target_port=14444,
        )

    @patch.object(proxy_support, "probe")
    def test_assigns_one_proxy_per_six_workers(self, probe) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            workers = self.make_workers(root, 13)
            args = self.make_args(root, workers, 6)

            self.assertEqual(proxy_support.assign(args), 0)
            self.assertEqual(probe.call_count, 3)
            for index in range(1, 14):
                value = (workers / f"worker-{index:02d}" / ".proxy").read_text().strip()
                self.assertEqual(value, PROXIES[(index - 1) // 6])

    @patch.object(proxy_support, "probe")
    def test_blocks_when_active_proxy_count_is_insufficient(self, probe) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            workers = self.make_workers(root, 13)
            args = self.make_args(root, workers, 6)
            args.proxy_file = str(root / "proxy-tk.txt")
            Path(args.proxy_file).write_text("\n".join(PROXIES[:2]) + "\n", encoding="utf-8")

            with self.assertRaises(proxy_support.ProxyError):
                proxy_support.assign(args)

    def test_zero_group_size_removes_assignments(self) -> None:
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            workers = self.make_workers(root, 1)
            proxy_path = workers / "worker-01" / ".proxy"
            proxy_path.write_text(PROXIES[0], encoding="utf-8")
            (workers / ".proxy-group-size").write_text("6", encoding="utf-8")
            args = self.make_args(root, workers, 0)

            self.assertEqual(proxy_support.assign(args), 0)
            self.assertFalse(proxy_path.exists())
            self.assertFalse((workers / ".proxy-group-size").exists())


if __name__ == "__main__":
    unittest.main()
