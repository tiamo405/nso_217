import csv
import importlib.util
import os
from pathlib import Path
import subprocess
import tempfile
import unittest


path = Path(__file__).resolve().parents[1] / "headless-runtime/scripts/report_metrics.py"
spec = importlib.util.spec_from_file_location("report_metrics", path)
report = importlib.util.module_from_spec(spec)
spec.loader.exec_module(report)


class MetricsReportTests(unittest.TestCase):
    def test_launcher_profiles_and_validation(self):
        script = path.with_name("tuning-options.sh")
        env = {k: v for k, v in os.environ.items() if not k.startswith("HEADLESS_")}
        def options(**settings):
            return subprocess.run(["bash", "-c", 'source "$1"; printf "%s\\n" "${headless_tuning_args[@]}"',
                                   "test", str(script)], env=dict(env, **settings), text=True, capture_output=True)
        baseline = options(HEADLESS_PROFILE="baseline", HEADLESS_METRICS_LABEL="trial with spaces")
        self.assertEqual(baseline.returncode, 0, baseline.stderr)
        self.assertIn("-Dnso.tick.ms=0\n", baseline.stdout)
        self.assertIn("-Dnso.event.sender=false\n", baseline.stdout)
        self.assertIn("-Dnso.metrics.label=trial with spaces\n", baseline.stdout)
        optimized = options()
        self.assertEqual(optimized.returncode, 0, optimized.stderr)
        self.assertIn("-Dnso.tick.ms=50\n", optimized.stdout)
        self.assertIn("-Dnso.event.sender=true\n", optimized.stdout)
        for settings in ({"HEADLESS_TICK_MS": "1001"}, {"HEADLESS_PROFILE": "typo"},
                         {"HEADLESS_EVENT_SENDER": "yes"}, {"HEADLESS_TICK_MS": "-10"}):
            with self.subTest(settings=settings):
                self.assertNotEqual(options(**settings).returncode, 0)

    def test_groups_separate_pass_workload_and_incomplete(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            rows = []
            for attempt, pass_id, tasks, outcome in (("a", "1", "20", "completed"),
                    ("b", "2", "0", "completed"), ("c", "1", "10", "interrupted")):
                rows.append(dict(attempt_id=attempt, label="baseline", pass_=pass_id,
                    class_id="2", level="40", outcome=outcome, daily_finished=str(outcome == "completed").lower(),
                    did_daily_work=str(tasks != "0").lower(), confirmed_task_returns=tasks,
                    wall_ms="100000", cpu_ms="5000", rss_mean_kb="51200", rss_sample_peak_kb="61440",
                    gc_ms="100", reconnects="0", **{p + "_ms": "0" for p in
                    ("select", "prepare", "daily", "flip", "cave", "reconnect")}))
                rows[-1]["pass"] = rows[-1].pop("pass_")
            with (root / "character-results.csv").open("w", newline="") as stream:
                writer = csv.DictWriter(stream, fieldnames=list(rows[0]))
                writer.writeheader(); writer.writerows(rows)
            with (root / "character-events.csv").open("w", newline="") as stream:
                writer = csv.DictWriter(stream, fieldnames=["attempt_id", "event", "label", "pass"])
                writer.writeheader()
                for attempt in ("a", "b", "c", "orphan"):
                    writer.writerow(dict(attempt_id=attempt, event="selected", label="baseline", **{"pass": "1"}))
            result = report.summarize(root)
            self.assertEqual(len(result["groups"]), 3)
            self.assertEqual([r["attempt_id"] for r in result["unfinished_attempts"]], ["orphan"])
            active = next(g for g in result["groups"] if g["confirmed_task_returns"] == 20)
            self.assertEqual(active["end_to_end_cpu_seconds_per_confirmed_return"], 0.25)
            self.assertEqual(active["rss_mean_mib"], 50)
            empty = next(g for g in result["groups"] if g["pass"] == "2")
            self.assertIsNone(empty["end_to_end_cpu_seconds_per_confirmed_return"])


if __name__ == "__main__":
    unittest.main()
