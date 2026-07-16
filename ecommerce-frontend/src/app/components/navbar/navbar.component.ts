import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { MenuItem, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { BadgeModule } from 'primeng/badge';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, ToolbarModule, ButtonModule, MenuModule, ToastModule, BadgeModule],
  providers: [MessageService],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  userName = '';
  items: MenuItem[] = [];
  isAdmin = false;
  ordersCount = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    console.log('🔍 NavBar inicializando...');

    this.authService.authState$
      .pipe(takeUntil(this.destroy$))
      .subscribe((authState) => {
        console.log('📊 NavBar recibió nuevo authState:', authState);

        if (authState.user) {
          console.log('✅ Usuario encontrado en authState:', authState.user);
          this.userName = authState.user.sub || 'Usuario';
          const roles = authState.user.roles || [];
          console.log('ℹ️  Roles:', roles);
          this.isAdmin = roles.includes('ROLE_ADMIN');
          console.log('🔐 isAdmin:', this.isAdmin);
          this.loadOrdersCount(authState.user);
        } else {
          console.log('⚠️  authState.user es null/undefined');
          this.userName = 'Usuario';
          this.isAdmin = false;
          this.ordersCount = 0;
        }
      });
      
    this.orderService.ordersUpdated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        const user = this.authService.getAuthState().user;
        if (user) {
          this.loadOrdersCount(user);
        }
      });

    this.setupMenu();
  }

  private loadOrdersCount(user: any): void {
    const customerId = Number(user['id'] || user['userId'] || user.sub) || 1;
    this.orderService.getOrdersByCustomer(customerId).subscribe({
      next: (response) => {
        if (response.rpta === 1 && response.body) {
          this.ordersCount = response.body.length;
        }
      },
      error: (err) => {
        console.error('Error al cargar cantidad de órdenes', err);
        this.ordersCount = 0;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupMenu(): void {
    this.items = [
      {
        label: 'Cerrar sesión',
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ];
  }

  navigateToAddProduct(): void {
    this.router.navigate(['/admin/products/add']);
  }

  navigateToMyOrders(): void {
    this.router.navigate(['/my-orders']);
  }

  logout(): void {
    this.authService.logout();
    this.messageService.add({
      severity: 'info',
      summary: 'Sesión cerrada',
      detail: 'Has cerrado sesión exitosamente',
      life: 2000
    });
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 2000);
  }
}
