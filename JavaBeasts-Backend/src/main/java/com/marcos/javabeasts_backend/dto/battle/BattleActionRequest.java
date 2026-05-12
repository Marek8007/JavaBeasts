package com.marcos.javabeasts_backend.dto.battle;

public record BattleActionRequest(
        String roomCode,
        String username,
        String actionType,
        Integer moveSlot,
        Integer switchSlot
) {
}
