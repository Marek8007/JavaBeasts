#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$SCRIPT_DIR/JavaBeasts-BBD"
COMPOSE_FILE="$COMPOSE_DIR/docker-compose.yml"

if ! command -v docker >/dev/null 2>&1 && ! command -v docker-compose >/dev/null 2>&1; then
  echo "Error: Docker Compose no esta instalado o no esta en PATH." >&2
  exit 1
fi

if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  docker compose --project-directory "$COMPOSE_DIR" -f "$COMPOSE_FILE" up -d
elif command -v docker-compose >/dev/null 2>&1; then
  docker-compose --project-directory "$COMPOSE_DIR" -f "$COMPOSE_FILE" up -d
else
  echo "Error: no se ha podido ejecutar Docker Compose." >&2
  exit 1
fi

echo "Contenedor de BBDD arrancado."
