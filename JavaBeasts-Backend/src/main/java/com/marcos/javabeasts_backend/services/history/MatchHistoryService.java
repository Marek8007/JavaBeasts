package com.marcos.javabeasts_backend.services.history;

import com.marcos.javabeasts_backend.dto.history.MatchHistoryJaBeaResponse;
import com.marcos.javabeasts_backend.dto.history.MatchHistoryResponse;
import com.marcos.javabeasts_backend.entity.JaBeasHistory;
import com.marcos.javabeasts_backend.entity.MatchHistory;
import com.marcos.javabeasts_backend.entity.User;
import com.marcos.javabeasts_backend.repositories.JaBeasHistoryRepository;
import com.marcos.javabeasts_backend.repositories.MatchHistoryRepository;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MatchHistoryService {

    private final UserRepository userRepository;
    private final MatchHistoryRepository matchHistoryRepository;
    private final JaBeasHistoryRepository jaBeasHistoryRepository;

    public MatchHistoryService(
            UserRepository userRepository,
            MatchHistoryRepository matchHistoryRepository,
            JaBeasHistoryRepository jaBeasHistoryRepository
    ) {
        this.userRepository = userRepository;
        this.matchHistoryRepository = matchHistoryRepository;
        this.jaBeasHistoryRepository = jaBeasHistoryRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchHistoryResponse> getHistoryByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return matchHistoryRepository.findByWinnerUserIdOrLoserUserIdOrderByMatchIdDesc(userId, userId)
                .stream()
                .map(match -> toResponse(match, user))
                .toList();
    }

    private MatchHistoryResponse toResponse(MatchHistory match, User user) {
        boolean won = match.getWinner() != null && match.getWinner().getUserId() == user.getUserId();
        User opponent = won ? match.getLoser() : match.getWinner();

        List<MatchHistoryJaBeaResponse> team = jaBeasHistoryRepository
                .findByMatchMatchIdAndOwnerUserIdOrderBySlotAsc(match.getMatchId(), user.getUserId())
                .stream()
                .map(this::toJaBeaResponse)
                .toList();

        return new MatchHistoryResponse(
                match.getMatchId(),
                won,
                opponent != null ? opponent.getUsername() : "Rival desconocido",
                match.getTurns(),
                team
        );
    }

    private MatchHistoryJaBeaResponse toJaBeaResponse(JaBeasHistory history) {
        return new MatchHistoryJaBeaResponse(
                history.getSlot(),
                history.getJaBeas() != null ? history.getJaBeas().getJaBeasId() : null,
                history.getJaBeas() != null ? history.getJaBeas().getName() : "JaBea desconocido",
                history.getMove1() != null ? history.getMove1().getName() : null,
                history.getMove2() != null ? history.getMove2().getName() : null
        );
    }
}
