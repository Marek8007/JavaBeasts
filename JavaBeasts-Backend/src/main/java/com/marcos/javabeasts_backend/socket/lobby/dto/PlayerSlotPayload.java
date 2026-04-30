package com.marcos.javabeasts_backend.socket.lobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerSlotPayload {
    private String username;
    private boolean ready;
}
