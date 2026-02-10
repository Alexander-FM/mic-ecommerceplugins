# Configuración de Autenticación OAuth 2.0

## Descripción General

La aplicación frontend implementa un flujo de autenticación OAuth 2.0 que permite a los usuarios iniciar sesión de forma segura y obtener tokens de acceso para consumir la API del backend.

## Flujo OAuth 2.0

### 1. Iniciación del Login
- El usuario hace clic en el botón "Iniciar sesión con OAuth" en la página de login
- Se redirige a: `http://127.0.0.1:9090/oauth2/authorization/maintenance-client`

### 2. Obtención del Código
- El backend de autenticación devuelve un código de autorización con estructura:
```json
{
  "rpta": 1,
  "message": "Operation completed successfully",
  "body": {
    "code": "OyQLyOMkYwrm9cESt7y4TYV6z2KnOrlJfz6HFu1jK2Nbbi9YwFlnL8X2soullTEKJvlU3_h9NXT8c_r9sTSW4HgR9z3sGGXsrBM2j0Dp1Jsg9nBErm0Fmk8mnVEyZbbO"
  }
}
```

### 3. Canje del Código por Token
- El frontend hace una solicitud POST a: `http://127.0.0.1:9001/oauth2/token`
- Body (x-www-form-urlencoded):
  - `code`: Código obtenido del paso anterior
  - `grant_type`: `authorization_code`
  - `redirect_uri`: `http://localhost:4200/auth/callback`
  - `client_id`: `maintenance-client`

- Respuesta:
```json
{
  "access_token": "eyJraWQiOi...",
  "refresh_token": "45WeS8yJdkZ4qgJg...",
  "id_token": "eyJraWQiOi...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "read openid profile write"
}
```

### 4. Almacenamiento de Tokens
- Los tokens se guardan en `localStorage`:
  - `auth_token`: Access token (usado en las requests)
  - `auth_refresh_token`: Refresh token
  - `auth_id_token`: ID token
  - `auth_expires_in`: Tiempo de expiración

### 5. Uso de Tokens en Requests
- El interceptor HTTP automáticamente agrega el token al header `Authorization`:
```
Authorization: Bearer <access_token>
```

## Configuración del Backend

### URLs Requeridas

Tu backend debe estar configurado con las siguientes URLs:

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `http://127.0.0.1:9090/oauth2/authorization/maintenance-client` | GET | Inicia el flujo de OAuth (redirección) |
| `http://127.0.0.1:9001/oauth2/token` | POST | Canjea código por token |

### Redirect URI

El `redirect_uri` configurado en la aplicación es:
```
http://localhost:4200/auth/callback
```

**Importante**: Debes registrar este URI en tu servidor OAuth para que acepte redirects desde aquí.

### Client ID

El `client_id` configurado es:
```
maintenance-client
```

Asegúrate de que este cliente esté registrado en tu servidor OAuth con los scopes:
- `read`
- `openid`
- `profile`
- `write`

### CORS Configuration

El servidor debe permitir requests desde `http://localhost:4200`:

```java
@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:3000", "http://localhost:4200", "http://localhost")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH")
      .allowedHeaders("*")
      .allowCredentials(true);
  }
}
```

## Estructura de Archivos Creados

```
src/
├── app/
│   ├── models/
│   │   └── auth.models.ts          # Interfaces de autenticación
│   ├── services/
│   │   └── auth.service.ts         # Servicio de autenticación
│   ├── interceptors/
│   │   └── auth.interceptor.ts     # Interceptor para agregar token
│   ├── guards/
│   │   └── auth.guard.ts           # Guard para proteger rutas
│   ├── pages/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   ├── login.component.html
│   │   │   └── login.component.scss
│   │   └── auth-callback/
│   │       └── auth-callback.component.ts
│   ├── components/
│   │   └── navbar/
│   │       ├── navbar.component.ts
│   │       ├── navbar.component.html
│   │       └── navbar.component.scss
│   ├── app.config.ts               # Configuración actualizada con interceptor
│   └── app.routes.ts               # Rutas actualizadas con login y guard
```

## Flujo en la Aplicación

### 1. Sin Autenticar
- Usuario accede a `http://localhost:4200`
- Se redirecciona a `/login`
- Se muestra el componente LoginComponent con botón "Iniciar sesión con OAuth"

### 2. Autenticación
- Usuario hace clic en el botón
- Se redirige a la URL de OAuth del backend
- Usuario se autentica en el servidor OAuth
- Servidor redirige a `http://localhost:4200/auth/callback?code=...`

### 3. Obtención de Token
- LoginComponent detecta el parámetro `code`
- Realiza intercambio de código por token
- AuthService almacena los tokens en localStorage
- Se redirecciona a `/products`

### 4. Consumo de API Protegida
- Todas las requests incluyen automáticamente el token en el header
- Si recibe error 401/403, se logout automáticamente
- Usuario es redirigido a `/login`

## Protección de Rutas

Las siguientes rutas están protegidas con `AuthGuard`:
- `/products`
- `/cart`

Si un usuario No autenticado intenta acceder, se le redirige a `/login`.

## Manejo de Sesión

### Obtener Estado de Autenticación
```typescript
const isAuthenticated = this.authService.isAuthenticated();
const authState = this.authService.getAuthState();
```

### Obtener Token Actual
```typescript
const token = this.authService.getToken();
```

### Cerrar Sesión
```typescript
this.authService.logout();
```

## Personalización

### Cambiar URLs del OAuth
En `src/app/services/auth.service.ts`:
```typescript
private readonly OAUTH_AUTH_URL = 'http://127.0.0.1:9090/oauth2/authorization/maintenance-client';
private readonly TOKEN_URL = 'http://127.0.0.1:9001/oauth2/token';
private readonly REDIRECT_URI = 'http://localhost:4200/auth/callback';
private readonly CLIENT_ID = 'maintenance-client';
```

### Cambiar el Tema del Login
El login usa **PrimeNG** components. Puedes personalizar el tema en:
- `src/app/pages/login/login.component.scss`
- `src/app.config.ts` (configuración de tema Lara)

### Agregar Más Información del Usuario
La decodificación del JWT ocurre en `AuthService.decodeToken()`. Puedes extender esto para obtener más información del usuario desde el token.

## Resolución de Problemas

### Problema: CORS Error
**Solución**: Asegúrate de que el backend tiene CORS configurado para `http://localhost:4200` en todos los métodos HTTP necesarios.

### Problema: Token no se envía en requests
**Solución**: Verifica que el `AuthInterceptor` está registrado en `app.config.ts`:
```typescript
{
  provide: HTTP_INTERCEPTORS,
  useClass: AuthInterceptor,
  multi: true
}
```

### Problema: Redirige a login después de iniciar sesión
**Solución**: Verifica que el token se obtuvo correctamente y que el `AuthService` está procesando correctamente la respuesta en `processTokenResponse()`.

### Problema: El refresh_token no se usa
**Nota**: Actualmente el código almacena el refresh_token pero no lo usa para renovar tokens expirados. Puedes implementar esto en el `AuthInterceptor` si es necesario.

## Próximos Pasos

1. Registra el `redirect_uri` en tu servidor OAuth
2. Prueba el flujo completo en desarrollo
3. Considera implementar refresh token logic si los tokens tienen corta duración
4. Personaliza el UI del login según tu marca
5. Implementa role-based access control si es necesario

