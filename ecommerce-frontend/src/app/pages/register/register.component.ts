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
import { TabViewModule } from 'primeng/tabview';
import { RegistrationService } from '../../services/registration.service';
import { Role, UserRequest, CustomerRequest, GenericResponse } from '../../models/ecommerce.models';
import { catchError, map, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, InputTextModule, ButtonModule, CardModule, ToastModule, SelectModule, TabViewModule],
  providers: [MessageService],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  isLoading = false;

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
  ) {}

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

    this.registrationService.getRoles().pipe(
      map(response => {
        const validated = this.validateResponse(response, 'No se pudo obtener los roles');
        return validated ? this.findUserRole(validated.body) : null;
      }),
      switchMap(role => {
        if (!role) {
          return of(null);
        }

        if (!role) {
          throw new Error('No se encontró el rol USER');
        }

        const userPayload: UserRequest = {
          username: this.form.username,
          password: this.form.password,
          isActive: true,
          roles: [role]
        };

        return this.registrationService.createUser(userPayload).pipe(
          map(response => this.validateResponse(response, 'No se pudo crear el usuario'))
        );
      }),
      switchMap(userResponse => {
        if (!userResponse) {
          return of(null);
        }

        if (!userResponse.body || !userResponse.body.id) {
          throw new Error('No se pudo crear el usuario');
        }

        const phoneNumberOne = this.toNull(this.form.phoneNumberOne);
        const phoneNumberTwo = this.toNull(this.form.phoneNumberTwo);
        const phoneNumberThree = this.toNull(this.form.phoneNumberThree);

        const customerPayload: CustomerRequest = {
          name: this.form.name,
          lastName: this.form.lastName,
          gender: this.form.gender,
          birthdate: this.form.birthdate ? new Date(this.form.birthdate).toISOString() : null,
          email: this.form.email,
          phoneNumberOne,
          phoneNumberTwo,
          phoneNumberThree,
          address: {
            type: this.toNull(this.form.addressType),
            addressName: this.toNull(this.form.addressName),
            residenceNumber: this.toNull(this.form.residenceNumber),
            department: this.toNull(this.form.department),
            province: this.toNull(this.form.province),
            district: this.toNull(this.form.district),
            placeReference: this.toNull(this.form.placeReference),
            postalCode: this.toNull(this.form.postalCode)
          },
          isActive: true,
          userId: userResponse.body.id
        };

        return this.registrationService.createCustomer(customerPayload).pipe(
          map(response => this.validateResponse(response, 'No se pudo crear el cliente'))
        );
      }),
      catchError(error => {
        this.isLoading = false;
        const message = error?.error?.message || error?.message || 'No se pudo completar el registro';
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: message
        });
        return of(null);
      })
    ).subscribe(result => {
      if (!result) {
        return;
      }

      this.isLoading = false;
      const message = result?.message || 'Registro exitoso';
      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: message
      });

      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 1500);
    });
  }

  cancel(): void {
    this.router.navigate(['/login']);
  }

  private findUserRole(roles: Role[] | null | undefined): Role | null {
    if (!roles) {
      return null;
    }

    return roles.find(role => role.description === 'USER') || null;
  }

  private validateResponse<T>(
    response: GenericResponse<T>,
    fallbackMessage: string
  ): GenericResponse<T> | null {
    const status = this.getResponseStatus(response);

    if (status === 1) {
      return response;
    }

    if (status === 0) {
      this.isLoading = false;
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: response?.message || 'La operacion devolvio una advertencia'
      });
      return null;
    }

    throw new Error(response?.message || fallbackMessage);
  }

  private getResponseStatus<T>(response: GenericResponse<T>): number {
    return typeof response?.rpta === 'number' ? response.rpta : -1;
  }

  private toNull(value: string): string | null {
    const trimmed = value?.trim();
    return trimmed ? trimmed : null;
  }
}
