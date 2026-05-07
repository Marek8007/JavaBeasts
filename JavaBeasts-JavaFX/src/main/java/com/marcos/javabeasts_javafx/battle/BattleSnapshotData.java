package com.marcos.javabeasts_javafx.battle;

public class BattleSnapshotData {

    private String roomCode;
    private Integer turnNumber;
    private BattlePlayerSnapshotData playerOne;
    private BattlePlayerSnapshotData playerTwo;

    public String getRoomCode() {
        return roomCode;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public BattlePlayerSnapshotData getPlayerOne() {
        return playerOne;
    }

    public BattlePlayerSnapshotData getPlayerTwo() {
        return playerTwo;
    }
}
