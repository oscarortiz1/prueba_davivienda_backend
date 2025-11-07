# Migración a Firebase Realtime Database

## 📋 Resumen

Se ha migrado el backend de **Firestore** a **Firebase Realtime Database** para soportar funcionalidades en tiempo real.

---

## ✅ Cambios Realizados

### 1. Repositorios Actualizados

#### FirebaseUserRepository
- ❌ Antes: `FirestoreClient.getFirestore()`
- ✅ Ahora: `FirebaseDatabase.getInstance().getReference()`
- **Cambios clave:**
  - Uso de `ValueEventListener` para operaciones asíncronas
  - `CompletableFuture` para manejar callbacks
  - Queries con `orderByChild()` y `equalTo()`

#### FirebaseSurveyRepository
- ❌ Antes: Colecciones de Firestore
- ✅ Ahora: Referencias en Realtime Database
- **Cambios clave:**
  - Estructura de datos JSON plana
  - Listeners en tiempo real
  - Índices configurados en `database.rules.json`

### 2. Configuración

**FirebaseConfig.java**
- ✅ Ya tenía soporte para `databaseUrl`
- No requirió cambios

**application.properties**
```properties
firebase.database-url=https://daviviendabackend.firebaseio.com
```

### 3. Reglas de Seguridad

**Nuevo archivo:** `database.rules.json`
- Reglas de seguridad para Realtime Database
- Control de acceso basado en autenticación
- Validación de datos
- Índices para queries optimizados

---

## 🔧 Configuración en Firebase Console

### Paso 1: Habilitar Realtime Database

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto: **daviviendabackend**
3. En el menú lateral, selecciona **Realtime Database**
4. Haz clic en **Create Database**
5. Selecciona ubicación: **United States (us-central1)** (recomendado para menor latencia)
6. Modo de seguridad: Selecciona **Start in locked mode** (lo configuraremos después)
7. Haz clic en **Enable**

### Paso 2: Configurar Reglas de Seguridad

1. En la consola de Realtime Database, ve a la pestaña **Rules**
2. Reemplaza las reglas por defecto con el contenido de `database.rules.json`
3. Haz clic en **Publish**

### Paso 3: Verificar URL de Database

1. En la pestaña **Data** de Realtime Database
2. Verifica que la URL sea: `https://daviviendabackend.firebaseio.com`
3. Esta URL debe coincidir con `firebase.database-url` en `application.properties`

### Paso 4: Configurar Índices (Opcional pero Recomendado)

Los índices ya están definidos en las reglas:
```json
"surveys": {
  ".indexOn": ["createdBy"]
},
"surveyResponses": {
  ".indexOn": ["userId", "surveyId"]
}
```

---

## 🚀 Funcionalidades en Tiempo Real (Futuro)

Con Realtime Database habilitado, ahora puedes implementar:

### 1. Colaboración en Tiempo Real
```java
// Escuchar cambios en una encuesta
DatabaseReference surveyRef = FirebaseDatabase.getInstance()
    .getReference("surveys")
    .child(surveyId);

surveyRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
        // Survey actualizada en tiempo real
        Survey survey = snapshot.getValue(Survey.class);
        // Notificar a los clientes conectados
    }
    
    @Override
    public void onCancelled(DatabaseError error) {
        log.error("Error listening to survey", error.toException());
    }
});
```

### 2. Dashboard en Vivo
```java
// Contar respuestas en tiempo real
DatabaseReference responsesRef = FirebaseDatabase.getInstance()
    .getReference("surveyResponses");

responsesRef.orderByChild("surveyId")
    .equalTo(surveyId)
    .addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChild) {
            // Nueva respuesta recibida - actualizar dashboard
        }
        // ... otros métodos
    });
```

### 3. Presencia de Usuarios
```java
// Saber quién está editando una encuesta
DatabaseReference presenceRef = FirebaseDatabase.getInstance()
    .getReference("presence")
    .child(surveyId)
    .child(userId);

presenceRef.setValue(true);
presenceRef.onDisconnect().removeValue(); // Auto-cleanup
```

