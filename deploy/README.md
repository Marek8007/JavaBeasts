# Despliegue con Docker

JavaBeasts puede arrancarse con dos configuraciones:

- `local`: MySQL y backend dentro de Docker; JavaFX se lanza localmente desde el script.
- `cloud`: backend dentro de Docker, conectando a una base de datos externa compatible con MySQL, como TiDB Cloud; JavaFX se lanza localmente desde el script.

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

El script levanta MySQL y backend en Docker, espera al puerto TCP del lobby y abre la aplicación JavaFX fuera de Docker.

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

El script levanta el backend en Docker, espera al puerto TCP del lobby y abre la aplicación JavaFX fuera de Docker.

Importante: en los archivos `.env`, `DB_URL_OPTIONS` debe escribirse sin comillas:

```env
DB_URL_OPTIONS=?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3
```

## Comprobaciones rápidas

```bash
curl http://localhost:9234/test/db
printf '{"code":"room_status","data":{}}\n' | nc 127.0.0.1 7878
```

## Nota sobre JavaFX

JavaFX se ejecuta fuera de Docker para evitar dependencias del servidor gráfico del sistema anfitrión. El script lo lanza con Maven y le pasa estas variables:

- `JAVABEASTS_LOBBY_HOST=127.0.0.1`
- `JAVABEASTS_LOBBY_PORT`, tomado del `.env` de despliegue

El equipo anfitrión debe tener JDK 21 disponible. Si hay varias versiones instaladas, se puede indicar antes de arrancar:

```bash
export JAVA_HOME=/ruta/al/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
```

Para una distribución real de escritorio, una alternativa más cómoda sería empaquetar JavaFX con `jpackage`.
