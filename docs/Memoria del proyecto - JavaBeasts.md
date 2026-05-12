# Memoria del proyecto - JavaBeasts

Autor: Marcos Miquel Lisarde  
Tutor: Daniel Sebastia Perez  
Ciclo: Desarrollo de Aplicaciones Multiplataforma  
Centro: IES Salvador Gadea  
Fecha: mayo de 2026

## Resumen

JavaBeasts es un proyecto de desarrollo de un juego multijugador por turnos inspirado en juegos de criaturas por equipos. El sistema propone una arquitectura cliente-servidor en la que dos jugadores se conectan desde sus dispositivos moviles a una sesion de juego iniciada desde una aplicacion de escritorio. La aplicacion de escritorio muestra el estado global de la partida, mientras que los usuarios interactuan desde el movil para registrarse, gestionar sus equipos y seleccionar sus acciones durante el combate.

El proyecto integra distintas areas tecnicas del ciclo de DAM: persistencia en base de datos relacional, desarrollo de servicios en red con Spring Boot, comunicacion en tiempo real mediante sockets, desarrollo movil y documentacion de aspectos complementarios como computacion en la nube y sostenibilidad.

Palabras clave: Java, Spring Boot, React Native, JavaFX, TiDB, MySQL, sockets, videojuego, multijugador, DAM.

## Abstract

JavaBeasts is a turn-based multiplayer game project inspired by creature battle games. The system follows a client-server architecture in which two players connect from their mobile devices to a game session started from a desktop application. The desktop application displays the global state of the match, while players interact from their mobile devices to register, manage their teams and choose their actions during battle.

The project integrates several technical areas from the DAM curriculum: relational database persistence, network services with Spring Boot, real-time communication through sockets, mobile development, and complementary documentation on cloud computing and sustainability.

Keywords: Java, Spring Boot, React Native, JavaFX, TiDB, MySQL, sockets, game, multiplayer, DAM.

## Indice propuesto

1. Introduccion y objetivos  
2. Descripcion general del proyecto  
3. Tecnologias utilizadas  
4. Planificacion del desarrollo  
5. Analisis del sistema  
6. Diseno del sistema  
7. Implementacion  
8. Base de datos  
9. Pruebas y validacion  
10. Despliegue y ejecucion  
11. Sostenibilidad y nube  
12. Conclusiones y trabajo futuro  
13. Bibliografia y recursos  
14. Anexos

## 1. Introduccion y objetivos

### 1.1. Contexto

El proyecto JavaBeasts surge como una propuesta de aplicacion ludica que permite aplicar de forma integrada los conocimientos adquiridos a lo largo del ciclo de Desarrollo de Aplicaciones Multiplataforma. En lugar de limitarse a una aplicacion CRUD tradicional, el proyecto plantea un sistema con varios clientes, persistencia, logica de negocio, interfaces diferenciadas y sincronizacion en tiempo real.

La idea principal es construir un juego por turnos para dos jugadores, con una pantalla principal de escritorio y dos clientes moviles. Esta estructura permite demostrar de forma clara el trabajo realizado en backend, persistencia, interfaz, conectividad y logica de aplicacion.

### 1.2. Motivacion

La motivacion principal del proyecto es combinar el interes personal por el desarrollo de videojuegos con el uso de tecnologias del ecosistema Java, especialmente Spring Boot, junto con herramientas multiplataforma para cliente movil y persistencia.

Ademas de la motivacion personal, el proyecto se ha planteado con un criterio academico claro: priorizar la cobertura de los resultados de aprendizaje definidos en la propuesta sobre el refinamiento estetico o la complejidad jugable no esencial.

### 1.3. Objetivos generales

- Desarrollar un sistema cliente-servidor funcional para partidas multijugador por turnos.
- Permitir el registro y autenticacion de usuarios.
- Permitir la gestion de equipos de criaturas por parte de cada jugador.
- Implementar la persistencia del sistema en una base de datos relacional.
- Implementar una API REST para la parte de gestion fuera de partida.
- Implementar comunicacion en tiempo real para el flujo de partida.
- Desarrollar una aplicacion movil funcional como mando del jugador.
- Desarrollar una interfaz de escritorio en JavaFX como pantalla principal del juego.

### 1.4. Objetivos especificos

