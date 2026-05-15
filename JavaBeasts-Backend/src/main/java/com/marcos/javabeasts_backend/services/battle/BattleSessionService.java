package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleActionRequest;
import com.marcos.javabeasts_backend.dto.battle.BattleActionSubmissionResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleCreatureSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleMoveSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattlePlayerSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.entity.JaBeasHistory;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.MatchHistory;
import com.marcos.javabeasts_backend.entity.User;
import com.marcos.javabeasts_backend.repositories.JaBeasHistoryRepository;
import com.marcos.javabeasts_backend.repositories.JaBeasTeamedRepository;
import com.marcos.javabeasts_backend.repositories.MatchHistoryRepository;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

@Service
public class BattleSessionService {

    private static final int MIN_ATTACK_SLOT = 0;
    private static final int MAX_ATTACK_SLOT = 2;
    private static final int MIN_SWITCH_SLOT = 1;
    private static final int MAX_SWITCH_SLOT = 4;

    private final BattleSetupService battleSetupService;
    private final UserRepository userRepository;
    private final MatchHistoryRepository matchHistoryRepository;
    private final JaBeasTeamedRepository jaBeasTeamedRepository;
    private final JaBeasHistoryRepository jaBeasHistoryRepository;
    private final Map<String, BattleSession> sessionsByRoomCode = new ConcurrentHashMap<>();

    public BattleSessionService(
            BattleSetupService battleSetupService,
            UserRepository userRepository,
            MatchHistoryRepository matchHistoryRepository,
            JaBeasTeamedRepository jaBeasTeamedRepository,
            JaBeasHistoryRepository jaBeasHistoryRepository
    ) {
        this.battleSetupService = battleSetupService;
        this.userRepository = userRepository;
        this.matchHistoryRepository = matchHistoryRepository;
        this.jaBeasTeamedRepository = jaBeasTeamedRepository;
        this.jaBeasHistoryRepository = jaBeasHistoryRepository;
    }

    @Transactional
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

            if (session.getCurrentSnapshot().finished()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El combate ya ha terminado");
            }

            BattleTurnAction action = buildAction(request, username, session);
            session.registerAction(action);

            boolean turnResolved = false;
            String message = "Accion registrada. Esperando al rival.";

