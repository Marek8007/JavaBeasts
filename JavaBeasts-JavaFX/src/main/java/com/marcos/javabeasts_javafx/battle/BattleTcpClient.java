package com.marcos.javabeasts_javafx.battle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.marcos.javabeasts_javafx.socket.SocketEnvelope;
import com.marcos.javabeasts_javafx.socket.SocketErrorData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;

public class BattleTcpClient {

    private final String host;
    private final int port;
    private final Gson gson = new Gson();

    public BattleTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public BattleSnapshotData fetchInitialSnapshot(String roomCode) throws IOException {
        JsonObject data = new JsonObject();
        data.addProperty("roomCode", roomCode);
        return sendRequest("battle_snapshot", data, new TypeToken<SocketEnvelope<BattleSnapshotData>>() { }.getType());
    }

    private <T> T sendRequest(String code, JsonObject data, Type responseType) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("code", code);
        payload.add("data", data);

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(gson.toJson(payload));
            String responseLine = in.readLine();

            if (responseLine == null || responseLine.isBlank()) {
                throw new IOException("Respuesta vacia del backend TCP");
            }

            SocketEnvelope<T> response = gson.fromJson(responseLine, responseType);
            if (response == null) {
                throw new IOException("No se pudo parsear la respuesta TCP del combate");
            }

            if (!"success".equalsIgnoreCase(response.getStatus())) {
                Type errorType = new TypeToken<SocketEnvelope<SocketErrorData>>() { }.getType();
                SocketEnvelope<SocketErrorData> errorResponse = gson.fromJson(responseLine, errorType);
                String errorMessage = errorResponse != null
                        && errorResponse.getData() != null
                        && errorResponse.getData().getMessage() != null
                        ? errorResponse.getData().getMessage()
                        : "Error desconocido";
                throw new IOException("El backend TCP devolvio error: " + errorMessage);
            }

            return response.getData();
        }
    }
}
