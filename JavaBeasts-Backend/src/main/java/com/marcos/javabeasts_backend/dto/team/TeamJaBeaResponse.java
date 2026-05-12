package com.marcos.javabeasts_backend.dto.team;

public record TeamJaBeaResponse(
        Integer jaBeasId,
        String name,
        Integer typeId,
        String typeName,
        MoveSummaryResponse uniqueMove,
        MoveSummaryResponse move1,
        MoveSummaryResponse move2
) {
}
