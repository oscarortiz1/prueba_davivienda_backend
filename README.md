# Survey Platform API - Backend

Backend API para la Plataforma de Encuestas Davivienda construido con Spring Boot, Firebase y arquitectura hexagonal.

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture / Arquitectura Hexagonal** con las siguientes capas:

```
src/main/java/com/davivienda/survey/
├── domain/                 # Capa de Dominio (Entidades y Puertos)
│   ├── model/             # Entidades del dominio
│   └── port/              # Interfaces de repositorios (Puertos)
├── application/           # Capa de Aplicación (Casos de Uso)
│   ├── service/          # Servicios de aplicación
│   └── dto/              # Data Transfer Objects
├── infrastructure/        # Capa de Infraestructura (Adaptadores)
│   ├── adapter/          # Implementaciones de Firebase
│   ├── config/           # Configuraciones
│   ├── security/         # JWT y Spring Security
│   └── exception/        # Manejo de excepciones
└── presentation/          # Capa de Presentación (Controllers)
    └── controller/       # REST Controllers
```

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** con JWT
- **Firebase Firestore** (Base de datos)
- **Maven**
- **Lombok**

## ⚙️ Configuración de Firebase

### 1. Crear proyecto en Firebase
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto
3. Habilita Firestore Database
4. Descarga el archivo de credenciales JSON

### 2. Configurar
- Coloca `firebase-config.json` en `src/main/resources/`
- Actualiza `application.properties` con tu database URL

## 📦 Ejecutar

```bash
mvn spring-boot:run
```

API disponible en: `http://localhost:8080/api`
Prueba técnica back end en nest para davivienda
