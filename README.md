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

### Core
- **Java 21** - Última versión LTS con características modernas
- **Spring Boot 3.4.0** - Framework principal
- **Spring Security 6** - Autenticación y autorización
- **Maven 3.9.11** - Gestión de dependencias y build

### Base de Datos
- **Firebase Realtime Database** - Base de datos NoSQL en tiempo real
- **Firebase Admin SDK** - Integración con servicios Firebase

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking framework para tests unitarios
- **Spring Boot Test** - Utilidades de testing

### Utilidades
- **Lombok** - Reducción de código boilerplate
- **JJWT (JSON Web Token)** - Generación y validación de tokens JWT
- **Spring Validation** - Validación de datos
- **BCrypt** - Encriptación de contraseñas

## 🔥 Firebase Realtime Database

Este proyecto usa **Firebase Realtime Database** en lugar de Firestore para soportar funcionalidades en tiempo real:

### Ventajas
- ✅ **Sincronización en tiempo real** - Cambios se reflejan instantáneamente
- ✅ **Offline support** - La app funciona sin conexión
- ✅ **Low latency** - Menor latencia que Firestore
- ✅ **Perfect for real-time features** - Ideal para chat, colaboración, dashboards en vivo

### Estructura de Datos
```json
{
  "users": {
    "userId": {
      "id": "string",
      "name": "string",
      "email": "string",
      "password": "string (hashed)",
      "createdAt": "ISO timestamp",
      "updatedAt": "ISO timestamp"
    }
  },
  "surveys": {
    "surveyId": {
      "id": "string",
      "title": "string",
      "description": "string",
      "createdBy": "userId",
      "isPublished": boolean,
      "createdAt": "ISO timestamp",
      "updatedAt": "ISO timestamp",
      "questions": [...]
    }
  }
}
```

## ⚙️ Configuración

### Requisitos Previos

