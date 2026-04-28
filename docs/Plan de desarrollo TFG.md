# Plan de desarrollo TFG - JavaBeasts

Fecha de inicio del plan: 28 de abril de 2026
Fecha limite interna: 14 de mayo de 2026
Fecha de entrega oficial: 15 de mayo de 2026

## Objetivo de este plan

Este plan esta pensado para llevar `JavaBeasts` a una version entregable y defendible priorizando por encima de todo el cumplimiento de los resultados de aprendizaje definidos en la propuesta del TFG.

La prioridad no es construir la version mas ambiciosa del juego, sino una version completa, coherente y demostrable que incluya:

- base de datos relacional funcional con datos de prueba
- backend REST en Spring Boot
- comunicacion en tiempo real por sockets para la partida
- aplicacion movil funcional para login, equipos y control de partida
- pantalla de escritorio en JavaFX para mostrar la partida
- pruebas y documentacion suficientes para sostener la entrega
- evidencias para nube y sostenibilidad

## Criterios de alcance

### Imprescindible para entregar

- Registro y login
- Gestion de varios equipos por usuario
- Seleccion de equipo activo antes de ponerse listo
- Sala unica con codigo numerico corto de 6 digitos
- Conexion de 2 jugadores
- Estado listo de ambos jugadores
- Combate 1v1 por equipos de 4 JaBeas
- Acciones de turno: atacar, cambiar, rendirse
- Precision en los movimientos
- Cambio de JaBea como accion de turno
- Cambio manual cuando un JaBea cae debilitado
- Historial de partidas segun la BBDD actual
- Pantalla JavaFX conectada y mostrando el estado de la partida
- Configuracion por variables de entorno para usar MySQL local o TiDB
- Evidencias documentales de nube y sostenibilidad

### Secundario o reducible si falta tiempo

- Efectos complejos de movimientos
- Pulido visual
- Animaciones
- Feedback audiovisual
- Reglas avanzadas adicionales de combate

## Arquitectura objetivo resumida

- `JavaBeasts-BBDD`: MySQL en Docker, scripts SQL, datos semilla
- `JavaBeasts-Backend`: Spring Boot, REST, acceso a datos, autenticacion, logica de negocio
- `Servidor de sockets`: puede vivir dentro del ecosistema Java del proyecto, pero con responsabilidad clara de partida en tiempo real
- `JavaFX`: pantalla principal automatica de partida
- `JavaBeasts-Mobile`: cliente movil para login, equipos, ready y acciones de combate

## Estrategia general

1. Cerrar primero la base tecnica y la configuracion para no perder tiempo despues.
2. Asegurar muy pronto un flujo minimo extremo a extremo.
3. Añadir luego reglas de combate y gestion de equipos.
4. Integrar JavaFX cuando el backend y sockets ya tengan un estado estable.
5. Reservar los ultimos dias para pruebas, documentacion, correcciones y preparacion de entrega.

## Plan diario

### Martes 28 de abril de 2026

Objetivo: dejar fijado el alcance y ordenar el proyecto para trabajar sin ambiguedades.

- Crear este plan de desarrollo
- Revisar y limpiar contradicciones entre documentacion y codigo
- Definir formalmente los modulos del proyecto
- Decidir estructura de configuracion con variables de entorno
- Crear backlog minimo de funcionalidades obligatorias

Entregable del dia:
- plan de desarrollo aprobado
- lista clara de modulos y responsabilidades

### Miercoles 29 de abril de 2026

Objetivo: dejar saneada la base de datos y su configuracion.

- Revisar esquema SQL completo
- Confirmar restricciones de equipos, slots y relaciones
- Ajustar semillas de datos si hace falta
- Preparar `.env.example` para base de datos local y nube
- Revisar `.gitignore` para no versionar secretos reales

Entregable del dia:
- BBDD estable
- configuracion reproducible
- secretos fuera del codigo

### Jueves 30 de abril de 2026

Objetivo: dejar operativo el backend base.

- Corregir configuracion de `application.properties`
- Preparar perfiles o estrategia limpia para local y TiDB
- Revisar entidades JPA y repositorios
- Crear estructura de paquetes mas clara si hace falta
- Validar que el backend arranca contra base de datos real

Entregable del dia:
- backend arrancando con configuracion por entorno
- entidades y persistencia listas para trabajar

### Viernes 1 de mayo de 2026

Objetivo: cerrar autenticacion y usuarios.

