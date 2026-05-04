USE JavaBeasts;

START TRANSACTION;

-- Seed base data for JavaBeasts.
-- Source: docs/JavaBeasts.pdf
-- Decision: "Movimiento Fantasma" is used as Swiftor's unique move because the move list
-- defines it explicitly, even though the JaBeas summary mentions "Movimiento invisible".
-- Decision: Sylphyra Speed is seeded as 80; the extracted PDF text shows 8, which appears
-- to be an OCR truncation given the stat pattern used throughout the document.

-- =========================
-- 0. RESET
-- =========================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE JaBeas_History;
TRUNCATE TABLE Matches_History;
TRUNCATE TABLE JaBeas_Teamed;
TRUNCATE TABLE Teams;
TRUNCATE TABLE Moves;
TRUNCATE TABLE JaBeas;
TRUNCATE TABLE Users;
TRUNCATE TABLE Types;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- 1. TYPES
-- =========================
INSERT INTO Types (Type, Description)
VALUES
    ('Fuego', 'Criaturas agresivas con gran poder ofensivo. Sus ataques de fuego suelen causar mucho dano rapidamente, aunque a veces sacrifican defensa o estabilidad en combate.'),
    ('Agua', 'Criaturas equilibradas y resistentes. Sus habilidades se centran en controlar el combate, resistir ataques y recuperarse durante la batalla.'),
    ('Planta', 'Criaturas estrategicas que usan efectos y control del combate. Sus habilidades pueden debilitar, ralentizar o impedir acciones del rival mientras se mantienen en lucha.'),
    ('Electrico', 'Criaturas muy rapidas que destacan por sus ataques repentinos. Pueden alterar el ritmo del combate y golpear antes que sus oponentes.'),
    ('Normal', 'Criaturas versatiles sin especializacion concreta. Su equilibrio entre ataque, defensa y velocidad les permite adaptarse a distintos estilos de combate.')
ON DUPLICATE KEY UPDATE
    Description = VALUES(Description);

-- =========================
-- 2. JABEAS
-- =========================
INSERT INTO JaBeas (Name, Description, Health, Damage, Defence, Speed, Type)
VALUES
    ('Ignarok', 'JaBeas de fuego muy ofensivo cuyo movimiento unico es Erupcion.', 70, 100, 60, 70, (SELECT Type_Id FROM Types WHERE Type = 'Fuego')),
    ('Pyronox', 'JaBeas de fuego resistente cuyo movimiento unico es Nucleo Ardiente.', 100, 70, 100, 40, (SELECT Type_Id FROM Types WHERE Type = 'Fuego')),
    ('Cindrel', 'JaBeas de fuego veloz cuyo movimiento unico es Llama Veloz.', 60, 75, 55, 105, (SELECT Type_Id FROM Types WHERE Type = 'Fuego')),
    ('Flamara', 'JaBeas de fuego equilibrado cuyo movimiento unico es Tormenta Ignea.', 80, 85, 75, 80, (SELECT Type_Id FROM Types WHERE Type = 'Fuego')),
    ('Aquaryx', 'JaBeas de agua resistente cuyo movimiento unico es Marea Viva.', 105, 65, 95, 50, (SELECT Type_Id FROM Types WHERE Type = 'Agua')),
    ('Tiderra', 'JaBeas de agua equilibrado cuyo movimiento unico es Ola Gigante.', 85, 80, 80, 75, (SELECT Type_Id FROM Types WHERE Type = 'Agua')),
    ('Marvyn', 'JaBeas de agua ofensivo cuyo movimiento unico es Corriente Salvaje.', 70, 95, 65, 85, (SELECT Type_Id FROM Types WHERE Type = 'Agua')),
    ('Nerulon', 'JaBeas de agua rapido cuyo movimiento unico es Remolino Abisal.', 80, 70, 75, 95, (SELECT Type_Id FROM Types WHERE Type = 'Agua')),
    ('Verdrax', 'JaBeas de planta muy resistente cuyo movimiento unico es Bosque Viviente.', 110, 60, 100, 40, (SELECT Type_Id FROM Types WHERE Type = 'Planta')),
    ('Branther', 'JaBeas de planta ofensivo cuyo movimiento unico es Furia Natural.', 80, 95, 70, 75, (SELECT Type_Id FROM Types WHERE Type = 'Planta')),
    ('Florion', 'JaBeas de planta veloz cuyo movimiento unico es Tormenta Floral.', 65, 80, 60, 105, (SELECT Type_Id FROM Types WHERE Type = 'Planta')),
    ('Sylphyra', 'JaBeas de planta de control cuyo movimiento unico es Polen Somnifero.', 85, 65, 80, 80, (SELECT Type_Id FROM Types WHERE Type = 'Planta')),
    ('Voltari', 'JaBeas electrico extremadamente rapido cuyo movimiento unico es Sobrecarga.', 60, 85, 55, 110, (SELECT Type_Id FROM Types WHERE Type = 'Electrico')),
    ('Stormix', 'JaBeas electrico equilibrado cuyo movimiento unico es Tormenta Electrica.', 80, 80, 80, 80, (SELECT Type_Id FROM Types WHERE Type = 'Electrico')),
    ('Electryn', 'JaBeas electrico ofensivo cuyo movimiento unico es Relampago.', 75, 100, 60, 80, (SELECT Type_Id FROM Types WHERE Type = 'Electrico')),
    ('Zapphir', 'JaBeas electrico rapido cuyo movimiento unico es Campo Magnetico.', 70, 70, 70, 100, (SELECT Type_Id FROM Types WHERE Type = 'Electrico')),
    ('Griffel', 'JaBeas normal equilibrado cuyo movimiento unico es Golpe Maestro.', 85, 80, 80, 80, (SELECT Type_Id FROM Types WHERE Type = 'Normal')),
    ('Codrex', 'JaBeas normal ofensivo cuyo movimiento unico es Furia.', 75, 95, 70, 80, (SELECT Type_Id FROM Types WHERE Type = 'Normal')),
    ('Brawlex', 'JaBeas normal muy resistente cuyo movimiento unico es Guardia Total.', 110, 65, 100, 45, (SELECT Type_Id FROM Types WHERE Type = 'Normal')),
    ('Swiftor', 'JaBeas normal extremadamente rapido cuyo movimiento unico es Movimiento Fantasma.', 65, 75, 60, 110, (SELECT Type_Id FROM Types WHERE Type = 'Normal'))
