package com.marcos.javabeasts_backend.dto.jabeas;

public record JaBeaMoveResponse(
        Integer moveId,
        String name,
        String description,
        Integer typeId,
        String typeName,
        Integer damage,
        Integer accuracy,
        String specialEffect
) {
}
