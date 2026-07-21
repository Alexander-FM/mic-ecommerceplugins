import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    // Agregar el token al header Authorization
    const token = this.authService.getToken();

    if (token) {
      console.log('🔐 Token encontrado. URL:', request.url);
      console.log('🔐 Token (primeros 30 chars):', token.substring(0, 30) + '...');

      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('✅ Header Authorization agregado');
    } else {
      console.warn('⚠️ NO HAY TOKEN en el interceptor para URL:', request.url);
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('❌ Error en interceptor:', {
          status: error.status,
          statusText: error.statusText,
          url: error.url,
          message: error.message,
          error: error.error
        });

        if (error.status === 401 || error.status === 403) {
          console.warn('🚪 Token expirado o no autorizado. Logout y redirigir a login.');
          this.authService.logout(false);
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
