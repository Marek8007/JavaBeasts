package com.marcos.javabeasts_backend.services;

import com.marcos.javabeasts_backend.dto.jabeas.JaBeaAvailableMovesResponse;
import com.marcos.javabeasts_backend.dto.jabeas.JaBeaCatalogResponse;
import com.marcos.javabeasts_backend.dto.jabeas.JaBeaMoveResponse;
import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.MoveRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class JaBeaCatalogService {

    private static final Map<String, String> MIRROR_TYPES = Map.of(
            "Fuego", "Electrico",
            "Electrico", "Fuego",
            "Agua", "Planta",
            "Planta", "Agua"
    );

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

    @Transactional(readOnly = true)
    public JaBeaAvailableMovesResponse getAvailableMoves(Integer jaBeasId) {
        JaBeas jaBea = jaBeaRepository.findById(jaBeasId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "JaBea no encontrado"));

        Type type = jaBea.getType();
        String mirrorType = MIRROR_TYPES.get(type.getType());

        List<JaBeaMoveResponse> configurableMoves = moveRepository.findAll(Sort.by(Sort.Direction.ASC, "moveId"))
                .stream()
                .filter(move -> move.getUniqueJabea() == null)
                .filter(move -> isMoveAllowedForJaBea(move, type.getType(), mirrorType))
                .map(this::toMoveResponse)
                .toList();

        return new JaBeaAvailableMovesResponse(
                jaBea.getJaBeasId(),
                jaBea.getName(),
                type.getTypeId(),
                type.getType(),
                toMoveResponse(moveRepository.findByUniqueJabea(jaBea)),
                configurableMoves
        );
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

    private boolean isMoveAllowedForJaBea(Move move, String jaBeaType, String mirrorType) {
        String moveType = move.getType().getType();

        return moveType.equals(jaBeaType)
                || moveType.equals("Normal")
                || moveType.equals(mirrorType);
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
