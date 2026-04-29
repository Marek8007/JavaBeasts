package com.marcos.javabeasts_backend.dto.auth;

public record AuthResponse(
        Integer userId,
        String username,
        boolean logged,
        int matchesWon,
        int matchesLost,
        String message
) {
}
