package com.marcos.javabeasts_backend.dto.team;

public record TeamSlotResponse(
        Integer slot,
        TeamJaBeaResponse member
) {
}