- Modelar las entidades principales del dominio: usuarios, equipos, criaturas, movimientos, tipos e historial.
- Configurar el backend para funcionar tanto con base local MySQL como con base remota TiDB.
- Implementar una sala unica a la que se conectan dos jugadores mediante codigo.
- Implementar flujo de prepartida: entrada en sala, login, seleccion de equipo activo y estado listo.
- Implementar combate 1v1 por equipos de 4 JaBeas.
- Guardar el resultado de las partidas y los equipos utilizados.

## 2. Descripcion general del proyecto

### 2.1. Idea principal

JavaBeasts es un juego multijugador por turnos en el que dos jugadores participan desde dispositivos moviles, mientras una aplicacion de escritorio representa el estado de la partida. Cada jugador dispone de equipos configurables formados por criaturas llamadas JaBeas.

### 2.2. Flujo general de uso

1. Se inicia la aplicacion de escritorio.
2. Se genera una sala virtual con un codigo corto.
3. Dos jugadores se unen desde sus moviles.
4. Cada jugador se registra o inicia sesion.
5. Cada jugador consulta sus equipos y marca uno como activo.
6. Ambos jugadores se marcan como listos.
7. Se inicia la partida.
8. Durante la partida, cada jugador puede atacar, cambiar de JaBea o rendirse.
9. El backend resuelve el turno y actualiza el estado global.
10. La partida termina y el resultado queda registrado.

### 2.3. Alcance funcional actual

El alcance del proyecto se ha definido buscando el equilibrio entre funcionalidad real y viabilidad academica. La version objetivo incluye:

- autenticacion basica
- gestion de varios equipos por usuario
- seleccion de equipo activo
- conexion de dos jugadores a una sala
- combate simplificado por equipos
- persistencia del historial

No se consideran prioritarios en la primera entrega aspectos como animaciones, efectos visuales complejos o todos los efectos avanzados de movimientos.

## 3. Tecnologias utilizadas

### 3.1. Backend

- **Java 21**: lenguaje principal del backend.
- **Spring Boot**: framework para el desarrollo de la API REST y configuracion del backend.
- **Spring Data JPA / Hibernate**: persistencia y mapeo objeto-relacional.
- **MySQL Connector/J**: conexion JDBC.
- **Sockets en Java**: base prevista para la comunicacion en tiempo real durante la partida.

### 3.2. Persistencia

- **TiDB Cloud**: base de datos remota utilizada actualmente durante el desarrollo.
- **MySQL + Docker**: alternativa local prevista para pruebas y despliegue en entorno controlado.

### 3.3. Cliente movil

- **React Native / Expo**
- **TypeScript**

### 3.4. Aplicacion de escritorio

- **JavaFX**

### 3.5. Herramientas de desarrollo

- **IntelliJ IDEA**
- **Visual Studio Code**
- **Git y GitHub**
- **Postman**
- **Trello**
- **Docker**

## 4. Planificacion del desarrollo

El proyecto se ha organizado con una planificacion diaria desde el 28 de abril de 2026 hasta el 14 de mayo de 2026, dejando el 15 de mayo como fecha de entrega oficial y el 14 como limite interno.

La estrategia de desarrollo se basa en:

1. estabilizar configuracion y persistencia
2. asegurar un flujo extremo a extremo minimo
3. desarrollar autenticacion y gestion de equipos
4. implementar la comunicacion de partida
5. integrar clientes movil y escritorio
6. cerrar pruebas, memoria y evidencias

### 4.1. Estado actual del proyecto

A fecha de redaccion de este borrador:

- se ha saneado la configuracion del backend
- se han eliminado secretos hardcodeados del codigo
- se ha configurado el backend mediante variables de entorno
- se ha validado la conexion a TiDB
- se ha preparado un endpoint de prueba de conexion a base de datos
- se han actualizado las tablas `Users` y `Teams` con nuevos campos de soporte funcional

## 5. Analisis del sistema

### 5.1. Actores

- **Jugador**: usuario que se conecta desde el movil, gestiona equipos y participa en la partida.
- **Sistema de escritorio**: representa el estado global de la partida.
- **Backend**: coordina datos, validaciones, autenticacion y resolucion de la partida.

### 5.2. Requisitos funcionales principales

- RF1. El sistema debe permitir el registro de usuarios.
- RF2. El sistema debe permitir el inicio de sesion.
- RF3. El sistema debe permitir la gestion de equipos.
- RF4. El sistema debe permitir marcar un equipo como activo.
- RF5. El sistema debe permitir que dos jugadores se unan a una sala.
- RF6. El sistema debe permitir marcar el estado listo antes de comenzar.
- RF7. El sistema debe permitir iniciar una partida.
- RF8. El sistema debe permitir atacar, cambiar de JaBea y rendirse.
- RF9. El sistema debe resolver el orden de turno y actualizar el combate.
- RF10. El sistema debe guardar el resultado de la partida y los datos necesarios del historial.

