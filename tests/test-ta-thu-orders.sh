#!/usr/bin/env bash
set -euo pipefail
REPO_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
TEST_DIR=$(mktemp -d /tmp/nso-ta-thu-orders-test.XXXXXX)
trap 'rm -rf -- "$TEST_DIR"' EXIT
TA_THU_BUILD_DIR="$TEST_DIR/build" TA_THU_ACCOUNT_CSV=/dev/null "$REPO_DIR/ta-thu-runtime/build-ta-thu.sh"
mkdir -p "$TEST_DIR/test-classes"
javac -encoding UTF-8 -cp "$TEST_DIR/build/classes" -d "$TEST_DIR/test-classes" \
    "$REPO_DIR/tests/ta-thu-orders/TaThuOrdersTest.java" \
    "$REPO_DIR/ta-thu-runtime/src/AutoTaThuOrders.java" \
    "$REPO_DIR/ta-thu-runtime/src/AutoTaThuDaily.java" \
    "$REPO_DIR/ta-thu-runtime/src/TaThuDailyState.java"
timeout 30 java "-Dta.thu.state.dir=$TEST_DIR/state" \
    -cp "$TEST_DIR/test-classes:$TEST_DIR/build/classes" TaThuOrdersTest
