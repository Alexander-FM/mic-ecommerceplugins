import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ButtonModule, CardModule, MessageModule, ToastModule],
  providers: [MessageService],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    // Verificar si hay un código en los query parameters
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      const state = params['state'];

      // Debug: log todos los parámetros recibidos
      console.log('🔍 Query params recibidos:', { code, state, allParams: params });

      if (code) {
        this.handleOAuthCallback(code, state);
      } else if (this.router.url.includes('/auth/callback')) {
        // Retorno de post-logout (sin código OAuth): redirigir a la página principal de productos
        this.router.navigate(['/products']);
      }
    });
  }

  /**
   * Inicia el flujo de autenticación OAuth
   */
  async initiateLogin(): Promise<void> {
    this.isLoading = true;
    try {
      await this.authService.initiateOAuthFlow();
    } catch (error) {
      this.isLoading = false;
      this.errorMessage = 'No se pudo iniciar el flujo OAuth. Intente nuevamente.';
      console.error('Error initiating OAuth flow:', error);
    }
  }

  /**
   * Maneja el callback del OAuth
   */
  private handleOAuthCallback(code: string, state?: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    console.log('🔐 Procesando callback OAuth...');
    console.log('📦 Code:', code.substring(0, 20) + '...');
    console.log('🔒 State recibido:', state);

    const storedState = this.authService.getStoredState();
    console.log('🔍 State almacenado:', storedState);

    if (!state || !storedState || state !== storedState) {
      this.isLoading = false;
      this.errorMessage = 'Estado OAuth invalido. Intente nuevamente.';
      console.error('❌ State mismatch:', { received: state, stored: storedState });
      this.authService.clearPkceState();
      return;
    }

    console.log('✅ State válido. Intercambiando código por token...');

    this.authService.exchangeCodeForToken(code).subscribe({
      next: (response) => {
        console.log('✅ Token recibido exitosamente:', { access_token: response.access_token.substring(0, 20) + '...', expires_in: response.expires_in });
        this.authService.processTokenResponse(response);
        this.authService.clearPkceState();
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Sesión iniciada correctamente',
          life: 3000
        });

        // Redirigir a la página de productos después de 1 segundo
        setTimeout(() => {
          this.router.navigate(['/products']);
        }, 1000);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('❌ Error intercambiando código:', error);

        let errorMsg = 'Error al obtener el token. Por favor, intente nuevamente.';
        if (error.status === 400) {
          errorMsg = 'Código inválido o expirado. Intente de nuevo.';
        } else if (error.status === 401) {
          errorMsg = 'No autorizado. Verifique sus credenciales.';
        } else if (error.status === 0) {
          errorMsg = 'Error de conexión. Verifique que el servidor OAuth esté disponible en http://127.0.0.1:9000';
        } else if (error.error?.error) {
          errorMsg = `${error.error.error}: ${error.error.error_description || ''}`;
        }

        this.errorMessage = errorMsg;
        this.authService.clearPkceState();
        console.error('📝 Detalles del error:', error);

        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMsg,
          life: 5000
        });
      }
    });
  }
}
