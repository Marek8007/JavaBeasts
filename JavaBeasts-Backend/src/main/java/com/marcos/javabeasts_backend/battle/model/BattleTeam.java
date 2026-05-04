package com.marcos.javabeasts_backend.battle.model;

import com.marcos.javabeasts_backend.battle.JaBeaCombat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BattleTeam {
    private List<JaBeaCombat> members = new ArrayList<>();
    private Integer activeSlot = 1;

    public BattleTeam(List<JaBeaCombat> members, Integer activeSlot) {
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
        this.activeSlot = activeSlot;
    }
}