ON DUPLICATE KEY UPDATE
    Description = VALUES(Description),
    Health = VALUES(Health),
    Damage = VALUES(Damage),
    Defence = VALUES(Defence),
    Speed = VALUES(Speed),
    Type = VALUES(Type);

-- =========================
-- 3. MOVES
-- Unique_JaBeas_Id:
-- NULL = movimiento general
-- valor = movimiento unico de ese JaBea
-- =========================
INSERT INTO Moves (Type_Id, Name, Description, Damage, Special_Effect, Accuracy, Unique_JaBeas_Id)
VALUES
    -- Movimientos fuego
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Colmillo Igneo', 'Un ataque ardiente con los colmillos', 55, '20% de quemar al rival', 95, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Explosion volcanica', 'Una explosion masiva de magma', 90, NULL, 70, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Calor ascendente', 'El usuario aumenta su temperatura corporal para potenciar su ataque.', 0, 'Aumenta el dano del usuario', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Brasa Persistente', 'Lanza brasas que siguen quemando', 40, 'Dano pequeno durante 2 turnos', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Fuego Cruzado', 'Un ataque envolvente de llamas', 65, '20% de reducir velocidad rival', 90, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Erupcion', 'Una erupcion volcanica golpea con enorme fuerza', 95, NULL, 80, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Ignarok')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Llama Veloz', 'Un ataque de fuego extremadamente rapido', 60, 'Ataque con prioridad', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Cindrel')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Tormenta Ignea', 'Una tormenta de fuego arrasa el campo', 90, '20% de reducir defensa rival', 85, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Flamara')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Fuego'), 'Nucleo Ardiente', 'El usuario libera energia del nucleo ardiente de su cuerpo', 0, 'Aumenta mucho la defensa', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Pyronox')),
    -- Movimientos agua
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Chorro Presurizado', 'Un potente chorro de agua comprimida', 60, NULL, 95, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Tormenta Marina', 'Invoca una tormenta que golpea al rival', 80, 'Reduce velocidad rival', 80, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Burbuja Protectora', 'El usuario se envuelve en una burbuja protectora', 0, 'Aumenta defensa', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Oleaje', 'Una gran ola impacta al enemigo', 70, '20% de bajar dano rival', 90, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Agua Vital', 'Agua revitalizante rodea al usuario', 0, 'Cura 25% de vida', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Ola Gigante', 'Una enorme ola cae sobre el enemigo', 95, NULL, 80, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Tiderra')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Marea Viva', 'El usuario invoca una marea regeneradora', 0, 'Cura 40% de vida', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Aquaryx')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Corriente Salvaje', 'Una corriente impulsa al usuario', 0, 'Aumenta velocidad', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Marvyn')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Agua'), 'Remolino Abisal', 'Un remolino oscuro atrapa al enemigo', 60, 'Reduce velocidad rival', 90, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Nerulon')),
    -- Movimientos planta
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Espina Veloz', 'Espinas afiladas salen disparadas', 50, 'Ataque con prioridad', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Bosque Enredado', 'Enredaderas atrapan al enemigo', 0, 'Reduce velocidad rival', 95, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Polen Irritante', 'Nube de polen ofensivo', 35, 'Reduce precision rival', 90, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Savia Curativa', 'Savia regeneradora fluye por el usuario', 0, 'Cura vida', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Hoja Torbellino', 'Torbellino de hojas cortantes', 75, NULL, 85, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Tormenta Floral', 'Una explosion de petalos golpea al enemigo', 90, NULL, 85, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Florion')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Bosque Viviente', 'La naturaleza protege al usuario', 0, 'Cura vida y aumenta defensa', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Verdrax')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Furia Natural', 'El usuario libera la fuerza de la naturaleza', 0, 'Aumenta dano', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Branther')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Planta'), 'Polen Somnifero', 'Un polen que induce al sueno', 0, 'Impide el turno rival', 80, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Sylphyra')),
    -- Movimientos electrico
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Pulso Electrico', 'Un pulso electrico directo', 60, '20% de reducir velocidad rival', 95, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Descarga Caotica', 'Descarga electrica descontrolada', 85, 'Puede paralizar', 80, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Carga Rapida', 'Golpe rapido electrificado', 40, 'Aumenta velocidad del usuario', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Tormenta Estatica', 'Electricidad acumulada en el campo', 0, 'Aumenta dano electrico del usuario', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Relampago Fragmentado', 'Un relampago se divide en multiples impactos', 70, 'Puede golpear dos veces (30%)', 90, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Sobrecarga', 'Una descarga electrica devastadora', 95, 'Reduce defensa del usuario', 80, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Voltari')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Relampago', 'Un relampago instantaneo golpea al enemigo', 80, 'Ataque muy rapido', 95, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Electryn')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Tormenta Electrica', 'Una tormenta electrica se desata', 90, 'Aumenta dano electrico del usuario', 85, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Stormix')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Electrico'), 'Campo Magnetico', 'Un campo magnetico protege al usuario', 0, 'Reduce dano recibido', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Zapphir')),
    -- Movimientos normal
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Embestida', 'Ataque fisico directo', 60, NULL, 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Guardia Firme', 'El usuario adopta una postura defensiva', 0, 'Aumenta defensa', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Finta', 'Movimiento enganoso que sorprende al rival', 45, 'Ignora buffs de defensa', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Inspiracion', 'El usuario se motiva para luchar mejor', 0, 'Aumenta dano', 100, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Golpe Determinante', 'Un ataque fuerte y preciso', 80, NULL, 85, NULL),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Golpe Maestro', 'Un golpe perfecto ejecutado con maestria', 95, NULL, 85, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Griffel')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Furia', 'La furia del usuario aumenta progresivamente', 60, 'Aumenta dano cada turno', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Codrex')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Guardia Total', 'El usuario se protege completamente', 0, 'Aumenta mucho la defensa', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Brawlex')),
    ((SELECT Type_Id FROM Types WHERE Type = 'Normal'), 'Movimiento Fantasma', 'Un movimiento tan rapido que parece invisible', 60, 'Ataque con prioridad', 100, (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Swiftor'))
ON DUPLICATE KEY UPDATE
    Type_Id = VALUES(Type_Id),
    Description = VALUES(Description),
    Damage = VALUES(Damage),
    Special_Effect = VALUES(Special_Effect),
    Accuracy = VALUES(Accuracy),
    Unique_JaBeas_Id = VALUES(Unique_JaBeas_Id);

-- =========================
-- 4. USERS
-- Sample credentials:
-- alba / alba123
-- bruno / bruno123
-- carla / carla123
-- =========================
INSERT INTO Users (Username, Password, Is_Logged, Matches_Won, Matches_Lost)
VALUES
    ('alba', '$argon2i$v=19$m=65536,t=4,p=1$WC50N1J0czdYYWVjNERIVA$oS4z6HjnDOu68l/AQwo31nqb6CiIz7a2BLf0rXrETr0', FALSE, 1, 1),
    ('bruno', '$argon2i$v=19$m=65536,t=4,p=1$MmkucUJqcEZrR1hWQ1BWZA$/zL9Kp6r/aLXou+kIVD0uzhcH2fix+NP2GF/55A1j+c', FALSE, 0, 1),
    ('carla', '$argon2i$v=19$m=65536,t=4,p=1$OTdVZk54elF5VnNRakswVg$/070gF2txMz4NVsLT0lNaXFyU4opLtRq0C3fRu5OVLg', FALSE, 1, 0)
ON DUPLICATE KEY UPDATE
    Password = VALUES(Password),
    Is_Logged = VALUES(Is_Logged),
    Matches_Won = VALUES(Matches_Won),
    Matches_Lost = VALUES(Matches_Lost);

-- =========================
-- 5. TEAMS
-- =========================
INSERT INTO Teams (User_Id, Name, Icon_Name, Is_Active)
VALUES
    ((SELECT User_Id FROM Users WHERE Username = 'alba'), 'Equipo Alba 1', 'flame-outline', TRUE),
    ((SELECT User_Id FROM Users WHERE Username = 'bruno'), 'Equipo Bruno 1', 'shield-outline', TRUE),
    ((SELECT User_Id FROM Users WHERE Username = 'carla'), 'Equipo Carla 1', 'leaf-outline', TRUE);

-- =========================
-- 6. JABEAS_TEAMMED
-- Each JaBea keeps its unique move plus these two selected moves.
-- =========================
INSERT INTO JaBeas_Teamed (Team_Id, JaBeas_Id, Move_1, Move_2, Slot)
VALUES
    -- Team alba
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'alba'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Cindrel'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Fuego Cruzado'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Carga Rapida'),
        1
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'alba'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Aquaryx'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Burbuja Protectora'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Savia Curativa'),
        2
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'alba'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Branther'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Hoja Torbellino'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Oleaje'),
        3
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'alba'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Swiftor'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Embestida'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Finta'),
        4
    ),
    -- Team bruno
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'bruno'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Pyronox'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Calor ascendente'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        1
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'bruno'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Nerulon'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Tormenta Marina'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Bosque Enredado'),
        2
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'bruno'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Zapphir'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Pulso Electrico'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Fuego Cruzado'),
        3
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'bruno'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Griffel'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Inspiracion'),
        4
    ),
    -- Team carla
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'carla'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Flamara'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Colmillo Igneo'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Descarga Caotica'),
        1
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'carla'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Florion'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Espina Veloz'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Agua Vital'),
        2
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'carla'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Stormix'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Relampago Fragmentado'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Brasa Persistente'),
        3
    ),
    (
        (SELECT t.Team_Id FROM Teams t JOIN Users u ON t.User_Id = u.User_Id WHERE u.Username = 'carla'),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Brawlex'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Embestida'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        4
    );

