package com.marcos.javabeasts_backend.socket.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LobbyTcpServerRunner implements CommandLineRunner {

    private final LobbyTcpServer lobbyTcpServer;

    public LobbyTcpServerRunner(LobbyTcpServer lobbyTcpServer) {
        this.lobbyTcpServer = lobbyTcpServer;
    }

    @Override
    public void run(String... args) {
        lobbyTcpServer.start();
    }
}
