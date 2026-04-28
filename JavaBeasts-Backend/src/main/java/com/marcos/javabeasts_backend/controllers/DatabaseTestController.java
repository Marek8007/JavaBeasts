package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.repositories.JaBeaRepository;
import com.marcos.javabeasts_backend.repositories.TeamRepository;
import com.marcos.javabeasts_backend.repositories.TypeRepository;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class DatabaseTestController {

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TypeRepository typeRepository;
    private final JaBeaRepository jaBeaRepository;

    public DatabaseTestController(
            DataSource dataSource,
            UserRepository userRepository,
            TeamRepository teamRepository,
            TypeRepository typeRepository,
            JaBeaRepository jaBeaRepository
    ) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.typeRepository = typeRepository;
        this.jaBeaRepository = jaBeaRepository;
    }

    @GetMapping("/db")
    public Map<String, Object> testDatabaseConnection() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            response.put("status", "ok");
            response.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
            response.put("databaseUrl", connection.getMetaData().getURL());
            response.put("databaseUser", connection.getMetaData().getUserName());
        }

        response.put("usersCount", userRepository.count());
        response.put("teamsCount", teamRepository.count());
        response.put("typesCount", typeRepository.count());
        response.put("jabeasCount", jaBeaRepository.count());

        return response;
    }
}
