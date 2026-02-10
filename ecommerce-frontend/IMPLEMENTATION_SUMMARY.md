# Implementación Completada: Sistema de Autenticación OAuth 2.0

## 📋 Resumen

Se ha implementado un sistema completo de autenticación OAuth 2.0 en tu aplicación Angular ecommerce. El sistema permite que los usuarios inicien sesión de forma segura, obtengan tokens de acceso y consuman la API del backend con autenticación.

---

## ✅ Archivos Creados

### 1. **Modelos** (`src/app/models/auth.models.ts`)
- Interfaces para las respuestas OAuth
- Interfaz para el estado de autenticación
- Interfaz para información del usuario

### 2. **Servicio de Autenticación** (`src/app/services/auth.service.ts`)
- Maneja el flujo completo de OAuth 2.0
- Almacena tokens en `localStorage`
- Proporciona métodos para obtener estado y tokens
- Implementa logout

### 3. **Interceptor HTTP** (`src/app/interceptors/auth.interceptor.ts`)
- Agrega automáticamente el token `Authorization` a todas las requests
- Maneja errores 401/403 (token expirado)
- Redirige a login en caso de autenticación fallida

### 4. **Guard de Rutas** (`src/app/guards/auth.guard.ts`)
- Protege rutas que requieren autenticación
- Redirige a `/login` si no está autenticado

### 5. **Componente de Login** (`src/app/pages/login/`)
- Interfaz personalizada con **PrimeNG**
- Botón "Iniciar sesión con OAuth"
- Maneja el callback con el código de autorización
- Canjea el código por tokens automáticamente
- Notificaciones de éxito/error

### 6. **Componente Callback** (`src/app/pages/auth-callback/`)
- Redirige a LoginComponent para procesar el callback

### 7. **Navbar Componente** (`src/app/components/navbar/`)
- Componente opcional para mostrar usuario y logout (no se usa actualmente)

---

## 🔧 Archivos Modificados

### 1. **app.config.ts**
- Agregado `HTTP_INTERCEPTORS` para el `AuthInterceptor`
- Agregado `ToastModule` para notificaciones

### 2. **app.routes.ts**
- Agregadas rutas `/login` y `/auth/callback`
- Protegidas rutas `/products` y `/cart` con `AuthGuard`

### 3. **app.component.ts**
- Integrado `AuthService` para logout
- Agregado check en `ngOnInit()` para verificar autenticación
- Método `logout()` con notificación

### 4. **app.component.html**
- Agregado `<p-toast>` para notificaciones
- Wrap de `<router-outlet>` en contenedor

### 5. **app.component.scss**
- Estilos para `.app-container` y `.router-container`

---

## 🔐 Flujo de Autenticación

### **Paso 1: Usuario accede a la aplicación**
```
http://localhost:4200 → Redirige a /login
```

### **Paso 2: Usuario hace clic en "Iniciar sesión con OAuth"**
```
Button Click → AuthService.initiateOAuthFlow()
              → Redirige a: http://127.0.0.1:9090/oauth2/authorization/maintenance-client
```

### **Paso 3: Usuario se autentica en el servidor OAuth**
```
Backend OAuth Server
↓
Verifica credenciales
↓
Genera código de autorización
↓
Redirige a: http://localhost:4200/auth/callback?code=...
```

### **Paso 4: Frontend canjea código por token**
```
LoginComponent detecta query param "code"
↓
AuthService.exchangeCodeForToken(code)
↓
POST a: http://127.0.0.1:9001/oauth2/token
Body (x-www-form-urlencoded):
  - code: <código authorization>
  - grant_type: authorization_code
  - redirect_uri: http://localhost:4200/auth/callback
  - client_id: maintenance-client
↓
Recibe: {access_token, refresh_token, id_token, expires_in, ...}
```

### **Paso 5: Almacenamiento de tokens**
```
localStorage.setItem('auth_token', access_token)
localStorage.setItem('auth_refresh_token', refresh_token)
localStorage.setItem('auth_id_token', id_token)
localStorage.setItem('auth_expires_in', expires_in)
↓
AuthService.authState$ actualizado
↓
Redirige a /products
```

### **Paso 6: Consumo de API protegida**
```
GET /api/products
↓
AuthInterceptor agrega header:
Authorization: Bearer <access_token>
↓
Backend valida token
↓
Responde con datos
```

---

## 🚀 Cómo Usar

### **1. Iniciar la Aplicación**
```bash
npm start
# O
ng serve
```
Accede a: `http://localhost:4200`

### **2. Login**
- Se redirigirá a `/login` automáticamente
- Haz clic en "Iniciar sesión con OAuth"
- Serás redirigido al backend de OAuth para autenticar
- Después de autenticarte, volverás a la aplicación con sesión activa

