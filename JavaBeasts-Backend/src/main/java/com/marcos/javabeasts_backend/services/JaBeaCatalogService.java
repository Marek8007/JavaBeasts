package com.marcos.javabeasts_backend.services;

import com.marcos.javabeasts_backend.dto.jabeas.JaBeaCatalogResponse;
import com.marcos.javabeasts_backend.dto.jabeas.JaBeaMoveResponse;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JaBeaCatalogService {

    private final JaBeaRepository jaBeaRepository;
    private final MoveRepository moveRepository;

    public JaBeaCatalogService(JaBeaRepository jaBeaRepository, MoveRepository moveRepository) {
        this.jaBeaRepository = jaBeaRepository;
        this.moveRepository = moveRepository;
    }

    @Transactional(readOnly = true)
    public List<JaBeaCatalogResponse> getJaBeasCatalog() {
        return jaBeaRepository.findAll(Sort.by(Sort.Direction.ASC, "jaBeasId"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private JaBeaCatalogResponse toResponse(JaBeas jaBea) {
        Type type = jaBea.getType();

        return new JaBeaCatalogResponse(
                jaBea.getJaBeasId(),
                jaBea.getName(),
                jaBea.getDescription(),
                jaBea.getHealth(),
                jaBea.getDamage(),
                jaBea.getDefence(),
                jaBea.getSpeed(),
                type.getTypeId(),
                type.getType(),
                toMoveResponse(moveRepository.findByUniqueJabea(jaBea))
        );
    }

    private JaBeaMoveResponse toMoveResponse(Move move) {
        if (move == null) {
            return null;
        }

        Type type = move.getType();

        return new JaBeaMoveResponse(
                move.getMoveId(),
                move.getName(),
                move.getDescription(),
                type.getTypeId(),
                type.getType(),
                move.getDamage(),
                move.getAccuracy(),
                move.getSpecialEffect()
        );
    }
}
