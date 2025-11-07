# Guía de Configuración y Pruebas

## 📋 Requisitos Previos

- ✅ Java 17 instalado
- ✅ Maven instalado
- ✅ Node.js 18+ instalado
- ✅ Cuenta de Firebase (gratuita)

## 🔥 Paso 1: Configurar Firebase

### 1.1. Crear Proyecto en Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto" o "Add project"
3. Ingresa un nombre (ej: `davivienda-surveys`)
4. Desactiva Google Analytics (no es necesario)
5. Haz clic en "Crear proyecto"

### 1.2. Habilitar Firestore

1. En el menú lateral, ve a **Build** > **Firestore Database**
2. Haz clic en "Crear base de datos" o "Create database"
3. Selecciona **"Iniciar en modo de producción"** (Production mode)
4. Selecciona la ubicación más cercana (ej: `us-east1`)
5. Haz clic en "Habilitar"

### 1.3. Obtener Credenciales de Service Account

1. Ve a **Configuración del proyecto** (ícono de engranaje junto a "Project Overview")
2. Ve a la pestaña **"Cuentas de servicio"** o **"Service accounts"**
3. Haz clic en **"Generar nueva clave privada"** o **"Generate new private key"**
4. Se descargará un archivo JSON (ej: `davivienda-surveys-firebase-adminsdk-xxxxx.json`)
5. **¡IMPORTANTE!** Renombra este archivo a `firebase-config.json`
6. Copia el archivo a: `prueba_davivienda_backend/src/main/resources/firebase-config.json`

**⚠️ Seguridad:** Este archivo contiene credenciales sensibles. Ya está incluido en `.gitignore` para que no se suba a Git.

### 1.4. Configurar JWT Secret

El JWT secret ya está configurado en `application.properties`:
```properties
jwt.secret=tu_clave_secreta_muy_segura_aqui_cambiar_en_produccion
```

**Para producción:** Cambia esta clave por una más segura. Puedes generar una usando:
```bash
# En PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

## 🚀 Paso 2: Iniciar el Backend

### 2.1. Verificar Configuración

Asegúrate de que existe el archivo:
```
prueba_davivienda_backend/src/main/resources/firebase-config.json
```

### 2.2. Compilar y Ejecutar

Abre una terminal en la carpeta del backend:

```powershell
cd C:\Users\ortiz\OneDrive\Documents\programacion\prueba_davivienda_backend

# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### 2.3. Verificar que Funciona

Deberías ver en la consola:
```
Started SurveyPlatformApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

Prueba en el navegador o Postman:
```
GET http://localhost:8080/api/health
```

## 🎨 Paso 3: Iniciar el Frontend

### 3.1. Instalar Dependencias (si no lo has hecho)

Abre otra terminal en la carpeta del frontend:

```powershell
cd C:\Users\ortiz\OneDrive\Documents\programacion\prueba_davivienda

# Instalar dependencias
npm install
```

### 3.2. Configurar Variables de Entorno (Opcional)

Si quieres cambiar la URL del backend, crea un archivo `.env`:

```powershell
# Copiar ejemplo
Copy-Item .env.example .env
```

El contenido por defecto es:
```env
VITE_API_URL=http://localhost:8080/api
```

### 3.3. Ejecutar Frontend

```powershell
npm run dev
```

Deberías ver:
```
VITE v5.x.x ready in XXX ms

➜  Local:   http://localhost:5173/
```

## 🧪 Paso 4: Probar la Aplicación

### 4.1. Registro de Usuario

1. Abre el navegador en `http://localhost:5173`
2. Deberías ver la pantalla de Login
3. Haz clic en **"¿No tienes cuenta? Regístrate"**
4. Ingresa los datos:
   - **Nombre:** Juan Pérez
   - **Email:** juan@example.com
   - **Contraseña:** Password123!

5. Haz clic en **"Registrarse"**

**Backend:** Puedes verificar en los logs que se ejecuta:
```
POST /api/auth/register
```

**Firebase:** Ve a Firebase Console > Firestore Database
- Deberías ver una colección `users` con el usuario creado

### 4.2. Iniciar Sesión

1. Usa las credenciales que acabas de crear:
   - **Email:** juan@example.com
   - **Contraseña:** Password123!

2. Haz clic en **"Iniciar Sesión"**

**Backend logs:**
```
POST /api/auth/login
Response: { "token": "eyJhbGc...", "userId": "...", ... }
```

**Frontend:** Se redirige al Dashboard automáticamente

### 4.3. Crear una Encuesta

1. En el Dashboard, haz clic en **"+ Nueva Encuesta"**
2. Ingresa:
   - **Título:** Encuesta de Satisfacción
   - **Descripción:** Queremos conocer tu opinión sobre nuestro servicio

3. Agrega preguntas:

   **Pregunta 1:**
   - **Título:** ¿Cuál es tu nombre completo?
   - **Tipo:** Texto
   - **Requerida:** Sí

   **Pregunta 2:**
   - **Título:** ¿Cómo calificarías nuestro servicio?
   - **Tipo:** Opción Múltiple
   - **Opciones:** Excelente, Bueno, Regular, Malo
   - **Requerida:** Sí

   **Pregunta 3:**
   - **Título:** ¿Del 1 al 10, qué tan probable es que nos recomiendes?
   - **Tipo:** Escala
   - **Requerida:** Sí

4. Haz clic en **"Guardar"**

**Backend logs:**
```
POST /api/surveys (crea la encuesta)
POST /api/surveys/{id}/questions (agrega cada pregunta)
```

