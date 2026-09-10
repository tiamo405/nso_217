#!/usr/bin/env bash
set -euo pipefail
REPO_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
TEST_DIR=$(mktemp -d /tmp/nso-headless-test.XXXXXX)
trap 'rm -rf -- "$TEST_DIR"' EXIT
HEADLESS_BUILD_DIR="$TEST_DIR/build" HEADLESS_ACCOUNT_CSV=/dev/null "$REPO_DIR/headless-runtime/build-headless.sh"
javac -cp "$TEST_DIR/build/classes" -d "$TEST_DIR/build/classes" "$REPO_DIR/tests/HeadlessRuntimeTest.java"
mkdir -p "$TEST_DIR/home"
touch "$TEST_DIR/home/worker.first-pass.done"
timeout 30 java -Dnso.nvhn.headless=true -Dnso.tick.ms=50 \
    "-Duser.home=$TEST_DIR/home" -Dnso.metrics.label=offline-test \
    -cp "$TEST_DIR/build/classes" HeadlessRuntimeTest
java -cp "$TEST_DIR/build/classes" HeadlessRuntimeTest baseline
java -Dnso.nvhn.headless=true -Dnso.tick.ms=0 -Dnso.skip.periodic.gc=false \
    -Dnso.skip.auto.popup=false -Dnso.skip.decorations=false -Dnso.event.sender=false \
    -cp "$TEST_DIR/build/classes" HeadlessRuntimeTest baseline
python3 - "$TEST_DIR/home/metrics" <<'PY'
import csv, sys
from pathlib import Path
root = Path(sys.argv[1])
with (root / 'character-results.csv').open() as stream:
    rows = list(csv.DictReader(stream))
assert len(rows) == 2, rows
assert rows[0]['confirmed_task_returns'] == '1'
assert rows[0]['reconnects'] == '1'
assert rows[0]['account'] == 'test,account' and rows[0]['character'] == 'character"one'
assert rows[0]['outcome'] == 'completed' and rows[1]['outcome'] == 'interrupted'
assert all(r['pass'] == '2' for r in rows)
for row in rows:
    phases = sum(int(row[p + '_ms']) for p in ('select', 'prepare', 'daily', 'flip', 'cave', 'reconnect'))
    assert 0 <= int(row['wall_ms']) - phases <= 6
print('PASS: CSV escaping, task acknowledgements, reconnect, shutdown, phase durations, pass 2')
PY
python3 "$REPO_DIR/headless-runtime/scripts/report_metrics.py" "$TEST_DIR/home"
