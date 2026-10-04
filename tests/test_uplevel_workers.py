import csv
import os
import subprocess
import tempfile
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
BUILD_WORKERS = ROOT / "uplevel/scripts/build-workers.sh"


class UplevelWorkersTest(unittest.TestCase):
    def test_accounts_are_split_and_debug_packet_hooks_are_removed(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            temp = Path(temp_dir)
            account_csv = temp / "accounts.csv"
            workers_dir = temp / "workers"
            with account_csv.open("w", newline="") as output:
                writer = csv.writer(output)
                writer.writerow(("username", "password"))
                for index in range(5):
                    writer.writerow((f"user{index}", f"pass{index}"))

            env = os.environ.copy()
            env.update(
                UPLEVEL_ACCOUNT_CSV=str(account_csv),
                UPLEVEL_WORKERS_DIR=str(workers_dir),
                UPLEVEL_BUILD_UPLEVEL="0",
            )
            subprocess.run([str(BUILD_WORKERS), "2"], cwd=ROOT, env=env, check=True)

            self.assertEqual(
                (workers_dir / "worker-01/account.csv").read_text().count("\n"),
                4,
            )
            self.assertEqual(
                (workers_dir / "worker-02/account.csv").read_text().count("\n"),
                3,
            )

        build_script = (ROOT / "uplevel/scripts/build.sh").read_text()
        self.assertNotIn("UPLEVEL RECV", build_script)
        self.assertNotIn("UPLEVEL SUBCMD", build_script)


if __name__ == "__main__":
    unittest.main()
