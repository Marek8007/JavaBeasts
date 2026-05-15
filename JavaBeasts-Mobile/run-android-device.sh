#!/usr/bin/env bash
set -euo pipefail

export JAVA_HOME="${JAVA_HOME_17:-/usr/lib/jvm/java-17-openjdk-amd64}"
export PATH="$JAVA_HOME/bin:$PATH"

if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "No se ha encontrado Java 17 en: $JAVA_HOME"
  echo "Instala JDK 17 o define JAVA_HOME_17 antes de ejecutar este script."
  exit 1
fi

npx expo run:android --device
