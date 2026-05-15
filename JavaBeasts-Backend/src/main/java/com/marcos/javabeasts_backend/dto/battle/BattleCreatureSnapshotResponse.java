package com.marcos.javabeasts_backend.dto.battle;

import java.util.List;

public record BattleCreatureSnapshotResponse(
        Integer slot,
        Integer jaBeasId,
        String name,
        Integer typeId,
        String typeName,
        Integer currentHealth,
        Integer maxHealth,
        Integer damage,
        Integer defence,
        Integer speed,
        List<BattleMoveSnapshotResponse> moves
) {
}
