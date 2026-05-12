package com.marcos.javabeasts_backend.dto.battle;

public record BattlePlayerSnapshotResponse(
        Integer userId,
        String username,
        Integer teamId,
        String teamName,
        BattleCreatureSnapshotResponse activeJaBea
) {
}
