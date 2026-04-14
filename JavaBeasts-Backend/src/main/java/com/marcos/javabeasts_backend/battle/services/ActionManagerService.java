package com.marcos.javabeasts_backend.battle.services;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.services.MoveService;
import com.marcos.javabeasts_backend.services.TypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class ActionManagerService {

    private final MoveService ms;

    public ActionManagerService(MoveService moveService) {
        this.ms = moveService;
    }

    public Move pickMove(JaBeasTeamed jabea, String player) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        availableMoves.add(jabea.getMove1());
        availableMoves.add(jabea.getMove2());

        // flujo = ms.funcion -> MoveRepository.funcion
        availableMoves.add(ms.findByUniqueJabea(jabea.getJaBeas()));

        System.out.printf("%s, elija una de las opciones:\n", player);

        for (Move move:availableMoves) {
            System.out.printf("%s", move);
        }



        return new Move();
    }
}
