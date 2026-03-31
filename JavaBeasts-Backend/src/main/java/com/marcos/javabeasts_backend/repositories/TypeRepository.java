package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Integer> {
}

