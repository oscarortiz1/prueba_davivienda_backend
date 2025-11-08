package com.davivienda.survey.application.service;

import com.davivienda.survey.application.dto.LoginRequest;
import com.davivienda.survey.application.dto.RegisterRequest;
import com.davivienda.survey.application.dto.AuthResponse;
import com.davivienda.survey.domain.model.User;
import com.davivienda.survey.domain.port.UserRepository;
import com.davivienda.survey.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void register_ShouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(anyString());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email ya existe")
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> authService.register(registerRequest));
        assertEquals("El correo electrónico ya está registrado", exception.getMessage());
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debería hacer login exitosamente")
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken("test@example.com");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario no existe")
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> authService.login(loginRequest));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}
