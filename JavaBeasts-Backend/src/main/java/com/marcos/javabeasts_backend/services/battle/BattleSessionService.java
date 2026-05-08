package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleActionRequest;
import com.marcos.javabeasts_backend.dto.battle.BattleActionSubmissionResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleCreatureSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattlePlayerSnapshotResponse;
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

        boolean turnResolved = false;
        String message = "Accion registrada. Esperando al rival.";

        if (session.isTurnReadyToResolve()) {
            resolveTurn(session);
            turnResolved = true;
            message = session.getLastResolutionMessage();
        }

        return new BattleActionSubmissionResponse(
                session.getRoomCode(),
                session.getTurnNumber(),
                session.isPlayerOneActionSubmitted(),
                session.isPlayerTwoActionSubmitted(),
                session.isTurnReadyToResolve(),
                turnResolved,
                message,
                session.getCurrentSnapshot()
        );
    }

    public BattleSnapshotResponse getCurrentSnapshot(String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo de sala es obligatorio");
        }

        BattleSession session = sessionsByRoomCode.computeIfAbsent(
                roomCode.trim(),
                this::createSession
        );

        return session.getCurrentSnapshot();
    }

    private BattleSession createSession(String roomCode) {
        BattleSnapshotResponse snapshot = battleSetupService.buildInitialSnapshot(roomCode);
        return new BattleSession(snapshot);
    }

    private void resolveTurn(BattleSession session) {
        BattleTurnAction playerOneAction = session.getPlayerOneAction();
        BattleTurnAction playerTwoAction = session.getPlayerTwoAction();

        if (playerOneAction.actionType() != BattleActionType.ATTACK || playerTwoAction.actionType() != BattleActionType.ATTACK) {
            session.completeTurn("Turno resuelto sin cambios de combate. Los cambios de JaBea se implementaran a continuacion.");
            session.updateSnapshot(withTurnNumber(session.getCurrentSnapshot(), session.getTurnNumber()));
            return;
        }

        BattleSnapshotResponse currentSnapshot = session.getCurrentSnapshot();
        BattlePlayerSnapshotResponse playerOne = currentSnapshot.playerOne();
        BattlePlayerSnapshotResponse playerTwo = currentSnapshot.playerTwo();

        BattleCreatureSnapshotResponse playerOneCreature = playerOne.activeJaBea();
        BattleCreatureSnapshotResponse playerTwoCreature = playerTwo.activeJaBea();

        boolean playerOneActsFirst = actsFirst(playerOneCreature, playerTwoCreature);

        BattlePlayerSnapshotResponse updatedPlayerOne = playerOne;
        BattlePlayerSnapshotResponse updatedPlayerTwo = playerTwo;

        StringBuilder resolution = new StringBuilder();

        if (playerOneActsFirst) {
            updatedPlayerTwo = applyAttack(playerOne, updatedPlayerTwo, resolution);
            if (updatedPlayerTwo.activeJaBea().currentHealth() > 0) {
                updatedPlayerOne = applyAttack(playerTwo, updatedPlayerOne, resolution);
            }
        } else {
            updatedPlayerOne = applyAttack(playerTwo, updatedPlayerOne, resolution);
            if (updatedPlayerOne.activeJaBea().currentHealth() > 0) {
                updatedPlayerTwo = applyAttack(playerOne, updatedPlayerTwo, resolution);
            }
        }

        BattleSnapshotResponse nextSnapshot = new BattleSnapshotResponse(
                currentSnapshot.roomCode(),
                session.getTurnNumber() + 1,
                updatedPlayerOne,
                updatedPlayerTwo
        );

        session.updateSnapshot(nextSnapshot);
        session.completeTurn(resolution.toString().trim());
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

    private boolean actsFirst(BattleCreatureSnapshotResponse playerOneCreature, BattleCreatureSnapshotResponse playerTwoCreature) {
        int playerOneSpeed = playerOneCreature.speed() != null ? playerOneCreature.speed() : 0;
        int playerTwoSpeed = playerTwoCreature.speed() != null ? playerTwoCreature.speed() : 0;
        return playerOneSpeed >= playerTwoSpeed;
    }

    private BattlePlayerSnapshotResponse applyAttack(
            BattlePlayerSnapshotResponse attacker,
            BattlePlayerSnapshotResponse defender,
            StringBuilder resolution
    ) {
        BattleCreatureSnapshotResponse attackerCreature = attacker.activeJaBea();
        BattleCreatureSnapshotResponse defenderCreature = defender.activeJaBea();

        int damage = calculateDamage(attackerCreature, defenderCreature);
        int currentHealth = defenderCreature.currentHealth() != null ? defenderCreature.currentHealth() : 0;
        int newHealth = Math.max(0, currentHealth - damage);

        resolution
                .append(attacker.username())
                .append(" ataca con ")
                .append(attackerCreature.name())
                .append(" y causa ")
                .append(damage)
                .append(" de daño a ")
                .append(defender.username())
                .append(". ");

        if (newHealth == 0) {
            resolution.append(defenderCreature.name()).append(" queda debilitado. ");
        }

        BattleCreatureSnapshotResponse updatedCreature = new BattleCreatureSnapshotResponse(
                defenderCreature.slot(),
                defenderCreature.jaBeasId(),
                defenderCreature.name(),
                newHealth,
                defenderCreature.maxHealth(),
                defenderCreature.damage(),
                defenderCreature.defence(),
                defenderCreature.speed()
        );

        return new BattlePlayerSnapshotResponse(
                defender.userId(),
                defender.username(),
                defender.teamId(),
                defender.teamName(),
                updatedCreature
        );
    }

    private int calculateDamage(BattleCreatureSnapshotResponse attacker, BattleCreatureSnapshotResponse defender) {
        int attackerDamage = attacker.damage() != null ? attacker.damage() : 0;
        int defenderDefence = defender.defence() != null ? defender.defence() : 0;
        return Math.max(1, attackerDamage - (defenderDefence / 2));
    }

    private BattleSnapshotResponse withTurnNumber(BattleSnapshotResponse snapshot, int turnNumber) {
        return new BattleSnapshotResponse(
                snapshot.roomCode(),
                turnNumber + 1,
                snapshot.playerOne(),
                snapshot.playerTwo()
        );
    }
}
