#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${1:?Informe o nome do container}"
MAX_ATTEMPTS="${2:-60}"
SLEEP_SECONDS="${3:-10}"

for attempt in $(seq 1 "$MAX_ATTEMPTS"); do
  status="$(docker inspect --format='{{.State.Health.Status}}' "$CONTAINER_NAME" 2>/dev/null || true)"

  if [[ "$status" == "healthy" ]]; then
    echo "Container $CONTAINER_NAME saudável."
    exit 0
  fi

  if [[ "$status" == "unhealthy" ]]; then
    echo "Container $CONTAINER_NAME ficou unhealthy." >&2
    exit 1
  fi

  echo "Aguardando $CONTAINER_NAME ficar saudável... tentativa $attempt/$MAX_ATTEMPTS"
  sleep "$SLEEP_SECONDS"
done

echo "Tempo limite excedido aguardando $CONTAINER_NAME." >&2
exit 1