- Implementar registro
- Implementar login
- Definir almacenamiento seguro de contraseñas
- Validaciones basicas de entrada
- Preparar respuestas REST limpias para cliente movil

Entregable del dia:
- flujo funcional de registro y login

### Sabado 2 de mayo de 2026

Objetivo: cerrar gestion de equipos por REST.

- CRUD de equipos
- Limite de hasta 10 equipos por usuario
- Asociacion equipo-usuario
- Marcar equipo activo
- Validar que cada equipo tiene 4 slots y sin JaBeas repetidos
- Validar movimientos permitidos por tipo

Entregable del dia:
- API de gestion de equipos funcional

### Domingo 3 de mayo de 2026

Objetivo: dejar preparado el dominio de combate.

- Modelar sala, jugador en sala y estado de ready
- Modelar partida, turnos y estado del combate
- Modelar acciones de combate
- Decidir estructura de mensajes de sockets
- Cerrar reglas minimas de dano, precision, cambio y rendicion

Entregable del dia:
- modelo de dominio de partida definido
- contrato inicial de mensajes por socket

### Lunes 4 de mayo de 2026

Objetivo: implementar gestion de sala por sockets.

- Crear sala unica al iniciar la aplicacion de escritorio o el servicio asociado
- Generar codigo numerico de 6 digitos
- Permitir union de hasta 2 jugadores
- Gestionar reconexion o rechazo basico si la sala esta llena
- Sincronizar estado de sala hacia clientes

Entregable del dia:
- sala funcional con 2 jugadores maximo

### Martes 5 de mayo de 2026

Objetivo: implementar flujo prepartida completo.

- Entrada del jugador en sala
- Login desde movil dentro del flujo
- Consulta de equipos del jugador
- Seleccion de equipo activo
- Estado listo
- Habilitar inicio cuando haya 2 jugadores y ambos listos

Entregable del dia:
- flujo completo antes del combate

### Miercoles 6 de mayo de 2026

Objetivo: implementar combate minimo jugable.

- Inicio de partida con equipos activos
- Turnos simultaneos
- Accion atacar
- Accion cambiar
- Accion rendirse
- Empates de velocidad aleatorios
- Cambio obligatorio si un JaBea cae

Entregable del dia:
- combate minimo funcional de extremo a extremo

### Jueves 7 de mayo de 2026

Objetivo: ampliar reglas de combate y cerrar historial.

- Aplicar precision
- Aplicar tabla de tipos
- Implementar curacion y efectos simples aprobados
- Guardar ganador, perdedor y numero de turnos
- Guardar JaBeas usados en historial segun BBDD

Entregable del dia:
- combate ya defendible
- persistencia del resultado de la partida

### Viernes 8 de mayo de 2026

Objetivo: hacer util la app movil.

- Sustituir pantallas de ejemplo de Expo por pantallas reales
- Pantalla de acceso a sala
- Pantalla de login y registro
- Pantalla de lista de equipos
- Pantalla para marcar equipo activo
- Pantalla de ready y espera
- Pantalla de acciones de combate

Entregable del dia:
- movil navegable con flujo real del proyecto

### Sabado 9 de mayo de 2026

Objetivo: integrar la app movil con backend y sockets.

- Conectar REST real desde movil
- Conectar socket real desde movil
- Gestionar estados de carga y error minimos
- Validar flujo completo con dos clientes
- Corregir fallos de sincronizacion

Entregable del dia:
- dos moviles jugando contra el backend

### Domingo 10 de mayo de 2026

Objetivo: construir la pantalla JavaFX minima.

- Crear modulo JavaFX separado o claramente aislado
- Conectar JavaFX al estado de sala y partida
- Mostrar jugadores, JaBeas activos, vida y acciones
- Mostrar progreso del combate de forma comprensible
- Mostrar final de partida

Entregable del dia:
- pantalla principal de escritorio funcionando

### Lunes 11 de mayo de 2026

Objetivo: integrar todo el sistema y probarlo como producto unico.

- Arranque de BBDD
- Arranque de backend
- Arranque de JavaFX
- Conexion de dos moviles
- Partida completa con historial
- Revancha manteniendo sala abierta

Entregable del dia:
- flujo completo del TFG funcionando de punta a punta

### Martes 12 de mayo de 2026

Objetivo: cubrir calidad, pruebas y sostenibilidad.

