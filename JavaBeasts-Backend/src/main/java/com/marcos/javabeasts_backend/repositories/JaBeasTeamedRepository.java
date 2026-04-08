package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeamedId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JaBeasTeamedRepository extends JpaRepository<JaBeasTeamed, JaBeaTeamedId> {
    JaBeasTeamed findByIdTeamIdAndIdSlot(Integer teamId, Integer slot);

}
