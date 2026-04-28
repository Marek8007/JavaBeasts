package com.marcos.javabeasts_backend.repositories;

import com.marcos.javabeasts_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
