# JavaBeasts

JavaBeasts es un juego por turnos 1v1 formado por:

- Backend Java/Spring Boot con API REST y servidor TCP.
- Base de datos MySQL local en Docker o TiDB Cloud.
- Aplicacion JavaFX de escritorio para mostrar sala y combate.
- Aplicacion movil Expo/React Native para los jugadores.

## Puertos

El backend publica dos puertos:

- `9234`: REST HTTP para autenticacion, equipos, catalogo, estadisticas e historial.
- `7878`: TCP socket para sala y combate.

El movil debe estar en la misma red local que el ordenador que ejecuta el backend.

## Requisitos

- Docker y Docker Compose.
- JDK 21 para lanzar JavaFX.
- Node.js y npm para la app movil.
- JDK 17 para compilar la app Android.
- Android SDK y un dispositivo/emulador configurado con ADB.

Comprobaciones utiles:

```bash
docker compose version
java -version
javac -version
node -v
npm -v
adb devices
```

## Variables de entorno

Los archivos reales de entorno no se suben al repositorio. Crea los necesarios desde las plantillas:

```bash
cp deploy/env/cloud.env.example deploy/env/cloud.env
cp deploy/env/local.env.example deploy/env/local.env
```

### Backend con BBDD en la nube

Edita `deploy/env/cloud.env` con los datos reales de TiDB u otra base compatible con MySQL:

```env
DB_HOST=...
DB_PORT=4000
DB_NAME=JavaBeasts
DB_USERNAME=...
DB_PASSWORD=...
DB_URL_OPTIONS=?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3

BACKEND_HTTP_PORT=9234
BACKEND_SOCKET_PORT=7878
```

`DB_URL_OPTIONS` debe ir sin comillas.

### Backend con MySQL local

Edita `deploy/env/local.env` si quieres cambiar usuarios o passwords. Por defecto levanta MySQL en Docker:

```env
MYSQL_DATABASE=JavaBeasts
MYSQL_ROOT_PASSWORD=change_me_root_password
MYSQL_USER=dbuser
MYSQL_PASSWORD=change_me_db_password
MYSQL_PUBLIC_PORT=33006

DB_HOST=mysql
DB_PORT=3306
DB_NAME=JavaBeasts
DB_USERNAME=dbuser
DB_PASSWORD=change_me_db_password
DB_URL_OPTIONS=?sslMode=DISABLED

BACKEND_HTTP_PORT=9234
BACKEND_SOCKET_PORT=7878
```

## Arrancar backend y JavaFX

Desde la raiz del repositorio:

```bash
cd /home/marcos/JavaBeasts
```

### Opcion A: BBDD en la nube

```bash
./deploy/scripts/run-cloud.sh
```

Este script:

- levanta el backend en Docker;
- espera a que el socket TCP del lobby este disponible;
- lanza JavaFX fuera de Docker usando JDK 21;
- conecta JavaFX a `127.0.0.1:7878`.

### Opcion B: MySQL local en Docker

```bash
./deploy/scripts/run-local.sh
```

Este script:

- levanta MySQL y backend en Docker;
- espera a que el socket TCP del lobby este disponible;
- lanza JavaFX fuera de Docker usando JDK 21;
- conecta JavaFX a `127.0.0.1:7878`.

Si JavaFX no arranca porque no encuentra JDK 21, instala JDK 21 o exporta `JAVA_HOME`:

```bash
export JAVA_HOME=/ruta/al/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
./deploy/scripts/run-cloud.sh
```

## Configurar la IP del movil

El movil no debe usar `127.0.0.1`, porque esa direccion seria el propio movil. Debe usar la IP local del ordenador que ejecuta Docker.

Para ver la IP del ordenador servidor:

```bash
ip -4 addr show
```

Busca una IP tipo:

```text
192.168.1.144
```

Edita `JavaBeasts-Mobile/.env`:

```env
EXPO_PUBLIC_API_BASE_URL=http://192.168.1.144:9234
EXPO_PUBLIC_SOCKET_PORT=7878
```

No hace falta definir `EXPO_PUBLIC_SOCKET_HOST`: la app deduce el host TCP desde `EXPO_PUBLIC_API_BASE_URL`.

Si cambia la IP del ordenador servidor, cambia este `.env` y reconstruye o reinicia Metro segun el tipo de build.

