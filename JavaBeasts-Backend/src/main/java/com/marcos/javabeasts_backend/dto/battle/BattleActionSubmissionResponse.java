package com.marcos.javabeasts_backend.dto.battle;

public record BattleActionSubmissionResponse(
        String roomCode,
        Integer turnNumber,
        boolean playerOneActionSubmitted,
        boolean playerTwoActionSubmitted,
        boolean turnReadyToResolve,
        boolean turnResolved,
        String message,
        BattleSnapshotResponse snapshot
) {
}
