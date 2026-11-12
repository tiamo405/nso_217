from __future__ import annotations

import argparse
import importlib.util
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

SCRIPT = Path(__file__).resolve().parents[1] / "optimized-runtime" / "scripts" / "proxy_support.py"
spec = importlib.util.spec_from_file_location("nvhn_proxy_support", SCRIPT)
proxy_support = importlib.util.module_from_spec(spec)
spec.loader.exec_module(proxy_support)


class NvhnProxySplitTest(unittest.TestCase):
    def run_split(self, workers: int, proxies: int, group_size: int = 1) -> dict[str, str]:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            workers_dir = root / "workers"
            for number in range(1, workers + 1):
                (workers_dir / f"worker-{number:02d}" / "home").mkdir(parents=True)
            proxy_file = root / "proxy-tk.txt"
            proxy_file.write_text(
                "\n".join(f"10.0.0.{n}:1080:user:pass" for n in range(1, proxies + 1)) + "\n",
                encoding="utf-8",
            )
            args = argparse.Namespace(
                workers_dir=str(workers_dir), proxy_file=str(proxy_file),
                group_size=group_size, target_host="x", target_port=1,
            )
            with patch.object(proxy_support, "probe"), patch("sys.stdout"):
                proxy_support.assign(args)
            return {
                path.parent.name: path.read_text(encoding="utf-8").split(":")[0]
                for path in sorted(workers_dir.glob("worker-*/.proxy"))
            }

    def test_even_contiguous_split_like_uplevel(self) -> None:
        plan = self.run_split(10, 4)
        counts = [list(plan.values()).count(f"10.0.0.{n}") for n in range(1, 5)]
        self.assertEqual(counts, [3, 3, 2, 2])
        self.assertEqual(plan["worker-01"], "10.0.0.1")
        self.assertEqual(plan["worker-04"], "10.0.0.2")
        self.assertEqual(plan["worker-10"], "10.0.0.4")

    def test_group_size_value_no_longer_matters(self) -> None:
        self.assertEqual(self.run_split(5, 2, group_size=1), self.run_split(5, 2, group_size=6))

    def test_empty_proxy_file_falls_back_to_machine_network(self) -> None:
        self.assertEqual(self.run_split(3, 0), {})

    def test_launch_env(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            workers_dir = Path(tmp)
            worker = workers_dir / "worker-01"
            worker.mkdir()
            self.assertEqual(proxy_support.launch_env(worker, enabled=False), {})
            with self.assertRaises(proxy_support.ProxyError):
                proxy_support.launch_env(worker, enabled=True)
            (workers_dir / ".proxy-group-size").write_text("even:0\n", encoding="utf-8")
            self.assertEqual(proxy_support.launch_env(worker, enabled=True), {})
            (worker / ".proxy").write_text("1.2.3.4:1080:u:p\n", encoding="utf-8")
            self.assertEqual(
                proxy_support.launch_env(worker, enabled=True),
                {"NSO_PROXY_REQUIRED": "1", "NSO_SOCKS5_PROXY": "1.2.3.4:1080:u:p"},
            )

    def test_windows_managers_load_shared_proxy_support(self) -> None:
        repo = Path(__file__).resolve().parents[1]
        for relative in ("optimized-runtime/windows/win_manager.py",
                         "ta-thu-runtime/windows/ta_thu_manager.py"):
            spec = importlib.util.spec_from_file_location("mgr_" + relative.split("/")[0].replace("-", "_"),
                                                          repo / relative)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)
            loaded = module._load_proxy_support()
            self.assertTrue(hasattr(loaded, "prepare"))
            self.assertTrue(hasattr(loaded, "launch_env"))

    def test_disabled_clears_assignments(self) -> None:
        self.assertEqual(self.run_split(3, 2, group_size=0), {})


if __name__ == "__main__":
    unittest.main()
