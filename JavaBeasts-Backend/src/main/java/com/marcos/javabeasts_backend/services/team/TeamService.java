package com.marcos.javabeasts_backend.services.team;

import com.marcos.javabeasts_backend.dto.team.CreateTeamRequest;
import com.marcos.javabeasts_backend.dto.team.TeamResponse;
import com.marcos.javabeasts_backend.dto.team.UpdateTeamRequest;
import com.marcos.javabeasts_backend.entity.Team;
import com.marcos.javabeasts_backend.entity.User;
import com.marcos.javabeasts_backend.repositories.TeamRepository;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {

    private static final long MAX_TEAMS_PER_USER = 10;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsByUser(Integer userId) {
        ensureUserExists(userId);
        return teamRepository.findByUserUserIdOrderByTeamIdAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request) {
        User user = getUserOrThrow(request.userId());

        if (teamRepository.countByUserUserId(user.getUserId()) >= MAX_TEAMS_PER_USER) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya tiene el maximo de equipos permitido");
        }

        Team team = new Team();
        team.setUser(user);
        team.setName(request.name().trim());
        team.setActive(teamRepository.findByUserUserIdAndActiveTrue(user.getUserId()).isEmpty());

        return toResponse(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse renameTeam(Integer userId, Integer teamId, UpdateTeamRequest request) {
        Team team = getTeamOrThrow(userId, teamId);
        team.setName(request.name().trim());
        return toResponse(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse activateTeam(Integer userId, Integer teamId) {
        Team selectedTeam = getTeamOrThrow(userId, teamId);
        List<Team> userTeams = teamRepository.findByUserUserIdOrderByTeamIdAsc(userId);

        for (Team team : userTeams) {
            team.setActive(team.getTeamId() == selectedTeam.getTeamId());
        }

        teamRepository.saveAll(userTeams);

        return toResponse(selectedTeam);
    }

    @Transactional
    public void deleteTeam(Integer userId, Integer teamId) {
        Team team = getTeamOrThrow(userId, teamId);
        boolean wasActive = team.isActive();

        teamRepository.delete(team);

        if (wasActive) {
            List<Team> remainingTeams = teamRepository.findByUserUserIdOrderByTeamIdAsc(userId);
            if (!remainingTeams.isEmpty()) {
                Team nextActive = remainingTeams.getFirst();
                nextActive.setActive(true);
                teamRepository.save(nextActive);
            }
        }
    }

    private void ensureUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
    }

    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Team getTeamOrThrow(Integer userId, Integer teamId) {
        return teamRepository.findByTeamIdAndUserUserId(teamId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));
    }

    private TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getTeamId(),
                team.getUser().getUserId(),
                team.getName(),
                team.isActive()
        );
    }
}
