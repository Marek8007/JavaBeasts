package com.marcos.javabeasts_backend.services.auth;

import com.marcos.javabeasts_backend.dto.auth.AuthResponse;
import com.marcos.javabeasts_backend.dto.auth.LoginRequest;
import com.marcos.javabeasts_backend.dto.auth.RegisterRequest;
import com.marcos.javabeasts_backend.entity.User;
import com.marcos.javabeasts_backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setLogged(false);
        user.setMatchesWon(0);
        user.setMatchesLost(0);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser, "Usuario registrado correctamente");
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        user.setLogged(true);

        return toResponse(userRepository.save(user), "Inicio de sesion correcto");
    }

    @Transactional
    public AuthResponse logout(String username) {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setLogged(false);

        return toResponse(userRepository.save(user), "Sesion cerrada correctamente");
    }

    private AuthResponse toResponse(User user, String message) {
        return new AuthResponse(
                user.getUserId(),
                user.getUsername(),
                user.isLogged(),
                user.getMatchesWon(),
                user.getMatchesLost(),
                message
        );
    }
}