            if (action.actionType() == BattleActionType.SURRENDER) {
                resolveSurrender(session, username);
                turnResolved = true;
                message = session.getLastResolutionMessage();
            } else if (session.isTurnReadyToResolve()) {
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

    public void resetBattle(String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo de sala es obligatorio");
        }

        BattleSession session = sessionsByRoomCode.get(roomCode.trim());
        if (session != null && !session.getCurrentSnapshot().finished()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El combate todavia no ha terminado");
        }

        sessionsByRoomCode.remove(roomCode.trim());
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
            updatedPlayerOne = applyAttack(session, playerTwo, updatedPlayerOne, playerTwoAction, resolution);
            updatedPlayerTwo = playerTwo;
        } else if (playerTwoAction.actionType() == BattleActionType.SWITCH) {
            updatedPlayerOne = playerOne;
            updatedPlayerTwo = applyAttack(session, playerOne, updatedPlayerTwo, playerOneAction, resolution);
        } else {
            boolean playerOneActsFirst = actsFirst(playerOneCreature, playerTwoCreature);

            if (playerOneActsFirst) {
                updatedPlayerTwo = applyAttack(session, playerOne, updatedPlayerTwo, playerOneAction, resolution);
                if (updatedPlayerTwo.activeJaBea().currentHealth() > 0) {
                    updatedPlayerOne = applyAttack(session, playerTwo, updatedPlayerOne, playerTwoAction, resolution);
                }
            } else {
                updatedPlayerOne = applyAttack(session, playerTwo, updatedPlayerOne, playerTwoAction, resolution);
                if (updatedPlayerOne.activeJaBea().currentHealth() > 0) {
                    updatedPlayerTwo = applyAttack(session, playerOne, updatedPlayerTwo, playerOneAction, resolution);
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
        boolean playerOneDefeated = !hasAnyAliveCreature(session, updatedPlayerOne);
        boolean playerTwoDefeated = !hasAnyAliveCreature(session, updatedPlayerTwo);
        boolean finished = playerOneDefeated || playerTwoDefeated;
        String winnerUsername = null;

        if (finished) {
            if (playerOneDefeated && playerTwoDefeated) {
                message = appendMessage(message, "El combate termina en empate.");
            } else if (playerOneDefeated) {
                winnerUsername = updatedPlayerTwo.username();
                message = appendMessage(message, updatedPlayerTwo.username() + " gana el combate.");
            } else {
                winnerUsername = updatedPlayerOne.username();
                message = appendMessage(message, updatedPlayerOne.username() + " gana el combate.");
            }
        }

        BattleSnapshotResponse nextSnapshot = new BattleSnapshotResponse(
                currentSnapshot.roomCode(),
                session.getTurnNumber() + 1,
                message,
                finished,
                winnerUsername,
                updatedPlayerOne,
                updatedPlayerTwo
        );

        session.updateSnapshot(nextSnapshot);
        session.completeTurn(message);
        if (finished) {
            persistMatchResultIfNeeded(session, nextSnapshot);
        }
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
                findSelectedMove(activeCreature, moveSlot);
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
            case SURRENDER -> new BattleTurnAction(username, actionType, null, null);
        };
    }

    private BattleActionType parseActionType(String actionType) {
        try {
            return BattleActionType.valueOf(actionType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de accion no es valido");
        }
    }

    private void resolveSurrender(BattleSession session, String surrenderUsername) {
        BattleSnapshotResponse currentSnapshot = session.getCurrentSnapshot();
        BattlePlayerSnapshotResponse playerOne = currentSnapshot.playerOne();
        BattlePlayerSnapshotResponse playerTwo = currentSnapshot.playerTwo();
        String winnerUsername = playerOne.username().equals(surrenderUsername)
                ? playerTwo.username()
                : playerOne.username();
        String message = surrenderUsername + " se ha rendido. " + winnerUsername + " gana el combate.";

        BattleSnapshotResponse nextSnapshot = new BattleSnapshotResponse(
                currentSnapshot.roomCode(),
                session.getTurnNumber() + 1,
                message,
                true,
                winnerUsername,
                playerOne,
                playerTwo
        );

        session.updateSnapshot(nextSnapshot);
        session.completeTurn(message);
        persistMatchResultIfNeeded(session, nextSnapshot);
    }

    private boolean actsFirst(BattleCreatureSnapshotResponse playerOneCreature, BattleCreatureSnapshotResponse playerTwoCreature) {
        int playerOneSpeed = playerOneCreature.speed() != null ? playerOneCreature.speed() : 0;
        int playerTwoSpeed = playerTwoCreature.speed() != null ? playerTwoCreature.speed() : 0;
        if (playerOneSpeed == playerTwoSpeed) {
            return ThreadLocalRandom.current().nextBoolean();
        }

        return playerOneSpeed > playerTwoSpeed;
    }

    private BattlePlayerSnapshotResponse applyAttack(
            BattleSession session,
            BattlePlayerSnapshotResponse attacker,
            BattlePlayerSnapshotResponse defender,
            BattleTurnAction action,
            StringBuilder resolution
    ) {
        BattleCreatureSnapshotResponse attackerCreature = attacker.activeJaBea();
        BattleCreatureSnapshotResponse defenderCreature = defender.activeJaBea();
        BattleMoveSnapshotResponse selectedMove = findSelectedMove(attackerCreature, action.moveSlot());

        if (!moveHits(selectedMove)) {
            resolution
                    .append(attacker.username())
                    .append(" usa ")
                    .append(selectedMove.name())
                    .append(", pero falla. ");
            return defender;
        }

        int damage = calculateDamage(selectedMove, defenderCreature);
        int currentHealth = defenderCreature.currentHealth() != null ? defenderCreature.currentHealth() : 0;
        int newHealth = Math.max(0, currentHealth - damage);

        resolution
                .append(attacker.username())
                .append(" usa ")
                .append(selectedMove.name())
                .append(" y causa ")
                .append(damage)
                .append(" de daño a ")
                .append(defender.username())
                .append(". ");

        double typeMultiplier = resolveTypeMultiplier(selectedMove.typeName(), defenderCreature.typeName());
        if (typeMultiplier > 1d) {
            resolution.append("Es muy eficaz. ");
        } else if (typeMultiplier < 1d) {
            resolution.append("No es muy eficaz. ");
        }

        if (newHealth == 0) {
            resolution.append(defenderCreature.name()).append(" queda debilitado. ");
        }

        BattleCreatureSnapshotResponse updatedCreature = new BattleCreatureSnapshotResponse(
                defenderCreature.slot(),
                defenderCreature.jaBeasId(),
                defenderCreature.name(),
                defenderCreature.typeId(),
                defenderCreature.typeName(),
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
                updatedCreature,
                buildTeamCreaturesSnapshot(session, defender.username(), defender.teamId(), updatedCreature)
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
                activeCreature,
                buildTeamCreaturesSnapshot(session, player.username(), player.teamId(), activeCreature)
        );
    }

    private boolean hasAnyAliveCreature(BattleSession session, BattlePlayerSnapshotResponse player) {
        session.rememberCreatureHealth(player.username(), player.activeJaBea());

        return battleSetupService.buildTeamCreatureSnapshots(player.teamId())
                .stream()
                .anyMatch(creature -> {
                    int currentHealth = session.getStoredHealth(player.username(), creature.slot())
                            .orElse(creature.maxHealth() != null ? creature.maxHealth() : 0);
                    return currentHealth > 0;
                });
    }

    private String appendMessage(String currentMessage, String extraMessage) {
        if (currentMessage == null || currentMessage.isBlank()) {
            return extraMessage;
        }

        return currentMessage + " " + extraMessage;
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
                creature.typeId(),
                creature.typeName(),
                currentHealth,
                creature.maxHealth(),
                creature.damage(),
                creature.defence(),
                creature.speed(),
                creature.moves()
        );
    }

    private List<BattleCreatureSnapshotResponse> buildTeamCreaturesSnapshot(
            BattleSession session,
            String username,
            Integer teamId,
            BattleCreatureSnapshotResponse activeCreature
    ) {
        return battleSetupService.buildTeamCreatureSnapshots(teamId)
                .stream()
                .map(creature -> {
                    if (activeCreature != null && activeCreature.slot() != null && activeCreature.slot().equals(creature.slot())) {
                        return activeCreature;
                    }

                    int currentHealth = session.getStoredHealth(username, creature.slot())
                            .orElse(creature.maxHealth() != null ? creature.maxHealth() : 0);
                    return withCurrentHealth(creature, currentHealth);
                })
                .toList();
    }

    private BattleMoveSnapshotResponse findSelectedMove(BattleCreatureSnapshotResponse attacker, Integer moveSlot) {
        if (attacker.moves() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El JaBea activo no tiene movimientos cargados");
        }

        return attacker.moves()
                .stream()
                .filter(move -> move.slot().equals(moveSlot))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El movimiento elegido no esta disponible"));
    }

    private int calculateDamage(BattleMoveSnapshotResponse move, BattleCreatureSnapshotResponse defender) {
        int moveDamage = move.damage() != null ? move.damage() : 0;
        int defenderDefence = defender.defence() != null ? defender.defence() : 0;
        int baseDamage = Math.max(1, moveDamage - (defenderDefence / 2));
        double multiplier = resolveTypeMultiplier(move.typeName(), defender.typeName());
        return Math.max(1, (int) Math.round(baseDamage * multiplier));
    }

    private boolean moveHits(BattleMoveSnapshotResponse move) {
        int accuracy = move.accuracy() != null ? move.accuracy() : 100;
        if (accuracy >= 100) {
            return true;
        }

        if (accuracy <= 0) {
            return false;
        }

        return ThreadLocalRandom.current().nextInt(100) < accuracy;
    }

    private double resolveTypeMultiplier(String attackTypeName, String defenderTypeName) {
        String attackType = normalizeTypeName(attackTypeName);
        String defenderType = normalizeTypeName(defenderTypeName);

        if (attackType == null || defenderType == null) {
            return 1d;
        }

        return switch (attackType) {
            case "fuego" -> switch (defenderType) {
                case "planta" -> 1.5d;
                case "agua" -> 0.5d;
                default -> 1d;
            };
            case "planta" -> switch (defenderType) {
                case "electrico", "rayo" -> 1.5d;
                case "fuego" -> 0.5d;
                default -> 1d;
            };
            case "electrico", "rayo" -> switch (defenderType) {
                case "agua" -> 1.5d;
                case "planta" -> 0.5d;
                default -> 1d;
            };
            case "agua" -> switch (defenderType) {
                case "fuego" -> 1.5d;
                case "electrico", "rayo" -> 0.5d;
                default -> 1d;
            };
            default -> 1d;
        };
    }

    private String normalizeTypeName(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return null;
        }

        return typeName.trim().toLowerCase();
    }

