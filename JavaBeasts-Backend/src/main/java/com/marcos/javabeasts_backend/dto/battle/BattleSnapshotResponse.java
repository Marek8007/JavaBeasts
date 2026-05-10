package com.marcos.javabeasts_backend.dto.battle;

public record BattleSnapshotResponse(
        String roomCode,
        Integer turnNumber,
        String message,
        boolean finished,
        String winnerUsername,
        BattlePlayerSnapshotResponse playerOne,
        BattlePlayerSnapshotResponse playerTwo
) {
}
