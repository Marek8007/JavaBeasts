package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleCreatureSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleMoveSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattlePlayerSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Team;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.entity.User;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import com.marcos.javabeasts_backend.repositories.JaBeasTeamedRepository;
import com.marcos.javabeasts_backend.repositories.TeamRepository;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import com.marcos.javabeasts_backend.socket.lobby.LobbyPlayer;
import com.marcos.javabeasts_backend.socket.lobby.LobbyRoom;
import com.marcos.javabeasts_backend.socket.lobby.LobbyRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BattleSetupService {

    private static final int INITIAL_TURN_NUMBER = 1;

    private final LobbyRoomService lobbyRoomService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final JaBeasTeamedRepository jaBeasTeamedRepository;
    private final MoveRepository moveRepository;

    public BattleSetupService(
            LobbyRoomService lobbyRoomService,
            UserRepository userRepository,
            TeamRepository teamRepository,
            JaBeasTeamedRepository jaBeasTeamedRepository,
            MoveRepository moveRepository
    ) {
        this.lobbyRoomService = lobbyRoomService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.jaBeasTeamedRepository = jaBeasTeamedRepository;
        this.moveRepository = moveRepository;
    }

    @Transactional(readOnly = true)
    public BattleSnapshotResponse buildInitialSnapshot(String roomCode) {
        LobbyRoom room = lobbyRoomService.getRoom();
        validateReadyRoom(roomCode, room);

        BattlePlayerSnapshotResponse playerOne = buildPlayerSnapshot(room.getPlayerOne());
        BattlePlayerSnapshotResponse playerTwo = buildPlayerSnapshot(room.getPlayerTwo());

        return new BattleSnapshotResponse(
                room.getRoomCode(),
                INITIAL_TURN_NUMBER,
                "Combate inicializado",
                false,
                null,
                playerOne,
                playerTwo
        );
    }

    private void validateReadyRoom(String roomCode, LobbyRoom room) {
        if (!room.getRoomCode().equals(roomCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La sala indicada no existe");
        }

        if (!room.canStartMatch()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sala todavia no esta lista para iniciar combate");
        }
    }

    private BattlePlayerSnapshotResponse buildPlayerSnapshot(LobbyPlayer lobbyPlayer) {
        User user = userRepository.findByUsername(lobbyPlayer.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario de sala no encontrado"));

        Team activeTeam = teamRepository.findByUserUserIdAndActiveTrue(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "El jugador no tiene un equipo activo"));

        List<JaBeasTeamed> teamMembers = jaBeasTeamedRepository.findByTeamTeamIdOrderByIdSlotAsc(activeTeam.getTeamId());
        if (teamMembers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El equipo activo del jugador no tiene JaBeas asignados");
        }

        JaBeasTeamed activeMember = teamMembers.getFirst();

        return new BattlePlayerSnapshotResponse(
                user.getUserId(),
                user.getUsername(),
                activeTeam.getTeamId(),
                activeTeam.getName(),
                toCreatureSnapshot(activeMember)
        );
    }

    private BattleCreatureSnapshotResponse toCreatureSnapshot(JaBeasTeamed teamMember) {
        JaBeas jaBea = teamMember.getJaBeas();

        return new BattleCreatureSnapshotResponse(
                teamMember.getSlot(),
                jaBea.getJaBeasId(),
                jaBea.getName(),
                jaBea.getHealth(),
                jaBea.getHealth(),
                jaBea.getDamage(),
                jaBea.getDefence(),
                jaBea.getSpeed(),
                toMoveSnapshots(teamMember)
        );
    }

    @Transactional(readOnly = true)
    public BattleCreatureSnapshotResponse buildCreatureSnapshot(Integer teamId, Integer slot) {
        JaBeasTeamed teamMember = jaBeasTeamedRepository.findByTeamTeamIdAndIdSlot(teamId, slot)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El slot de cambio no tiene JaBea asignado"));

        return toCreatureSnapshot(teamMember);
    }

    @Transactional(readOnly = true)
    public List<BattleCreatureSnapshotResponse> buildTeamCreatureSnapshots(Integer teamId) {
        return jaBeasTeamedRepository.findByTeamTeamIdOrderByIdSlotAsc(teamId)
                .stream()
                .map(this::toCreatureSnapshot)
                .toList();
    }

    private List<BattleMoveSnapshotResponse> toMoveSnapshots(JaBeasTeamed teamMember) {
        List<BattleMoveSnapshotResponse> moves = new ArrayList<>();
        addMoveSnapshot(moves, 0, moveRepository.findByUniqueJabea(teamMember.getJaBeas()));
        addMoveSnapshot(moves, 1, teamMember.getMove1());
        addMoveSnapshot(moves, 2, teamMember.getMove2());
        return moves;
    }

    private void addMoveSnapshot(List<BattleMoveSnapshotResponse> moves, int slot, Move move) {
        BattleMoveSnapshotResponse snapshot = toMoveSnapshot(slot, move);
        if (snapshot != null) {
            moves.add(snapshot);
        }
    }

    private BattleMoveSnapshotResponse toMoveSnapshot(int slot, Move move) {
        if (move == null) {
            return null;
        }

        Type type = move.getType();

        return new BattleMoveSnapshotResponse(
                slot,
                move.getMoveId(),
                move.getName(),
                type != null ? type.getTypeId() : null,
                type != null ? type.getType() : null,
                move.getDamage(),
                move.getAccuracy(),
                move.getSpecialEffect()
        );
    }
}
