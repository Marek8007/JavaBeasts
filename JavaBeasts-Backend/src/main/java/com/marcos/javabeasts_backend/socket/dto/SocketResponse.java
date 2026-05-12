package com.marcos.javabeasts_backend.socket.dto;

import com.google.gson.JsonObject;

public record SocketResponse(
        String status,
        JsonObject data
) {
}
