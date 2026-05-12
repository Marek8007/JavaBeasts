@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "COMPOSE_DIR=%SCRIPT_DIR%JavaBeasts-BBDD"
set "COMPOSE_FILE=%COMPOSE_DIR%\docker-compose.yml"
set "ENV_FILE=%COMPOSE_DIR%\.env"

if not exist "%ENV_FILE%" (
  echo Error: no se ha encontrado %ENV_FILE%.
  exit /b 1
)

set "CONTAINER_NAME="
for /f "usebackq tokens=1,* delims==" %%A in ("%ENV_FILE%") do (
  if /I "%%A"=="MYSQL_HOST" set "CONTAINER_NAME=%%B"
)

if not defined CONTAINER_NAME (
  echo Error: MYSQL_HOST no esta definido en %ENV_FILE%.
  exit /b 1
)

docker container inspect "%CONTAINER_NAME%" >nul 2>&1
if %errorlevel%==0 (
  for /f %%A in ('docker inspect -f "{{.State.Running}}" "%CONTAINER_NAME%"') do set "CONTAINER_RUNNING=%%A"
  if /I "%CONTAINER_RUNNING%"=="true" (
    echo El contenedor de BBDD ya esta arrancado.
    exit /b 0
  )
  docker start "%CONTAINER_NAME%" >nul
  if errorlevel 1 exit /b %errorlevel%
  echo Contenedor de BBDD arrancado.
  exit /b 0
)

docker compose version >nul 2>&1
if %errorlevel%==0 (
  docker compose --project-directory "%COMPOSE_DIR%" -f "%COMPOSE_FILE%" up -d --remove-orphans
  if errorlevel 1 exit /b %errorlevel%
  echo Contenedor de BBDD creado y arrancado.
  exit /b 0
)

docker-compose version >nul 2>&1
if %errorlevel%==0 (
  docker-compose --project-directory "%COMPOSE_DIR%" -f "%COMPOSE_FILE%" up -d --remove-orphans
  if errorlevel 1 exit /b %errorlevel%
  echo Contenedor de BBDD creado y arrancado.
  exit /b 0
)

echo Error: Docker Compose no esta instalado o no esta en PATH.
exit /b 1
