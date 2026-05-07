package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleActionRequest;
import com.marcos.javabeasts_backend.dto.battle.BattleActionSubmissionResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BattleSessionService {

    private static final int MIN_ATTACK_SLOT = 0;
    private static final int MAX_ATTACK_SLOT = 2;
    private static final int MIN_SWITCH_SLOT = 1;
    private static final int MAX_SWITCH_SLOT = 4;

    private final BattleSetupService battleSetupService;
    private final Map<String, BattleSession> sessionsByRoomCode = new ConcurrentHashMap<>();

    public BattleSessionService(BattleSetupService battleSetupService) {
        this.battleSetupService = battleSetupService;
    }

    public BattleActionSubmissionResponse submitAction(BattleActionRequest request) {
        validateRequest(request);

        BattleSession session = sessionsByRoomCode.computeIfAbsent(
                request.roomCode().trim(),
                this::createSession
        );

        String username = request.username().trim();
        if (!session.containsPlayer(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El jugador no pertenece a la sesion de combate");
        }

        BattleTurnAction action = buildAction(request, username);
        session.registerAction(action);

        return new BattleActionSubmissionResponse(
                session.getRoomCode(),
                session.getTurnNumber(),
                session.isPlayerOneActionSubmitted(),
                session.isPlayerTwoActionSubmitted(),
                session.isTurnReadyToResolve()
        );
    }

    private BattleSession createSession(String roomCode) {
        BattleSnapshotResponse snapshot = battleSetupService.buildInitialSnapshot(roomCode);
        return new BattleSession(snapshot);
    }

    private void validateRequest(BattleActionRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La accion de combate es obligatoria");
        }

        if (request.roomCode() == null || request.roomCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo de sala es obligatorio");
        }

        if (request.username() == null || request.username().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        }

        if (request.actionType() == null || request.actionType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de accion es obligatorio");
        }
    }

    private BattleTurnAction buildAction(BattleActionRequest request, String username) {
        BattleActionType actionType = parseActionType(request.actionType());

        return switch (actionType) {
            case ATTACK -> {
                Integer moveSlot = request.moveSlot();
                if (moveSlot == null || moveSlot < MIN_ATTACK_SLOT || moveSlot > MAX_ATTACK_SLOT) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El slot de ataque debe estar entre 0 y 2");
                }
                yield new BattleTurnAction(username, actionType, moveSlot, null);
            }
            case SWITCH -> {
                Integer switchSlot = request.switchSlot();
                if (switchSlot == null || switchSlot < MIN_SWITCH_SLOT || switchSlot > MAX_SWITCH_SLOT) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El slot de cambio debe estar entre 1 y 4");
                }
                yield new BattleTurnAction(username, actionType, null, switchSlot);
            }
        };
    }

    private BattleActionType parseActionType(String actionType) {
        try {
            return BattleActionType.valueOf(actionType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de accion no es valido");
        }
    }
}
