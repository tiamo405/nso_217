import importlib.util
import io
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

SCRIPT = Path(__file__).resolve().parents[1] / "uplevel" / "scripts" / "proxy_assign.py"
spec = importlib.util.spec_from_file_location("uplevel_proxy_assign", SCRIPT)
proxy_assign = importlib.util.module_from_spec(spec)
spec.loader.exec_module(proxy_assign)


class UplevelProxyAssignTest(unittest.TestCase):
    def run_assign(self, workers: int, proxies: int, use_local=None, env_local="1"):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            workers_dir = root / "workers"
            for number in range(1, workers + 1):
                (workers_dir / f"worker-{number:02d}").mkdir(parents=True)
                # .proxy cũ phải bị xoá nếu worker được chia cho mạng máy.
                (workers_dir / f"worker-{number:02d}" / ".proxy").write_text("old:1:u:p\n")
            proxy_file = root / "proxy-tk.txt"
            proxy_file.write_text(
                "".join(f"10.0.0.{n}:1080:user:pass\n" for n in range(1, proxies + 1)))
            with patch.dict("os.environ", {"NSO_PROXY_USE_LOCAL": env_local}):
                plan = proxy_assign.assign(workers_dir, proxy_file, check=False,
                                           out=io.StringIO(), use_local=use_local)
            files = {
                path.parent.name: path.read_text().strip().split(":")[0]
                for path in sorted(workers_dir.glob("worker-*/.proxy"))
            }
            return plan, files

    def test_machine_network_is_one_extra_share(self) -> None:
        plan, files = self.run_assign(50, 2)
        values = list(plan.values())
        self.assertEqual([values.count("10.0.0.1:1080:user:pass"),
                          values.count("10.0.0.2:1080:user:pass"), values.count(None)],
                         [17, 17, 16])
        self.assertEqual(plan["worker-34"], "10.0.0.2:1080:user:pass")
        self.assertIsNone(plan["worker-35"])
        # Worker chạy mạng máy không có file .proxy (start-workers hiểu là IP máy).
        self.assertEqual(len(files), 34)
        self.assertNotIn("worker-35", files)

    def test_no_local_keeps_old_split(self) -> None:
        plan, files = self.run_assign(10, 4, env_local="0")
        values = list(plan.values())
        self.assertEqual([values.count(f"10.0.0.{n}:1080:user:pass") for n in range(1, 5)],
                         [3, 3, 2, 2])
        self.assertEqual(len(files), 10)
        plan2, _ = self.run_assign(10, 4, use_local=False)
        self.assertEqual(plan, plan2)

    def test_empty_proxy_file_uses_machine_network(self) -> None:
        plan, files = self.run_assign(3, 0)
        self.assertEqual(plan, {"worker-01": None, "worker-02": None, "worker-03": None})
        self.assertEqual(files, {})


if __name__ == "__main__":
    unittest.main()
