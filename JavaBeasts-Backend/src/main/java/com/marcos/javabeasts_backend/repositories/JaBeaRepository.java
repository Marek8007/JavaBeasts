package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JaBeaRepository extends JpaRepository<JaBeas, Integer> {
    JaBeas findById(int id);


}
