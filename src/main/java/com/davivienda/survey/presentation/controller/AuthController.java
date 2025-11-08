package com.davivienda.survey.presentation.controller;

import com.davivienda.survey.application.dto.AuthResponse;
import com.davivienda.survey.application.dto.LoginRequest;
import com.davivienda.survey.application.dto.RegisterRequest;
import com.davivienda.survey.application.service.AuthService;
import com.davivienda.survey.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de autenticación de usuarios")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. El email debe ser único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de registro inválidos o email ya existe"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT para acceder a endpoints protegidos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @Operation(
        summary = "Obtener usuario actual",
        description = "Devuelve la información del usuario autenticado actualmente",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token inválido o expirado"
        )
    })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = authService.getCurrentUser(email);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
