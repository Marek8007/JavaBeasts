package com.marcos.javabeasts_javafx.socket;

public class RoomStatusData {
    private String roomCode;
    private boolean full;
    private boolean canStart;
    private RoomStatusPlayer playerOne;
    private RoomStatusPlayer playerTwo;

    public String getRoomCode() {
        return roomCode;
    }

    public boolean isFull() {
        return full;
    }

    public boolean isCanStart() {
        return canStart;
    }

    public RoomStatusPlayer getPlayerOne() {
        return playerOne;
    }

    public RoomStatusPlayer getPlayerTwo() {
        return playerTwo;
    }
}
