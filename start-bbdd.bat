@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "COMPOSE_DIR=%SCRIPT_DIR%JavaBeasts-BBD"
set "COMPOSE_FILE=%COMPOSE_DIR%\docker-compose.yml"

docker compose version >nul 2>&1
if %errorlevel%==0 (
  docker compose --project-directory "%COMPOSE_DIR%" -f "%COMPOSE_FILE%" up -d
  if errorlevel 1 exit /b %errorlevel%
  echo Contenedor de BBDD arrancado.
  exit /b 0
)

docker-compose version >nul 2>&1
if %errorlevel%==0 (
  docker-compose --project-directory "%COMPOSE_DIR%" -f "%COMPOSE_FILE%" up -d
  if errorlevel 1 exit /b %errorlevel%
  echo Contenedor de BBDD arrancado.
  exit /b 0
)

echo Error: Docker Compose no esta instalado o no esta en PATH.
exit /b 1
