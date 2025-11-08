package com.davivienda.survey.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Davivienda Survey Platform API",
        version = "1.0.0",
        description = """
            API REST para la plataforma de encuestas de Davivienda.
            
            Esta API permite:
            - Autenticación y autorización de usuarios
            - Creación y gestión de encuestas
            - Publicación de encuestas
            - Respuesta a encuestas públicas
            - Visualización de resultados y estadísticas
            - Exportación de datos en formato CSV
            
            **Autenticación:**
            La mayoría de los endpoints requieren autenticación mediante token JWT.
            Use el endpoint `/auth/login` para obtener un token.
            Luego incluya el token en el header Authorization: Bearer {token}
            
            **Endpoints Públicos:**
            - POST /auth/register - Registro de nuevos usuarios
            - POST /auth/login - Inicio de sesión
            - GET /surveys/published - Lista de encuestas publicadas
            - GET /surveys/public/{id} - Detalles de una encuesta pública
            - POST /surveys/{id}/responses - Enviar respuesta a una encuesta
            """,
        contact = @Contact(
            name = "Equipo de Desarrollo",
            email = "desarrollo@davivienda.com"
        ),
        license = @License(
            name = "Privado",
            url = "https://davivienda.com"
        )
    ),
    servers = {
        @Server(
            description = "Servidor Local",
            url = "http://localhost:8080/api"
        ),
        @Server(
            description = "Servidor de Desarrollo",
            url = "https://dev-api.davivienda.com/api"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "Autenticación JWT. Obtenga el token usando POST /auth/login",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
