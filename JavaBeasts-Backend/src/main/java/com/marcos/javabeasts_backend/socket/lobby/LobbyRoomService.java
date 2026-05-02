package com.marcos.javabeasts_backend.socket.lobby;

import com.marcos.javabeasts_backend.socket.lobby.dto.PlayerSlotPayload;
import com.marcos.javabeasts_backend.socket.lobby.dto.RoomStatusPayload;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class LobbyRoomService {

    private final LobbyRoom room = new LobbyRoom(generateRoomCode());

    public synchronized LobbyRoom getRoom() {
        return room;
    }

    public synchronized RoomStatusPayload getRoomStatus() {
        return toPayload(room);
    }

    public synchronized RoomStatusPayload joinRoom(String username) {
        room.joinPlayer(username);
        return toPayload(room);
    }

    public synchronized RoomStatusPayload leaveRoom(String username) {
        room.leavePlayer(username);
        return toPayload(room);
    }

    public synchronized RoomStatusPayload setReady(String username, boolean ready) {
        room.setReady(username, ready);
        return toPayload(room);
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
