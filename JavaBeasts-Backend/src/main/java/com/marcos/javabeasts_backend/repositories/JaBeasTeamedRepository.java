package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeamedId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JaBeasTeamedRepository extends JpaRepository<JaBeasTeamed, JaBeaTeamedId> {
    List<JaBeasTeamed> findByTeamTeamIdOrderByIdSlotAsc(Integer teamId);

    Optional<JaBeasTeamed> findByTeamTeamIdAndIdSlot(Integer teamId, Integer slot);

}
