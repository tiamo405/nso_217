#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)

worker_args=()
server_args=()
while (( $# > 0 )); do
    case "$1" in
        --server)
            if (( $# < 2 )); then
                echo "Thiếu tên server sau --server." >&2
                exit 1
            fi
            server_args+=("$1" "$2")
            shift 2
            ;;
        --server=*)
            server_args+=("$1")
            shift
            ;;
        *)
            worker_args+=("$1")
            shift
            ;;
    esac
done

"$SCRIPT_DIR/stop-workers.sh" "${worker_args[@]}"
"$SCRIPT_DIR/start-workers.sh" "${server_args[@]}" "${worker_args[@]}"
