import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CartService } from '../../services/cart.service';
import { CartItem, OrderRequest, OrderDetailRequest } from '../../models/ecommerce.models';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { CustomerService } from '../../services/customer.service';

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
    InputTextModule,
    DialogModule,
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

  showReceiverDialog: boolean = false;
  receivedByInput: string = '';
  deliveryAddressName: string | null = null;
  currentCustomerId: number = 0;

  constructor(
    private cartService: CartService,
    private messageService: MessageService,
    private router: Router,
    private authService: AuthService,
    private orderService: OrderService,
    private customerService: CustomerService
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

    this.currentCustomerId = Number(authState.user['customerId'] || authState.user['id'] || authState.user['userId']) || 0;

    // Paso 1: Consultar los datos del cliente para obtener addressName (deliveryAddressName)
    this.isProcessing = true;
    this.customerService.getCustomerById(this.currentCustomerId).subscribe({
      next: (res) => {
        this.isProcessing = false;
        if (res?.body) {
          this.deliveryAddressName = res.body.addressName || null;
        }
        // Paso 2: Abrir modal p-dialog para solicitar el receptor (receivedBy)
        this.showReceiverDialog = true;
      },
      error: (err) => {
        this.isProcessing = false;
        console.error('Error al obtener dirección del cliente:', err);
        // Abrir el diálogo igualmente para que no se bloquee el flujo
        this.showReceiverDialog = true;
      }
    });
  }

  confirmCheckout(): void {
    if (!this.receivedByInput || !this.receivedByInput.trim()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Campo incompleto',
        detail: 'Por favor, ingresa los datos de la persona que recibirá el pedido'
      });
      return;
    }

    this.isProcessing = true;

    const orderDetails: OrderDetailRequest[] = this.cartItems.map(item => ({
      productId: item.product.id!,
      quantity: item.quantity,
      unitPrice: item.product.price
    }));

    const orderRequest: OrderRequest = {
      customerId: this.currentCustomerId,
      deliveryAddressName: this.deliveryAddressName,
      receivedBy: this.receivedByInput.trim(),
      employeeId: null,
      totalAmount: this.cartService.getTotal(),
      orderNotes: "Pedido generado desde el carrito de compras",
      orderDetails: orderDetails
    };

    this.orderService.createOrder(orderRequest).subscribe({
      next: (response) => {
        this.isProcessing = false;
        if (response.rpta === 1) {
          this.showReceiverDialog = false;
          this.receivedByInput = '';
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
