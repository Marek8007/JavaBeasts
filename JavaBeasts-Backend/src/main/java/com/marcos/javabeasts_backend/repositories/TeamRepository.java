package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByUserUserIdOrderByTeamIdAsc(Integer userId);

    long countByUserUserId(Integer userId);

    Optional<Team> findByTeamIdAndUserUserId(Integer teamId, Integer userId);

    Optional<Team> findByUserUserIdAndActiveTrue(Integer userId);
}