- Añadir pruebas unitarias de servicios clave
- Añadir pruebas de componentes criticos del backend
- Revisar consultas y eliminar redundancias
- Documentar medidas de eficiencia aplicadas
- Revisar logs, errores y manejo basico de incidencias

Entregable del dia:
- evidencias tecnicas para tests y sostenibilidad

### Miercoles 13 de mayo de 2026

Objetivo: cerrar documentacion academica y despliegue.

- Actualizar memoria local con el estado real del proyecto
- Redactar evidencia de nube con propuesta de arquitectura
- Preparar estimacion de costes y justificacion tecnica
- Documentar pasos de ejecucion local
- Documentar variables de entorno y modos de BBDD
- Revisar capturas, diagramas y evidencias

Entregable del dia:
- documentacion de entrega casi cerrada

### Jueves 14 de mayo de 2026

Objetivo: dia de colchon, validacion final y empaquetado.

- Prueba completa final en entorno limpio
- Corregir errores de ultima hora
- Revisar que todos los RAs comprometidos tienen evidencia visible
- Revisar repo, nombres de archivos, configuracion y secretos
- Preparar version final entregable
- Preparar guion de demostracion

Entregable del dia:
- TFG listo para entregar sin depender del dia 15

## RAs y su aterrizaje practico

### Evidencia 1 - Base de datos relacional

Se cubre con:

- esquema SQL
- datos semilla
- entidades JPA
- CRUD real sobre usuarios, equipos, movimientos e historial

### Evidencia 2 - Comunicacion en red con sockets

Se cubre con:

- sala
- sincronizacion de jugadores
- ready
- envio de acciones
- resolucion de turnos
- actualizacion de estado en tiempo real

### Evidencia 3 - Servicio de red con Spring Boot

Se cubre con:

- registro
- login
- gestion de equipos
- consulta de datos del juego
- historial de partidas

### Evidencia 4 - Aplicacion movil

Se cubre con:

- acceso a sala
- login y registro
- gestion de equipos
- seleccion de equipo activo
- ready
- acciones durante combate

### Evidencia 5 - Nube publica

Se cubre con:

- documento tecnico de despliegue en nube
- arquitectura propuesta
- servicios equivalentes
- estimacion de costes

### Evidencia 6 - Sostenibilidad

Se cubre con:

- optimizacion de consultas
- estructuras de datos adecuadas
- reduccion de procesamiento redundante
- explicacion en memoria del impacto tecnico

## Riesgos y mitigaciones

### Riesgo 1: JavaFX llega tarde

Mitigacion:
- hacer una primera version minima el 10 de mayo
- priorizar estado visible sobre acabado visual

### Riesgo 2: sockets y combate consumen demasiado tiempo

Mitigacion:
- reducir efectos complejos
- cerrar reglas minimas antes de implementar extras

### Riesgo 3: movil tarda demasiado en interfaz

Mitigacion:
- usar pantallas simples y funcionales
- evitar pulido visual innecesario

### Riesgo 4: problemas entre MySQL local y TiDB

Mitigacion:
- unificar configuracion por variables de entorno
- probar ambos modos pronto, no al final

### Riesgo 5: documentacion se deja para el final

Mitigacion:
- actualizar memoria y evidencias a partir del 12 y 13 de mayo

## Regla de decision durante el desarrollo

Si aparece una duda entre hacer algo mas vistoso o hacer algo que garantice una evidencia de RA, siempre se prioriza la evidencia del RA.

Si aparece una duda entre hacer una regla compleja del juego o cerrar un flujo completo extremo a extremo, siempre se prioriza cerrar el flujo completo.

## Checklist de validacion final

- El backend arranca sin secretos hardcodeados
- La base de datos puede usarse en local y en nube
- Un usuario puede registrarse
- Un usuario puede hacer login
- Un usuario puede crear y gestionar equipos
- Dos jugadores pueden unirse a la sala
- Ambos pueden ponerse listos
- La partida empieza correctamente
- Se puede atacar
- Se puede cambiar de JaBea
- Se puede rendir
- Se calcula el turno
- Se aplica precision
- Se guarda historial
- JavaFX muestra la partida
- La app movil controla la partida
- Hay pruebas de backend
- La memoria refleja el proyecto real
- La evidencia de nube esta escrita
- La evidencia de sostenibilidad esta escrita

## Nota final

Este plan es exigente, pero realista si durante estos dias se trabaja con una prioridad muy clara: primero que funcione, despues que quede bonito, y solo al final cualquier complejidad extra del juego.
