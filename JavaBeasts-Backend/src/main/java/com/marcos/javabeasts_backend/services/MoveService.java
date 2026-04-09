package com.marcos.javabeasts_backend.services;

import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoveService {
    private final MoveRepository moveRepository;

    public MoveService(MoveRepository moveRepository) {
        this.moveRepository = moveRepository;
    }

    public Move findByUniqueJabea(JaBeas jb) {
        return moveRepository.findByUniqueJabea(jb);
    }
}
