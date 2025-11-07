# Arquitectura y Flujos del Sistema

## 🏗️ Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                         │
│                     http://localhost:5173                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐    ┌──────────────┐    ┌─────────────────┐   │
│  │   Pages      │───▶│   Stores     │───▶│   Axios HTTP    │   │
│  │  (UI/UX)     │    │  (Zustand)   │    │   (API Calls)   │   │
│  └──────────────┘    └──────────────┘    └─────────────────┘   │
│         │                   │                       │            │
│         │                   │                       │            │
└─────────┼───────────────────┼───────────────────────┼────────────┘
          │                   │                       │
          │                   │                       ▼
          │                   │              Authorization: Bearer <token>
          │                   │                       │
          ▼                   ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                         │
│                      http://localhost:8080/api                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────┐                                            │
│  │   Controllers    │  /auth, /surveys                           │
│  │  (REST API)      │                                            │
│  └────────┬─────────┘                                            │
│           │                                                       │
│           ▼                                                       │
│  ┌──────────────────┐    ┌─────────────────┐                    │
│  │    Services      │───▶│   Repositories  │                    │
│  │  (Business Logic)│    │   (Ports)       │                    │
│  └──────────────────┘    └────────┬────────┘                    │
│           │                        │                             │
│           │                        │                             │
│  ┌────────┴────────┐              │                             │
│  │   JWT Service   │              │                             │
│  │  (Auth/Token)   │              │                             │
│  └─────────────────┘              │                             │
│                                    ▼                             │
│                          ┌─────────────────┐                     │
│                          │ Firebase Adapter│                     │
│                          │  (Firestore DB) │                     │
│                          └────────┬────────┘                     │
└───────────────────────────────────┼──────────────────────────────┘
                                    │
                                    ▼
                          ┌─────────────────┐
                          │  Firebase Cloud │
                          │    Firestore    │
                          └─────────────────┘
```

## 🔐 Flujo de Autenticación (Registro y Login)

```
┌─────────┐              ┌─────────┐              ┌──────────┐
│ Browser │              │ Backend │              │ Firebase │
└────┬────┘              └────┬────┘              └────┬─────┘
     │                        │                        │
     │ 1. POST /auth/register │                        │
     │   {name, email, pass}  │                        │
     ├───────────────────────▶│                        │
     │                        │                        │
     │                        │ 2. Hash password       │
     │                        │    (BCrypt)            │
     │                        │                        │
     │                        │ 3. Save user           │
     │                        ├───────────────────────▶│
     │                        │                        │
     │                        │ 4. User saved          │
     │                        │◀───────────────────────┤
     │                        │                        │
     │                        │ 5. Generate JWT token  │
     │                        │    (24h expiration)    │
     │                        │                        │
     │ 6. Return token        │                        │
     │    {token, userId,...} │                        │
     │◀───────────────────────┤                        │
     │                        │                        │
     │ 7. Store token in      │                        │
     │    localStorage        │                        │
     │                        │                        │
     │ 8. POST /auth/login    │                        │
     │    {email, password}   │                        │
     ├───────────────────────▶│                        │
     │                        │                        │
     │                        │ 9. Find user by email  │
     │                        ├───────────────────────▶│
     │                        │                        │
     │                        │ 10. Return user        │
     │                        │◀───────────────────────┤
     │                        │                        │
     │                        │ 11. Verify password    │
     │                        │     (BCrypt compare)   │
     │                        │                        │
     │                        │ 12. Generate JWT       │
     │                        │                        │
     │ 13. Return token       │                        │
     │◀───────────────────────┤                        │
     │                        │                        │
     │ 14. Store in           │                        │
     │     localStorage       │                        │
     │                        │                        │
```

## 📝 Flujo de Creación de Encuesta

```
┌─────────┐              ┌─────────┐              ┌──────────┐
│ Browser │              │ Backend │              │ Firebase │
└────┬────┘              └────┬────┘              └────┬─────┘
     │                        │                        │
     │ 1. POST /surveys       │                        │
     │    Authorization:      │                        │
     │    Bearer <token>      │                        │
     │    {title, desc}       │                        │
     ├───────────────────────▶│                        │
     │                        │                        │
     │                        │ 2. Verify JWT token    │
     │                        │    Extract userId      │
     │                        │                        │
     │                        │ 3. Create survey       │
     │                        │    Set createdBy=userId│
     │                        ├───────────────────────▶│
     │                        │                        │
     │                        │ 4. Survey saved        │
     │                        │    Return survey ID    │
     │                        │◀───────────────────────┤
     │                        │                        │
     │ 5. Return survey       │                        │
     │    {id, title, desc}   │                        │
     │◀───────────────────────┤                        │
     │                        │                        │
     │ 6. For each question:  │                        │
     │    POST /surveys/{id}  │                        │
     │         /questions     │                        │
     │    {title, type, ...}  │                        │
     ├───────────────────────▶│                        │
     │                        │                        │
     │                        │ 7. Add question        │
     │                        │    to survey           │
     │                        ├───────────────────────▶│
     │                        │                        │
     │                        │ 8. Question saved      │
     │                        │◀───────────────────────┤
     │                        │                        │
     │ 9. Return question     │                        │
     │◀───────────────────────┤                        │
     │                        │                        │
     │ (Repeat 6-9 for each   │                        │
     │  question)             │                        │
     │                        │                        │
