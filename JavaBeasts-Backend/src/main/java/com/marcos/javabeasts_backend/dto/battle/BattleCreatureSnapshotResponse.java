package com.marcos.javabeasts_backend.dto.battle;

public record BattleCreatureSnapshotResponse(
        Integer slot,
        Integer jaBeasId,
        String name,
        Integer currentHealth,
        Integer maxHealth,
        Integer damage,
        Integer defence,
        Integer speed
) {
}
