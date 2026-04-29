package com.marcos.javabeasts_backend.dto.team;

import java.util.List;

public record TeamCompositionResponse(
        Integer teamId,
        Integer userId,
        String name,
        boolean active,
        List<TeamSlotResponse> slots
) {
}
