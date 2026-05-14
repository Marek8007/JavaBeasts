# Despliegue con Docker

JavaBeasts puede arrancarse con dos configuraciones:

- `local`: MySQL, backend y JavaFX dentro de Docker.
- `cloud`: backend y JavaFX dentro de Docker, conectando a una base de datos externa compatible con MySQL, como TiDB Cloud.

## Preparar variables

Copiar uno de los ejemplos y rellenar contraseñas reales:

```bash
cp deploy/env/local.env.example deploy/env/local.env
cp deploy/env/cloud.env.example deploy/env/cloud.env
```

Los archivos `local.env` y `cloud.env` no deben subirse al repositorio.

## Arranque local

Crear el archivo real de entorno:

```bash
cp deploy/env/local.env.example deploy/env/local.env
```

Arrancar MySQL, backend y JavaFX:

```bash
./deploy/scripts/run-local.sh
```

El backend publica:

- HTTP: `9234`
- TCP: `7878`

## Arranque con base de datos en la nube

Crear el archivo real de entorno:

```bash
cp deploy/env/cloud.env.example deploy/env/cloud.env
```

Arrancar backend y JavaFX conectando a TiDB Cloud:

```bash
./deploy/scripts/run-cloud.sh
```

En este modo no se levanta MySQL local. El backend conecta con la BBDD indicada por `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` y `DB_URL_OPTIONS`.

## Comprobaciones rápidas

```bash
curl http://localhost:9234/test/db
printf '{"code":"room_status","data":{}}\n' | nc 127.0.0.1 7878
```

## Nota sobre JavaFX

JavaFX es una aplicación gráfica. En Docker necesita acceso al servidor gráfico del host mediante `DISPLAY` y el volumen `/tmp/.X11-unix`. Para una distribución real de escritorio, una alternativa más cómoda sería empaquetar JavaFX con `jpackage`.
