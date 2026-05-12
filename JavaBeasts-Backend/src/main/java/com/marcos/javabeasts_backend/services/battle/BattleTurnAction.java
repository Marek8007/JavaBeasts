package com.marcos.javabeasts_backend.services.battle;

public record BattleTurnAction(
        String username,
        BattleActionType actionType,
        Integer moveSlot,
        Integer switchSlot
) {
}