**Firebase:** Verifica en Firestore:
- Colección `surveys` con la nueva encuesta
- Cada encuesta tiene un array `questions` con las preguntas

### 4.4. Editar y Publicar

1. En el Dashboard, verás tu encuesta creada
2. Haz clic en el botón **"Editar"** (ícono de lápiz)
3. Modifica el título o agrega más preguntas
4. Guarda los cambios
5. Haz clic en **"Publicar"** para activar la encuesta

**Backend logs:**
```
PUT /api/surveys/{id}
PUT /api/surveys/{id}/publish
```

### 4.5. Eliminar Encuesta

1. Haz clic en el botón **"Eliminar"** (ícono de basura)
2. Confirma la eliminación

**Backend logs:**
```
DELETE /api/surveys/{id}
```

## 🔍 Verificación en Firebase

### Ver los Datos Almacenados

1. Ve a Firebase Console > Firestore Database
2. Deberías ver las colecciones:

**Colección `users`:**
```json
{
  "id": "abc123...",
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "$2a$10$...", // Hasheada con BCrypt
  "createdAt": "2025-11-06T10:30:00",
  "updatedAt": "2025-11-06T10:30:00"
}
```

**Colección `surveys`:**
```json
{
  "id": "survey123...",
  "title": "Encuesta de Satisfacción",
  "description": "Queremos conocer tu opinión...",
  "createdBy": "abc123...", // ID del usuario
  "isPublished": true,
  "createdAt": "2025-11-06T10:35:00",
  "updatedAt": "2025-11-06T10:40:00",
  "questions": [
    {
      "id": "q1...",
      "surveyId": "survey123...",
      "title": "¿Cuál es tu nombre completo?",
      "type": "TEXT",
      "required": true,
      "order": 0
    },
    {
      "id": "q2...",
      "surveyId": "survey123...",
      "title": "¿Cómo calificarías nuestro servicio?",
      "type": "MULTIPLE_CHOICE",
      "options": ["Excelente", "Bueno", "Regular", "Malo"],
      "required": true,
      "order": 1
    }
  ]
}
```

## 🐛 Solución de Problemas

### Error: "Cannot connect to backend"

**Problema:** El frontend no puede conectarse al backend

**Solución:**
1. Verifica que el backend esté ejecutándose: `http://localhost:8080/api/health`
2. Revisa la configuración CORS en `application.properties`
3. Verifica que el `.env` del frontend tenga la URL correcta

### Error: "Firebase initialization failed"

**Problema:** El backend no puede conectarse a Firebase

**Solución:**
1. Verifica que existe `src/main/resources/firebase-config.json`
2. Verifica que el JSON tiene la estructura correcta
3. Revisa los logs del backend para más detalles
4. Verifica que Firestore esté habilitado en Firebase Console

### Error: "Invalid credentials" al iniciar sesión

**Problema:** Las credenciales no son correctas

**Solución:**
1. Verifica que el email y contraseña sean correctos
2. Si olvidaste la contraseña, registra un nuevo usuario
3. Verifica en Firebase Console que el usuario existe

### Error: "Token expired"

**Problema:** El token JWT ha expirado

**Solución:**
1. El token dura 24 horas por defecto
2. Cierra sesión y vuelve a iniciar sesión
3. O limpia localStorage: `localStorage.clear()` en la consola del navegador

### Backend no compila

**Problema:** `mvn clean install` falla

**Solución:**
1. Verifica que Java 17 esté instalado: `java -version`
2. Verifica que Maven esté instalado: `mvn -version`
3. Revisa el error específico en los logs
4. Intenta: `mvn clean install -U` para forzar actualización de dependencias

## 📊 Pruebas con Postman

Si prefieres probar el backend directamente con Postman:

### 1. Registro
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Test User",
  "email": "test@example.com",
  "password": "Test123!"
}
```

### 2. Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test123!"
}

Response: 
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "abc123",
  "name": "Test User",
  "email": "test@example.com"
}
```

### 3. Crear Encuesta (con token)
```
POST http://localhost:8080/api/surveys
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "title": "Mi Encuesta",
  "description": "Descripción de la encuesta"
}
```

### 4. Listar Mis Encuestas
```
GET http://localhost:8080/api/surveys/my-surveys
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## ✅ Checklist de Verificación

- [ ] Java 17 instalado
- [ ] Maven instalado
- [ ] Node.js instalado
- [ ] Proyecto Firebase creado
- [ ] Firestore habilitado
- [ ] `firebase-config.json` descargado y copiado
- [ ] Backend compilado sin errores
- [ ] Backend ejecutándose en puerto 8080
- [ ] Frontend con dependencias instaladas
- [ ] Frontend ejecutándose en puerto 5173
- [ ] Usuario registrado exitosamente
- [ ] Login funciona correctamente
- [ ] Encuesta creada y visible en Dashboard
- [ ] Datos visibles en Firebase Console

## 🎉 ¡Listo!

Si completaste todos los pasos, tu aplicación está funcionando correctamente. Ahora puedes:

- Crear múltiples usuarios
- Cada usuario puede crear sus propias encuestas
- Las encuestas se almacenan en Firebase Firestore
- La autenticación es segura con JWT
- El frontend y backend están completamente integrados

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del backend
2. Revisa la consola del navegador (F12)
3. Verifica Firebase Console para ver si los datos se están guardando
4. Revisa la sección de "Solución de Problemas" arriba
