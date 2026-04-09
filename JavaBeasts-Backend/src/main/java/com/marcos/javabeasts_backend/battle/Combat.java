package com.marcos.javabeasts_backend.battle;

import com.marcos.javabeasts_backend.battle.services.ActionManagerService;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Combat {

    //TODO: poner equipos en vez de JaBeas sueltos
    private final JaBeasTeamed jabea1;
    private final JaBeasTeamed jabea2;
    private final ActionManagerService ams;

    //TODO: Implementar
    private User user1;
    private User user2;

    private TurnManager tm = new TurnManager();

    public void play() {
        Move moveJb1 = ams.pickMove(jabea1, "Player 1");
        Move moveJb2 = ams.pickMove(jabea2, "Player 2");

        tm.playTurn(jabea1, jabea2, moveJb1, moveJb2);
    }


}
