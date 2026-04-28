package com.marcos.javabeasts_backend;

import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseSmokeTestRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final UserRepository userRepository;

    @Value("${app.db.test-on-startup:false}")
    private boolean testOnStartup;

    public DatabaseSmokeTestRunner(DataSource dataSource, UserRepository userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!testOnStartup) {
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("[DB TEST] Conexion correcta con " + connection.getMetaData().getDatabaseProductName());
            System.out.println("[DB TEST] URL: " + connection.getMetaData().getURL());
            System.out.println("[DB TEST] Usuario de BD: " + connection.getMetaData().getUserName());
        }

        System.out.println("[DB TEST] Usuarios detectados: " + userRepository.count());
    }
}
