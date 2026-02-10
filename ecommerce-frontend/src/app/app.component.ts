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
  cartItemCount = 0;
  menuItems: MenuItem[] = [];

  constructor(
    private categoryService: CategoryService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.loadCategories();
      this.loadCartCount();
    }
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

