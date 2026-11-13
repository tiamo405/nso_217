import csv
import os
import subprocess
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RUNTIME = ROOT / "uplevel1to35"


class Uplevel1to35Test(unittest.TestCase):
    def test_build_workers_splits_round_robin_without_build(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            temp = Path(temp_dir)
            account_csv = temp / "accounts.csv"
            workers_dir = temp / "workers"
            with account_csv.open("w", newline="") as output:
                writer = csv.writer(output, lineterminator="\n")
                writer.writerow(("username", "password"))
                for index in range(5):
                    writer.writerow((f"user{index}", f"pass{index}"))

            env = os.environ.copy()
            env.update(
                UPLEVEL1TO35_ACCOUNT_CSV=str(account_csv),
                UPLEVEL1TO35_WORKERS_DIR=str(workers_dir),
                UPLEVEL1TO35_SKIP_BUILD="1",
                UPLEVEL1TO35_PROXY_FILE=str(temp / "no-proxy.txt"),
            )
            subprocess.run([str(RUNTIME / "scripts/build-workers.sh"), "2"], cwd=ROOT, env=env,
                           check=True, stdout=subprocess.DEVNULL)

            first = (workers_dir / "worker-01/account.csv").read_text().splitlines()
            second = (workers_dir / "worker-02/account.csv").read_text().splitlines()
            self.assertEqual(first, ["username,password", "user0,pass0", "user2,pass2", "user4,pass4"])
            self.assertEqual(second, ["username,password", "user1,pass1", "user3,pass3"])

    def test_transforms_fail_loudly_on_missing_needle(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            temp = Path(temp_dir)
            for name in ("AccountAutoManager.java", "Controller.java", "As35.java"):
                (temp / name).write_text("class X {}\n", encoding="utf-8")
            result = subprocess.run(["python3", str(RUNTIME / "transforms/apply.py"), str(temp)],
                                    capture_output=True, text=True)
            self.assertNotEqual(result.returncode, 0)
            self.assertIn("uplevel1to35 transform lỗi", result.stderr)

    def test_uplevel_build_uses_shared_transforms(self):
        build = (ROOT / "uplevel/scripts/build.sh").read_text(encoding="utf-8")
        combined = (RUNTIME / "scripts/build.sh").read_text(encoding="utf-8")
        self.assertIn('"$SCRIPT_DIR/apply-transforms.sh" "$WORK_SRC_DIR"', build)
        self.assertIn('"$UPLEVEL_DIR/scripts/apply-transforms.sh" "$WORK_SRC_DIR"', combined)

    def test_windows_wrappers_exist_with_crlf(self):
        for name in ("build", "build-workers", "start-workers", "stop-workers", "status-workers", "logs-workers"):
            data = (RUNTIME / "scripts" / f"{name}.cmd").read_bytes()
            self.assertIn(b"\r\n", data, name)
            self.assertIn(f"uplevel1to35 {name}".encode(), data)


if __name__ == "__main__":
    unittest.main()
