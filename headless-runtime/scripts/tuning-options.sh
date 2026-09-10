#!/usr/bin/env bash
# Sourced by NVHN launchers only. Arrays preserve spaces in labels and paths.
HEADLESS_PROFILE=${HEADLESS_PROFILE:-optimized}
case "$HEADLESS_PROFILE" in
    baseline) tuning_tick=0; tuning_switch=0 ;;
    optimized) tuning_tick=50; tuning_switch=1 ;;
    *) echo 'HEADLESS_PROFILE phải là baseline hoặc optimized' >&2; exit 1 ;;
esac
HEADLESS_TICK_MS=${HEADLESS_TICK_MS:-$tuning_tick}
HEADLESS_SKIP_PERIODIC_GC=${HEADLESS_SKIP_PERIODIC_GC:-$tuning_switch}
HEADLESS_SKIP_AUTO_POPUP=${HEADLESS_SKIP_AUTO_POPUP:-$tuning_switch}
HEADLESS_SKIP_DECORATIONS=${HEADLESS_SKIP_DECORATIONS:-$tuning_switch}
HEADLESS_EVENT_SENDER=${HEADLESS_EVENT_SENDER:-$tuning_switch}
HEADLESS_METRICS=${HEADLESS_METRICS:-1}
HEADLESS_METRICS_LABEL=${HEADLESS_METRICS_LABEL:-$HEADLESS_PROFILE}
if ! [[ "$HEADLESS_TICK_MS" =~ ^(0|[1-9][0-9]{0,3})$ ]] || (( HEADLESS_TICK_MS > 1000 )); then
    echo 'HEADLESS_TICK_MS phải là số nguyên 0..1000 (0 = dùng RMS)' >&2; exit 1
fi
headless_tuning_args=("-Dnso.nvhn.headless=true" "-Dnso.tick.ms=$HEADLESS_TICK_MS"
    "-Dnso.metrics.label=$HEADLESS_METRICS_LABEL")
for tuning_pair in \
    "nso.skip.periodic.gc:$HEADLESS_SKIP_PERIODIC_GC" \
    "nso.skip.auto.popup:$HEADLESS_SKIP_AUTO_POPUP" \
    "nso.skip.decorations:$HEADLESS_SKIP_DECORATIONS" \
    "nso.event.sender:$HEADLESS_EVENT_SENDER" \
    "nso.metrics:$HEADLESS_METRICS"; do
    tuning_value=${tuning_pair#*:}
    case "$tuning_value" in
        1) tuning_boolean=true ;;
        0) tuning_boolean=false ;;
        *) echo "Công tắc ${tuning_pair%%:*} phải là 0 hoặc 1" >&2; exit 1 ;;
    esac
    headless_tuning_args+=("-D${tuning_pair%%:*}=$tuning_boolean")
done
