import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { SelectModule } from 'primeng/select';
import { StepperModule } from 'primeng/stepper';
import { RegistrationService } from '../../services/registration.service';
import { GenericResponse, RegisterRequestDto, CustomerResponseDto, CustomerRequest } from '../../models/ecommerce.models';
import { catchError, finalize, of } from 'rxjs';


@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, InputTextModule, ButtonModule, CardModule, ToastModule, SelectModule, StepperModule],
  providers: [MessageService],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  isLoading = false;
  activeStep: number = 1;

  form = {
    username: '',
    password: '',
    name: '',
    lastName: '',
    gender: 'M',
    birthdate: '',
    email: '',
    phoneNumberOne: '',
    phoneNumberTwo: '',
    phoneNumberThree: '',
    addressType: 'Home',
    addressName: '',
    residenceNumber: '',
    department: '',
    province: '',
    district: '',
    placeReference: '',
    postalCode: ''
  };

  genderOptions = [
    { label: 'Masculino', value: 'M' },
    { label: 'Femenino', value: 'F' }
  ];

  constructor(
    private registrationService: RegistrationService,
    private messageService: MessageService,
    private router: Router
  ) { }

  submit(): void {
    if (!this.form.username || !this.form.password || !this.form.name || !this.form.lastName || !this.form.email) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Campos incompletos',
        detail: 'Completa los campos obligatorios para continuar'
      });
      return;
    }

    this.isLoading = true;

    // Construimos el payload unificado tal cual lo armaste en Postman
    const payload: RegisterRequestDto = {
      username: this.form.username,
      password: this.form.password,
      customer: {
        name: this.form.name,
        lastName: this.form.lastName,
        gender: this.form.gender,
        birthdate: this.form.birthdate ? new Date(this.form.birthdate).toISOString() : null,
        email: this.form.email,
        phoneNumberOne: this.toNull(this.form.phoneNumberOne),
        phoneNumberTwo: this.toNull(this.form.phoneNumberTwo),
        phoneNumberThree: this.toNull(this.form.phoneNumberThree),
        isActive: true,
        // Nota: Omitimos "userId" porque el backend lo genera e inyecta dinámicamente
        address: {
          type: this.toNull(this.form.addressType),
          addressName: this.toNull(this.form.addressName),
          residenceNumber: this.toNull(this.form.residenceNumber),
          department: this.toNull(this.form.department),
          province: this.toNull(this.form.province),
          district: this.toNull(this.form.district),
          placeReference: this.toNull(this.form.placeReference),
          postalCode: this.toNull(this.form.postalCode)
        }
      }
    };

    // Realizamos una sola llamada al backend
    this.registrationService.register(payload).pipe(
      // finalize se ejecuta al terminar, sin importar si hubo error o éxito
      finalize(() => this.isLoading = false),
      catchError(error => {
        // Atrapamos errores HTTP (ej: 400 Bad Request o 500)
        // El backend envía un JSON con el error real en "error.message"
        const errorMessage = error?.error?.message || error?.message || 'No se pudo completar el registro debido a un error del servidor.';
        this.messageService.add({
          severity: 'error',
          summary: 'Error de Registro',
          detail: errorMessage
        });

        return of(null);
      })
    ).subscribe(response => {
      // Si la respuesta es null, significa que catchError ya manejó el problema
      if (!response) {
        return;
      }

      this.isLoading = false;
      const status = this.getResponseStatus(response);

      if (status === 1) {
        // ¡ÉXITO! Tal como se ve en tu imagen "image_e5a2ff.png"
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: response.message || 'Registro completado con éxito'
        });

        console.log(response.body);

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2500);

      } else if (status === 0) {
        // ADVERTENCIA (rpta = 0)
        this.messageService.add({
          severity: 'warn',
          summary: 'Advertencia',
          detail: response?.message || 'La operación devolvió una advertencia'
        });

      } else {
        // ERROR CONTROLADO (rpta = -1) pero que devolvió HTTP 200 (Ej: Duplicado)
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: response?.message || 'Ocurrió un error al procesar tu solicitud'
        });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/login']);
  }

  // Método auxiliar para extraer el estado de forma segura
  private getResponseStatus<T>(response: GenericResponse<T>): number {
    return typeof response?.rpta === 'number' ? response.rpta : -1;
  }

  // Método auxiliar para evitar strings vacíos ("") y enviarlos como null
  private toNull(value: string): string | null {
    const trimmed = value?.trim();
    return trimmed ? trimmed : null;
  }
}