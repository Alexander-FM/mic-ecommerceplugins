import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TooltipModule } from 'primeng/tooltip';
import { MessageService } from 'primeng/api';
import { OrderService } from '../../../services/order.service';
import { AuthService } from '../../../services/auth.service';
import { OrderResponse, OrderStatus, OrderStatusUpdateRequest } from '../../../models/ecommerce.models';

interface StatusOption {
  label: string;
  value: string | null;
}

interface DialogStatusOption {
  label: string;
  value: number;
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    CardModule,
    TagModule,
    ButtonModule,
    ToastModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    ProgressSpinnerModule,
    TooltipModule
  ],
  providers: [MessageService],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.scss']
})
export class AdminOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  statuses: OrderStatus[] = [];
  statusOptions: StatusOption[] = [];
  dialogStatusOptions: DialogStatusOption[] = [];

  // Filtros
  searchTerm: string = '';
  selectedStatus: string | null = null;
  startDate: string | null = null;
  endDate: string | null = null;

  isLoading = true;

  // Modal para Cambiar Estado
  showStatusDialog = false;
  isSavingStatus = false;
  selectedOrder: OrderResponse | null = null;
  newStatusId: number | null = null;
  observation: string = '';

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadStatuses();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.rpta === 1 && response.body) {
          this.orders = response.body.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime());
        } else {
          this.orders = [];
        }
        this.applyFilters();
      },
      error: (err) => {
        this.isLoading = false;
        this.orders = [];
        this.filteredOrders = [];
        const msg = err.error?.message || 'No se pudieron cargar las órdenes.';
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: msg
        });
      }
    });
  }

  loadStatuses(): void {
    this.orderService.getOrderStatuses().subscribe({
      next: (response) => {
        if (response.rpta === 1 && response.body) {
          this.statuses = response.body.filter(s => s.isActive);
          
          this.statusOptions = [
            { label: 'Todos los estados', value: null },
            ...this.statuses.map(s => ({ label: s.statusName, value: s.statusName }))
          ];

          this.dialogStatusOptions = this.statuses.map(s => ({
            label: s.statusName,
            value: s.id
          }));
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'warn',
          summary: 'Advertencia',
          detail: 'No se pudieron cargar los estados de pedidos.'
        });
      }
    });
  }

  applyFilters(): void {
    let result = [...this.orders];

    // 1. Filtro por Número de Pedido (order.id)
    const cleanSearch = this.searchTerm.trim().replace(/^#/, '');
    if (cleanSearch) {
      const exactMatchExists = result.some(o => o.id.toString() === cleanSearch);
      if (exactMatchExists) {
        result = result.filter(o => o.id.toString() === cleanSearch);
      } else {
        result = result.filter(o => o.id.toString().includes(cleanSearch));
      }
    }

    // 2. Filtro por Estado
    if (this.selectedStatus) {
      result = result.filter(o => o.orderStatusName === this.selectedStatus);
    }

    // 3. Filtro por Rango de Fechas (Desde - Hasta)
    if (this.startDate) {
      const start = new Date(this.startDate);
      start.setHours(0, 0, 0, 0);
      result = result.filter(o => new Date(o.orderDate) >= start);
    }

    if (this.endDate) {
      const end = new Date(this.endDate);
      end.setHours(23, 59, 59, 999);
      result = result.filter(o => new Date(o.orderDate) <= end);
    }

    // Mantener orden descendente por fecha
    result.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime());

    this.filteredOrders = result;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatus = null;
    this.startDate = null;
    this.endDate = null;
    this.applyFilters();
  }

  openChangeStatusDialog(order: OrderResponse): void {
    this.selectedOrder = order;
    const current = this.statuses.find(s => s.statusName === order.orderStatusName);
    this.newStatusId = current ? current.id : null;
    this.observation = '';
    this.showStatusDialog = true;
  }

  saveStatusChange(): void {
    if (!this.selectedOrder) return;

    if (!this.newStatusId) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Campo requerido',
        detail: 'Por favor selecciona el nuevo estado.'
      });
      return;
    }

    if (!this.observation.trim()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Campo requerido',
        detail: 'Por favor ingresa una observación para el cambio de estado.'
      });
      return;
    }

    const authState = this.authService.getAuthState();
    const changedBy = authState.user?.['sub'];

    if (!changedBy) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error de autenticación',
        detail: 'No se pudo obtener la identidad del usuario en el token.'
      });
      return;
    }

    this.isSavingStatus = true;

    const request: OrderStatusUpdateRequest = {
      newStatusId: this.newStatusId,
      changedBy: changedBy,
      observation: this.observation.trim()
    };

    this.orderService.updateOrderStatus(this.selectedOrder.id, request).subscribe({
      next: (response) => {
        this.isSavingStatus = false;
        this.showStatusDialog = false;

        this.messageService.add({
          severity: 'success',
          summary: 'Estado actualizado',
          detail: response.message || 'El estado del pedido fue actualizado exitosamente.'
        });

        // Volver a cargar desde el backend para garantizar consistencia real
        this.loadOrders();
      },
      error: (err) => {
        this.isSavingStatus = false;
        const msg = err.error?.message || err.error?.detail || err.message || 'Ocurrió un error al actualizar el estado.';
        this.messageService.add({
          severity: 'error',
          summary: 'Error al actualizar el estado',
          detail: msg
        });
      }
    });
  }

  getOrderStatusSeverity(status: string): any {
    switch (status) {
      case 'Pendiente':
        return 'warn';
      case 'Recepcionado':
        return 'info';
      case 'Preparando pedido':
      case 'Preparando':
        return 'contrast';
      case 'En camino':
        return 'primary';
      case 'Entregado':
        return 'success';
      case 'Cancelado':
        return 'danger';
      default:
        return 'secondary';
    }
  }

  getOrderStatusIcon(status: string): string {
    switch (status) {
      case 'Pendiente':
        return 'pi pi-clock';
      case 'En camino':
        return 'pi pi-truck';
      case 'Entregado':
        return 'pi pi-check';
      case 'Cancelado':
        return 'pi pi-ban';
      default:
        return 'pi pi-info-circle';
    }
  }
}
