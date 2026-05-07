package com.marcos.javabeasts_backend.services.battle;

import com.marcos.javabeasts_backend.dto.battle.BattleSnapshotResponse;

public class BattleSession {

    private final String roomCode;
    private final int turnNumber;
    private final String playerOneUsername;
    private final String playerTwoUsername;
    private final BattleSnapshotResponse initialSnapshot;
    private BattleTurnAction playerOneAction;
    private BattleTurnAction playerTwoAction;

    public BattleSession(BattleSnapshotResponse initialSnapshot) {
        this.roomCode = initialSnapshot.roomCode();
        this.turnNumber = initialSnapshot.turnNumber();
        this.playerOneUsername = initialSnapshot.playerOne().username();
        this.playerTwoUsername = initialSnapshot.playerTwo().username();
        this.initialSnapshot = initialSnapshot;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public BattleSnapshotResponse getInitialSnapshot() {
        return initialSnapshot;
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
}
