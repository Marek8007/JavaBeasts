# Especificacion base del proyecto - JavaBeasts

Fecha: 28 de abril de 2026

## Proposito del documento

Este documento fija la version canon del proyecto `JavaBeasts` para evitar contradicciones entre la documentacion previa, el codigo actual y las decisiones tomadas durante la planificacion del TFG.

Su objetivo es dejar cerrados:

- el alcance funcional real
- la arquitectura del sistema
- las decisiones de producto ya confirmadas
- la estrategia de configuracion
- el backlog minimo imprescindible para entregar

## Vision del proyecto

`JavaBeasts` es un juego multijugador por turnos `1v1` en el que dos jugadores se conectan desde sus dispositivos moviles a una sala iniciada desde una aplicacion de escritorio en `JavaFX`.

La aplicacion de escritorio no actua como cliente jugable, sino como pantalla principal automatica de la partida. Los jugadores interactuan desde el movil, tanto fuera de combate como durante la partida.

La prioridad principal del TFG no es la complejidad del juego, sino construir una version completa, funcional y defendible que cubra todos los resultados de aprendizaje definidos en la propuesta.

## Alcance funcional canon

### Flujo general

1. La aplicacion de escritorio se inicia.
2. Se crea una sala virtual unica.
3. La sala dispone de un codigo numerico corto de 6 digitos.
4. Dos jugadores pueden unirse desde movil.
5. Una vez conectados, pueden registrarse o iniciar sesion.
6. Cada jugador puede gestionar sus equipos.
7. Antes de empezar, cada jugador selecciona un equipo activo.
8. Cuando hay 2 jugadores, se habilita el inicio de partida.
9. Ambos jugadores deben marcarse como `listo`.
10. Comienza el combate.
11. Al terminar la partida, la sala sigue abierta y ambos jugadores permanecen conectados.

### Acciones permitidas en combate

- Atacar
- Cambiar de JaBea
- Rendirse

### Reglas de combate confirmadas

- Combate `1v1`
- Cada jugador usa un equipo de `4` JaBeas
- Un usuario puede tener varios equipos
- Se fija un limite razonable de `10` equipos por usuario
- No se permiten JaBeas repetidos dentro de un mismo equipo
- Cada JaBea tiene:
  - 1 movimiento unico fijo
  - 2 movimientos configurables antes de entrar en combate
- Los 2 movimientos configurables deben respetar la regla de tipos ya definida
- La precision de los movimientos se implementa
- Si un jugador cambia de JaBea, el cambio consume su accion de turno
- Si un jugador cambia y el rival ataca, el nuevo JaBea recibe el ataque
- Si ambos jugadores cambian, ambos cambios se ejecutan y el turno termina
- Si un JaBea cae, el jugador elige manualmente el siguiente
- Los empates de velocidad se resuelven aleatoriamente
- Rendirse cuenta como derrota inmediata y victoria para el rival
- Los efectos complejos de movimientos no son prioridad en el MVP
- Los efectos simples, como curacion, si deben implementarse

## Tabla de tipos canon

- `Fuego` hace mas dano a `Planta`
- `Planta` hace mas dano a `Electrico`
- `Electrico` hace mas dano a `Agua`
- `Agua` hace mas dano a `Fuego`
- La relacion inversa reduce el dano
- `Normal` o `Neutro` no tiene interacciones especiales

## Arquitectura canon

El proyecto queda dividido conceptualmente en 4 bloques:

### 1. Base de datos

Ubicacion actual:

- `JavaBeasts-BBDD`

Responsabilidad:

- esquema SQL
- semillas de datos
- modo local con Docker y MySQL

### 2. Backend REST

Ubicacion actual:

- `JavaBeasts-Backend`

Responsabilidad:

- autenticacion
- gestion de usuarios
- gestion de equipos
- consulta de datos del juego
- historial de partidas
- validaciones de negocio fuera de partida

### 3. Servidor de partida en tiempo real

Ubicacion prevista:

- ecosistema Java del proyecto, separado por responsabilidad del REST

Responsabilidad:

- gestion de sala
- conexion de jugadores
- ready check
- flujo de prepartida
- acciones de combate
- sincronizacion del estado en tiempo real

### 4. Aplicacion de escritorio JavaFX

Ubicacion prevista:

- modulo propio dentro del monorepo

Responsabilidad:

- iniciar la sala
- mostrar estado de sala
- mostrar estado global del combate
- reflejar turnos, vida, cambios y final de partida

### Cliente movil

Ubicacion actual:

- `JavaBeasts-Mobile`

Responsabilidad:

