#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_FILE="$ROOT_DIR/deploy/env/local.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "No existe $ENV_FILE"
  echo "Crea el archivo con:"
  echo "  cp deploy/env/local.env.example deploy/env/local.env"
  exit 1
fi

DB_URL_OPTIONS_VALUE="$(grep -E '^DB_URL_OPTIONS=' "$ENV_FILE" | cut -d= -f2- || true)"
if [[ "$DB_URL_OPTIONS_VALUE" == *\"* || "$DB_URL_OPTIONS_VALUE" == *\'* ]]; then
  echo "DB_URL_OPTIONS no debe llevar comillas en $ENV_FILE"
  echo "Correcto: DB_URL_OPTIONS=?sslMode=DISABLED"
  echo "Incorrecto: DB_URL_OPTIONS='?sslMode=DISABLED'"
  exit 1
fi

if [[ "$DB_URL_OPTIONS_VALUE" == *"?sslMode="*"?sslMode="* ]]; then
  echo "DB_URL_OPTIONS tiene sslMode duplicado en $ENV_FILE"
  echo "Correcto: DB_URL_OPTIONS=?sslMode=DISABLED"
  exit 1
fi

if command -v xhost >/dev/null 2>&1 && [ -n "${DISPLAY:-}" ]; then
  xhost +local:docker >/dev/null || true
fi

cd "$ROOT_DIR"

docker compose \
  --env-file "$ENV_FILE" \
  --profile local \
  up --build
