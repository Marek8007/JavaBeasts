package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeasTeammed;
import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeammedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JaBeasTeammedRepository extends JpaRepository<JaBeasTeammed, JaBeaTeammedId> {
}
