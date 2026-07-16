import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { BadgeModule } from 'primeng/badge';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';
import { MenuItem, MessageService } from 'primeng/api';
import { CategoryService } from './services/category.service';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';
import { OrderService } from './services/order.service';
import { Category } from './models/ecommerce.models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    MenubarModule,
    ButtonModule,
    BadgeModule,
    TooltipModule,
    ToastModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [MessageService]
})
export class AppComponent implements OnInit {
  title = 'ecommerce-frontend';
  showMenu = true;
  username = 'Usuario';
  isAdmin = false;
  cartItemCount = 0;
  ordersCount = 0;
  menuItems: MenuItem[] = [];

  constructor(
    private categoryService: CategoryService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    // Escuchar cambios en el estado de autenticación
    this.authService.authState$.subscribe((authState) => {
      if (authState.isAuthenticated) {
        console.log('✅ Usuario autenticado, cargando menú...');
        this.showMenu = true;
        this.username = authState.user?.sub || 'Usuario';
        const roles = authState.user?.roles || [];
        this.isAdmin = roles.includes('ROLE_ADMIN');
        console.log('🔐 isAdmin:', this.isAdmin);
        this.loadCategories();
        this.loadCartCount();
        this.loadOrdersCount();
      } else {
        console.log('❌ Usuario no autenticado, ocultando menú...');
        this.showMenu = false;
        this.menuItems = [];
        this.cartItemCount = 0;
        this.ordersCount = 0;
        this.isAdmin = false;
      }
    });
  }

  loadOrdersCount(): void {
    const authState = this.authService.getAuthState();
    if (!authState.isAuthenticated || !authState.user) return;
    
    const customerId = Number(authState.user['id'] || authState.user['userId'] || authState.user.sub) || 1;
    
    const fetchOrders = () => {
      this.orderService.getOrdersByCustomer(customerId).subscribe({
        next: (response) => {
          if (response.rpta === 1 && response.body) {
            this.ordersCount = response.body.length;
          }
        },
        error: (err) => console.error('Error loading orders count', err)
      });
    };

    fetchOrders();

    this.orderService.ordersUpdated$.subscribe(() => {
      fetchOrders();
    });
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (response) => {
        if (response.body) {
          this.buildMenu(response.body);
        }
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.buildDefaultMenu();
      }
    });
  }

  loadCartCount(): void {
    this.cartService.cartItems$.subscribe(() => {
      this.cartItemCount = this.cartService.getCartItemCount();
      // Rebuild menu to update badge
      if (this.menuItems.length > 0) {
        const cartMenuItem = this.menuItems.find(item => item.label === 'Carrito');
        if (cartMenuItem) {
          cartMenuItem.badge = this.cartItemCount > 0 ? this.cartItemCount.toString() : undefined;
        }
      }
    });
  }

  buildMenu(categories: Category[]): void {
    this.menuItems = [
      {
        label: 'Inicio',
        icon: 'pi pi-home',
        routerLink: '/products'
      },
      {
        label: 'Categorías',
        icon: 'pi pi-th-large',
        items: this.buildCategoryMenuItems(categories)
      },
      {
        label: 'Carrito',
        icon: 'pi pi-shopping-cart',
        routerLink: '/cart',
        badge: this.cartItemCount > 0 ? this.cartItemCount.toString() : undefined
      }
    ];

    this.appendAdminMaintenanceMenu();
  }

  buildDefaultMenu(): void {
    this.menuItems = [
      {
        label: 'Inicio',
        icon: 'pi pi-home',
        routerLink: '/products'
      },
      {
        label: 'Carrito',
        icon: 'pi pi-shopping-cart',
        routerLink: '/cart',
        badge: this.cartItemCount > 0 ? this.cartItemCount.toString() : undefined
      }
    ];

    this.appendAdminMaintenanceMenu();
  }

  private appendAdminMaintenanceMenu(): void {
    if (!this.isAdmin) {
      return;
    }

    this.menuItems.push({
      label: 'Mantenimiento',
      icon: 'pi pi-cog',
      items: [
        {
          label: 'Productos',
          icon: 'pi pi-box',
          routerLink: '/admin/maintenance/products'
        },
        {
          label: 'Categorías',
          icon: 'pi pi-tags',
          routerLink: '/admin/maintenance/categories'
        },
        {
          label: 'Marcas',
          icon: 'pi pi-bookmark',
          routerLink: '/admin/maintenance/brands'
        }
      ]
    });
  }

  buildCategoryMenuItems(categories: Category[]): MenuItem[] {
    return categories.map(category => {
      const item: MenuItem = {
        label: category.description,
        command: () => this.navigateToCategory(category)
      };

      if (category.subCategories && category.subCategories.length > 0) {
        item.items = this.buildCategoryMenuItems(category.subCategories);
      }

      return item;
    });
  }

  navigateToCategory(category: Category): void {
    this.router.navigate(['/products'], {
      queryParams: { categoryId: category.id }
    });
  }

  logout(): void {
    this.authService.logout();
    // El navbar se ocultará automáticamente gracias a authState$ subscription
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

