package com.marcos.javabeasts_backend.battle;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.repositories.MoveRepository;

import java.util.ArrayList;

public class ActionManager {
    public static Move pickMove(JaBeasTeamed jabea) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        availableMoves.add(jabea.getMove1());
        availableMoves.add(jabea.getMove2());
        availableMoves.add()

        return new Move();
    }
}
