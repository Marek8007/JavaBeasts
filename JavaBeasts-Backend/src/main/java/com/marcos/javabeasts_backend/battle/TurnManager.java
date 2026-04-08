package com.marcos.javabeasts_backend.battle;

import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;


public class TurnManager {
    private static int turns = 0;

    public boolean playTurn(JaBeasTeamed jb1, JaBeasTeamed jb2, Move moveJb1, Move moveJb2) {
        turns++;

        JaBeasTeamed[] jbs = checkTurnOrder(jb1, jb2);





        return true;
    }

    private JaBeasTeamed[] checkTurnOrder(JaBeasTeamed jb1, JaBeasTeamed jb2) {
        if (jb1.getJaBeas().getSpeed()>jb2.getJaBeas().getSpeed()) {
            return new JaBeasTeamed[] {jb1, jb2};
        } else {
            return new JaBeasTeamed[] {jb2, jb1};
        }
    }

}
