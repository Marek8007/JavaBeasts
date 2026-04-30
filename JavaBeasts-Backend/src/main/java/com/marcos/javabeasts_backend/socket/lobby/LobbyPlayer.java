package com.marcos.javabeasts_backend.socket.lobby;

import lombok.Data;

@Data
public class LobbyPlayer {

    private final String username;
    private boolean ready;

    public LobbyPlayer(String username) {
        this.username = username;
        this.ready = false;
    }
}