    private void persistMatchResultIfNeeded(BattleSession session, BattleSnapshotResponse snapshot) {
        if (session.isResultPersisted() || snapshot == null || !snapshot.finished()) {
            return;
        }

        if (snapshot.winnerUsername() == null || snapshot.winnerUsername().isBlank()) {
            session.markResultPersisted();
            return;
        }

        BattlePlayerSnapshotResponse winnerSnapshot = snapshot.playerOne().username().equals(snapshot.winnerUsername())
                ? snapshot.playerOne()
                : snapshot.playerTwo();
        BattlePlayerSnapshotResponse loserSnapshot = snapshot.playerOne().username().equals(snapshot.winnerUsername())
                ? snapshot.playerTwo()
                : snapshot.playerOne();

        User winner = userRepository.findById(winnerSnapshot.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ganador no encontrado"));
        User loser = userRepository.findById(loserSnapshot.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perdedor no encontrado"));

        winner.setMatchesWon(winner.getMatchesWon() + 1);
        loser.setMatchesLost(loser.getMatchesLost() + 1);
        userRepository.save(winner);
        userRepository.save(loser);

        MatchHistory matchHistory = new MatchHistory();
        matchHistory.setWinner(winner);
        matchHistory.setLoser(loser);
        matchHistory.setTurns(Math.max(1, session.getTurnNumber()));
        MatchHistory savedMatch = matchHistoryRepository.save(matchHistory);

        saveTeamHistory(savedMatch, winnerSnapshot, winner);
        saveTeamHistory(savedMatch, loserSnapshot, loser);

        session.markResultPersisted();
    }

    private void saveTeamHistory(MatchHistory matchHistory, BattlePlayerSnapshotResponse playerSnapshot, User owner) {
        List<JaBeasTeamed> teamMembers = jaBeasTeamedRepository.findByTeamTeamIdOrderByIdSlotAsc(playerSnapshot.teamId());
        for (JaBeasTeamed teamMember : teamMembers) {
            if (teamMember.getJaBeas() == null || teamMember.getMove1() == null || teamMember.getMove2() == null) {
                continue;
            }

            JaBeasHistory jaBeasHistory = new JaBeasHistory();
            jaBeasHistory.setMatch(matchHistory);
            jaBeasHistory.setOwner(owner);
            jaBeasHistory.setJaBeas(teamMember.getJaBeas());
            jaBeasHistory.setMove1(teamMember.getMove1());
            jaBeasHistory.setMove2(teamMember.getMove2());
            jaBeasHistory.setSlot(teamMember.getSlot() != null ? teamMember.getSlot() : 0);
            jaBeasHistoryRepository.save(jaBeasHistory);
        }
    }

}
