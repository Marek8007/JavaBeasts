package com.marcos.javabeasts_backend.dto.team;

import jakarta.validation.constraints.NotNull;

public record UpsertTeamSlotRequest(
        @NotNull
        Integer jaBeasId,

        @NotNull
        Integer move1Id,

        @NotNull
        Integer move2Id
) {
}
