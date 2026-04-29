package com.marcos.javabeasts_backend.dto.team;

public record MoveSummaryResponse(
        Integer moveId,
        String name,
        Integer typeId,
        String typeName,
        Integer damage,
        Integer accuracy,
        String specialEffect
) {
}
