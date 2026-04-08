package com.marcos.javabeasts_backend.battle;

import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Combat {

    //TODO: poner equipos en vez de JaBeas sueltos
    private final JaBeasTeamed jabea1;
    private final JaBeasTeamed jabea2;

    //TODO: Implementar
    private User user1;
    private User user2;

    private TurnManager tm = new TurnManager();

    public void play() {
        Move moveJb1 = ActionManager.pickMove(jabea1);
        Move moveJb2 = ActionManager.pickMove(jabea2);

        tm.playTurn(jabea1, jabea2, moveJb1, moveJb2);
    }


}
