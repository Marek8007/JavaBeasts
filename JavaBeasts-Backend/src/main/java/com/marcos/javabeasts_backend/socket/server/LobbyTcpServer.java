package com.marcos.javabeasts_backend.socket.server;

import com.google.gson.Gson;
import com.marcos.javabeasts_backend.socket.lobby.LobbyRoomService;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class LobbyTcpServer {

    private final LobbyRoomService lobbyRoomService;
    private final Gson gson = new Gson();

    @Value("${app.socket.port}")
    private int socketPort;

    private volatile boolean running;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public LobbyTcpServer(LobbyRoomService lobbyRoomService) {
        this.lobbyRoomService = lobbyRoomService;
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        acceptThread = new Thread(this::acceptLoop, "lobby-tcp-server");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void acceptLoop() {
        try (ServerSocket server = new ServerSocket(socketPort)) {
            this.serverSocket = server;
            System.out.println("[TCP LOBBY] Escuchando en puerto " + socketPort);

            while (running) {
                Socket clientSocket = server.accept();
                Thread handlerThread = new Thread(
                        new LobbyClientHandler(clientSocket, lobbyRoomService, gson),
                        "lobby-client-" + clientSocket.getPort()
                );
                handlerThread.start();
            }
        } catch (IOException e) {
            if (running) {
                throw new RuntimeException("No se pudo iniciar el servidor TCP del lobby", e);
            }
        }
    }

    @PreDestroy
    public synchronized void stop() {
        running = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