```

## 🔄 Flujo de Listado de Encuestas

```
┌─────────┐              ┌─────────┐              ┌──────────┐
│ Browser │              │ Backend │              │ Firebase │
└────┬────┘              └────┬────┘              └────┬─────┘
     │                        │                        │
     │ 1. GET /surveys/       │                        │
     │     my-surveys         │                        │
     │    Authorization:      │                        │
     │    Bearer <token>      │                        │
     ├───────────────────────▶│                        │
     │                        │                        │
     │                        │ 2. Verify JWT          │
     │                        │    Extract userId      │
     │                        │                        │
     │                        │ 3. Query surveys       │
     │                        │    WHERE createdBy=    │
     │                        │          userId        │
     │                        ├───────────────────────▶│
     │                        │                        │
     │                        │ 4. Return surveys      │
     │                        │    with questions      │
     │                        │◀───────────────────────┤
     │                        │                        │
     │ 5. Return survey list  │                        │
     │    [{id, title, ...}]  │                        │
     │◀───────────────────────┤                        │
     │                        │                        │
     │ 6. Render in UI        │                        │
     │                        │                        │
```

## 🗄️ Estructura de Datos en Firestore

### Colección: `users`

```json
{
  "id": "user_abc123",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
  "createdAt": "2025-11-06T10:30:00",
  "updatedAt": "2025-11-06T10:30:00"
}
```

### Colección: `surveys`

```json
{
  "id": "survey_xyz789",
  "title": "Encuesta de Satisfacción",
  "description": "Queremos conocer tu opinión",
  "createdBy": "user_abc123",
  "isPublished": true,
  "createdAt": "2025-11-06T10:35:00",
  "updatedAt": "2025-11-06T10:40:00",
  "questions": [
    {
      "id": "q1",
      "surveyId": "survey_xyz789",
      "title": "¿Cuál es tu nombre?",
      "type": "TEXT",
      "options": [],
      "required": true,
      "order": 0
    },
    {
      "id": "q2",
      "surveyId": "survey_xyz789",
      "title": "¿Cómo calificarías el servicio?",
      "type": "MULTIPLE_CHOICE",
      "options": ["Excelente", "Bueno", "Regular", "Malo"],
      "required": true,
      "order": 1
    },
    {
      "id": "q3",
      "surveyId": "survey_xyz789",
      "title": "¿Qué tan probable es que nos recomiendes?",
      "type": "SCALE",
      "options": [],
      "required": true,
      "order": 2
    }
  ]
}
```

## 🛡️ Seguridad

### JWT Token

**Formato:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyX2FiYzEyMyIsImlhdCI6MTYzNjIwMDAwMCwiZXhwIjoxNjM2Mjg2NDAwfQ.signature
```

**Payload decodificado:**
```json
{
  "sub": "user_abc123",         // userId
  "iat": 1636200000,             // Issued at
  "exp": 1636286400              // Expiration (24h después)
}
```

### Headers de Peticiones Autenticadas

```http
GET /api/surveys/my-surveys HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

### Flujo de Interceptor (Frontend)

```javascript
// Axios interceptor agrega automáticamente el token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

## 📊 Tipos de Preguntas Soportados

| Frontend (kebab-case) | Backend (ENUM)     | Descripción           |
|-----------------------|--------------------|-----------------------|
| `text`                | `TEXT`             | Respuesta abierta     |
| `multiple-choice`     | `MULTIPLE_CHOICE`  | Una opción            |
| `checkbox`            | `CHECKBOX`         | Múltiples opciones    |
| `dropdown`            | `DROPDOWN`         | Lista desplegable     |
| `scale`               | `SCALE`            | Escala numérica       |

## 🔄 Conversión de Tipos (Frontend → Backend)

```javascript
// En surveyStore.ts
const questionType = question.type
  .toUpperCase()           // 'multiple-choice' → 'MULTIPLE-CHOICE'
  .replace(/-/g, '_')      // 'MULTIPLE-CHOICE' → 'MULTIPLE_CHOICE'
```

## 🚀 URLs de la Aplicación

| Servicio  | URL                               | Puerto |
|-----------|-----------------------------------|--------|
| Frontend  | http://localhost:5173             | 5173   |
| Backend   | http://localhost:8080/api         | 8080   |
| Firebase  | https://console.firebase.google.com | Cloud  |

## 📁 Estructura de Archivos Clave

### Backend
```
prueba_davivienda_backend/
├── src/main/java/com/davivienda/survey/
│   ├── domain/model/          # Modelos (User, Survey, Question)
│   ├── domain/port/           # Interfaces de repositorio
│   ├── application/service/   # Lógica de negocio
│   ├── application/dto/       # DTOs de request/response
│   ├── infrastructure/
│   │   ├── adapter/          # Implementación Firebase
│   │   └── security/         # JWT, Security Config
│   └── presentation/
│       └── controller/       # REST Controllers
└── src/main/resources/
    ├── application.properties # Configuración
    └── firebase-config.json   # Credenciales Firebase

### Frontend
```
prueba_davivienda/
├── src/
│   ├── stores/               # Zustand stores
│   │   ├── authStore.ts     # Autenticación
│   │   └── surveyStore.ts   # Encuestas
│   ├── pages/               # Páginas/Rutas
│   │   ├── Login.tsx
│   │   ├── Register.tsx
│   │   ├── Dashboard.tsx
│   │   └── SurveyEditor.tsx
│   ├── ui/components/       # Componentes UI
│   └── domain/              # Tipos TypeScript
└── .env                     # Variables de entorno
```
