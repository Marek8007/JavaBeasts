package com.marcos.javabeasts_backend.dto.history;

import java.util.List;

public record MatchHistoryResponse(
        Integer matchId,
        boolean won,
        String opponentUsername,
        Integer turns,
        List<MatchHistoryJaBeaResponse> team
) {
}
