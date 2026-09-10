#!/usr/bin/env bash
set -euo pipefail
REPO_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
TEST_DIR=$(mktemp -d /tmp/nso-character-reconnect.XXXXXX)
trap 'rm -rf -- "$TEST_DIR"' EXIT
HEADLESS_BUILD_DIR="$TEST_DIR/headless" HEADLESS_ACCOUNT_CSV=/dev/null bash "$REPO_DIR/headless-runtime/build-headless.sh"
TA_THU_BUILD_DIR="$TEST_DIR/ta-thu" TA_THU_ACCOUNT_CSV=/dev/null bash "$REPO_DIR/ta-thu-runtime/build-ta-thu.sh"
for runtime in headless ta-thu; do
    classes="$TEST_DIR/$runtime/classes"
    mkdir -p "$TEST_DIR/$runtime/probe" "$TEST_DIR/$runtime/route" "$TEST_DIR/home"
    javac -encoding UTF-8 -cp "$classes" -d "$TEST_DIR/$runtime/probe" \
        "$REPO_DIR/tests/reconnect/CharacterReconnectTest.java"
    manager=AccountAutoManager
    if [[ "$runtime" == ta-thu ]]; then manager=TaThuAccountManager; fi
    timeout 30 java "-Duser.home=$TEST_DIR/home" \
        -cp "$TEST_DIR/$runtime/probe:$classes" CharacterReconnectTest "$manager"
    javac -encoding UTF-8 -cp "$classes" -d "$TEST_DIR/$runtime/route" \
        "$REPO_DIR/tests/reconnect/SessionReconnectRouteTest.java"
    timeout 10 java "-Duser.home=$TEST_DIR/home" \
        -cp "$TEST_DIR/$runtime/route:$classes" SessionReconnectRouteTest "$runtime"
done
