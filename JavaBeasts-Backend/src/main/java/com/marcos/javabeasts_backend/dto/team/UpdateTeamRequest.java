package com.marcos.javabeasts_backend.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String name
) {
}
