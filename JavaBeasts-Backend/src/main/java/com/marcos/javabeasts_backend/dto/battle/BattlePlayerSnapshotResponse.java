package com.marcos.javabeasts_backend.dto.battle;

import java.util.List;

public record BattlePlayerSnapshotResponse(
        Integer userId,
        String username,
        Integer teamId,
        String teamName,
        BattleCreatureSnapshotResponse activeJaBea,
        List<BattleCreatureSnapshotResponse> teamCreatures
) {
}
