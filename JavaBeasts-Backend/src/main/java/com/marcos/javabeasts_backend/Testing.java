package com.marcos.javabeasts_backend;

import com.marcos.javabeasts_backend.entity.Jabea;
import com.marcos.javabeasts_backend.entity.Type;
import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.TypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Testing implements CommandLineRunner {

    private final JaBeaRepository jabeaRepository;

    public Testing(JaBeaRepository jabeaRepository) {
        this.jabeaRepository = jabeaRepository;
    }

    @Override
    public void run(String... args) {
        for (Jabea jabea : jabeaRepository.findAll()) {
            System.out.println(jabea);
        }
    }
}
