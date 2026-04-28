package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.JaBeas;
import com.marcos.javabeasts_backend.entity.Move;
import com.marcos.javabeasts_backend.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.ListResourceBundle;

public interface MoveRepository extends JpaRepository<Move, Integer> {
    List<Move> findByType(Type type);
    Move findByUniqueJabea(JaBeas jaBeas);

}
