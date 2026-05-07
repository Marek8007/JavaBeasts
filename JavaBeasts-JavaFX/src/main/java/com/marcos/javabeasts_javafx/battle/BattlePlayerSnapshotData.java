package com.marcos.javabeasts_javafx.battle;

public class BattlePlayerSnapshotData {

    private Integer userId;
    private String username;
    private Integer teamId;
    private String teamName;
    private BattleCreatureSnapshotData activeJaBea;

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public BattleCreatureSnapshotData getActiveJaBea() {
        return activeJaBea;
    }
}
