package com.marcos.javabeasts_backend.dto.team;

public record TeamResponse(
        Integer teamId,
        Integer userId,
        String name,
        String iconName,
        boolean active
) {
}
