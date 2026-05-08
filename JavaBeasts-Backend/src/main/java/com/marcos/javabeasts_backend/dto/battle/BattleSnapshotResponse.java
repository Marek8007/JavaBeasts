package com.marcos.javabeasts_backend.dto.battle;

public record BattleSnapshotResponse(
        String roomCode,
        Integer turnNumber,
        String message,
        BattlePlayerSnapshotResponse playerOne,
        BattlePlayerSnapshotResponse playerTwo
) {
}
