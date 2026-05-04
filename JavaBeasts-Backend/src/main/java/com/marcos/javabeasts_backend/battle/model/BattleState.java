package com.marcos.javabeasts_backend.battle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleState {
    private BattlePlayer playerOne;
    private BattlePlayer playerTwo;
    private BattleTeam teamOne;
    private BattleTeam teamTwo;
    private Integer turnNumber = 0;
    private boolean finished = false;
}
