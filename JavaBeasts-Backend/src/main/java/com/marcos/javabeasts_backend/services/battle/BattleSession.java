package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;
import com.marcos.javabeasts_backend.dto.battle.BattleCreatureSnapshotResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BattleSession {

    private final String roomCode;
    private final String playerOneUsername;
    private final String playerTwoUsername;
    private int turnNumber;
    private BattleSnapshotResponse currentSnapshot;
    private BattleTurnAction playerOneAction;
    private BattleTurnAction playerTwoAction;
    private String lastResolutionMessage;
    private final Map<String, Map<Integer, Integer>> healthByPlayerSlot = new HashMap<>();
    private boolean resultPersisted;

    public BattleSession(BattleSnapshotResponse initialSnapshot) {
        this.roomCode = initialSnapshot.roomCode();
        this.turnNumber = initialSnapshot.turnNumber();
        this.playerOneUsername = initialSnapshot.playerOne().username();
        this.playerTwoUsername = initialSnapshot.playerTwo().username();
        this.currentSnapshot = initialSnapshot;
        this.lastResolutionMessage = "Combate inicializado";
        rememberActiveCreatureHealth(initialSnapshot);
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public BattleSnapshotResponse getCurrentSnapshot() {
        return currentSnapshot;
    }

    public String getLastResolutionMessage() {
        return lastResolutionMessage;
    }

    public boolean containsPlayer(String username) {
        return playerOneUsername.equals(username) || playerTwoUsername.equals(username);
    }

    public void registerAction(BattleTurnAction action) {
        if (playerOneUsername.equals(action.username())) {
            playerOneAction = action;
            return;
        }

        if (playerTwoUsername.equals(action.username())) {
            playerTwoAction = action;
            return;
        }

        throw new IllegalArgumentException("El jugador no pertenece a esta sesion de combate");
    }

    public boolean isPlayerOneActionSubmitted() {
        return playerOneAction != null;
    }

    public boolean isPlayerTwoActionSubmitted() {
        return playerTwoAction != null;
    }

    public boolean isTurnReadyToResolve() {
        return isPlayerOneActionSubmitted() && isPlayerTwoActionSubmitted();
    }

    public BattleTurnAction getPlayerOneAction() {
        return playerOneAction;
    }

    public BattleTurnAction getPlayerTwoAction() {
        return playerTwoAction;
    }

    public void updateSnapshot(BattleSnapshotResponse currentSnapshot) {
        this.currentSnapshot = currentSnapshot;
        rememberActiveCreatureHealth(currentSnapshot);
    }

    public void completeTurn(String resolutionMessage) {
        this.turnNumber++;
        this.lastResolutionMessage = resolutionMessage;
        this.playerOneAction = null;
        this.playerTwoAction = null;
    }

    public Optional<Integer> getStoredHealth(String username, Integer slot) {
        if (username == null || slot == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(healthByPlayerSlot.getOrDefault(username, Map.of()).get(slot));
    }

    public void rememberCreatureHealth(String username, BattleCreatureSnapshotResponse creature) {
        if (username == null || creature == null || creature.slot() == null || creature.currentHealth() == null) {
            return;
        }

        healthByPlayerSlot
                .computeIfAbsent(username, ignored -> new HashMap<>())
                .put(creature.slot(), creature.currentHealth());
    }

    public boolean isResultPersisted() {
        return resultPersisted;
    }

    public void markResultPersisted() {
        this.resultPersisted = true;
    }

    private void rememberActiveCreatureHealth(BattleSnapshotResponse snapshot) {
        rememberCreatureHealth(snapshot.playerOne().username(), snapshot.playerOne().activeJaBea());
        rememberCreatureHealth(snapshot.playerTwo().username(), snapshot.playerTwo().activeJaBea());
    }
}
