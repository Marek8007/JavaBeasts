package com.marcos.javabeasts_javafx.battle;

public class BattleSnapshotData {

    private String roomCode;
    private Integer turnNumber;
    private String message;
    private boolean finished;
    private String winnerUsername;
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

    public boolean isFinished() {
        return finished;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public BattlePlayerSnapshotData getPlayerOne() {
        return playerOne;
    }

    public BattlePlayerSnapshotData getPlayerTwo() {
        return playerTwo;
    }
}
