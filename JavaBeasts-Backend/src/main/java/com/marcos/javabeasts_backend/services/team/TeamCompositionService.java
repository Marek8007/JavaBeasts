package com.marcos.javabeasts_backend.services.team;

import com.marcos.javabeasts_backend.dto.team.MoveSummaryResponse;
import com.marcos.javabeasts_backend.dto.team.TeamCompositionResponse;
import com.marcos.javabeasts_backend.dto.team.TeamJaBeaResponse;
import com.marcos.javabeasts_backend.dto.team.TeamSlotResponse;
import com.marcos.javabeasts_backend.dto.team.UpsertTeamSlotRequest;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Team;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeamedId;
import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.JaBeasTeamedRepository;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import com.marcos.javabeasts_backend.repositories.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeamCompositionService {

    private static final int MIN_SLOT = 1;
    private static final int MAX_SLOT = 4;

    private static final Map<String, String> MIRROR_TYPES = Map.of(
            "Fuego", "Electrico",
            "Electrico", "Fuego",
            "Agua", "Planta",
            "Planta", "Agua"
    );

    private final TeamRepository teamRepository;
    private final JaBeasTeamedRepository jaBeasTeamedRepository;
    private final JaBeaRepository jaBeaRepository;
    private final MoveRepository moveRepository;

    public TeamCompositionService(
            TeamRepository teamRepository,
            JaBeasTeamedRepository jaBeasTeamedRepository,
            JaBeaRepository jaBeaRepository,
            MoveRepository moveRepository
    ) {
        this.teamRepository = teamRepository;
        this.jaBeasTeamedRepository = jaBeasTeamedRepository;
        this.jaBeaRepository = jaBeaRepository;
        this.moveRepository = moveRepository;
    }

    @Transactional(readOnly = true)
    public TeamCompositionResponse getComposition(Integer userId, Integer teamId) {
        Team team = getTeamOrThrow(userId, teamId);
        return toCompositionResponse(team);
    }

    @Transactional
    public TeamCompositionResponse upsertTeamSlot(Integer userId, Integer teamId, Integer slot, UpsertTeamSlotRequest request) {
        validateSlot(slot);

        Team team = getTeamOrThrow(userId, teamId);

        JaBeas jaBea = jaBeaRepository.findById(request.jaBeasId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JaBea no encontrado"));

        Move move1 = moveRepository.findById(request.move1Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento 1 no encontrado"));
        Move move2 = moveRepository.findById(request.move2Id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento 2 no encontrado"));

        validateMovesForJaBea(jaBea, move1, move2);

        JaBeasTeamed teamSlot = jaBeasTeamedRepository.findByTeamTeamIdAndIdSlot(teamId, slot)
                .orElseGet(() -> createTeamSlot(team, slot));

        teamSlot.setJaBeas(jaBea);
        teamSlot.setMove1(move1);
        teamSlot.setMove2(move2);

        jaBeasTeamedRepository.save(teamSlot);

        return toCompositionResponse(team);
    }

    @Transactional
    public TeamCompositionResponse deleteTeamSlot(Integer userId, Integer teamId, Integer slot) {
        validateSlot(slot);

        Team team = getTeamOrThrow(userId, teamId);
        JaBeasTeamed teamSlot = jaBeasTeamedRepository.findByTeamTeamIdAndIdSlot(teamId, slot)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot vacio o no encontrado"));

        jaBeasTeamedRepository.delete(teamSlot);

        return toCompositionResponse(team);
    }

    private Team getTeamOrThrow(Integer userId, Integer teamId) {
        return teamRepository.findByTeamIdAndUserUserId(teamId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipo no encontrado"));
    }

    private void validateSlot(Integer slot) {
        if (slot == null || slot < MIN_SLOT || slot > MAX_SLOT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El slot debe estar entre 1 y 4");
        }
    }

    private JaBeasTeamed createTeamSlot(Team team, Integer slot) {
        JaBeaTeamedId id = new JaBeaTeamedId();
        id.setTeamId(team.getTeamId());
        id.setSlot(slot);

        JaBeasTeamed teamSlot = new JaBeasTeamed();
        teamSlot.setId(id);
        teamSlot.setTeam(team);
        teamSlot.setSlot(slot);
        return teamSlot;
    }

    private void validateMovesForJaBea(JaBeas jaBea, Move move1, Move move2) {
        if (move1.getMoveId() == move2.getMoveId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los dos movimientos elegidos deben ser distintos");
        }

        validateSingleMoveForJaBea(jaBea, move1, "Movimiento 1");
        validateSingleMoveForJaBea(jaBea, move2, "Movimiento 2");
    }

    private void validateSingleMoveForJaBea(JaBeas jaBea, Move move, String label) {
        if (move.getUniqueJabea() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " no puede ser un movimiento unico");
        }

        String jaBeaType = jaBea.getType().getType();
        String moveType = move.getType().getType();
        String mirrorType = MIRROR_TYPES.get(jaBeaType);

        boolean allowed = moveType.equals(jaBeaType)
                || moveType.equals("Normal")
                || moveType.equals(mirrorType);

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    label + " no es valido para un JaBea de tipo " + jaBeaType
            );
        }
    }

    private TeamCompositionResponse toCompositionResponse(Team team) {
        List<JaBeasTeamed> configuredSlots = jaBeasTeamedRepository.findByTeamTeamIdOrderByIdSlotAsc(team.getTeamId());
        Map<Integer, JaBeasTeamed> bySlot = new HashMap<>();

        for (JaBeasTeamed configuredSlot : configuredSlots) {
            bySlot.put(configuredSlot.getSlot(), configuredSlot);
        }

        List<TeamSlotResponse> slots = List.of(
                toSlotResponse(1, bySlot.get(1)),
                toSlotResponse(2, bySlot.get(2)),
                toSlotResponse(3, bySlot.get(3)),
                toSlotResponse(4, bySlot.get(4))
        );

        return new TeamCompositionResponse(
                team.getTeamId(),
                team.getUser().getUserId(),
                team.getName(),
                team.isActive(),
                slots
        );
    }

    private TeamSlotResponse toSlotResponse(int slot, JaBeasTeamed configuredSlot) {
        return new TeamSlotResponse(
                slot,
                configuredSlot == null ? null : new TeamJaBeaResponse(
                        configuredSlot.getJaBeas().getJaBeasId(),
                        configuredSlot.getJaBeas().getName(),
                        configuredSlot.getJaBeas().getType().getTypeId(),
                        configuredSlot.getJaBeas().getType().getType(),
                        toMoveSummary(moveRepository.findByUniqueJabea(configuredSlot.getJaBeas())),
                        toMoveSummary(configuredSlot.getMove1()),
                        toMoveSummary(configuredSlot.getMove2())
                )
        );
    }

    private MoveSummaryResponse toMoveSummary(Move move) {
        if (move == null) {
            return null;
        }

        Type type = move.getType();

        return new MoveSummaryResponse(
                move.getMoveId(),
                move.getName(),
                type.getTypeId(),
                type.getType(),
                move.getDamage(),
                move.getAccuracy(),
                move.getSpecialEffect()
        );
    }
}
