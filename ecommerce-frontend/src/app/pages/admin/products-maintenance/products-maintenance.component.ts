import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AdminService } from '../../../services/admin.service';
import { GenericResponse, Product } from '../../../models/ecommerce.models';

@Component({
  selector: 'app-products-maintenance',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    TagModule,
    TooltipModule,
    ConfirmDialogModule,
    ToastModule,
    InputTextModule,
    DropdownModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './products-maintenance.component.html',
  styleUrl: './products-maintenance.component.scss'
})
export class ProductsMaintenanceComponent implements OnInit {
  products: Product[] = [];
  isLoading = false;
  searchText = '';
  statusFilter: 'all' | 'active' | 'inactive' = 'all';
  readonly statusOptions = [
    { label: 'Todos', value: 'all' },
    { label: 'Activos', value: 'active' },
    { label: 'Inactivos', value: 'inactive' }
  ];

  get filteredProducts(): Product[] {
    const search = this.searchText.trim().toLowerCase();

    return this.products.filter((product) => {
      const matchSearch = !search || [
        product.barCode,
        product.name,
        product.categoryName,
        product.brandName
      ]
        .filter((value) => !!value)
        .some((value) => (value || '').toLowerCase().includes(search));

      const matchStatus =
        this.statusFilter === 'all' ||
        (this.statusFilter === 'active' && !!product.isActive) ||
        (this.statusFilter === 'inactive' && !product.isActive);

      return matchSearch && matchStatus;
    });
  }

  constructor(
    private router: Router,
    private adminService: AdminService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  goToAddProduct(): void {
    this.router.navigate(['/admin/products/add']);
  }

  clearFilters(): void {
    this.searchText = '';
    this.statusFilter = 'all';
  }

  onEdit(product: Product): void {
    if (!product.id) {
      return;
    }
    this.router.navigate(['/admin/products/edit', product.id]);
  }

  onToggleStatus(product: Product): void {
    if (!product.id) {
      return;
    }

    const newStatus = !(product.isActive ?? false);
    this.adminService.updateProductStatus(product.id, newStatus).subscribe({
      next: () => {
        this.showSuccess(`Producto ${newStatus ? 'activado' : 'desactivado'} correctamente`);
        this.loadProducts();
      },
      error: (error: any) => {
        console.error('Error updating product status:', error);
        this.showError(error?.error?.message || 'No se pudo actualizar el estado del producto');
      }
    });
  }

  onDelete(product: Product): void {
    if (!product.id) {
      return;
    }

    this.confirmationService.confirm({
      header: 'Confirmar eliminación',
      message: `¿Seguro que deseas eliminar el producto "${product.name}"?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.adminService.deleteProduct(product.id!).subscribe({
          next: () => {
            this.showSuccess('Producto eliminado correctamente');
            this.loadProducts();
          },
          error: (error: any) => {
            console.error('Error deleting product:', error);
            this.showError(error?.error?.message || 'No se pudo eliminar el producto');
          }
        });
      }
    });
  }

  private loadProducts(): void {
    this.isLoading = true;
    this.adminService.getAllProducts().subscribe({
      next: (response: GenericResponse<Product[]>) => {
        this.products = response.body || [];
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading products:', error);
        this.showError('No se pudieron cargar los productos');
        this.isLoading = false;
      }
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

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 4000
    });
  }
}
