import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TimelineModule } from 'primeng/timeline';
import { DialogModule } from 'primeng/dialog';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { OrderResponse, OrderDetail, OrderStatusHistory } from '../../models/ecommerce.models';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    ButtonModule,
    TimelineModule,
    DialogModule,
    ProgressSpinnerModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {
  orderId = 0;
  order: OrderResponse | null = null;
  orderDetails: OrderDetail[] = [];
  orderStatusHistory: OrderStatusHistory[] = [];

  isLoading = true;
  errorMessage: string | null = null;

  isDelivered = false;
  deliveredStatus: OrderStatusHistory | null = null;

  totalUnits = 0;
  totalAmount = 0;
  showTimelineModal = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.orderId = Number(idParam);

    if (!this.orderId || isNaN(this.orderId)) {
      this.isLoading = false;
      this.errorMessage = 'Número de pedido no válido.';
      return;
    }

    this.loadOrderData();
  }

  loadOrderData(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const authState = this.authService.getAuthState();
    const customerId = (authState.user && Number(authState.user['customerId'])) || 0;

    // 1. Cargar metadatos del pedido (OrderResponse)
    if (customerId) {
      this.orderService.getOrdersByCustomer(customerId).subscribe({
        next: (res) => {
          if (res.rpta === 1 && res.body) {
            const found = res.body.find(o => o.id === this.orderId);
            if (found) {
              this.order = found;
              this.totalAmount = found.totalAmount;
            }
          }
        },
        error: () => {}
      });
    }

    // 2. Cargar detalles del producto (OrderDetail[])
    this.orderService.getOrderDetails(this.orderId).subscribe({
      next: (res) => {
        if (res.rpta === 1 && res.body) {
          this.orderDetails = res.body.map(detail => ({
            ...detail,
            productImageUrl: this.processImageUrl(detail.productImageUrl)
          }));
          this.totalUnits = this.orderDetails.reduce((sum, item) => sum + (item.quantity || 0), 0);

          if (!this.totalAmount && this.orderDetails.length > 0) {
            this.totalAmount = this.orderDetails.reduce((sum, item) => sum + (item.totalPrice || (item.unitPrice * item.quantity)), 0);
          }
        } else {
          this.orderDetails = [];
        }

        // 3. Cargar historial de estados
        this.loadStatusHistory();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'No se pudieron cargar los productos del pedido.';
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudieron cargar los productos del pedido.'
        });
      }
    });
  }

  loadStatusHistory(): void {
    this.orderService.getOrderStatusHistory(this.orderId).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.rpta === 1 && res.body && res.body.length > 0) {
          // Ordenar cronológicamente por changedAt / id
          this.orderStatusHistory = res.body.sort((a, b) => {
            const timeA = new Date(a.changedAt).getTime();
            const timeB = new Date(b.changedAt).getTime();
            return timeA - timeB;
          });

          // Determinar estado actual (último registro en el historial)
          const lastStatus = this.orderStatusHistory[this.orderStatusHistory.length - 1];
          this.isDelivered = lastStatus.orderStatusName === 'Entregado';
          this.deliveredStatus = this.orderStatusHistory.find(h => h.orderStatusName === 'Entregado') || lastStatus;
        } else {
          this.orderStatusHistory = [];
          this.isDelivered = false;
          this.deliveredStatus = null;
        }
      },
      error: () => {
        this.isLoading = false;
        // Si no existe historial remoto pero hay orden, no bloqueamos la pantalla completa
        this.orderStatusHistory = [];
        this.isDelivered = false;
      }
    });
  }

  openTimelineModal(): void {
    this.showTimelineModal = true;
  }

  goBack(): void {
    this.router.navigate(['/my-orders']);
  }

  private processImageUrl(url: string | null | undefined): string | undefined {
    if (!url) return undefined;
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
