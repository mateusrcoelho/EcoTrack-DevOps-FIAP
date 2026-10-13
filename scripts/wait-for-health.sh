#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${1:?Informe o nome do container}"
MAX_ATTEMPTS="${2:-60}"
SLEEP_SECONDS="${3:-10}"

for attempt in $(seq 1 "$MAX_ATTEMPTS"); do
  container_status="$(docker inspect --format='{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null || true)"
  health_status="$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{end}}' "$CONTAINER_NAME" 2>/dev/null || true)"

  if [[ "$health_status" == "healthy" ]]; then
    echo "Container $CONTAINER_NAME saudável."
    exit 0
  fi

  if [[ "$health_status" == "unhealthy" ]]; then
    echo "Container $CONTAINER_NAME ficou unhealthy." >&2
    docker logs --tail=120 "$CONTAINER_NAME" || true
    exit 1
  fi

  if [[ "$container_status" == "exited" || "$container_status" == "dead" || "$container_status" == "restarting" ]]; then
    echo "Container $CONTAINER_NAME está em estado '$container_status'." >&2
    docker logs --tail=120 "$CONTAINER_NAME" || true
    exit 1
  fi

  echo "Aguardando $CONTAINER_NAME ficar saudável... tentativa $attempt/$MAX_ATTEMPTS"
  sleep "$SLEEP_SECONDS"
done

echo "Tempo limite excedido aguardando $CONTAINER_NAME." >&2
docker logs --tail=120 "$CONTAINER_NAME" || true
exit 1
