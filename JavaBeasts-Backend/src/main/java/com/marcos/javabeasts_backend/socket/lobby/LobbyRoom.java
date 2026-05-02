package com.marcos.javabeasts_backend.socket.lobby;

import lombok.Data;

@Data
public class LobbyRoom {

    private final String roomCode;
    private LobbyPlayer playerOne;
    private LobbyPlayer playerTwo;

    public LobbyRoom(String roomCode) {
        this.roomCode = roomCode;
    }

    public boolean hasFreeSlot() {
        return playerOne == null || playerTwo == null;
    }

    public boolean isEmpty() {
        return playerOne == null && playerTwo == null;
    }

    public boolean isFull() {
        return playerOne != null && playerTwo != null;
    }

    public boolean containsPlayer(String username) {
        return (playerOne != null && playerOne.getUsername().equals(username))
                || (playerTwo != null && playerTwo.getUsername().equals(username));
    }

    public boolean canStartMatch() {
        return isFull() && playerOne.isReady() && playerTwo.isReady();
    }

    public LobbyPlayer joinPlayer(String username) {
        if (playerOne != null && playerOne.getUsername().equals(username)) {
            return playerOne;
        }

        if (playerTwo != null && playerTwo.getUsername().equals(username)) {
            return playerTwo;
        }

        if (playerOne == null) {
            playerOne = new LobbyPlayer(username);
            return playerOne;
        }

        if (playerTwo == null) {
            playerTwo = new LobbyPlayer(username);
            return playerTwo;
        }

        throw new IllegalStateException("La sala ya esta llena");
    }

    public void leavePlayer(String username) {
        if (playerOne != null && playerOne.getUsername().equals(username)) {
            playerOne = null;
            return;
        }

        if (playerTwo != null && playerTwo.getUsername().equals(username)) {
            playerTwo = null;
        }
    }

    public void setReady(String username, boolean ready) {
        LobbyPlayer player = getRequiredPlayer(username);
        player.setReady(ready);
    }

    public void resetReadyStates() {
        if (playerOne != null) {
            playerOne.setReady(false);
        }

        if (playerTwo != null) {
            playerTwo.setReady(false);
        }
    }

    private LobbyPlayer getRequiredPlayer(String username) {
        if (playerOne != null && playerOne.getUsername().equals(username)) {
            return playerOne;
        }

        if (playerTwo != null && playerTwo.getUsername().equals(username)) {
            return playerTwo;
        }

        throw new IllegalArgumentException("El jugador no esta dentro de la sala");
    }
}
