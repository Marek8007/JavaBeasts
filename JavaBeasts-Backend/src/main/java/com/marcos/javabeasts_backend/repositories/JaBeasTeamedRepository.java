package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeasTeamed;
import com.marcos.javabeasts_backend.entity.embeddables.JaBeaTeamedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JaBeasTeamedRepository extends JpaRepository<JaBeasTeamed, JaBeaTeamedId> {
}
