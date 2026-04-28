package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Integer> {
}
