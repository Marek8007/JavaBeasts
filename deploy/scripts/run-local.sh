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

if [ -z "${JAVA_HOME:-}" ]; then
  for candidate in /usr/lib/jvm/java-21-openjdk-* /usr/lib/jvm/jdk-21* /usr/lib/jvm/temurin-21*; do
    if [ -x "$candidate/bin/javac" ]; then
      export JAVA_HOME="$candidate"
      export PATH="$JAVA_HOME/bin:$PATH"
      break
    fi
  done
fi

JAVA_VERSION_OUTPUT="$(javac -version 2>&1 || true)"
if [[ "$JAVA_VERSION_OUTPUT" != javac\ 21* ]]; then
  echo "JavaFX se ejecuta fuera de Docker y necesita JDK 21."
  echo "Version actual: ${JAVA_VERSION_OUTPUT:-javac no encontrado}"
  echo "Instala JDK 21 o exporta JAVA_HOME apuntando a un JDK 21 antes de lanzar el script."
  exit 1
fi

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
  --profile local \
  up --build -d mysql backend

BACKEND_SOCKET_PORT_VALUE="$(grep -E '^BACKEND_SOCKET_PORT=' "$ENV_FILE" | cut -d= -f2- || true)"
JAVABEASTS_LOBBY_PORT="${BACKEND_SOCKET_PORT_VALUE:-7878}"
export JAVABEASTS_LOBBY_HOST="127.0.0.1"
export JAVABEASTS_LOBBY_PORT

echo "Esperando al lobby TCP en ${JAVABEASTS_LOBBY_HOST}:${JAVABEASTS_LOBBY_PORT}..."
for _ in {1..30}; do
  if timeout 1 bash -c "cat < /dev/null > /dev/tcp/${JAVABEASTS_LOBBY_HOST}/${JAVABEASTS_LOBBY_PORT}" >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

if command -v mvn >/dev/null 2>&1; then
  MAVEN_COMMAND=(mvn)
else
  MAVEN_COMMAND=("$ROOT_DIR/JavaBeasts-Backend/mvnw")
fi

echo "Backend y MySQL arrancados en Docker. Lanzando JavaFX local..."
"${MAVEN_COMMAND[@]}" -f "$ROOT_DIR/JavaBeasts-JavaFX/pom.xml" javafx:run
