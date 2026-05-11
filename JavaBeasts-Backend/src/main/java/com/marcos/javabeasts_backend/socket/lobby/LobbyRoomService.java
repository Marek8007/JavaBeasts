package com.marcos.javabeasts_backend.socket.lobby;

import com.marcos.javabeasts_backend.socket.lobby.dto.PlayerSlotPayload;
import com.marcos.javabeasts_backend.socket.lobby.dto.RoomStatusPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class LobbyRoomService {

    private final LobbyRoom room = new LobbyRoom(generateRoomCode());

    @PostConstruct
    public void logRoomCode() {
        System.out.println("[TCP LOBBY] Codigo de sala generado: " + room.getRoomCode());
    }

    public synchronized LobbyRoom getRoom() {
        return room;
    }

    public synchronized RoomStatusPayload getRoomStatus() {
        return toPayload(room);
    }

    public synchronized RoomStatusPayload joinRoom(String roomCode, String username) {
        validateRoomCode(roomCode);
        room.joinPlayer(username);
        return toPayload(room);
    }

    public synchronized RoomStatusPayload leaveRoom(String roomCode, String username) {
        validateRoomCode(roomCode);
        room.leavePlayer(username);
        return toPayload(room);
    }

    public synchronized RoomStatusPayload setReady(String roomCode, String username, boolean ready) {
        validateRoomCode(roomCode);
        room.setReady(username, ready);
        return toPayload(room);
    }

    public synchronized RoomStatusPayload resetReadyStates(String roomCode) {
        validateRoomCode(roomCode);
        room.resetReadyStates();
        return toPayload(room);
    }

    private void validateRoomCode(String roomCode) {
        if (!room.getRoomCode().equals(roomCode)) {
            throw new IllegalArgumentException("El codigo de sala no es valido");
        }
    }

    private String generateRoomCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    private RoomStatusPayload toPayload(LobbyRoom room) {
        return new RoomStatusPayload(
                room.getRoomCode(),
                room.isFull(),
                room.canStartMatch(),
                toPlayerPayload(room.getPlayerOne()),
                toPlayerPayload(room.getPlayerTwo())
        );
    }

    private PlayerSlotPayload toPlayerPayload(LobbyPlayer player) {
        if (player == null) {
            return null;
        }

        return new PlayerSlotPayload(player.getUsername(), player.isReady());
    }
}
