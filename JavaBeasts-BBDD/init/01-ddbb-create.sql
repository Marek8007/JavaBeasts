CREATE DATABASE IF NOT EXISTS JavaBeasts;
USE JavaBeasts;

-- =========================
-- 1. TYPES
-- =========================
CREATE TABLE IF NOT EXISTS Types (
    Type_Id INT AUTO_INCREMENT PRIMARY KEY,
    Type VARCHAR(25) NOT NULL UNIQUE,
    Description VARCHAR(200)
);

-- =========================
-- 2. USERS
-- =========================
CREATE TABLE IF NOT EXISTS Users (
    User_Id INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(25) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Is_Logged BOOLEAN NOT NULL DEFAULT FALSE,
    Matches_Won INT NOT NULL DEFAULT 0,
    Matches_Lost INT NOT NULL DEFAULT 0
);

-- =========================
-- 3. JABEAS
-- =========================
CREATE TABLE IF NOT EXISTS JaBeas (
    JaBeas_Id INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(25) NOT NULL UNIQUE,
    Description VARCHAR(200) NOT NULL,
    Health INT NOT NULL,
    Damage INT NOT NULL,
    Defence INT NOT NULL,
    Speed INT NOT NULL,
    Type INT NOT NULL,
    CONSTRAINT fk_jabeas_type
        FOREIGN KEY (Type) REFERENCES Types(Type_Id)
);

-- =========================
-- 4. MOVES
-- Unique_JaBeas_Id:
-- NULL = movimiento normal
-- valor = movimiento unico de ese JaBea
-- =========================
CREATE TABLE IF NOT EXISTS Moves (
    Move_Id INT AUTO_INCREMENT PRIMARY KEY,
    Type_Id INT NOT NULL,
    Name VARCHAR(25) NOT NULL UNIQUE,
    Description VARCHAR(200) NOT NULL,
    Damage INT NOT NULL DEFAULT 0,
    Special_Effect VARCHAR(200),
    Accuracy INT NOT NULL,
    Unique_JaBeas_Id INT NULL,
    CONSTRAINT fk_moves_type
        FOREIGN KEY (Type_Id) REFERENCES Types(Type_Id),
    CONSTRAINT fk_moves_unique_jabeas
        FOREIGN KEY (Unique_JaBeas_Id) REFERENCES JaBeas(JaBeas_Id)
);

-- =========================
-- 5. TEAMS
-- =========================
CREATE TABLE IF NOT EXISTS Teams (
    Team_Id INT AUTO_INCREMENT PRIMARY KEY,
    User_Id INT NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Icon_Name VARCHAR(60) NOT NULL DEFAULT 'paw-outline',
    Is_Active BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_teams_user
        FOREIGN KEY (User_Id) REFERENCES Users(User_Id)
        ON DELETE CASCADE
);

-- =========================
-- 6. JABEAS_TEAMMED
-- JaBea configurado dentro de un equipo
-- PK compuesta: un slot concreto dentro de un equipo
-- =========================
CREATE TABLE IF NOT EXISTS JaBeas_Teamed (
    Team_Id INT NOT NULL,
    JaBeas_Id INT NOT NULL,
    Move_1 INT NOT NULL,
    Move_2 INT NOT NULL,
    Slot INT NOT NULL,
    PRIMARY KEY (Team_Id, Slot),
    CONSTRAINT uq_teamed_moves UNIQUE (Team_Id, JaBeas_Id, Slot),
    CONSTRAINT fk_teamed_team
        FOREIGN KEY (Team_Id) REFERENCES Teams(Team_Id)
        ON DELETE CASCADE,
    CONSTRAINT fk_teamed_jabeas
        FOREIGN KEY (JaBeas_Id) REFERENCES JaBeas(JaBeas_Id),
    CONSTRAINT fk_teamed_move1
        FOREIGN KEY (Move_1) REFERENCES Moves(Move_Id),
    CONSTRAINT fk_teamed_move2
        FOREIGN KEY (Move_2) REFERENCES Moves(Move_Id)
);

-- =========================
-- 7. MATCHES_HISTORY
-- =========================
CREATE TABLE IF NOT EXISTS Matches_History (
    Match_Id INT AUTO_INCREMENT PRIMARY KEY,
    Winner_Id INT NOT NULL,
    Loser_Id INT NOT NULL,
    Turns INT NOT NULL,
    CONSTRAINT fk_matches_winner
        FOREIGN KEY (Winner_Id) REFERENCES Users(User_Id),
    CONSTRAINT fk_matches_loser
        FOREIGN KEY (Loser_Id) REFERENCES Users(User_Id)
);

-- =========================
-- 8. JABEAS_HISTORY
-- Guarda los JaBeas usados en una partida
-- =========================
CREATE TABLE IF NOT EXISTS JaBeas_History (
    JaBeas_History_Id INT AUTO_INCREMENT PRIMARY KEY,
    Match_Id INT NOT NULL,
    JaBeas_Id INT NOT NULL,
    Owner_Id INT NOT NULL,
    Move_1 INT NOT NULL,
    Move_2 INT NOT NULL,
    Slot INT NOT NULL,
    CONSTRAINT fk_jabeas_history_match
        FOREIGN KEY (Match_Id) REFERENCES Matches_History(Match_Id)
        ON DELETE CASCADE,
    CONSTRAINT fk_jabeas_history_jabeas
        FOREIGN KEY (JaBeas_Id) REFERENCES JaBeas(JaBeas_Id),
    CONSTRAINT fk_jabeas_history_owner
        FOREIGN KEY (Owner_Id) REFERENCES Users(User_Id),
    CONSTRAINT fk_jabeas_history_move1
        FOREIGN KEY (Move_1) REFERENCES Moves(Move_Id),
    CONSTRAINT fk_jabeas_history_move2
        FOREIGN KEY (Move_2) REFERENCES Moves(Move_Id)
);
