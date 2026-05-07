package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleCreatureSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattlePlayerSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Team;
import com.marcos.javabeasts_backend.entity.User;
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

import java.util.List;

@Service
public class BattleSetupService {

    private static final int INITIAL_TURN_NUMBER = 1;

    private final LobbyRoomService lobbyRoomService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final JaBeasTeamedRepository jaBeasTeamedRepository;

    public BattleSetupService(
            LobbyRoomService lobbyRoomService,
            UserRepository userRepository,
            TeamRepository teamRepository,
            JaBeasTeamedRepository jaBeasTeamedRepository
    ) {
        this.lobbyRoomService = lobbyRoomService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.jaBeasTeamedRepository = jaBeasTeamedRepository;
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
                jaBea.getSpeed()
        );
    }
}
