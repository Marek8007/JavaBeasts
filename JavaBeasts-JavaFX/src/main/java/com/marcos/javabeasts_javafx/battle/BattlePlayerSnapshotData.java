package com.marcos.javabeasts_javafx.battle;

import java.util.List;

public class BattlePlayerSnapshotData {

    private Integer userId;
    private String username;
    private Integer teamId;
    private String teamName;
    private BattleCreatureSnapshotData activeJaBea;
    private List<BattleCreatureSnapshotData> teamCreatures;

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

    public List<BattleCreatureSnapshotData> getTeamCreatures() {
        return teamCreatures;
    }
}
