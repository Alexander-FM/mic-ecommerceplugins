# Preguntas Frecuentes - Sistema de Autenticación OAuth

## 1. ¿Login por defecto de OAuth o Personalizado con PrimeNG?

### ✅ Decisión: **Personalizado con PrimeNG**

Se ha implementado un login **personalizado con PrimeNG** porque:

**Ventajas:**
- 🎨 Interfaz profesional y moderna
- 🎯 Controla completamente la experiencia del usuario
- 🔧 Fácil de personalizar (colores, textos, estilos)
- 📱 Responsive y mobile-friendly
- 🚀 Mejor User Experience

### Alternativa: Login por Defecto de OAuth

Si prefieres usar el login por defecto del servidor OAuth (usualmente en `http://127.0.0.1:9090/login`), simplemente:

```typescript
// En auth.service.ts, cambiar:
initiateOAuthFlow(): void {
  // Opción A: Ir directamente al servidor OAuth (login por defecto)
  window.location.href = 'http://127.0.0.1:9090/login?client_id=maintenance-client&redirect_uri=...';
  
  // Opción actual (Personalizado): Va a la ruta de autorización que se conecta a oauth2/authorization
  window.location.href = this.OAUTH_AUTH_URL;
}
```

### Personalizar el Login Actual

El login actual está en `src/app/pages/login/login.component.scss`. Puedes cambiar:

```scss
// Cambiar gradiente
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
// Cambiar a:
background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); // Azul
// O simplemente:
background: #007bff; // Color plano
```

Edita `login.component.html` para agregar logo, texto, etc:
```html
<img src="assets/logo.png" alt="Logo" class="logo">
<h1 class="title">Mi E-Commerce</h1>
```

---

## 2. ¿Cómo Se Envía el Token en las Consultas de API?

### 📡 **Automático - Interceptor HTTP**

El token se envía **automáticamente** en todas las requests gracias al `AuthInterceptor`:

#### Cómo Funciona:

```typescript
// En src/app/interceptors/auth.interceptor.ts
intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
  const token = this.authService.getToken();
  
  if (token) {
    // Agrega header automáticamente
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // ← Token se envía aquí
      }
    });
  }
  
  return next.handle(request);
}
```

#### Ejemplo de Request:

```
GET http://127.0.0.1:9089/api/products/active

Headers:
  Authorization: Bearer eyJraWQiOiJkM2E2OTBkOC01ODU1LTQ2MDItODZlOC0zY2E1OWYwNTk3ZTQiLCJhbGciOiJSUzI1NiJ9...
  Content-Type: application/json
```

### ✨ **No Necesitas Hacer Nada**

En tus servicios (`product.service.ts`, `category.service.ts`, etc.), simplemente haz requests normales:

```typescript
// En product.service.ts
getProducts(): Observable<GenericResponse<Product[]>> {
  return this.api.get<Product[]>('/api/products/active');
  // ↑ El token se agrega automáticamente por el interceptor
}
```

### 📍 Ubicación del Token en localStorage

Después del login, el token se guarda automáticamente:

```javascript
// En DevTools Console o inspeccione localStorage
localStorage.getItem('auth_token')
// Devuelve: eyJraWQiOi...

localStorage.getItem('auth_refresh_token')
// Devuelve: 45WeS8yJd...

localStorage.getItem('auth_id_token')
// Devuelve: eyJraWQiOi...
```

---

## 3. Flujo Completo de Consultas

### Ejemplo: Obtener Productos

#### **ANTES** (Sin autenticación - ❌ CORS Error)
```
GET /api/products/active
→ Backend responde 401 (Sin token)
→ Intenta redirigir a OAuth
→ CORS bloquea la redirección en preflight
→ Error: "Redirect is not allowed for a preflight request"
```

#### **AHORA** (Con autenticación - ✅ Funciona)
```
1. Usuario hace login
   - Va a /login
   - Hace clic en botón "Iniciar sesión con OAuth"
   - Se redirecciona a servidor OAuth
   - Usuario se autentica
   - Vuelve a /auth/callback con código

2. Dentro del LoginComponent:
   - Detecta código en query params
   - Llama a AuthService.exchangeCodeForToken(code)
   - Backend devuelve access_token + refresh_token
   - AuthService guarda todo en localStorage
   - Se redirige a /products

3. En ProductsComponent:
   - Llama a productService.getProducts()
   - ProductService hace GET a /api/products/active
   - AuthInterceptor automáticamente agrega header Authorization
   - Request llega con: Authorization: Bearer <token>
   - Backend valida token
   - Devuelve lista de productos ✅

4. En CartComponent:
   - Mismo proceso, todas las requests llevan el token
```

---

## 4. Qué Sucede si el Token Expira

### ⏰ Tiempo de Expiración

Del token que recibiste en el ejemplo:
```json
{
  "expires_in": 3600  // 1 hora en segundos
}
```

