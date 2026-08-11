import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DataViewModule } from 'primeng/dataview';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { DrawerModule } from 'primeng/drawer';
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
    SelectModule,
    DataViewModule,
    TagModule,
    ToastModule,
    DrawerModule
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
  filterDrawerVisible: boolean = false;

  constructor(
    private productService: ProductService,
    private brandService: BrandService,
    private cartService: CartService,
    private messageService: MessageService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadProducts();
    this.loadBrands();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getActiveProducts().subscribe({
      next: (response) => {
        console.log('✅ Productos recibidos:', response);
        if (response.body) {
          // Procesar URLs de imágenes (convertir Google Drive links si es necesario)
          this.products = response.body.map(product => ({
            ...product,
            mainImageUrl: this.processImageUrl(product.mainImageUrl)
          }));
          this.filteredProducts = this.products;
          console.log('📦 Total productos:', this.products.length);
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('❌ Error loading products:', error);

        let errorMsg = 'No se pudieron cargar los productos';
        if (error.status === 401) {
          errorMsg = 'No autorizado. El token ha expirado.';
        } else if (error.status === 403) {
          errorMsg = 'Acceso denegado a los productos.';
        } else if (error.status === 0) {
          errorMsg = 'Error de conexión. Verifica que la API está disponible.';
        } else if (error.error?.message) {
          errorMsg = error.error.message;
        }

        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: errorMsg
        });

        console.log('📝 Status:', error.status);
        console.log('📝 Error completo:', error.error);

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

  goToProductDetail(product: Product): void {
    if (!product.id) {
      return;
    }

    this.router.navigate(['/products', product.id]);
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
    target.src = 'imagen_not_found_sorry.png';
  }

  /**
   * Convierte URLs de Google Drive a formato accesible usando proxy
   * Google Drive: https://drive.google.com/file/d/{FILE_ID}/view
   * Convertidas a: URL preview o proxy de imágenes si es necesario
   *
   * El proxy images.weserv.nl permite acceder a imágenes bloqueadas por CORS
   */
  private processImageUrl(url: string | null | undefined): string | undefined {
    if (!url) {
      return undefined;
    }

    console.log('🖼️ Procesando URL:', url);

    // Detectar si es URL de Google Drive
    if (url.includes('drive.google.com')) {
      try {
        // Extraer FILE_ID del URL
        const fileIdMatch = url.match(/\/d\/([a-zA-Z0-9-_]+)/);
        if (fileIdMatch && fileIdMatch[1]) {
          const fileId = fileIdMatch[1];
          // Convertir a URL preview que funciona mejor
          const previewUrl = `https://drive.google.com/uc?id=${fileId}&export=view`;

          // Si necesitas usar un proxy para CORS issues, descomenta esto:
          const proxyUrl = `https://images.weserv.nl/?url=${encodeURIComponent(previewUrl)}&w=400`;
          return proxyUrl;
        }
      } catch (error) {
        console.error('❌ Error procesando URL de Google Drive:', error);
      }
    }

    // Si no es Google Drive o no pudo procesar, retornar URL original
    console.log('ℹ️ URL original:', url);
    return url;
  }
}
