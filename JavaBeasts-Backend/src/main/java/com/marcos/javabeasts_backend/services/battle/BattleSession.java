package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;

public class BattleSession {

    private final String roomCode;
    private final String playerOneUsername;
    private final String playerTwoUsername;
    private int turnNumber;
    private BattleSnapshotResponse currentSnapshot;
    private BattleTurnAction playerOneAction;
    private BattleTurnAction playerTwoAction;
    private String lastResolutionMessage;

    public BattleSession(BattleSnapshotResponse initialSnapshot) {
        this.roomCode = initialSnapshot.roomCode();
        this.turnNumber = initialSnapshot.turnNumber();
        this.playerOneUsername = initialSnapshot.playerOne().username();
        this.playerTwoUsername = initialSnapshot.playerTwo().username();
        this.currentSnapshot = initialSnapshot;
        this.lastResolutionMessage = "Combate inicializado";
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
    }

    public void completeTurn(String resolutionMessage) {
        this.turnNumber++;
        this.lastResolutionMessage = resolutionMessage;
        this.playerOneAction = null;
        this.playerTwoAction = null;
    }
}
