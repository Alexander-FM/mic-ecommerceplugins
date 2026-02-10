# Arquitectura del Sistema de Autenticación OAuth 2.0

## Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                      APLICACIÓN ANGULAR                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              app.component.ts                             │  │
│  │  (Punto de entrada, maneja navegación principal)         │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              │                                  │
│                              ▼                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           Router (app.routes.ts)                         │  │
│  │  /login (sin protección)                                │  │
│  │  /auth/callback (sin protección)                        │  │
│  │  /products (protegido con AuthGuard)                   │  │
│  │  /cart (protegido con AuthGuard)                       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              │                                  │
│         ┌────────────────────┼────────────────────┐             │
│         ▼                    ▼                    ▼             │
│  ┌────────────────┐  ┌──────────────────┐  ┌──────────────┐   │
│  │ LoginComponent │  │ ProductsComponent│  │ CartComponent│   │
│  │                │  │                  │  │              │   │
│  │ - iniciateOAuth│  │ - loadProducts() │  │ - loadCart() │   │
│  │ - handleCallback                    │  │ - checkout() │   │
│  └────────────────┘  │ - use token      │  └──────────────┘   │
│         ▲            └──────────────────┘         ▲            │
│         │                    ▲                    │            │
│         └────────────────────┼────────────────────┘            │
│                              │                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │             AuthService (auth.service.ts)               │  │
│  │                                                          │  │
│  │  • initiateOAuthFlow()     → Redirige a OAuth server    │  │
│  │  • exchangeCodeForToken()  → POST /oauth2/token         │  │
│  │  • processTokenResponse()  → Guarda en localStorage     │  │
│  │  • getToken()              → Obtiene token              │  │
│  │  • isAuthenticated()       → Verifica si logueado       │  │
│  │  • logout()                → Limpia localStorage        │  │
│  │                                                          │  │
│  │  ┌────────────────────────────────────────────────┐    │  │
│  │  │ localStorage                                   │    │  │
│  │  │  ✓ auth_token:          eyJraWQiOi...       │    │  │
│  │  │  ✓ auth_refresh_token:  45WeS8yJd...        │    │  │
│  │  │  ✓ auth_id_token:       eyJraWQiOi...       │    │  │
│  │  │  ✓ auth_expires_in:     3600                │    │  │
│  │  └────────────────────────────────────────────────┘    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ▲                                  │
│                              │                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         ProductService, CategoryService, etc.           │  │
│  │                                                          │  │
│  │  getProducts(): Observable<GenericResponse<Product[]>> │  │
│  │  ↓ (usa ApiService para hacer requests)                │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ▲                                  │
│                              │                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │         ApiService + HttpClient (Angular)              │  │
│  │                                                          │  │
│  │  - GET/POST/PUT/DELETE                                 │  │
│  │  ↓ (Pasa por el interceptor)                          │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ▲                                  │
│                              │                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │     AuthInterceptor (auth.interceptor.ts)              │  │
│  │                                                          │  │
│  │  intercept(request, next) {                            │  │
│  │    • Obtiene token de AuthService                      │  │
│  │    • Agrega header: Authorization: Bearer <token>      │  │
│  │    • Maneja errores 401/403                           │  │
│  │    • Redirige a /login si token expiró               │  │
│  │  }                                                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              ▲                                  │
│                              │                                  │
└──────────────────────────────┼──────────────────────────────────┘
                               │
                               │ HTTP Requests
                               │ con Token en Header
                               ▼
                    ┌──────────────────────┐
                    │   BACKEND (HTTP)     │
                    ├──────────────────────┤
                    │                      │
        ┌───────────┼───────────┬──────────┤
        ▼           ▼           ▼          ▼
    ┌────────┐  ┌────────┐  ┌────────┐  ┌────────┐
    │ 9001   │  │ 9089   │  │ 9090   │  │ 9090   │
    │ OAuth  │  │ API    │  │ Auth   │  │ OAuth  │
    │ Token  │  │Product │  │Server  │  │Flow    │
    │ Server │  │Server  │  │        │  │Server  │
    └────────┘  └────────┘  └────────┘  └────────┘
       POST        GET/POST    GET        GET
      /token      /api/*      /oauth2/    /oauth2/
                              token      auth/...
```

## Flujo de Autenticación Detallado

```
┌─────────────────────────────────────────────────────────────────┐
│                       FLUJO OAUTH 2.0                          │
└─────────────────────────────────────────────────────────────────┘

PASO 1: Usuario accede a /login
─────────────────────────────────
  App Browser        |       Angular App        |   Backend
  ────────────────────────────────────────────────────────
  http://localhost:4200
        │
        ├─→ no hay token en localStorage
        │
        ├─→ AuthGuard redirige a /login
        │
        └─→ Muestra LoginComponent


PASO 2: Usuario hace clic en "Iniciar sesión"
──────────────────────────────────────────────
  LoginComponent
        │
        ├─→ authService.initiateOAuthFlow()
        │
        └─→ window.location.href = OAUTH_AUTH_URL
                │
                └─→ Se redirige a:
                   http://127.0.0.1:9090/oauth2/authorization/maintenance-client


PASO 3: OAuth Server redirige después de autenticación
────────────────────────────────────────────────────
  OAuth Server        |     Angular Browser
  ────────────────────────────────────────
  Verifica credenciales
        │
        ├─→ Genera código
        │
        └─→ 302 Redirect a: http://localhost:4200/auth/callback?code=ABC123


PASO 4: Callback - Intercambiar código por token
─────────────────────────────────────────────────
  Angular App        |      Backend (9001)
  ────────────────────────────────────────
  Detecta query param "code"
        │
        ├─→ authService.exchangeCodeForToken(code)
        │
        ├─→ POST http://127.0.0.1:9001/oauth2/token
        │
        │   Body (x-www-form-urlencoded):
        │     code=ABC123
        │     grant_type=authorization_code
        │     redirect_uri=http://localhost:4200/auth/callback
        │     client_id=maintenance-client
        │
        ├─← Response:
        │   {
        │     "access_token": "eyJraWQiOi...",
        │     "refresh_token": "45WeS8yJd...",
        │     "id_token": "eyJraWQiOi...",
        │     "token_type": "Bearer",
        │     "expires_in": 3600
        │   }
        │
        └─→ authService.processTokenResponse(response)
                │
                ├─→ localStorage.setItem('auth_token', access_token)
                ├─→ localStorage.setItem('auth_refresh_token', refresh_token)
                ├─→ localStorage.setItem('auth_id_token', id_token)
                ├─→ localStorage.setItem('auth_expires_in', expires_in)
                │
                └─→ router.navigate(['/products'])


PASO 5: Acceso a recursos protegidos
────────────────────────────────────
  ProductsComponent  |  AuthInterceptor  |   Backend API (9089)
  ─────────────────────────────────────────────────────────────
  ngOnInit()
        │
        └─→ productService.getProducts()
                │
                └─→ http.get('/api/products/active')
                        │
                        └─→ AuthInterceptor.intercept()
                                │
                                ├─→ token = authService.getToken()
                                │   (obtiene de localStorage)
                                │
                                ├─→ request.clone({
                                │     setHeaders: {
                                │       Authorization: `Bearer ${token}`
                                │     }
                                │   })
                                │
                                └─→ next.handle(request)
                                        │
                                        └─→ GET /api/products/active
                                            Headers:
                                              Authorization: Bearer eyJraWQiOi...
                                                │
                                                ├─← 200 OK
                                                │   {products: [...]}
                                                │
                                                └─→ Muestra productos en la página


PASO 6: Token expira (después de 1 hora)
────────────────────────────────────────
  Usuario hace una request
        │
        └─→ Backend devuelve 401 Unauthorized
                │
                └─→ AuthInterceptor.intercept() catchError()
                        │
                        ├─→ if (error.status === 401)
                        │
                        ├─→ authService.logout()
                        │   (limpia localStorage)
                        │
                        └─→ router.navigate(['/login'])
                                │
                                └─→ Usuario debe iniciar sesión nuevamente
```

## Estructura de Carpetas

```
src/app/
├── models/
│   └── auth.models.ts              ← Interfaces OAuth
│       └── AuthState
│           ├── isAuthenticated
│           ├── token
│           ├── refreshToken
│           ├── idToken
│           ├── expiresIn
│           └── user
│
├── services/
│   ├── auth.service.ts             ← Lógica de OAuth
│   │   ├── initiateOAuthFlow()
│   │   ├── exchangeCodeForToken()
│   │   ├── processTokenResponse()
│   │   ├── getToken()
│   │   ├── isAuthenticated()
│   │   └── logout()
│   │
│   ├── product.service.ts          ← Usa tokens
│   ├── category.service.ts         ← Usa tokens
│   ├── brand.service.ts            ← Usa tokens
│   └── cart.service.ts             ← Usa tokens
│
├── interceptors/
│   └── auth.interceptor.ts         ← Agrega token automáticamente
│       └── intercept()
│           ├── Obtiene token
│           ├── Agrega Authorization header
│           └── Maneja errores 401/403
│
├── guards/
│   └── auth.guard.ts               ← Protege rutas
│       └── canActivate()
│           ├── Verifica autenticación
│           └── Redirige a /login si no está auth
│
├── pages/
│   ├── login/
│   │   ├── login.component.ts      ← Interfaz OAuth
│   │   ├── login.component.html
│   │   └── login.component.scss
│   │
│   ├── auth-callback/
│   │   └── auth-callback.component.ts ← Maneja callback
│   │
│   ├── products/
│   │   └── products.component.ts   ← Usa token
│   │
│   └── cart/
│       └── cart.component.ts       ← Usa token
│
├── components/
│   └── navbar/
│       ├── navbar.component.ts     ← Opcional
│       ├── navbar.component.html
│       └── navbar.component.scss
│
├── app.config.ts                   ← Registra interceptor
├── app.routes.ts                   ← Define rutas
└── app.component.ts                ← Punto entrada
```

## Requisitos

```
┌─────────────────────────────────────────────────────────────────┐
│                   REQUISITOS BACKEND                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│ OAuth 2.0 Server (Puerto 9001 y 9090)                          │
│ ├─ POST /oauth2/token                                          │
│ │  └─ Body: code, grant_type, redirect_uri, client_id         │
│ │     Response: access_token, refresh_token, id_token, ...    │
│ │                                                              │
│ └─ GET /oauth2/authorization/maintenance-client               │
│    └─ Redirige a login OAuth                                  │
│                                                                 │
│ API Server (Puerto 9089)                                       │
│ ├─ GET /api/products/active                                   │
│ ├─ GET /api/categories/active                                 │
│ ├─ GET /api/brands/active                                     │
│ └─ ... (todas las rutas deben validar token)                 │
│                                                                 │
│ CORS Configurado                                               │
│ ├─ Origen: http://localhost:4200                             │
│ ├─ Métodos: GET, POST, PUT, DELETE, OPTIONS                 │
│ └─ Headers: Authorization, Content-Type, *                    │
│                                                                 │
│ Cliente OAuth Registrado                                       │
│ ├─ client_id: maintenance-client                              │
│ ├─ redirect_uri: http://localhost:4200/auth/callback          │
│ └─ scopes: read, openid, profile, write                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Estado de Ejecución

```
ANTES de iniciar sesión:
──────────────────────
  AuthState {
    isAuthenticated: false,
    token: null,
    refreshToken: null,
    idToken: null,
    expiresIn: null,
    user: null
  }
  
  localStorage: {} (vació)
  
  Rutas accesibles:
    ✓ /login
    ✓ /auth/callback
    ✗ /products → Redirige a /login
    ✗ /cart → Redirige a /login


DESPUÉS de iniciar sesión:
──────────────────────────
  AuthState {
    isAuthenticated: true,
    token: "eyJraWQiOi...",
    refreshToken: "45WeS8yJd...",
    idToken: "eyJraWQiOi...",
    expiresIn: 3600,
    user: {
      sub: "AlexanderMax",
      email: "user@example.com",
      aud: "maintenance-client",
      scope: ["read", "openid", "profile", "write"],
      ...
    }
  }
  
  localStorage: {
    auth_token: "eyJraWQiOi...",
    auth_refresh_token: "45WeS8yJd...",
    auth_id_token: "eyJraWQiOi...",
    auth_expires_in: "3600"
  }
  
  Rutas accesibles:
    ✓ /login
    ✓ /auth/callback
    ✓ /products
    ✓ /cart
    
  Requests:
    ✓ Automáticamente incluyen: Authorization: Bearer <token>
    ✓ Backend valida token
    ✓ Devuelve datos protegidos
```

