import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
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
    TableModule,
    CardModule,
    TagModule,
    ButtonModule,
    ToastModule,
    DialogModule,
    ProgressSpinnerModule
  ],
  providers: [MessageService],
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.scss']
})
export class MyOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = true;
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
      return;
    }

    const customerId = Number(authState.user['customerId']) || 1;

    this.orderService.getOrdersByCustomer(customerId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.rpta === 1 && response.body) {
          this.orders = response.body.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime());
        } else {
          this.orders = [];
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudieron cargar las compras.'
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
