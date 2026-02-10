import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DataViewModule } from 'primeng/dataview';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProductService } from '../../services/product.service';
import { BrandService } from '../../services/brand.service';
import { CartService } from '../../services/cart.service';
import { Product, Brand } from '../../models/ecommerce.models';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    DropdownModule,
    DataViewModule,
    TagModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  brands: Brand[] = [];
  searchText: string = '';
  selectedBrand: Brand | null = null;
  minPrice: number = 0;
  maxPrice: number = 10000;
  loading: boolean = false;

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private cartService: CartService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadBrands();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getActiveProducts().subscribe({
      next: (response) => {
        if (response.body) {
          this.products = response.body;
          this.filteredProducts = response.body;
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudieron cargar los productos'
        });
        this.loading = false;
      }
    });
  }

  loadBrands(): void {
    this.brandService.getActiveBrands().subscribe({
      next: (response) => {
        if (response.body) {
          this.brands = response.body;
        }
      },
      error: (error) => {
        console.error('Error loading brands:', error);
      }
    });
  }

  filterProducts(): void {
    this.filteredProducts = this.products.filter(product => {
      const matchesSearch = !this.searchText || 
        product.name.toLowerCase().includes(this.searchText.toLowerCase()) ||
        (product.description?.toLowerCase().includes(this.searchText.toLowerCase()));
      
      const matchesBrand = !this.selectedBrand || 
        product.brandName === this.selectedBrand.description;
      
      const matchesPrice = product.price >= this.minPrice && 
        product.price <= this.maxPrice;

      return matchesSearch && matchesBrand && matchesPrice;
    });
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product, 1);
    this.messageService.add({
      severity: 'success',
      summary: 'Producto agregado',
      detail: `${product.name} agregado al carrito`
    });
  }

  buyNow(product: Product): void {
    this.cartService.addToCart(product, 1);
    this.router.navigate(['/cart']);
  }

  resetFilters(): void {
    this.searchText = '';
    this.selectedBrand = null;
    this.minPrice = 0;
    this.maxPrice = 10000;
    this.filteredProducts = this.products;
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    target.src = '/assets/images/product-placeholder.svg';
  }
}
