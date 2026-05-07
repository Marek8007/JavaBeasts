package com.marcos.javabeasts_javafx.battle;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BattleSnapshotClient {

    private final String host;
    private final int port;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public BattleSnapshotClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public BattleSnapshotData fetchInitialSnapshot(String roomCode) throws IOException {
        String encodedRoomCode = URLEncoder.encode(roomCode, StandardCharsets.UTF_8);
        URI uri = URI.create("http://" + host + ":" + port + "/battle/snapshot?roomCode=" + encodedRoomCode);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("La peticion del snapshot de combate fue interrumpida", e);
        }

        if (response.statusCode() != 200) {
            throw new IOException("El backend devolvio " + response.statusCode() + " al pedir el snapshot de combate");
        }

        try (InputStream bodyStream = response.body()) {
            BattleSnapshotData snapshot = gson.fromJson(new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8), BattleSnapshotData.class);
            if (snapshot == null) {
                throw new IOException("No se pudo parsear el snapshot inicial del combate");
            }
            return snapshot;
        }
    }
}