- **Java 21** o superior ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Cuenta de Firebase** ([Crear cuenta](https://firebase.google.com/))

### 1. Clonar el Repositorio

```bash
git clone https://github.com/oscarortiz1/prueba_davivienda.git
cd prueba_davivienda_backend
```

### 2. Configurar Firebase

1. **Crear proyecto en Firebase Console:**
   - Ve a [Firebase Console](https://console.firebase.google.com/)
   - Crea un nuevo proyecto (ej: "survey-platform")
   - Habilita **Realtime Database** (no Firestore)
   - En reglas de seguridad, configura temporalmente:
     ```json
     {
       "rules": {
         ".read": "auth != null",
         ".write": "auth != null"
       }
     }
     ```

2. **Descargar credenciales:**
   - Ve a **Project Settings** → **Service Accounts**
   - Click en **Generate New Private Key**
   - Guarda el archivo como `firebase-config.json`

3. **Configurar en el proyecto:**
   ```bash
   # Copiar archivo de ejemplo
   cp src/main/resources/firebase-config.example.json src/main/resources/firebase-config.json
   
   # Reemplazar con tus credenciales descargadas
   ```

4. **Configurar Database URL:**
   - Obtén tu Database URL desde Firebase Console (ej: `https://tu-proyecto.firebaseio.com`)
   - Edita `src/main/resources/application.properties`:
     ```properties
     firebase.database.url=https://tu-proyecto.firebaseio.com
     ```

### 3. Configurar JWT Secret

**Opción A: Variables de entorno (Recomendado para producción)**
```bash
# Windows PowerShell
$env:JWT_SECRET="tu_secreto_super_seguro_minimo_256_bits"
$env:JWT_EXPIRATION="86400000"

# Linux/Mac
export JWT_SECRET="tu_secreto_super_seguro_minimo_256_bits"
export JWT_EXPIRATION="86400000"
```

**Opción B: Archivo application.properties (Solo desarrollo)**
```properties
# src/main/resources/application.properties
jwt.secret=tu_secreto_super_seguro_minimo_256_bits
jwt.expiration=86400000
```

⚠️ **IMPORTANTE**: 
- NO subas `firebase-config.json` al repositorio
- NO subas secrets en `application.properties` al repositorio
- Usa variables de entorno en producción
- El JWT secret debe tener al menos 256 bits (32 caracteres)

### 4. Instalar Dependencias

```bash
mvn clean install
```

## 🏃 Ejecución

### Modo Desarrollo
```bash
mvn spring-boot:run
```

### Compilar y ejecutar JAR
```bash
mvn clean package
java -jar target/survey-platform-1.0.0.jar
```

### Ejecutar con profile específico
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

La API estará disponible en: **http://localhost:8080/api**

### Verificar que funciona
```bash
curl http://localhost:8080/api/health
# Respuesta esperada: {"status":"UP"}
```

### 📚 Documentación Swagger/OpenAPI

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/api/v3/api-docs.yaml

**Características de Swagger UI:**
- 🔍 Explorar todos los endpoints disponibles
- 📝 Ver esquemas de request/response
- 🧪 Probar endpoints directamente desde el navegador
- 🔐 Autenticación JWT integrada (botón "Authorize")
- 📋 Ejemplos de payloads para cada endpoint

**Cómo usar Swagger:**
1. Abre http://localhost:8080/api/swagger-ui.html
2. Registra un usuario usando `POST /auth/register`
3. Inicia sesión con `POST /auth/login` para obtener el token JWT
4. Click en el botón **"Authorize"** (candado verde)
5. Pega tu token en el campo (sin "Bearer", solo el token)
6. Ahora puedes probar todos los endpoints protegidos

## 🧪 Testing

### Ejecutar todos los tests
```bash
mvn test
```

### Tests actuales: 17 passing ✅

**AuthServiceTest (4 tests)**
- ✅ Registro exitoso de usuario
- ✅ Validación de email duplicado
- ✅ Login con credenciales válidas
- ✅ Error al login con usuario inexistente

**SurveyServiceTest (8 tests)**
- ✅ Crear encuesta exitosamente
- ✅ Obtener encuesta por ID
- ✅ Error al buscar encuesta inexistente
- ✅ Listar encuestas del usuario
- ✅ Actualizar encuesta existente
- ✅ Eliminar encuesta
- ✅ Publicar encuesta con preguntas
- ✅ Filtrar solo encuestas publicadas

**JwtServiceTest (5 tests)**
- ✅ Generar token JWT válido
- ✅ Extraer email del token
- ✅ Validar token correcto
- ✅ Rechazar token con email incorrecto
- ✅ Generar tokens únicos por usuario

### Ejecutar tests específicos
```bash
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=SurveyServiceTest
mvn test -Dtest=JwtServiceTest
```

### Cobertura de tests (opcional)
```bash
mvn test jacoco:report
# Ver reporte en: target/site/jacoco/index.html
```

## 📡 API Endpoints

### Base URL: `http://localhost:8080/api`

### 🔐 Autenticación

#### Registro
```http
POST /auth/register
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123"
}
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "user-uuid",
    "name": "Juan Pérez",
    "email": "juan@example.com"
  }
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

#### Obtener usuario actual
```http
GET /auth/me
Authorization: Bearer {token}
```

### 📋 Encuestas

#### Crear encuesta
```http
POST /surveys
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Encuesta de Satisfacción",
  "description": "Queremos conocer tu opinión"
}
```

#### Listar mis encuestas
```http
GET /surveys/my-surveys
Authorization: Bearer {token}
```

#### Obtener encuesta por ID
```http
GET /surveys/{surveyId}
Authorization: Bearer {token}
```

#### Actualizar encuesta
```http
PUT /surveys/{surveyId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Nuevo título",
  "description": "Nueva descripción"
}
```

#### Eliminar encuesta
```http
DELETE /surveys/{surveyId}
Authorization: Bearer {token}
```

#### Publicar encuesta
```http
PUT /surveys/{surveyId}/publish
Authorization: Bearer {token}
```

#### Despublicar encuesta
```http
PUT /surveys/{surveyId}/unpublish
Authorization: Bearer {token}
```

#### Listar encuestas publicadas
```http
GET /surveys/published
Authorization: Bearer {token}
```

### ❓ Preguntas

#### Agregar pregunta a encuesta
```http
POST /surveys/{surveyId}/questions
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "¿Cómo calificarías nuestro servicio?",
  "type": "SCALE",
  "required": true,
  "order": 1,
  "options": ["1", "2", "3", "4", "5"]
}
```

**Tipos de pregunta disponibles:**
- `TEXT` - Respuesta de texto libre
- `MULTIPLE_CHOICE` - Opción múltiple (una respuesta)
- `CHECKBOX` - Casillas de verificación (múltiples respuestas)
- `DROPDOWN` - Lista desplegable
- `SCALE` - Escala numérica (1-5, 1-10, etc.)

#### Actualizar pregunta
```http
PUT /surveys/{surveyId}/questions/{questionId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "¿Cómo calificarías nuestro servicio? (actualizado)",
  "required": false
}
```

#### Eliminar pregunta
```http
DELETE /surveys/{surveyId}/questions/{questionId}
Authorization: Bearer {token}
```

### 📊 Respuestas

#### Enviar respuesta a encuesta
```http
POST /responses
Authorization: Bearer {token}
Content-Type: application/json

{
  "surveyId": "survey-uuid",
  "answers": [
    {
      "questionId": "question-uuid-1",
      "value": "5"
    },
    {
      "questionId": "question-uuid-2",
      "value": ["Opción A", "Opción C"]
    }
  ]
}
```

#### Obtener respuestas de una encuesta
```http
GET /responses/survey/{surveyId}
Authorization: Bearer {token}
```
