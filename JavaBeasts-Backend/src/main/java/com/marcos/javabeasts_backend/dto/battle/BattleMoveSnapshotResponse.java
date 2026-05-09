package com.marcos.javabeasts_backend.dto.battle;

public record BattleMoveSnapshotResponse(
        Integer slot,
        Integer moveId,
        String name,
        Integer typeId,
        String typeName,
        Integer damage,
        Integer accuracy,
        String specialEffect
) {
}