Para una demo estable, lo recomendable es reservar una IP fija al ordenador servidor desde el router.

## App movil en desarrollo

En una terminal:

```bash
cd /home/marcos/JavaBeasts/JavaBeasts-Mobile
npx expo start -c --dev-client
```

Para instalar/abrir en un dispositivo fisico por USB:

```bash
cd /home/marcos/JavaBeasts/JavaBeasts-Mobile
./run-android-device.sh
```

Este script usa JDK 17 y ejecuta:

```bash
npx expo run:android --device
```

Si JDK 17 esta en otra ruta:

```bash
JAVA_HOME_17=/ruta/al/jdk-17 ./run-android-device.sh
```

## App movil sin Metro

Para generar una build release que se pueda abrir sin Metro:

```bash
cd /home/marcos/JavaBeasts/JavaBeasts-Mobile
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
PATH="$JAVA_HOME/bin:$PATH" \
npx expo run:android --variant release
```

En una build release, las variables `EXPO_PUBLIC_*` quedan incluidas en la app. Si cambias la IP del backend en `JavaBeasts-Mobile/.env`, debes reconstruir la app.

## Comprobaciones rapidas

Desde el ordenador que ejecuta Docker:

```bash
curl http://127.0.0.1:9234/test/db
printf '{"code":"room_status","data":{}}\n' | nc -w 3 127.0.0.1 7878
```

Desde otro dispositivo de la misma red, sustituyendo la IP:

```bash
curl http://192.168.1.144:9234/test/db
printf '{"code":"room_status","data":{}}\n' | nc -w 3 192.168.1.144 7878
```

En el navegador del movil fisico debe abrir:

```text
http://192.168.1.144:9234/test/db
```

Si el navegador del movil no abre esa URL, el problema es de red, IP o firewall.

## Firewall y red local

El ordenador servidor debe aceptar conexiones entrantes a:

- `9234/tcp`
- `7878/tcp`

En Linux con UFW:

```bash
sudo ufw allow 9234/tcp
sudo ufw allow 7878/tcp
sudo ufw status
```

En Windows, la red debe estar marcada como privada y deben permitirse esos puertos en el firewall.

Todos los dispositivos deben estar en la misma red local. Si el movil esta con datos moviles, VPN o una red Wi-Fi distinta, no podra conectar al backend local.

## Problemas comunes

### El movil muestra `Network Error`

Revisa:

- que `JavaBeasts-Mobile/.env` tenga la IP correcta;
- que el movil abra `/test/db` en el navegador;
- que el backend este publicado en `0.0.0.0:9234`;
- que hayas reiniciado Metro con `npx expo start -c --dev-client`;
- que hayas reinstalado la build si cambiaste configuracion nativa.

### La sala da timeout

Eso afecta al socket TCP. Revisa:

- puerto `7878` abierto;
- `EXPO_PUBLIC_SOCKET_PORT=7878`;
- que no exista `EXPO_PUBLIC_SOCKET_HOST` apuntando a una IP antigua;
- prueba `room_status` con `nc`.

### Login devuelve `409`

Significa conflicto. Normalmente el usuario ya esta marcado como conectado en BBDD. Se puede cerrar sesion o resetear `Is_Logged` en la tabla `Users`.

### JavaFX necesita Java 21, pero Android necesita Java 17

No cambies Java globalmente. Usa `JAVA_HOME` por comando:

```bash
# JavaFX/backend
export JAVA_HOME=/ruta/al/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
./deploy/scripts/run-cloud.sh

# Android
cd JavaBeasts-Mobile
JAVA_HOME_17=/ruta/al/jdk-17 ./run-android-device.sh
```

### Docker pide permisos

En Linux puedes usar Docker sin `sudo` anadiendo el usuario al grupo `docker`:

```bash
sudo usermod -aG docker $USER
```

Despues cierra sesion completamente y vuelve a entrar.

## Orden recomendado para demo

1. Arrancar backend y JavaFX:

```bash
./deploy/scripts/run-cloud.sh
```

2. Comprobar desde el movil:

```text
http://IP_DEL_SERVIDOR:9234/test/db
```

3. Abrir la app movil release o, si estas en desarrollo, arrancar Metro:

```bash
cd JavaBeasts-Mobile
npx expo start -c --dev-client
```

4. Abrir la app en uno o dos dispositivos y conectarse al codigo mostrado por JavaFX.
