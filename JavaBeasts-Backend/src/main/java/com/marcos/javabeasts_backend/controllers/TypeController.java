package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.TypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TypeController {

    private final TypeRepository typeRepository;

    public TypeController(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    @GetMapping("/types")
    public List<Type> getTypes() {
        return typeRepository.findAll();
    }
}