### 🔄 Cuando Expira (2 Opciones)

#### **Opción 1: Automático (Implementado)**
```typescript
// En AuthInterceptor
intercept(...) {
  return next.handle(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        // Token expirado
        this.authService.logout(); // Limpia localStorage
        this.router.navigate(['/login']); // Redirige a login
      }
      return throwError(() => error);
    })
  );
}
```

**Flujo:**
1. Token expira
2. Backend devuelve 401
3. Interceptor detecta 401
4. Logout automático
5. Redirige a /login

#### **Opción 2: Usar Refresh Token (Opcional)**
Si quieres renovar el token sin que el usuario inicie sesión nuevamente:

```typescript
// Agregar en AuthInterceptor
if (error.status === 401) {
  const refreshToken = localStorage.getItem('auth_refresh_token');
  
  if (refreshToken) {
    // Implementar POST a /oauth2/token con refresh_token
    // Obtener nuevo access_token
    // Guardar en localStorage
    // Reintentar la request original
  } else {
    // No hay refresh token, hacer logout
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
```

---

## 5. Ejemplo de Uso en Componentes

### ProductsComponent

```typescript
import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/ecommerce.models';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  loading = false;
  errorMessage = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';

    // Esta request automáticamente lleva el token
    this.productService.getActiveProducts().subscribe({
      next: (response) => {
        this.loading = false;
        if (response.body) {
          this.products = response.body;
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Error al cargar productos';
        console.error('Error:', error);
        // Si es 401, el interceptor ya redirigió a /login
      }
    });
  }
}
```

### CartComponent

```typescript
import { Component, OnInit } from '@angular/core';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cartItems$ = this.cartService.cartItems$;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    // El servicio ya está cargando items con token
    this.cartService.loadCart();
  }

  checkout(): void {
    // Esta request también lleva el token automáticamente
    this.cartService.submitOrder().subscribe({
      next: (response) => {
        console.log('Orden enviada:', response);
      },
      error: (error) => {
        console.error('Error en checkout:', error);
      }
    });
  }
}
```

---

## 6. Verificar que el Token se Está Enviando

### En DevTools (F12)

#### **Red → Pestanya Network**
1. Abre Developer Tools (F12)
2. Ve a la pestaña "Network"
3. Recarga la página (/products)
4. Busca una request (ej: GET `/api/products/active`)
5. Haz clic en ella
6. Ve a "Headers"
7. Verifica que existe:
   ```
   Authorization: Bearer eyJraWQiOi...
   ```

#### **Console → localStorage**
```javascript
// En DevTools Console, escribe:
localStorage.getItem('auth_token')

// Deberías ver el token (largo string que empieza con eyJ...)
```

#### **Decodificar JWT** (Opcional)
```javascript
// En DevTools Console:
atob('eyJraWQiOi...'.split('.')[1]) // Decodifica el payload
// Verá información como: {sub: "AlexanderMax", aud: "maintenance-client", iss: "http://127.0.0.1:9001", ...}
```

---

## 7. Checklist PostLogin

Después de que un usuario inicie sesión con OAuth, verifica:

- ✅ `localStorage` contiene `auth_token`
- ✅ `AuthService.isAuthenticated()` devuelve `true`
- ✅ Requests a `/api/products` llevan header `Authorization`
- ✅ Backend devuelve 200 con productos (no 401)
- ✅ Categorías, marcas y otros datos se cargan sin errores CORS

---

## 8. Resumiendo...

| Aspecto | Detalles |
|--------|----------|
| **Login** | Personalizado con PrimeNG en `/login` |
| **Flujo** | OAuth 2.0 estándar |
| **Token en Requests** | Automático via `AuthInterceptor` |
| **Almacenamiento** | `localStorage` con claves: `auth_token`, `auth_refresh_token`, etc |
| **Protección de Rutas** | `AuthGuard` en `/products` y `/cart` |
| **Expiración** | Detectada automáticamente (401 response) |
| **Error CORS** | Se resuelve porque now requests son CON token, no redirecciona |

---

## 9. Las Consultas Ahora Funcionan ✅

**Ejemplo de flujo que now funciona:**

```javascript
// ANTES (❌ Error CORS - redirect):
fetch('http://127.0.0.1:9089/api/products/active')
// → Respuesta: 302 Redirect a OAuth
// → CORS error: "Redirect is not allowed for a preflight request"

// AHORA (✅ Funciona):
fetch('http://127.0.0.1:9089/api/products/active', {
  headers: {
    'Authorization': 'Bearer eyJraWQiOi...'
  }
})
// → Backend valida token
// → Respuesta 200 con productos
// ✅ Éxito!
```

---

¡La autorización está lista! Ahora todas tus consultas a productos, categorías, marcas, etc. funcionarán correctamente. 🚀

