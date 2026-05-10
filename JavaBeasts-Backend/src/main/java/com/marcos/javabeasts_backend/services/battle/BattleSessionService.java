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

        synchronized (session) {
            String username = request.username().trim();
            if (!session.containsPlayer(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El jugador no pertenece a la sesion de combate");
            }

            BattleTurnAction action = buildAction(request, username, session);
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

        BattleSnapshotResponse currentSnapshot = session.getCurrentSnapshot();
        BattlePlayerSnapshotResponse playerOne = currentSnapshot.playerOne();
        BattlePlayerSnapshotResponse playerTwo = currentSnapshot.playerTwo();

        StringBuilder resolution = new StringBuilder();

        if (playerOneAction.actionType() == BattleActionType.SWITCH) {
            playerOne = switchActiveCreature(session, playerOne, playerOneAction.switchSlot(), resolution);
        }

        if (playerTwoAction.actionType() == BattleActionType.SWITCH) {
            playerTwo = switchActiveCreature(session, playerTwo, playerTwoAction.switchSlot(), resolution);
        }

        if (playerOneAction.actionType() == BattleActionType.SWITCH && playerTwoAction.actionType() == BattleActionType.SWITCH) {
            completeResolvedTurn(session, currentSnapshot, playerOne, playerTwo, resolution);
            return;
        }

        BattleCreatureSnapshotResponse playerOneCreature = playerOne.activeJaBea();
        BattleCreatureSnapshotResponse playerTwoCreature = playerTwo.activeJaBea();

        BattlePlayerSnapshotResponse updatedPlayerOne = playerOne;
        BattlePlayerSnapshotResponse updatedPlayerTwo = playerTwo;

        if (playerOneAction.actionType() == BattleActionType.SWITCH) {
            updatedPlayerOne = applyAttack(playerTwo, updatedPlayerOne, resolution);
            updatedPlayerTwo = playerTwo;
        } else if (playerTwoAction.actionType() == BattleActionType.SWITCH) {
            updatedPlayerOne = playerOne;
            updatedPlayerTwo = applyAttack(playerOne, updatedPlayerTwo, resolution);
        } else {
            boolean playerOneActsFirst = actsFirst(playerOneCreature, playerTwoCreature);

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
        }

        completeResolvedTurn(session, currentSnapshot, updatedPlayerOne, updatedPlayerTwo, resolution);
    }

    private void completeResolvedTurn(
            BattleSession session,
            BattleSnapshotResponse currentSnapshot,
            BattlePlayerSnapshotResponse updatedPlayerOne,
            BattlePlayerSnapshotResponse updatedPlayerTwo,
            StringBuilder resolution
    ) {
        String message = resolution.toString().trim();

        BattleSnapshotResponse nextSnapshot = new BattleSnapshotResponse(
                currentSnapshot.roomCode(),
                session.getTurnNumber() + 1,
                message,
                updatedPlayerOne,
                updatedPlayerTwo
        );

        session.updateSnapshot(nextSnapshot);
        session.completeTurn(message);
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

    private BattleTurnAction buildAction(BattleActionRequest request, String username, BattleSession session) {
        BattleActionType actionType = parseActionType(request.actionType());
        BattlePlayerSnapshotResponse player = findPlayerSnapshot(session.getCurrentSnapshot(), username);
        BattleCreatureSnapshotResponse activeCreature = player.activeJaBea();

        return switch (actionType) {
            case ATTACK -> {
                if (activeCreature.currentHealth() != null && activeCreature.currentHealth() <= 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El JaBea activo esta debilitado. Debes cambiar de JaBea");
                }

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

                if (switchSlot.equals(activeCreature.slot())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El JaBea elegido ya esta activo");
                }

                BattleCreatureSnapshotResponse switchCreature = battleSetupService.buildCreatureSnapshot(player.teamId(), switchSlot);
                int switchHealth = session.getStoredHealth(username, switchSlot)
                        .orElse(switchCreature.maxHealth() != null ? switchCreature.maxHealth() : 0);
                if (switchHealth <= 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes cambiar a un JaBea debilitado");
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
                defenderCreature.speed(),
                defenderCreature.moves()
        );

        return new BattlePlayerSnapshotResponse(
                defender.userId(),
                defender.username(),
                defender.teamId(),
                defender.teamName(),
                updatedCreature
        );
    }

    private BattlePlayerSnapshotResponse switchActiveCreature(
            BattleSession session,
            BattlePlayerSnapshotResponse player,
            Integer switchSlot,
            StringBuilder resolution
    ) {
        session.rememberCreatureHealth(player.username(), player.activeJaBea());

        BattleCreatureSnapshotResponse switchCreature = battleSetupService.buildCreatureSnapshot(player.teamId(), switchSlot);
        int switchHealth = session.getStoredHealth(player.username(), switchSlot)
                .orElse(switchCreature.maxHealth() != null ? switchCreature.maxHealth() : 0);
        BattleCreatureSnapshotResponse activeCreature = withCurrentHealth(switchCreature, switchHealth);

        resolution
                .append(player.username())
                .append(" cambia a ")
                .append(activeCreature.name())
                .append(". ");

        return new BattlePlayerSnapshotResponse(
                player.userId(),
                player.username(),
                player.teamId(),
                player.teamName(),
                activeCreature
        );
    }

    private BattlePlayerSnapshotResponse findPlayerSnapshot(BattleSnapshotResponse snapshot, String username) {
        if (snapshot.playerOne().username().equals(username)) {
            return snapshot.playerOne();
        }

        if (snapshot.playerTwo().username().equals(username)) {
            return snapshot.playerTwo();
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El jugador no pertenece a la sesion de combate");
    }

    private BattleCreatureSnapshotResponse withCurrentHealth(BattleCreatureSnapshotResponse creature, int currentHealth) {
        return new BattleCreatureSnapshotResponse(
                creature.slot(),
                creature.jaBeasId(),
                creature.name(),
                currentHealth,
                creature.maxHealth(),
                creature.damage(),
                creature.defence(),
                creature.speed(),
                creature.moves()
        );
    }

    private int calculateDamage(BattleCreatureSnapshotResponse attacker, BattleCreatureSnapshotResponse defender) {
        int attackerDamage = attacker.damage() != null ? attacker.damage() : 0;
        int defenderDefence = defender.defence() != null ? defender.defence() : 0;
        return Math.max(1, attackerDamage - (defenderDefence / 2));
    }

}
