# 🚀 Inicio Rápido

## Paso 1: Firebase (5 minutos)

1. Ve a https://console.firebase.google.com/
2. Crea un proyecto nuevo: `davivienda-surveys`
3. Habilita **Firestore Database** (modo producción)
4. Ve a **⚙️ Configuración** > **Cuentas de servicio**
5. Clic en **"Generar nueva clave privada"**
6. Descarga el JSON y renómbralo a: `firebase-config.json`
7. Cópialo a: `prueba_davivienda_backend/src/main/resources/firebase-config.json`

## Paso 2: Backend (2 minutos)

```powershell
cd C:\Users\ortiz\OneDrive\Documents\programacion\prueba_davivienda_backend

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

✅ Debe mostrar: `Started SurveyPlatformApplication on port 8080`

## Paso 3: Frontend (1 minuto)

Abre OTRA terminal:

```powershell
cd C:\Users\ortiz\OneDrive\Documents\programacion\prueba_davivienda

# Si no has instalado dependencias
npm install

# Ejecutar
npm run dev
```

✅ Debe mostrar: `Local: http://localhost:5173/`

## Paso 4: Probar (3 minutos)

1. Abre http://localhost:5173
2. Clic en **"¿No tienes cuenta? Regístrate"**
3. Registra un usuario:
   - Nombre: `Juan Pérez`
   - Email: `juan@test.com`
   - Password: `Test123!`
4. Inicia sesión con esas credenciales
5. Crea una encuesta con título y descripción
6. Agrega preguntas
7. Guarda

✅ Verifica en Firebase Console > Firestore que aparecen:
- Colección `users`
- Colección `surveys`

## 🎯 Credenciales para Probar

**Usuario de Prueba:**
- Email: `juan@test.com`
- Password: `Test123!`

(O usa cualquier email/password que quieras al registrarte)

## ⚠️ Importante

- El backend DEBE estar corriendo en puerto **8080**
- El frontend DEBE estar corriendo en puerto **5173**
- El archivo `firebase-config.json` DEBE existir en `src/main/resources/`

## 🐛 Si algo falla:

```powershell
# Ver logs del backend
# (En la terminal donde ejecutaste mvn spring-boot:run)

# Ver logs del frontend
# Presiona F12 en el navegador > Pestaña Console
```

## 📚 Documentación Completa

Ver: `SETUP.md` para instrucciones detalladas y solución de problemas.
