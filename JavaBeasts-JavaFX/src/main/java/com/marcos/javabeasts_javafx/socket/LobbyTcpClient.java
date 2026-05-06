package com.marcos.javabeasts_javafx.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LobbyTcpClient {

    private final String host;
    private final int port;
    private final Gson gson = new Gson();

    public LobbyTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RoomStatusData fetchRoomStatus() throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("code", "room_status");
        payload.add("data", new JsonObject());

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(gson.toJson(payload));
            String responseLine = in.readLine();

            if (responseLine == null || responseLine.isBlank()) {
                throw new IOException("Respuesta vacía del lobby TCP");
            }

            SocketRoomStatusResponse response = gson.fromJson(responseLine, SocketRoomStatusResponse.class);

            if (response == null) {
                throw new IOException("No se pudo parsear la respuesta del lobby TCP");
            }

            if (!"success".equalsIgnoreCase(response.getStatus())) {
                String errorMessage = response.getData() != null ? response.getData().toString() : "Error desconocido";
                throw new IOException("El lobby TCP devolvio error: " + errorMessage);
            }

            return response.getData();
        }
    }
}
