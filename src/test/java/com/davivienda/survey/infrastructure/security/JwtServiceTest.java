package com.davivienda.survey.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Service Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private String testEmail;
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testEmail = "test@example.com";
        secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        
        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    @DisplayName("Debería generar un token JWT válido")
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateToken(testEmail);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes
    }

    @Test
    @DisplayName("Debería extraer el email del token")
    void extractUsername_ShouldReturnEmail() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        String extractedEmail = jwtService.extractUsername(token);

        // Assert
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    @DisplayName("Debería validar un token correcto")
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testEmail);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debería rechazar un token con email incorrecto")
    void isTokenValid_ShouldReturnFalse_ForWrongEmail() {
        // Arrange
        String token = jwtService.generateToken(testEmail);
        String wrongEmail = "wrong@example.com";

        // Act
        boolean isValid = jwtService.isTokenValid(token, wrongEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debería generar tokens diferentes para emails diferentes")
    void generateToken_ShouldCreateDifferentTokens_ForDifferentEmails() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // Act
        String token1 = jwtService.generateToken(email1);
        String token2 = jwtService.generateToken(email2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals(email1, jwtService.extractUsername(token1));
        assertEquals(email2, jwtService.extractUsername(token2));
    }
}
