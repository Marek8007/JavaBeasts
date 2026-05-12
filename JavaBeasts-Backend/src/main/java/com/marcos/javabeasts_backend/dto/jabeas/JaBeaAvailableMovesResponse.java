package com.marcos.javabeasts_backend.dto.jabeas;

import java.util.List;

public record JaBeaAvailableMovesResponse(
        Integer jaBeasId,
        String name,
        Integer typeId,
        String typeName,
        JaBeaMoveResponse uniqueMove,
        List<JaBeaMoveResponse> configurableMoves
) {
}