- union a sala
- login y registro
- gestion de equipos
- seleccion de equipo activo
- estado listo
- acciones de combate

## Decisiones que quedan oficialmente descartadas

Estas ideas quedan fuera de la version actual del proyecto:

- PHP y Symfony como modulo de autenticacion
- cliente de escritorio mezclado dentro del backend sin separacion de responsabilidad
- enfoque centrado en pulido visual por encima de evidencias academicas
- dependencia exclusiva de una sola base de datos sin posibilidad de alternar entorno local y nube

## Estrategia de configuracion canon

La configuracion del proyecto debe dejar de depender de secretos escritos directamente en el codigo.

### Objetivo

Soportar dos modos principales:

- modo local con MySQL en Docker
- modo nube con TiDB

### Enfoque recomendado

- usar variables de entorno para todos los datos sensibles y de conexion
- mantener un archivo de ejemplo versionable como `.env.example`
- permitir alternar entre local y nube cambiando variables, sin modificar el codigo fuente
- cargar las variables del backend desde el IDE, la shell o la configuracion de ejecucion correspondiente

### Variables esperadas

Minimo:

- host
- puerto
- nombre de base de datos
- usuario
- contraseña
- modo o proveedor de base de datos

Archivos de ejemplo actuales:

- `JavaBeasts-BBDD/.env.example`
- `JavaBeasts-Backend/.env.example`

### Decision practica

No hace falta una arquitectura compleja de perfiles si con variables de entorno bien definidas se puede alternar de forma limpia entre local y nube.

## Contradicciones previas resueltas

### JavaFX

Antes:
- aparecia en la documentacion, pero no existia aun en el repo

Ahora:
- sigue siendo parte obligatoria del proyecto
- se desarrollara como modulo separado y no como parte informal del backend

### Autenticacion

Antes:
- un documento mencionaba PHP + Symfony

Ahora:
- toda la autenticacion ira en Spring Boot

### Base de datos

Antes:
- coexistian MySQL local y TiDB sin estrategia clara

Ahora:
- ambos son modos validos de ejecucion
- la alternancia se hara por variables de entorno

### Sala y codigo

Antes:
- no estaba claro si existian varias salas o una sola

Ahora:
- la implementacion real gira en torno a una sala unica
- el codigo numerico de 6 digitos se mantiene como parte del flujo de uso

## Backlog minimo imprescindible

### Bloque 1 - Configuracion y base tecnica

- sacar secretos de `application.properties`
- crear `.env.example`
- revisar `.gitignore`
- validar arranque con MySQL local
- validar arranque con TiDB

### Bloque 2 - Base de datos y persistencia

- validar modelo relacional
- ajustar restricciones de equipos
- revisar y completar semillas
- alinear entidades JPA con el esquema final

### Bloque 3 - Backend REST

- registro
- login
- CRUD de equipos
- equipo activo
- consulta de JaBeas, movimientos y tipos
- historial de partidas

### Bloque 4 - Tiempo real

- creacion de sala
- union de hasta 2 jugadores
- codigo corto de sala
- ready check
- inicio de partida
- sincronizacion de acciones
- resolucion de turnos

### Bloque 5 - Combate

- cargar equipos activos
- seleccionar JaBea inicial
- atacar
- cambiar
- rendirse
- precision
- daño base
- efectividad de tipos
- curacion simple
- seleccion de reemplazo al caer un JaBea
- fin de partida

### Bloque 6 - Cliente movil

- acceso a sala
- registro
- login
- pantalla de equipos
- seleccion de equipo activo
- pantalla de espera y listo
- pantalla de combate

### Bloque 7 - JavaFX

- vista de sala
- vista de prepartida
- vista de combate
- vista de resultado

### Bloque 8 - Evidencias academicas

- pruebas backend
- documento de nube
- evidencia de sostenibilidad
- memoria actualizada
- instrucciones de ejecucion

## Criterio de prioridad

Cuando haya conflicto entre dos tareas, se sigue este orden:

1. Evidencias de RA
2. Flujo funcional completo
3. Estabilidad tecnica
4. UX
5. Pulido visual
6. Complejidad extra del juego

## Resultado esperado al terminar el TFG

Al cerrar el proyecto debe existir una demostracion completa y entendible en la que:

- se inicia la aplicacion de escritorio
- dos jugadores se conectan desde movil
- pueden registrarse o iniciar sesion
- pueden gestionar equipos
- seleccionan un equipo activo
- se ponen listos
- juegan una partida por turnos
- el escritorio refleja la partida
- el resultado queda persistido

Ese es el producto minimo defendible y alineado con la propuesta.
