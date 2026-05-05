package com.marcos.javabeasts_backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotNull
        Integer userId,

        @NotBlank
        @Size(min = 3, max = 50)
        String name,

        @Size(max = 60)
        String iconName
) {
}