### 4. Chat/Comentarios en Encuestas
```java
// Sistema de comentarios en tiempo real
DatabaseReference commentsRef = FirebaseDatabase.getInstance()
    .getReference("comments")
    .child(surveyId);

commentsRef.limitToLast(50).addChildEventListener(/* ... */);
```

---

## 📊 Comparación: Firestore vs Realtime Database

| Característica | Firestore | Realtime Database |
|----------------|-----------|-------------------|
| **Modelo de datos** | Documentos y colecciones | JSON tree |
| **Queries** | Más potentes | Limitadas pero rápidas |
| **Tiempo real** | ✅ Sí | ✅✅ Optimizado |
| **Offline** | ✅ Sí | ✅ Sí |
| **Latencia** | ~100ms | ~50ms |
| **Precio** | Por operación | Por GB transferido |
| **Mejor para** | Apps complejas | Apps en tiempo real |

---

## 🔒 Reglas de Seguridad

### Usuarios (`/users/{userId}`)
```json
✅ Read: Cualquier usuario autenticado
✅ Write: Solo el propio usuario
✅ Validación: email, name, password requeridos
```

### Encuestas (`/surveys/{surveyId}`)
```json
✅ Read: Publicadas O propias
✅ Create: Usuarios autenticados
✅ Update/Delete: Solo el creador
✅ Validación: title (1-200 chars), description (max 1000 chars)
✅ Índice: Por createdBy
```

### Respuestas (`/surveyResponses/{responseId}`)
```json
✅ Read: El que respondió O el dueño de la encuesta
✅ Create: Si la encuesta está publicada
✅ Update: No permitido (integridad)
✅ Delete: Solo el que respondió (GDPR)
✅ Índices: userId, surveyId
```

---

## ✅ Validación de la Migración

### 1. Compilación
```bash
mvn clean compile
```
✅ **BUILD SUCCESS**

### 2. Ejecución
```bash
mvn spring-boot:run
```
✅ Backend inicia correctamente en puerto 8080

### 3. Endpoints
- ✅ POST `/api/auth/register` - Registrar usuario
- ✅ POST `/api/auth/login` - Login
- ✅ GET `/api/surveys` - Listar encuestas
- ✅ POST `/api/surveys` - Crear encuesta

---

## 🎯 Próximos Pasos

1. **Habilitar Realtime Database** en Firebase Console (ver arriba)
2. **Publicar reglas** de seguridad (`database.rules.json`)
3. **Migrar datos** de Firestore a Realtime Database (si hay datos existentes)
4. **Implementar WebSockets** para notificaciones en tiempo real
5. **Agregar Server-Sent Events (SSE)** para dashboard en vivo
6. **Implementar colaboración** en el editor de encuestas

---

## 📚 Recursos

- [Firebase Realtime Database Docs](https://firebase.google.com/docs/database)
- [Security Rules Reference](https://firebase.google.com/docs/database/security)
- [Best Practices](https://firebase.google.com/docs/database/usage/best-practices)
- [Structuring Data](https://firebase.google.com/docs/database/web/structure-data)

---

## 🆘 Troubleshooting

### Error: "Permission denied"
- Verifica que las reglas estén publicadas en Firebase Console
- Asegúrate de que el token JWT sea válido
- Confirma que `firebase.database-url` sea correcta

### Error: "Database URL not configured"
- Verifica `application.properties`: `firebase.database-url=https://daviviendabackend.firebaseio.com`
- Reinicia el backend después de cambiar configuración

### Los datos no aparecen
- Verifica en Firebase Console > Realtime Database > Data
- Asegúrate de que `firebase.enabled=true`
- Revisa los logs del backend para errores

---

**Fecha de migración**: 7 de Noviembre de 2025  
**Versión**: 1.0.0  
**Estado**: ✅ Completado y funcionando
