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
import { CartItem } from '../../models/ecommerce.models';

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

  constructor(
    private cartService: CartService,
    private messageService: MessageService,
    private router: Router
  ) {}

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
    this.messageService.add({
      severity: 'info',
      summary: 'Función en desarrollo',
      detail: 'La funcionalidad de checkout estará disponible pronto'
    });
  }
}
