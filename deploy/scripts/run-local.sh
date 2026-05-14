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

if command -v xhost >/dev/null 2>&1 && [ -n "${DISPLAY:-}" ]; then
  xhost +local:docker >/dev/null || true
fi

cd "$ROOT_DIR"

docker compose \
  --env-file "$ENV_FILE" \
  --profile local \
  up --build