-- =========================
-- 7. MATCHES_HISTORY
-- =========================
INSERT INTO Matches_History (Winner_Id, Loser_Id, Turns)
VALUES
    (
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT User_Id FROM Users WHERE Username = 'bruno'),
        12
    ),
    (
        (SELECT User_Id FROM Users WHERE Username = 'carla'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        15
    );

-- =========================
-- 8. JABEAS_HISTORY
-- Stored lineups for the sample matches above.
-- =========================
INSERT INTO JaBeas_History (Match_Id, JaBeas_Id, Owner_Id, Move_1, Move_2, Slot)
VALUES
    -- Match 1: alba vs bruno
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Cindrel'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Fuego Cruzado'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Carga Rapida'),
        1
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Aquaryx'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Burbuja Protectora'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Savia Curativa'),
        2
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Branther'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Hoja Torbellino'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Oleaje'),
        3
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Swiftor'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Embestida'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Finta'),
        4
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Pyronox'),
        (SELECT User_Id FROM Users WHERE Username = 'bruno'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Calor ascendente'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        1
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Nerulon'),
        (SELECT User_Id FROM Users WHERE Username = 'bruno'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Tormenta Marina'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Bosque Enredado'),
        2
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Zapphir'),
        (SELECT User_Id FROM Users WHERE Username = 'bruno'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Pulso Electrico'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Fuego Cruzado'),
        3
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'bruno') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Griffel'),
        (SELECT User_Id FROM Users WHERE Username = 'bruno'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Inspiracion'),
        4
    ),
    -- Match 2: carla vs alba
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Flamara'),
        (SELECT User_Id FROM Users WHERE Username = 'carla'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Colmillo Igneo'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Descarga Caotica'),
        1
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Florion'),
        (SELECT User_Id FROM Users WHERE Username = 'carla'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Espina Veloz'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Agua Vital'),
        2
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Stormix'),
        (SELECT User_Id FROM Users WHERE Username = 'carla'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Relampago Fragmentado'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Brasa Persistente'),
        3
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Brawlex'),
        (SELECT User_Id FROM Users WHERE Username = 'carla'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Embestida'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Guardia Firme'),
        4
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Cindrel'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Fuego Cruzado'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Carga Rapida'),
        1
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Aquaryx'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Burbuja Protectora'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Savia Curativa'),
        2
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Branther'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Hoja Torbellino'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Oleaje'),
        3
    ),
    (
        (SELECT Match_Id FROM Matches_History WHERE Winner_Id = (SELECT User_Id FROM Users WHERE Username = 'carla') AND Loser_Id = (SELECT User_Id FROM Users WHERE Username = 'alba') LIMIT 1),
        (SELECT JaBeas_Id FROM JaBeas WHERE Name = 'Swiftor'),
        (SELECT User_Id FROM Users WHERE Username = 'alba'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Embestida'),
        (SELECT Move_Id FROM Moves WHERE Name = 'Finta'),
        4
    );

COMMIT;
