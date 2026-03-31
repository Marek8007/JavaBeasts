package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoveRepository extends JpaRepository<Move, Integer> {
}
