package com.marcos.javabeasts_backend.socket.dto;

import com.google.gson.JsonObject;

public record SocketRequest(
        String code,
        JsonObject data
) {
}
