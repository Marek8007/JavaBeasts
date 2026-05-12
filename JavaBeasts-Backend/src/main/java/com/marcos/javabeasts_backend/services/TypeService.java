package com.marcos.javabeasts_backend.services;

import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeService {
    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }
}

