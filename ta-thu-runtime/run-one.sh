#!/usr/bin/env bash
set -euo pipefail

RUNTIME_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
CLASSES_DIR="$RUNTIME_DIR/build/classes"
RUNTIME_HOME=${TA_THU_HOME:-"$RUNTIME_DIR/run/home"}
JAVA_BIN=${JAVA_BIN:-java}
JAVA_XMS=${JAVA_XMS:-8m}
JAVA_XMX=${JAVA_XMX:-48m}
TA_THU_STAGE=${TA_THU_STAGE:-full}
JAVA_OPTS=${JAVA_OPTS:-"-XX:+UseSerialGC -Djava.awt.headless=true -Xss256k"}
# shellcheck source=../optimized-runtime/scripts/tuning-options.sh
source "$RUNTIME_DIR/../optimized-runtime/scripts/tuning-options.sh"

if [[ ! -d "$CLASSES_DIR" ]]; then
    "$RUNTIME_DIR/build-ta-thu.sh"
fi
mkdir -p "$RUNTIME_HOME"
read -r -a java_opts_array <<<"$JAVA_OPTS"

exec "$JAVA_BIN" \
    "-Xms$JAVA_XMS" \
    "-Xmx$JAVA_XMX" \
    "${java_opts_array[@]}" \
    "${OPTIMIZED_SYSTEM_PROPS[@]}" \
    -Dnso.runtime=ta-thu \
    "-Dta.thu.stage=$TA_THU_STAGE" \
    "-Duser.home=$RUNTIME_HOME" \
    -cp "$CLASSES_DIR" \
    OptimizedMain