### 5.3. Requisitos no funcionales principales

- RNF1. La aplicacion debe ser entendible aunque su interfaz sea simple.
- RNF2. La configuracion del backend no debe depender de credenciales incrustadas en el codigo.
- RNF3. La aplicacion debe poder ejecutarse sobre una base remota y, de forma alternativa, sobre una base local.
- RNF4. La solucion debe estar suficientemente modularizada para separar persistencia, negocio y presentacion.

## 6. Diseno del sistema

### 6.1. Arquitectura general

La arquitectura de JavaBeasts sigue un modelo cliente-servidor dividido en varios bloques:

- **Aplicacion movil**: entrada del usuario, login, gestion de equipos y acciones de combate.
- **Backend REST**: operaciones fuera de partida.
- **Servidor de sockets**: coordinacion en tiempo real durante la partida.
- **Aplicacion JavaFX**: pantalla principal del juego.
- **Base de datos**: persistencia de usuarios, equipos, catalogo del juego e historial.

### 6.2. Arquitectura por modulos

#### Base de datos

- estructura SQL
- datos semilla
- soporte a entorno local y remoto

#### Backend

- entidades
- repositorios
- servicios
- controladores REST
- logica de combate en evolucion

#### Movil

- pantallas y navegacion
- consumo de API
- cliente de partida

#### Escritorio

- vista de sala
- vista de combate
- vista de resultado

### 6.3. Modelo de partida

La partida se plantea como un combate 1v1 por equipos. Cada jugador selecciona un equipo de 4 JaBeas antes de comenzar. En combate puede:

- usar uno de sus movimientos disponibles
- cambiar de JaBea
- rendirse

El orden de turno se basa principalmente en la velocidad, con desempate aleatorio.

## 7. Implementacion

### 7.1. Estado de implementacion del backend

Actualmente el backend contiene:

- configuracion del proyecto con Maven
- entidades JPA del dominio principal
- repositorios de acceso a datos
- servicios iniciales
- controladores REST basicos
- clases de apoyo para combate en fase de prototipo
- endpoint de comprobacion de conexion a base de datos

### 7.2. Configuracion del backend

