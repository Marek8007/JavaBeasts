package com.marcos.javabeasts_backend.controllers;

import com.marcos.javabeasts_backend.dto.auth.AuthResponse;
import com.marcos.javabeasts_backend.dto.auth.LoginRequest;
import com.marcos.javabeasts_backend.dto.auth.RegisterRequest;
import com.marcos.javabeasts_backend.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public AuthResponse logout(@RequestParam String username) {
        return authService.logout(username);
    }
}
