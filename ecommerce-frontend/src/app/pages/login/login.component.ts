import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule, MessageModule, ToastModule],
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
  ) {}

  ngOnInit(): void {
    // Verificar si hay un código en los query parameters
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      const state = params['state'];

      if (code) {
        this.handleOAuthCallback(code);
      }
    });
  }

  /**
   * Inicia el flujo de autenticación OAuth
   */
  initiateLogin(): void {
    this.isLoading = true;
    this.authService.initiateOAuthFlow();
  }

  /**
   * Maneja el callback del OAuth
   */
  private handleOAuthCallback(code: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.exchangeCodeForToken(code).subscribe({
      next: (response) => {
        this.authService.processTokenResponse(response);
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
        this.errorMessage = 'Error al obtener el token. Por favor, intente nuevamente.';
        console.error('Error exchanging code for token:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: this.errorMessage,
          life: 5000
        });
      }
    });
  }
}