### **3. Acceder a Páginas Protegidas**
- `/products` - Requiere autenticación
- `/cart` - Requiere autenticación
- `/login` - Accesible sin autenticación

### **4. Logout**
- Haz clic en el botón de logout en la navbar (esquina superior derecha)
- Se limpian los tokens y se redirige a `/login`

---

## 🔧 Configuración del Backend

**Importante**: Asegúrate de que tu backend está configurado correctly:

### 1. **Registrar el Redirect URI**
El cliente OAuth debe tener registrado:
```
http://localhost:4200/auth/callback
```

### 2. **Registrar el Client ID**
El cliente debe estar registrado con:
```
client_id: maintenance-client
```

Con estos scopes:
- `read`
- `openid`
- `profile`
- `write`

### 3. **Endpoints Necesarios**
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `http://127.0.0.1:9090/oauth2/authorization/maintenance-client` | GET | Inicia OAuth flow |
| `http://127.0.0.1:9001/oauth2/token` | POST | Canjea código por token |

### 4. **CORS Configurado**
Asegúrate que el CORS está configurado:
```java
.allowedOrigins("http://localhost:4200")
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
```

---

## 📝 Personalización

### **Cambiar URLs del OAuth**
En `src/app/services/auth.service.ts`:
```typescript
private readonly OAUTH_AUTH_URL = 'http://127.0.0.1:9090/oauth2/authorization/maintenance-client';
private readonly TOKEN_URL = 'http://127.0.0.1:9001/oauth2/token';
private readonly REDIRECT_URI = 'http://localhost:4200/auth/callback';
private readonly CLIENT_ID = 'maintenance-client';
```

### **Cambiar Tema del Login**
Edita `src/app/pages/login/login.component.scss` para personalizar los colores y estilos.

### **Agregar Más Información del Usuario**
En `AuthService.decodeToken()`, puedes extraer información adicional del JWT:
```typescript
const decoded = JSON.parse(atob(token.split('.')[1]));
// Aquí puedes acceder a: decoded.sub, decoded.email, decoded.name, etc.
```

---

## 🐛 Troubleshooting

### **Problema: "CORS error" al iniciar login**
**Solución**: Verifica que el backend tiene CORS configurado para `http://localhost:4200`:
```java
registry.addMapping("/**")
  .allowedOrigins("http://localhost:4200")
  .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
  .allowedHeaders("*")
  .allowCredentials(true);
```

### **Problema: "Token no se envía en requests"**
**Solución**: Verifica que `AuthInterceptor` está registrado en `app.config.ts`:
```typescript
{
  provide: HTTP_INTERCEPTORS,
  useClass: AuthInterceptor,
  multi: true
}
```

### **Problema: "Redirige a login después de iniciar sesión"**
**Solución**: 
1. Verifica que recibiste el `access_token` correctamente
2. Asegúrate que el `token` se está decodificando bien en `processTokenResponse()`
3. Verifica logs en la consola del navegador

### **Problema: "401 Unauthorized"**
**Soluciones**:
1. Verifica que el token se está enviando correctamente
2. Verifica que el token no ha expirado
3. Verifica que el backend está validando el token correctamente

---

## 📚 Documentación Adicional

- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Angular HTTP Interceptors](https://angular.io/guide/http#intercepting-requests-and-responses)
- [PrimeNG Buttons](https://primeng.org/button)
- [PrimeNG Cards](https://primeng.org/card)

---

## ✨ Características Implementadas

- ✅ Flujo OAuth 2.0 completo
- ✅ Almacenamiento seguro de tokens en localStorage
- ✅ Interceptor HTTP automático
- ✅ Protección de rutas con Guards
- ✅ UI personalizada con PrimeNG
- ✅ Manejo de errores y notificaciones
- ✅ Decodificación de JWT
- ✅ Logout con limpieza de datos
- ✅ Soporte para refresh tokens (almacenado, listo para implementar lógica de refresh)

---

## 🎯 Próximos Pasos Opcionales

1. **Implementar Refresh Token**: Usar el `refresh_token` para renovar el `access_token` automáticamente
2. **Interceptar errores 401**: Automáticamente refrescar token en caso de expiración
3. **Persistencia mejorada**: Encriptar tokens antes de guardar en localStorage
4. **Role-based Access Control (RBAC)**: Proteger rutas basándose en roles del usuario
5. **Loading States**: Mostrar spinners mientras se procesan requests
6. **Validación de token**: Decodificar y validar JWT antes de usarlo

---

## 📞 Soporte

Si tienes problemas con la implementación, verifica:
1. Que el backend está ejecutándose en los puertos correctos
2. Que CORS está configurado correctamente
3. Que los endpoints están correctos
4. Los logs en la consola del navegador
5. Las pestañas Network en DevTools para ver las requests

