# 🚀 Quick Start - Sistema OAuth

## ⚡ En 5 Minutos

### 1. **Inicia el Servidor Frontend**
```bash
cd c:\dev\mic-ecommerceplugins\ecommerce-frontend
npm start
# O: ng serve
```
→ App en: `http://localhost:4200`

### 2. **Asegúrate que el Backend está en ejecución**
```
✓ OAuth Server en: http://127.0.0.1:9090
✓ API Server en: http://127.0.0.1:9089
✓ Token Server en: http://127.0.0.1:9001
```

### 3. **Prueba el Login**
- Abre: `http://localhost:4200`
- Se redirecciona automáticamente a: `/login`
- Haz clic en: "Iniciar sesión con OAuth"
- Inicia sesión en el servidor OAuth
- Serás redirigido a `/products` con sesión activa

### 4. **Verifica que el Token se envía**
- Abre DevTools: `F12`
- Ve a: Network → Busca una request `products`
- Headers → Verifica existe: `Authorization: Bearer ...`

---

## ✅ Checklist Implementación

- ✅ Servicio de autenticación (`auth.service.ts`)
- ✅ Interceptor HTTP (`auth.interceptor.ts`)
- ✅ Guard de rutas (`auth.guard.ts`)
- ✅ Componente Login personalizado con PrimeNG
- ✅ Almacenamiento en localStorage
- ✅ Rutas protegidas
- ✅ Manejo de errores 401

---

## 📂 Archivos Creados/Modificados

```
✨ CREADOS:
  - src/app/models/auth.models.ts
  - src/app/services/auth.service.ts
  - src/app/interceptors/auth.interceptor.ts
  - src/app/guards/auth.guard.ts
  - src/app/pages/login/login.component.ts
  - src/app/pages/login/login.component.html
  - src/app/pages/login/login.component.scss
  - src/app/pages/auth-callback/auth-callback.component.ts
  - src/app/components/navbar/navbar.component.ts
  - src/app/components/navbar/navbar.component.html
  - src/app/components/navbar/navbar.component.scss

🔄 MODIFICADOS:
  - src/app/app.config.ts (agregado interceptor)
  - src/app/app.routes.ts (rutas protegidas + login)
  - src/app/app.component.ts (integrado logout)
  - src/app/app.component.html (añadido toast)
  - src/app/app.component.scss (nuevo layout)

📚 DOCUMENTACIÓN:
  - OAUTH_SETUP.md (guía de configuración)
  - IMPLEMENTATION_SUMMARY.md (resumen técnico)
  - FAQ_AUTHENTICATION.md (preguntas frecuentes)
  - ARCHITECTURE.md (diagramas y arquitectura)
  - QUICK_START.md (este archivo)
```

---

## 🔧 Personalización Rápida

### Cambiar Colores del Login
Edita: `src/app/pages/login/login.component.scss`
```scss
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
// Cambiar a:
background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
```

### Cambiar Texto del Botón
Edita: `src/app/pages/login/login.component.html`
```html
label="Iniciar sesión con OAuth"
<!-- Cambiar a: -->
label="Inicia Sesión Aquí"
```

### Cambiar Mensajes de Notificación
Edita: `src/app/pages/login/login.component.ts`
```typescript
this.messageService.add({
  severity: 'success',
  summary: 'Éxito',  // ← Cambiar
  detail: 'Sesión iniciada correctamente',  // ← Cambiar
  life: 3000
});
```

---

## 🐛 Troubleshooting Rápido

| Problema | Solución |
|---------|----------|
| CORS Error | Asegúrate que backend tiene CORS en `http://localhost:4200` |
| Token no se envía | Verifica que `AuthInterceptor` está en `app.config.ts` |
| Redirige a login después de logout | Normal, es el comportamiento esperado |
| 401 en requests | Token expiró, usuario debe iniciar sesión nuevamente |
| No se ve el botón login | Abre DevTools (F12) y verifica console errors |

---

## 📞 URLs Importantes

| URL | Propósito |
|-----|-----------|
| `http://localhost:4200` | Frontend |
| `http://localhost:4200/login` | Página de login |
| `http://localhost:4200/products` | Productos (protegido) |
| `http://127.0.0.1:9090/oauth2/authorization/maintenance-client` | OAuth Authorization Server |
| `http://127.0.0.1:9001/oauth2/token` | Token Exchange Server |
| `http://127.0.0.1:9089/api/products/active` | API Productos |
| `http://127.0.0.1:9089/api/categories/active` | API Categorías |

