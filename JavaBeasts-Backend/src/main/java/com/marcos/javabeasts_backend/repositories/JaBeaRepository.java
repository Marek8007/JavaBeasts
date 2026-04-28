package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JaBeaRepository extends JpaRepository<JaBeas, Integer> {
    JaBeas findById(int id);


}
