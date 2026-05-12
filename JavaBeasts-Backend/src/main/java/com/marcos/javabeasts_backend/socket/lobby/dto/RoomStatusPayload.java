package com.marcos.javabeasts_backend.socket.lobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomStatusPayload {
    private String roomCode;
    private boolean full;
    private boolean canStart;
    private PlayerSlotPayload playerOne;
    private PlayerSlotPayload playerTwo;
}