Se ha sustituido la configuracion con credenciales embebidas por variables de entorno:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_URL_OPTIONS`

Esto permite reutilizar el mismo backend tanto con TiDB como con MySQL local.

### 7.3. Configuracion de puertos

El puerto del backend puede ajustarse desde propiedades o desde la configuracion de ejecucion en el IDE para evitar conflictos con otros proyectos activos durante el desarrollo.

## 8. Base de datos

### 8.1. Modelo de datos

La base de datos se ha diseñado separando tres bloques principales:

- catalogo del juego
- configuracion del jugador
- historial de partidas

### 8.2. Tablas principales

- `Users`: almacena credenciales, estado de login y estadisticas de partidas.
- `Teams`: almacena equipos asociados a un usuario, con nombre y estado activo.
- `JaBeas`: catalogo de criaturas disponibles.
- `Moves`: catalogo de movimientos normales y unicos.
- `Types`: tipos elementales del juego.
- `JaBeas_Teamed`: configuracion de criaturas dentro de un equipo.
- `Matches_History`: resultado de partidas.
- `JaBeas_History`: criaturas utilizadas en el historial de partidas.

### 8.3. Cambios recientes en el modelo

Durante la fase de ajuste del proyecto se añadieron los siguientes campos:

- `Users.Is_Logged`
- `Teams.Name`
- `Teams.Is_Active`

Estos campos se incorporaron tanto al esquema SQL como a las entidades JPA correspondientes.

### 8.4. Base remota y base local

Durante el desarrollo se ha trabajado con TiDB Cloud como base activa por comodidad de acceso y consistencia entre equipos. Adicionalmente, se mantiene preparada una alternativa local mediante MySQL y Docker para entornos donde se quiera ejecutar el proyecto sin depender de la nube.

## 9. Pruebas y validacion

### 9.1. Verificacion de conectividad

Se ha implementado un endpoint de prueba (`/test/db`) que permite comprobar:

- disponibilidad de la base de datos
- usuario de conexion
- URL de conexion
- conteo de tablas principales

Esta prueba ha permitido validar la configuracion del backend contra la base remota TiDB.

### 9.2. Pruebas previstas

Las pruebas previstas para el resto del proyecto incluyen:

- pruebas unitarias de servicios
- pruebas de endpoints REST
- pruebas de integracion del flujo de autenticacion
- pruebas del flujo de sala y partida

## 10. Despliegue y ejecucion

### 10.1. Requisitos de entorno

- Java 21 para backend
- Maven Wrapper del proyecto
- acceso a TiDB o, alternativamente, MySQL local
- entorno Node/Expo para el cliente movil

### 10.2. Ejecucion del backend

El backend puede ejecutarse desde:

- terminal con Maven Wrapper
- IntelliJ IDEA con variables de entorno

### 10.3. Configuracion mediante `.env`

El proyecto incluye ficheros de ejemplo para variables de entorno:

- `JavaBeasts-BBDD/.env.example`
- `JavaBeasts-Backend/.env.example`

Los secretos reales no deben mantenerse versionados en el repositorio.

## 11. Sostenibilidad y nube

### 11.1. Nube

Como parte de las evidencias del proyecto se contempla una propuesta de despliegue en infraestructura cloud. El uso de TiDB durante el desarrollo ya introduce una parte del proyecto en un entorno remoto gestionado, aunque la memoria final debera incluir una arquitectura mas formal y una estimacion aproximada de costes.

### 11.2. Sostenibilidad

Desde el punto de vista de sostenibilidad, el proyecto debera justificar medidas como:

- evitar consultas redundantes
- reutilizar datos ya cargados cuando tenga sentido
- optimizar estructuras y procesamiento
- reducir el coste de red y CPU en operaciones repetidas

Esta parte debera completarse con ejemplos concretos a medida que avance la implementacion.

## 12. Conclusiones y trabajo futuro

JavaBeasts plantea una solucion suficientemente compleja como para integrar varias competencias del ciclo DAM sin perder el control del alcance. El proyecto combina persistencia, servicios, clientes diferenciados y logica de juego en un unico sistema coherente.

En el momento actual, la base tecnica del sistema ya se encuentra encauzada, especialmente en lo referente a configuracion, persistencia y estructura general del backend. Las siguientes fases de trabajo se centran en cerrar la autenticacion, la gestion de equipos y la logica de partida.

Como trabajo futuro o mejoras no criticas se contemplan:

- ampliacion de efectos avanzados de movimientos
- mejora visual de clientes
- mayor profundidad en las reglas del combate
- mejora de la interfaz de escritorio
- gestion de reconexion y tolerancia a fallos en tiempo real

## 13. Bibliografia y recursos

### Documentacion tecnica

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/
- Lombok: https://projectlombok.org/features/
- Expo: https://docs.expo.dev/
- React Native: https://reactnative.dev/
- JavaFX: https://openjfx.io/
- Docker: https://docs.docker.com/
- TiDB Cloud: https://docs.pingcap.com/tidbcloud/

### Referencias de estructura de memoria y ejemplos consultados

- Universidad de Valladolid, ejemplo de TFG con estructura por introduccion, tecnologias, desarrollo, conclusiones y anexos: https://uvadoc.uva.es/bitstream/handle/10324/44427/TFG-G4660.pdf?sequence=1
- Universidad de Valladolid, ejemplo de memoria con capitulos de metodologia, planificacion, analisis, diseno, implementacion, evaluacion y anexos: https://uvadoc.uva.es/bitstream/handle/10324/79363/TFG-G7671.pdf?sequence=1
- Plantilla de memoria de proyecto DAM en ILERNA, con introduccion, motivacion e indice academico tipico: https://www.studocu.com/es/document/ilerna/proyecto-dam/dam-plantilla-memoria-escrita-2s2324/85902453

## 14. Anexos propuestos

- Anexo A. Manual de usuario
- Anexo B. Manual de instalacion
- Anexo C. Variables de entorno y configuracion
- Anexo D. Diagramas de base de datos y arquitectura
- Anexo E. Casos de uso y capturas

## Secciones que conviene seguir actualizando durante el desarrollo

Para que la memoria no se desfasase respecto al proyecto real, deberan mantenerse especialmente actualizadas estas secciones:

- tecnologias utilizadas
- arquitectura del sistema
- estado de implementacion
- diseno de base de datos
- pruebas realizadas
- despliegue
- evidencias de nube y sostenibilidad
