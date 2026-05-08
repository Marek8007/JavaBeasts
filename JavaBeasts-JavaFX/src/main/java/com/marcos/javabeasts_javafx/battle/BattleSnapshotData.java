package com.marcos.javabeasts_javafx.battle;

public class BattleSnapshotData {

    private String roomCode;
    private Integer turnNumber;
    private String message;
    private BattlePlayerSnapshotData playerOne;
    private BattlePlayerSnapshotData playerTwo;

    public String getRoomCode() {
        return roomCode;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public String getMessage() {
        return message;
    }

    public BattlePlayerSnapshotData getPlayerOne() {
        return playerOne;
    }

    public BattlePlayerSnapshotData getPlayerTwo() {
        return playerTwo;
    }
}
