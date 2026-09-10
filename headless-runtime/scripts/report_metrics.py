#!/usr/bin/env python3
"""Summarize passive NVHN measurements; never starts or connects a worker."""
import argparse
import csv
import json
import statistics
from collections import defaultdict
from pathlib import Path


def read_rows(root, name):
    for path in sorted(root.rglob(name)):
        with path.open(encoding="utf-8", newline="") as stream:
            for row in csv.DictReader(stream):
                # Ignore a partially written final row while a worker is running.
                if row and None not in row and all(value is not None for value in row.values()):
                    yield row


def summarize(root):
    results = {r["attempt_id"]: r for r in read_rows(root, "character-results.csv")}
    starts = {r["attempt_id"]: r for r in read_rows(root, "character-events.csv") if r["event"] == "selected"}
    groups = defaultdict(list)
    for row in results.values():
        key = (row["label"], row["pass"], row["class_id"], row["level"],
               row["outcome"], row["daily_finished"], row["did_daily_work"], row["confirmed_task_returns"])
        groups[key].append(row)
    summaries = []
    for key, rows in sorted(groups.items()):
        wall = [int(r["wall_ms"]) / 1000 for r in rows]
        cpu = [int(r["cpu_ms"]) / 1000 for r in rows]
        task_count = sum(int(r["confirmed_task_returns"]) for r in rows)
        ram = [int(r["rss_mean_kb"]) / 1024 for r in rows if int(r["rss_mean_kb"]) >= 0]
        peaks = [int(r["rss_sample_peak_kb"]) / 1024 for r in rows if int(r["rss_sample_peak_kb"]) >= 0]
        summaries.append(dict(zip(("label", "pass", "class_id", "level", "outcome", "daily_finished", "did_daily_work", "returns_per_character"), key),
            characters=len(rows), confirmed_task_returns=task_count,
            wall_seconds_median=round(statistics.median(wall), 3),
            cpu_seconds_median=round(statistics.median(cpu), 3),
            rss_mean_mib=round(statistics.mean(ram), 3) if ram else None,
            rss_sample_peak_mib=round(max(peaks), 3) if peaks else None,
            gc_seconds=sum(int(r["gc_ms"]) for r in rows) / 1000,
            reconnects=sum(int(r["reconnects"]) for r in rows),
            observed_errors=sum(int(r.get("error_count", "0")) for r in rows),
            end_to_end_cpu_seconds_per_confirmed_return=round(sum(cpu) / task_count, 3) if task_count else None,
            phase_seconds_median={p: round(statistics.median(int(r[p + "_ms"]) / 1000 for r in rows), 3)
                                  for p in ("select", "prepare", "daily", "flip", "cave", "reconnect")}))
    return {"groups": summaries,
            "unfinished_attempts": [{"attempt_id": k, "label": v["label"], "pass": v["pass"]}
                                    for k, v in starts.items() if k not in results],
            "notes": ["Unfinished means running OR abruptly stopped; never counted as completed.",
                      "RSS peaks are sampled, not exact character peaks. RSS includes retained worker data.",
                      "Task returns count server removals after a completed-task return request; zero is not proof of no work.",
                      "Compare matched class/level/pass and workload across multiple runs; no automatic speedup claim."]}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("root", type=Path, nargs="?", default=Path(__file__).resolve().parents[1] / "workers")
    parser.add_argument("--json", action="store_true")
    args = parser.parse_args()
    if not args.root.is_dir():
        parser.error("metrics root does not exist")
    report = summarize(args.root)
    if args.json:
        print(json.dumps(report, ensure_ascii=False, indent=2))
    else:
        print("label | pass | class/level | outcome | chars | returns | wall median s | CPU median s | RSS mean/peak MiB | reconnects")
        for g in report["groups"]:
            print(f'{g["label"]} | {g["pass"]} | {g["class_id"]}/{g["level"]} | {g["outcome"]} '
                  f'| {g["characters"]} | {g["confirmed_task_returns"]} | {g["wall_seconds_median"]} '
                  f'| {g["cpu_seconds_median"]} | {g["rss_mean_mib"]}/{g["rss_sample_peak_mib"]} | {g["reconnects"]}')
        print(f'Unfinished attempts (running or interrupted): {len(report["unfinished_attempts"])}')
        if not report["groups"]:
            print("No completed measurement rows yet.")
        for note in report["notes"]:
            print(note)


if __name__ == "__main__":
    main()
