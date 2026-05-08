package com.marcos.javabeasts_backend.socket.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.marcos.javabeasts_backend.dto.battle.BattleActionRequest;
import com.marcos.javabeasts_backend.services.battle.BattleSessionService;
import com.marcos.javabeasts_backend.services.battle.BattleSetupService;
import com.marcos.javabeasts_backend.socket.SocketCodes;
import com.marcos.javabeasts_backend.socket.dto.SocketRequest;
import com.marcos.javabeasts_backend.socket.dto.SocketResponse;
import com.marcos.javabeasts_backend.socket.lobby.LobbyRoomService;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LobbyClientHandler implements Runnable {

    private final Socket socket;
    private final LobbyRoomService lobbyRoomService;
    private final BattleSetupService battleSetupService;
    private final BattleSessionService battleSessionService;
    private final Gson gson;

    public LobbyClientHandler(
            Socket socket,
            LobbyRoomService lobbyRoomService,
            BattleSetupService battleSetupService,
            BattleSessionService battleSessionService,
            Gson gson
    ) {
        this.socket = socket;
        this.lobbyRoomService = lobbyRoomService;
        this.battleSetupService = battleSetupService;
        this.battleSessionService = battleSessionService;
        this.gson = gson;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            try {
                String line = in.readLine();
                if (line == null || line.isBlank()) {
                    out.println(gson.toJson(errorResponse("Peticion vacia")));
                    return;
                }

                SocketRequest request = gson.fromJson(line, SocketRequest.class);
                SocketResponse response = handleRequest(request);
                out.println(gson.toJson(response));
            } catch (ResponseStatusException e) {
                out.println(gson.toJson(errorResponse(e.getReason() != null ? e.getReason() : e.getMessage())));
            } catch (IllegalArgumentException | IllegalStateException e) {
                out.println(gson.toJson(errorResponse(e.getMessage())));
            } catch (Exception e) {
                out.println(gson.toJson(errorResponse("Error interno del servidor TCP")));
            }
        } catch (Exception e) {
            writeBestEffortError("Error interno del servidor TCP");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private SocketResponse handleRequest(SocketRequest request) {
        if (request == null || request.code() == null || request.code().isBlank()) {
            return errorResponse("Codigo de operacion no valido");
        }

        JsonObject data = request.data() != null ? request.data() : new JsonObject();

        return switch (request.code()) {
            case SocketCodes.ROOM_STATUS -> successResponse(lobbyRoomService.getRoomStatus());
            case SocketCodes.JOIN_ROOM -> successResponse(
                    lobbyRoomService.joinRoom(requiredString(data, "roomCode"), requiredString(data, "username"))
            );
            case SocketCodes.LEAVE_ROOM -> successResponse(
                    lobbyRoomService.leaveRoom(requiredString(data, "roomCode"), requiredString(data, "username"))
            );
            case SocketCodes.SET_READY -> successResponse(
                    lobbyRoomService.setReady(
                            requiredString(data, "roomCode"),
                            requiredString(data, "username"),
                            requiredBoolean(data, "ready")
                    )
            );
            case SocketCodes.BATTLE_SNAPSHOT -> successResponse(
                    battleSetupService.buildInitialSnapshot(requiredString(data, "roomCode"))
            );
            case SocketCodes.SUBMIT_ACTION -> successResponse(
                    battleSessionService.submitAction(new BattleActionRequest(
                            requiredString(data, "roomCode"),
                            requiredString(data, "username"),
                            requiredString(data, "actionType"),
                            optionalInteger(data, "moveSlot"),
                            optionalInteger(data, "switchSlot")
                    ))
            );
            default -> errorResponse("Codigo de operacion no soportado");
        };
    }

    private SocketResponse successResponse(Object payload) {
        return new SocketResponse("success", gson.toJsonTree(payload).getAsJsonObject());
    }

    private SocketResponse errorResponse(String message) {
        JsonObject data = new JsonObject();
        data.addProperty("message", message);
        return new SocketResponse("error", data);
    }

    private String requiredString(JsonObject data, String field) {
        if (!data.has(field) || data.get(field).isJsonNull()) {
            throw new IllegalArgumentException("Falta el campo obligatorio: " + field);
        }

        String value = data.get(field).getAsString().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("El campo " + field + " no puede estar vacio");
        }

        return value;
    }

    private boolean requiredBoolean(JsonObject data, String field) {
        if (!data.has(field) || data.get(field).isJsonNull()) {
            throw new IllegalArgumentException("Falta el campo obligatorio: " + field);
        }

        return data.get(field).getAsBoolean();
    }

    private Integer optionalInteger(JsonObject data, String field) {
        if (!data.has(field) || data.get(field).isJsonNull()) {
            return null;
        }

        return data.get(field).getAsInt();
    }

    private void writeBestEffortError(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(gson.toJson(errorResponse(message)));
        } catch (IOException ignored) {
        }
    }
}
