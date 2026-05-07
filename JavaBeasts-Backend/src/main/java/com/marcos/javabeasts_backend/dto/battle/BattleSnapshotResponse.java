package com.marcos.javabeasts_backend.dto.battle;

public record BattleSnapshotResponse(
        String roomCode,
        Integer turnNumber,
        BattlePlayerSnapshotResponse playerOne,
        BattlePlayerSnapshotResponse playerTwo
) {
}