---

## 💾 localStorage Keys

```
auth_token           → JWT Access Token
auth_refresh_token   → Refresh Token
auth_id_token        → ID Token
auth_expires_in      → Segundos de expiración (3600)
```

---

## 🎯 Flujo de Usuario Nuevamente

```
1. Usuario accede a http://localhost:4200
                    ↓
2. No tiene token → Se redirige a /login
                    ↓
3. Ve página de login con botón "Iniciar sesión con OAuth"
                    ↓
4. Hace clic → OAuth Server
                    ↓
5. Se autentica → OAuth Server redirige con código
                    ↓
6. Frontend canjea código por token → localStorage
                    ↓
7. Se redirige a /products
                    ↓
8. Puede ver productos, carrito, etc. (con token en headers)
                    ↓
9. Sale de sesión → localStorage se limpia → Vuelve a /login
```

---

## 📊 Request/Response Ejemplo

### Request a API Protegida

```bash
GET http://127.0.0.1:9089/api/products/active HTTP/1.1
Host: 127.0.0.1:9089
Authorization: Bearer eyJraWQiOiJkM2E2OTBkOC01ODU1LTQ2MDItODZlOC0zY2E1OWYwNTk3ZTQiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBbGV4YW5kZXJNYXgiLCJhdWQiOiJtYWludGVuYW5jZS1jbGllbnQiLCJuYmYiOjE3NzA3MDQ2MDEsInNjb3BlIjpbInJlYWQiLCJvcGVuaWQiLCJwcm9maWxlIiwid3JpdGUiXSwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo5MDAxIiwiZXhwIjoxNzcwNzA4MjAxLCJpYXQiOjE3NzA3MDQ2MDEsImp0aSI6IjdkMDNjOTZjLTlhYzctNDBjNS04ZWQ2LWIwZDI0OGIxZTA2ZCJ9.pPdmZ4SzRW_CZWj8yXAw6DRolBr2qW-ZTLOb-6WlJrsHQ2Xha3QlErDwS4ePVC4WLo3wmFQ3U_kPQdQy880yT0XznAmpw6YGa2geBHXCv3u2e6XTYGes1GJeJn71lvjNY8WON2iuPyJnhLiNzd2-WodIETSFEsGVbR8rq8mZroA-Go3IpgTMZ_jkror5fZ0vo0aXeSU60-zwU7o4t6_tgVgtG8P-HCwi845fOIOluKLTZkWWRA8CuIK-IR9D32c2NqraDKWLR2eKL0nejUtB-hrKvgY4GIkhi1KaWnsoJBMk3HyOWe106sZM3Gn4rjh7eD20CCYmPCmoHWbsN8aB_A
Content-Type: application/json
```

### Response en caso de éxito

```json
{
  "rpta": 1,
  "message": "OK",
  "body": [
    {
      "id": 1,
      "name": "Producto 1",
      "price": 99.99,
      "description": "Descripción..."
    }
  ]
}
```

### Response en caso de token expirado

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token has expired"
}
```
→ El interceptor detecta 401 → logout automático → Redirige a /login

---

## ✨ Características Completadas

- ✅ Login OAuth 2.0 completamente funcional
- ✅ Interfaz personalizada con PrimeNG
- ✅ Tokens almacenados en localStorage
- ✅ Autenticación automática en requests
- ✅ Protección de rutas
- ✅ Manejo de expiración de tokens
- ✅ Logout limpio
- ✅ Notificaciones de éxito/error
- ✅ Compilación sin errores

---

## 🎓 Próximos Pasos (Opcionales)

1. **Refresh Token Automation**: Renovar automáticamente si token expira
2. **Remember Me**: Mantener sesión más tiempo
3. **Social Login**: Agregar Google, GitHub, etc.
4. **Two Factor Authentication**: Autenticación de dos factores
5. **Role-Based Access**: Proteger componentes por rol

---

## 📞 Soporte

Si tienes problemas:
1. Revisa la consola del navegador (F12 → Console)
2. Abre DevTools Network y verifica las requests
3. Revisa los archivos de documentación
4. Verifica que el backend está en ejecución en los puertos correctos

---

✅ **¡Listo!** Tu sistema OAuth está completamente implementado y funcional.

