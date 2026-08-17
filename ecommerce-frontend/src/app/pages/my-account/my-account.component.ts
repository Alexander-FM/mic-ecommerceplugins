import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectModule } from 'primeng/select';
import { DropdownModule } from 'primeng/dropdown';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { CustomerService } from '../../services/customer.service';
import { CustomerResponseDto, CustomerRequest } from '../../models/ecommerce.models';

@Component({
  selector: 'app-my-account',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    ButtonModule,
    DatePickerModule,
    SelectModule,
    ToastModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent implements OnInit {
  customerForm!: FormGroup;
  isLoading = true;
  isSubmitting = false;
  customerId: number = 0;
  userId: number = 0;
  username: string = '';
  formattedAddressName: string | null = null;
  customer: CustomerResponseDto | null = null;

  readonly genderOptions = [
    { label: 'Masculino', value: 'M' },
    { label: 'Femenino', value: 'F' }
  ];

  readonly addressTypeOptions = [
    { label: 'Casa', value: 'Casa' },
    { label: 'Departamento', value: 'Departamento' },
    { label: 'Oficina', value: 'Oficina' },
    { label: 'Otro', value: 'Otro' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerService: CustomerService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCustomerData();
  }

  private initForm(): void {
    this.customerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      gender: ['M', [Validators.required]],
      birthdate: [null as Date | null, [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumberOne: ['', [Validators.required]],
      phoneNumberTwo: [''],
      phoneNumberThree: [''],
      address: this.fb.group({
        id: [null],
        type: ['Casa', [Validators.required]],
        addressName: ['', [Validators.required]],
        residenceNumber: ['', [Validators.required]],
        department: ['', [Validators.required]],
        province: ['', [Validators.required]],
        district: ['', [Validators.required]],
        placeReference: [''],
        postalCode: ['', [Validators.required]]
      })
    });
  }

  private loadCustomerData(): void {
    const authState = this.authService.getAuthState();
    const userObj = authState.user;
    
    this.customerId = Number(userObj?.['customerId'] || userObj?.['id'] || userObj?.['userId']) || 0;

    if (!this.customerId) {
      console.warn('⚠️ No se encontró customerId en authState. Redirigiendo a login...');
      this.showError('No se pudo identificar tu cuenta de usuario.');
      this.isLoading = false;
      this.router.navigate(['/login']);
      return;
    }

    this.isLoading = true;
    this.customerService.getCustomerById(this.customerId).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res?.body) {
          this.customer = res.body;
          this.userId = this.customer.userResponseDto?.id ?? this.customer.id;
          this.username = this.customer.userResponseDto?.username || userObj?.sub || '';
          this.formattedAddressName = this.customer.addressName || null;

          this.populateForm(this.customer);
        } else {
          this.showError('No se pudieron cargar los datos de tu cuenta.');
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error cargando cliente:', err);
        const errorMsg = err?.error?.message || err?.message || 'No se pudieron cargar los datos de tu cuenta.';
        this.showError(errorMsg);
      }
    });
  }

  private normalizeAddressType(type?: string | null): string {
    if (!type) return 'Casa';
    const trimmed = type.trim();
    const lower = trimmed.toLowerCase();
    if (lower === 'casa' || lower === 'home') return 'Casa';
    if (lower === 'departamento' || lower === 'depa' || lower === 'apartment') return 'Departamento';
    if (lower === 'oficina' || lower === 'office') return 'Oficina';
    const match = this.addressTypeOptions.find(o => o.value.toLowerCase() === lower);
    return match ? match.value : 'Otro';
  }

  private populateForm(customer: CustomerResponseDto): void {
    const addr = customer.address;

    this.customerForm.patchValue({
      name: customer.name || '',
      lastName: customer.lastName || '',
      gender: customer.gender || 'M',
      birthdate: this.parseBackendDate(customer.birthdate),
      email: customer.email || '',
      phoneNumberOne: customer.phoneNumberOne || '',
      phoneNumberTwo: customer.phoneNumberTwo || '',
      phoneNumberThree: customer.phoneNumberThree || '',
      address: {
        id: addr?.id || null,
        type: this.normalizeAddressType(addr?.type),
        addressName: addr?.addressName || '',
        residenceNumber: addr?.residenceNumber || '',
        department: addr?.department || '',
        province: addr?.province || '',
        district: addr?.district || '',
        placeReference: addr?.placeReference || '',
        postalCode: addr?.postalCode || ''
      }
    });
  }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      this.customerForm.markAllAsTouched();
      this.showWarning('Por favor, completa todos los campos obligatorios.');
      return;
    }

    this.isSubmitting = true;
    const formVal = this.customerForm.value;
    const addrVal = formVal.address;

    const payload: CustomerRequest = {
      id: this.customer?.id || this.customerId,
      name: formVal.name ? formVal.name.trim() : '',
      lastName: formVal.lastName ? formVal.lastName.trim() : '',
      gender: formVal.gender || 'M',
      birthdate: this.formatBirthdateToIso(formVal.birthdate),
      email: formVal.email ? formVal.email.trim() : '',
      phoneNumberOne: formVal.phoneNumberOne ? formVal.phoneNumberOne.trim() : '',
      phoneNumberTwo: formVal.phoneNumberTwo ? formVal.phoneNumberTwo.trim() : '',
      phoneNumberThree: formVal.phoneNumberThree ? formVal.phoneNumberThree.trim() : '',
      address: {
        id: addrVal.id || this.customer?.address?.id || 1,
        type: addrVal.type || 'Casa',
        addressName: addrVal.addressName ? addrVal.addressName.trim() : '',
        residenceNumber: addrVal.residenceNumber ? addrVal.residenceNumber.trim() : '',
        department: addrVal.department ? addrVal.department.trim() : '',
        province: addrVal.province ? addrVal.province.trim() : '',
        district: addrVal.district ? addrVal.district.trim() : '',
        placeReference: addrVal.placeReference ? addrVal.placeReference.trim() : '',
        postalCode: addrVal.postalCode ? addrVal.postalCode.trim() : ''
      },
      isActive: this.customer?.isActive ?? true,
      userId: this.userId
    };

    this.customerService.updateCustomer(this.customerId, payload).pipe(
      finalize(() => this.isSubmitting = false)
    ).subscribe({
      next: (res) => {
        if (res.rpta === 1 || res.rpta === undefined) {
          this.showSuccess(res.message || 'Operation completed successfully');
          if (res.body) {
            this.customer = res.body;
            this.formattedAddressName = res.body.addressName || this.formattedAddressName;
            this.populateForm(res.body);
          }
        } else {
          this.showError(res.message || 'Ocurrió un error al actualizar los datos.');
        }
      },
      error: (err) => {
        console.error('Error actualizando perfil:', err);
        const errorMsg = err?.error?.message || err?.message || 'Error al actualizar el perfil.';
        this.showError(errorMsg);
      }
    });
  }

  private parseBackendDate(dateStr?: string | null): Date | null {
    if (!dateStr) return null;
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return null;
    return new Date(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate());
  }

  private formatBirthdateToIso(dateVal: any): string | null {
    if (!dateVal) return null;
    const d = dateVal instanceof Date ? dateVal : new Date(dateVal);
    if (isNaN(d.getTime())) return null;
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}T12:00:00.000Z`;
  }

  private toNull(val: string | null | undefined): string | null {
    const trimmed = val?.trim();
    return trimmed ? trimmed : null;
  }

  private showSuccess(message: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Perfil actualizado',
      detail: message,
      life: 3000
    });
  }

  private showWarning(message: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: 'Campos incompletos',
      detail: message,
      life: 3000
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error al actualizar el perfil',
      detail: message,
      life: 4000
    });
  }
}
