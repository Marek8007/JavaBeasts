#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$ROOT_DIR/deploy/env/cloud.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "No existe $ENV_FILE"
  echo "Crea el archivo con:"
  echo "  cp deploy/env/cloud.env.example deploy/env/cloud.env"
  exit 1
fi

DB_URL_OPTIONS_VALUE="$(grep -E '^DB_URL_OPTIONS=' "$ENV_FILE" | cut -d= -f2- || true)"
if [[ "$DB_URL_OPTIONS_VALUE" == *\"* || "$DB_URL_OPTIONS_VALUE" == *\'* ]]; then
  echo "DB_URL_OPTIONS no debe llevar comillas en $ENV_FILE"
  echo "Correcto: DB_URL_OPTIONS=?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3"
  echo "Incorrecto: DB_URL_OPTIONS='?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3'"
  exit 1
fi

if [[ "$DB_URL_OPTIONS_VALUE" == *"?sslMode="*"?sslMode="* ]]; then
  echo "DB_URL_OPTIONS tiene sslMode duplicado en $ENV_FILE"
  echo "Correcto: DB_URL_OPTIONS=?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3"
  exit 1
fi

if command -v xhost >/dev/null 2>&1 && [ -n "${DISPLAY:-}" ]; then
  xhost +local:docker >/dev/null || true
  xhost +SI:localuser:root >/dev/null || true
fi

export HOST_UID="$(id -u)"
export HOST_GID="$(id -g)"

cd "$ROOT_DIR"

if docker compose version >/dev/null 2>&1; then
  COMPOSE_COMMAND=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_COMMAND=(docker-compose)
else
  echo "No se ha encontrado Docker Compose."
  echo "Instala el plugin de Compose v2 o docker-compose en este equipo."
  echo "Comprueba con: docker compose version"
  exit 1
fi

"${COMPOSE_COMMAND[@]}" \
  --env-file "$ENV_FILE" \
  up --build backend javafx
