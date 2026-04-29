package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.team.CreateTeamRequest;
import com.marcos.javabeasts_backend.dto.team.TeamResponse;
import com.marcos.javabeasts_backend.dto.team.UpdateTeamRequest;
import com.marcos.javabeasts_backend.services.team.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/user/{userId}")
    public List<TeamResponse> getTeamsByUser(@PathVariable Integer userId) {
        return teamService.getTeamsByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request);
    }

    @PatchMapping("/{teamId}")
    public TeamResponse renameTeam(
            @PathVariable Integer teamId,
            @RequestParam Integer userId,
            @Valid @RequestBody UpdateTeamRequest request
    ) {
        return teamService.renameTeam(userId, teamId, request);
    }

    @PatchMapping("/{teamId}/active")
    public TeamResponse activateTeam(@PathVariable Integer teamId, @RequestParam Integer userId) {
        return teamService.activateTeam(userId, teamId);
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Integer teamId, @RequestParam Integer userId) {
        teamService.deleteTeam(userId, teamId);
    }
}
