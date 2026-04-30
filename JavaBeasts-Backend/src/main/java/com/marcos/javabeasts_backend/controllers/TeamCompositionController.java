package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.team.TeamCompositionResponse;
import com.marcos.javabeasts_backend.dto.team.UpsertTeamSlotRequest;
import com.marcos.javabeasts_backend.services.team.TeamCompositionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams/{teamId}")
public class TeamCompositionController {

    private final TeamCompositionService teamCompositionService;

    public TeamCompositionController(TeamCompositionService teamCompositionService) {
        this.teamCompositionService = teamCompositionService;
    }

    @GetMapping("/composition")
    public TeamCompositionResponse getComposition(@PathVariable Integer teamId, @RequestParam Integer userId) {
        return teamCompositionService.getComposition(userId, teamId);
    }

    @PutMapping("/slots/{slot}")
    public TeamCompositionResponse upsertTeamSlot(
            @PathVariable Integer teamId,
            @PathVariable Integer slot,
            @RequestParam Integer userId,
            @Valid @RequestBody UpsertTeamSlotRequest request
    ) {
        return teamCompositionService.upsertTeamSlot(userId, teamId, slot, request);
    }

    @DeleteMapping("/slots/{slot}")
    public TeamCompositionResponse deleteTeamSlot(
            @PathVariable Integer teamId,
            @PathVariable Integer slot,
            @RequestParam Integer userId
    ) {
        return teamCompositionService.deleteTeamSlot(userId, teamId, slot);
    }
}
