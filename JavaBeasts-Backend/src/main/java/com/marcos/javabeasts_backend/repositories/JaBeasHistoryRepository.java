package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeasHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JaBeasHistoryRepository extends JpaRepository<JaBeasHistory, Integer> {
    List<JaBeasHistory> findByMatchMatchIdAndOwnerUserIdOrderBySlotAsc(Integer matchId, Integer ownerUserId);
}
