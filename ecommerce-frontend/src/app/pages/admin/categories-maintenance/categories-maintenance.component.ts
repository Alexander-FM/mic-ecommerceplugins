import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { Category, GenericResponse } from '../../../models/ecommerce.models';
import { CategoryService } from '../../../services/category.service';

interface CategoryView extends Category {
  parentId?: number;
  parentDescription?: string;
}

interface DropdownOption {
  label: string;
  value: number;
}

@Component({
  selector: 'app-categories-maintenance',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    InputTextModule,
    DropdownModule,
    CheckboxModule,
    ButtonModule,
    TagModule,
    ToastModule,
    TooltipModule,
    ConfirmDialogModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './categories-maintenance.component.html',
  styleUrl: './categories-maintenance.component.scss'
})
export class CategoriesMaintenanceComponent implements OnInit {
  categoryForm!: FormGroup;
  categories: CategoryView[] = [];
  parentOptions: DropdownOption[] = [];
  isLoading = false;
  isSubmitting = false;
  editCategoryId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadData();
  }

  get isEditMode(): boolean {
    return this.editCategoryId !== null;
  }

  private initializeForm(): void {
    this.categoryForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      parentCategory: [null],
      isActive: [true]
    });
  }

  private loadData(): void {
    this.isLoading = true;

    this.categoryService.getAllCategories().subscribe({
      next: (response: GenericResponse<Category[]>) => {
        const tree = response.body || [];
        this.categories = this.flattenCategories(tree);
        this.loadParentOptions();
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading categories:', error);
        this.showError('No se pudieron cargar las categorías');
        this.isLoading = false;
      }
    });
  }

  private loadParentOptions(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (response: GenericResponse<Category[]>) => {
        const options = this.flattenActiveOptions(response.body || []);
        this.parentOptions = this.editCategoryId
          ? options.filter((item) => item.value !== this.editCategoryId)
          : options;
      },
      error: (error: any) => {
        console.error('Error loading active categories:', error);
      }
    });
  }

  private flattenCategories(tree: Category[], parentDescription?: string, parentId?: number): CategoryView[] {
    const flattened: CategoryView[] = [];

    for (const category of tree) {
      flattened.push({
        id: category.id,
        description: category.description,
        isActive: category.isActive,
        parentId: parentId,
        parentDescription: parentDescription,
        subCategories: category.subCategories
      });

      if (category.subCategories?.length) {
        flattened.push(...this.flattenCategories(category.subCategories, category.description, category.id));
      }
    }

    return flattened;
  }

  private flattenActiveOptions(tree: Category[]): DropdownOption[] {
    const options: DropdownOption[] = [];

    const traverse = (nodes: Category[]) => {
      for (const node of nodes) {
        if (node.isActive && node.id) {
          options.push({
            label: node.description,
            value: node.id
          });
        }

        if (node.subCategories?.length) {
          traverse(node.subCategories);
        }
      }
    };

    traverse(tree);
    return options;
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      this.showWarning('Completa los campos obligatorios');
      return;
    }

    this.isSubmitting = true;
    const formValue = this.categoryForm.value;

    const payload: Partial<Category> = {
      description: formValue.description,
      isActive: formValue.isActive,
      parentCategory: formValue.parentCategory || null
    };

    if (this.isEditMode && this.editCategoryId) {
      this.categoryService.updateCategory(this.editCategoryId, payload).subscribe({
        next: () => {
          this.showSuccess('Categoría actualizada correctamente');
          this.resetForm();
          this.loadData();
          this.isSubmitting = false;
        },
        error: (error: any) => {
          console.error('Error updating category:', error);
          this.showError(error?.error?.message || 'No se pudo actualizar la categoría');
          this.isSubmitting = false;
        }
      });
      return;
    }

    this.categoryService.createCategory(payload).subscribe({
      next: () => {
        this.showSuccess('Categoría registrada correctamente');
        this.resetForm();
        this.loadData();
        this.isSubmitting = false;
      },
      error: (error: any) => {
        console.error('Error creating category:', error);
        this.showError(error?.error?.message || 'No se pudo registrar la categoría');
        this.isSubmitting = false;
      }
    });
  }

  onEdit(category: CategoryView): void {
    this.editCategoryId = category.id || null;
    this.categoryForm.patchValue({
      description: category.description,
      parentCategory: category.parentId ?? null,
      isActive: category.isActive ?? true
    });

    this.loadParentOptions();
  }

  onCancelEdit(): void {
    this.resetForm();
    this.loadParentOptions();
  }

  onToggleStatus(category: CategoryView): void {
    if (!category.id) {
      return;
    }

    const newStatus = !(category.isActive ?? false);

    this.categoryService.updateCategoryStatus(category.id, newStatus).subscribe({
      next: () => {
        this.showSuccess(`Estado actualizado a ${newStatus ? 'activo' : 'inactivo'}`);
        this.loadData();
      },
      error: (error: any) => {
        console.error('Error updating status:', error);
        this.showError(error?.error?.message || 'No se pudo actualizar el estado');
      }
    });
  }

  onDelete(category: CategoryView): void {
    if (!category.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Confirmar eliminación',
      message: `¿Seguro que deseas eliminar la categoría "${category.description}"?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.categoryService.deleteCategory(category.id!).subscribe({
          next: () => {
            this.showSuccess('Categoría eliminada correctamente');
            if (this.editCategoryId === category.id) {
              this.resetForm();
            }
            this.loadData();
          },
          error: (error: any) => {
            console.error('Error deleting category:', error);
            this.showError(error?.error?.message || 'No se pudo eliminar la categoría');
          }
        });
      }
    });
  }

  private resetForm(): void {
    this.editCategoryId = null;
    this.categoryForm.reset({
      description: '',
      parentCategory: null,
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
