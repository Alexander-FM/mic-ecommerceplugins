import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { MessageService } from 'primeng/api';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { OrderResponse, OrderDetail } from '../../models/ecommerce.models';

@Component({
  selector: 'app-my-orders',
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
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.scss']
})
export class MyOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  isLoading = true;

  // Filtros y Búsqueda
  searchTerm: string = '';
  selectedDateFilter: string = 'ultimas';
  tempDateFilter: string = 'ultimas';
  showFilterModal = false;

  // Años dinámicos
  currentYear: number = new Date().getFullYear();
  previousYear: number = new Date().getFullYear() - 1;

  // Detalle de pedido (modal existente)
  showDetailModal = false;
  isDetailLoading = false;
  selectedOrderDetails: OrderDetail[] = [];
  selectedOrderId: number | null = null;
  selectedOrderTotal = 0;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private messageService: MessageService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    const authState = this.authService.getAuthState();
    if (!authState.isAuthenticated || !authState.user) {
      this.isLoading = false;
      this.filteredOrders = [];
      return;
    }

    const customerId = Number(authState.user['customerId']) || 0;

    this.orderService.getOrdersByCustomer(customerId).subscribe({
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
        this.filteredOrders = [];
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudieron cargar las compras.'
        });
      }
    });
  }

  applyFilters(): void {
    let result = [...this.orders];

    // 1. Filtro por Fecha
    const now = new Date();
    if (this.selectedDateFilter === '6months') {
      const sixMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 6, now.getDate(), 0, 0, 0);
      result = result.filter(order => new Date(order.orderDate) >= sixMonthsAgo);
    } else if (this.selectedDateFilter === '3months') {
      const threeMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate(), 0, 0, 0);
      result = result.filter(order => new Date(order.orderDate) >= threeMonthsAgo);
    } else if (this.selectedDateFilter === '30days') {
      const thirtyDaysAgo = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 30, 0, 0, 0);
      result = result.filter(order => new Date(order.orderDate) >= thirtyDaysAgo);
    } else if (this.selectedDateFilter === this.currentYear.toString()) {
      result = result.filter(order => new Date(order.orderDate).getFullYear() === this.currentYear);
    } else if (this.selectedDateFilter === this.previousYear.toString()) {
      result = result.filter(order => new Date(order.orderDate).getFullYear() === this.previousYear);
    }

    // 2. Filtro por Número de Pedido (order.id)
    const cleanSearch = this.searchTerm.trim().replace(/^#/, '');
    if (cleanSearch) {
      const exactMatchExists = result.some(order => order.id.toString() === cleanSearch);
      if (exactMatchExists) {
        result = result.filter(order => order.id.toString() === cleanSearch);
      } else {
        result = result.filter(order => order.id.toString().includes(cleanSearch));
      }
    }

    // Mantener orden de más reciente a más antigua
    result.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime());

    this.filteredOrders = result;
  }

  openFilterModal(): void {
    this.tempDateFilter = this.selectedDateFilter;
    this.showFilterModal = true;
  }

  selectTempFilter(filterKey: string): void {
    this.tempDateFilter = filterKey;
  }

  confirmFilter(): void {
    this.selectedDateFilter = this.tempDateFilter;
    this.applyFilters();
    this.showFilterModal = false;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedDateFilter = 'ultimas';
    this.tempDateFilter = 'ultimas';
    this.applyFilters();
    this.showFilterModal = false;
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }

  getDateFilterLabel(key: string): string {
    switch (key) {
      case '6months':
        return 'Últimos 6 meses';
      case '3months':
        return 'Últimos 3 meses';
      case '30days':
        return 'Últimos 30 días';
      case this.currentYear.toString():
        return `${this.currentYear}`;
      case this.previousYear.toString():
        return `${this.previousYear}`;
      default:
        return 'Últimas compras';
    }
  }

  getOrderStatusSeverity(status: string): any {
    switch (status) {
      case 'Pendiente':
        return 'warn';
      case 'Recepcionado':
        return 'info';
      case 'Preparando pedido':
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

  viewDetail(orderId: number): void {
    this.selectedOrderId = orderId;
    this.showDetailModal = true;
    this.isDetailLoading = true;
    this.selectedOrderDetails = [];
    this.selectedOrderTotal = 0;

    this.orderService.getOrderDetails(orderId).subscribe({
      next: (response) => {
        this.isDetailLoading = false;
        if (response.rpta === 1 && response.body) {
          this.selectedOrderDetails = response.body.map(detail => ({
            ...detail,
            productImageUrl: this.processImageUrl(detail.productImageUrl) || '/imagen_not_found_sorry.png'
          }));
          this.selectedOrderTotal = this.selectedOrderDetails.reduce((sum, item) => sum + (item.totalPrice || 0), 0);
        } else {
          this.selectedOrderDetails = [];
        }
      },
      error: (err) => {
        this.isDetailLoading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo cargar el detalle del pedido.'
        });
      }
    });
  }

  continueShopping(): void {
    this.router.navigate(['/products']);
  }

  private processImageUrl(url: string | null | undefined): string | undefined {
    if (!url) {
      return undefined;
    }

    if (url.includes('drive.google.com')) {
      const fileIdMatch = url.match(/\/d\/([a-zA-Z0-9-_]+)/);
      if (fileIdMatch && fileIdMatch[1]) {
        const fileId = fileIdMatch[1];
        const previewUrl = `https://drive.google.com/uc?id=${fileId}&export=view`;
        return `https://images.weserv.nl/?url=${encodeURIComponent(previewUrl)}&w=900`;
      }
    }

    return url;
  }
}
