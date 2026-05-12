package com.marcos.javabeasts_backend.dto.history;

public record MatchHistoryJaBeaResponse(
        Integer slot,
        Integer jaBeasId,
        String name,
        String move1Name,
        String move2Name
) {
}
