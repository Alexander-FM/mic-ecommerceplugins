import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CartService } from '../../services/cart.service';
import { CartItem, OrderRequest, OrderDetailRequest } from '../../models/ecommerce.models';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    CardModule,
    InputNumberModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  subtotal: number = 0;
  total: number = 0;
  isProcessing: boolean = false;

  constructor(
    private cartService: CartService,
    private messageService: MessageService,
    private router: Router,
    private authService: AuthService,
    private orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.loadCart();
    this.cartService.cartItems$.subscribe(() => {
      this.loadCart();
    });
  }

  loadCart(): void {
    this.cartItems = this.cartService.getCartItems();
    this.calculateTotals();
  }

  calculateTotals(): void {
    this.subtotal = this.cartService.getSubtotal();
    this.total = this.cartService.getTotal();
  }

  updateQuantity(productId: number | undefined, quantity: number): void {
    if (!productId) return;
    if (quantity > 0) {
      this.cartService.updateQuantity(productId, quantity);
      this.messageService.add({
        severity: 'success',
        summary: 'Cantidad actualizada',
        detail: 'La cantidad del producto ha sido actualizada'
      });
    }
  }

  removeItem(productId: number | undefined): void {
    if (!productId) return;
    this.cartService.removeFromCart(productId);
    this.messageService.add({
      severity: 'info',
      summary: 'Producto eliminado',
      detail: 'El producto ha sido eliminado del carrito'
    });
  }

  clearCart(): void {
    this.cartService.clearCart();
    this.messageService.add({
      severity: 'warn',
      summary: 'Carrito vaciado',
      detail: 'Todos los productos han sido eliminados'
    });
  }

  continueShopping(): void {
    this.router.navigate(['/products']);
  }

  getItemTotal(item: CartItem): number {
    return item.product.price * item.quantity;
  }

  checkout(): void {
    if (this.cartItems.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Carrito vacío',
        detail: 'Agrega productos antes de proceder al pago'
      });
      return;
    }

    const authState = this.authService.getAuthState();
    if (!authState.isAuthenticated || !authState.user) {
      this.messageService.add({
        severity: 'error',
        summary: 'No autenticado',
        detail: 'Debes iniciar sesión para procesar la compra'
      });
      return;
    }

    this.isProcessing = true;

    const customerId = Number(authState.user['customerId']) || 0;

    const orderDetails: OrderDetailRequest[] = this.cartItems.map(item => ({
      productId: item.product.id!,
      quantity: item.quantity,
      unitPrice: item.product.price
    }));

    const orderRequest: OrderRequest = {
      customerId: customerId,
      employeeId: null,
      orderStatus: { id: 1 },
      totalAmount: this.cartService.getTotal(),
      orderNotes: "Pedido generado desde el carrito de compras",
      orderDetails: orderDetails
    };

    this.orderService.createOrder(orderRequest).subscribe({
      next: (response) => {
        this.isProcessing = false;
        if (response.rpta === 1) {
          this.messageService.add({
            severity: 'success',
            summary: 'Compra exitosa',
            detail: response.message || 'La orden se ha creado correctamente'
          });
          this.orderService.notifyOrderCreated();
          this.cartService.clearCart();
          this.router.navigate([`/my-orders`]);
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Error al procesar',
            detail: response.message || 'Ocurrió un error al crear la orden'
          });
        }
      },
      error: (err) => {
        this.isProcessing = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error de servidor',
          detail: err.error?.message || 'Ocurrió un error al procesar la compra'
        });
      }
    });
  }
}
