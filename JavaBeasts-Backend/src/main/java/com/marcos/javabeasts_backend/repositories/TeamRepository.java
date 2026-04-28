package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
