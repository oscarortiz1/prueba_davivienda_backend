package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.AuthResponse;
import com.davivienda.survey.application.dto.LoginRequest;
import com.davivienda.survey.application.dto.RegisterRequest;
import com.davivienda.survey.application.service.AuthService;
import com.davivienda.survey.domain.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.getCurrentUser(email);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
