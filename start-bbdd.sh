#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$SCRIPT_DIR/JavaBeasts-BBDD"
COMPOSE_FILE="$COMPOSE_DIR/docker-compose.yml"
ENV_FILE="$COMPOSE_DIR/.env"

if ! command -v docker >/dev/null 2>&1 && ! command -v docker-compose >/dev/null 2>&1; then
  echo "Error: Docker Compose no esta instalado o no esta en PATH." >&2
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "Error: no se ha encontrado $ENV_FILE." >&2
  exit 1
fi

set -a
source "$ENV_FILE"
set +a

CONTAINER_NAME="${MYSQL_HOST:-}"

if [ -z "$CONTAINER_NAME" ]; then
  echo "Error: MYSQL_HOST no esta definido en $ENV_FILE." >&2
  exit 1
fi

if docker container inspect "$CONTAINER_NAME" >/dev/null 2>&1; then
  if [ "$(docker inspect -f '{{.State.Running}}' "$CONTAINER_NAME")" = "true" ]; then
    echo "El contenedor de BBDD ya esta arrancado."
  else
    docker start "$CONTAINER_NAME" >/dev/null
    echo "Contenedor de BBDD arrancado."
  fi
  exit 0
fi

if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  docker compose --project-directory "$COMPOSE_DIR" -f "$COMPOSE_FILE" up -d --remove-orphans
elif command -v docker-compose >/dev/null 2>&1; then
  docker-compose --project-directory "$COMPOSE_DIR" -f "$COMPOSE_FILE" up -d --remove-orphans
else
  echo "Error: no se ha podido ejecutar Docker Compose." >&2
  exit 1
fi

echo "Contenedor de BBDD creado y arrancado."
