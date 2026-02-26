import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BrandService } from '../../../services/brand.service';
import { Brand, GenericResponse } from '../../../models/ecommerce.models';

@Component({
  selector: 'app-brands-maintenance',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    TagModule,
    InputTextModule,
    CheckboxModule,
    ButtonModule,
    ToastModule,
    TooltipModule,
    ConfirmDialogModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './brands-maintenance.component.html',
  styleUrl: './brands-maintenance.component.scss'
})
export class BrandsMaintenanceComponent implements OnInit {
  brandForm!: FormGroup;
  brands: Brand[] = [];
  isLoading = false;
  isSubmitting = false;
  editBrandId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private brandService: BrandService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadBrands();
  }

  get isEditMode(): boolean {
    return this.editBrandId !== null;
  }

  private initializeForm(): void {
    this.brandForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      isActive: [true]
    });
  }

  private loadBrands(): void {
    this.isLoading = true;
    this.brandService.getAllBrands().subscribe({
      next: (response: GenericResponse<Brand[]>) => {
        this.brands = response.body || [];
        this.isLoading = false;
      },
      error: () => {
        this.showError('No se pudieron cargar las marcas');
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.brandForm.invalid) {
      this.brandForm.markAllAsTouched();
      this.showWarning('Completa los campos obligatorios');
      return;
    }

    this.isSubmitting = true;
    const payload: Partial<Brand> = {
      description: this.brandForm.value.description,
      isActive: this.brandForm.value.isActive
    };

    if (this.isEditMode && this.editBrandId) {
      this.brandService.updateBrand(this.editBrandId, payload).subscribe({
        next: () => {
          this.showSuccess('Marca actualizada correctamente');
          this.resetForm();
          this.loadBrands();
          this.isSubmitting = false;
        },
        error: (error: any) => {
          console.error('Error updating brand:', error);
          this.showError(error?.error?.message || 'No se pudo actualizar la marca');
          this.isSubmitting = false;
        }
      });
      return;
    }

    this.brandService.createBrand(payload).subscribe({
      next: () => {
        this.showSuccess('Marca registrada correctamente');
        this.resetForm();
        this.loadBrands();
        this.isSubmitting = false;
      },
      error: (error: any) => {
        console.error('Error creating brand:', error);
        this.showError(error?.error?.message || 'No se pudo registrar la marca');
        this.isSubmitting = false;
      }
    });
  }

  onEdit(brand: Brand): void {
    this.editBrandId = brand.id || null;
    this.brandForm.patchValue({
      description: brand.description,
      isActive: brand.isActive ?? true
    });
  }

  onCancelEdit(): void {
    this.resetForm();
  }

  onToggleStatus(brand: Brand): void {
    if (!brand.id) {
      return;
    }

    const newStatus = !(brand.isActive ?? false);
    this.brandService.updateBrandStatus(brand.id, newStatus).subscribe({
      next: () => {
        this.showSuccess(`Estado actualizado a ${newStatus ? 'activo' : 'inactivo'}`);
        this.loadBrands();
      },
      error: (error: any) => {
        console.error('Error updating brand status:', error);
        this.showError(error?.error?.message || 'No se pudo actualizar el estado');
      }
    });
  }

  onDelete(brand: Brand): void {
    if (!brand.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Confirmar eliminación',
      message: `¿Seguro que deseas eliminar la marca "${brand.description}"?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.brandService.deleteBrand(brand.id!).subscribe({
          next: () => {
            this.showSuccess('Marca eliminada correctamente');
            if (this.editBrandId === brand.id) {
              this.resetForm();
            }
            this.loadBrands();
          },
          error: (error: any) => {
            console.error('Error deleting brand:', error);
            this.showError(error?.error?.message || 'No se pudo eliminar la marca');
          }
        });
      }
    });
  }

  private resetForm(): void {
    this.editBrandId = null;
    this.brandForm.reset({
      description: '',
      isActive: true
    });
  }

  private showSuccess(message: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: message,
      life: 3000
    });
  }

  private showWarning(message: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: 'Advertencia',
      detail: message,
      life: 3000
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 4000
    });
  }
}
