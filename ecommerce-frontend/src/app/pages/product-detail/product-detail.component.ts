import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product, ProductAttribute, ProductImage } from '../../models/ecommerce.models';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, ButtonModule, TagModule, TableModule, ToastModule],
  providers: [MessageService],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  productAttributes: ProductAttribute[] = [];
  loading = false;
  galleryImages: string[] = [];
  currentImageIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (!idParam) {
        this.router.navigate(['/products']);
        return;
      }

      this.loadProductDetail(Number(idParam));
    });
  }

  loadProductDetail(id: number): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (response) => {
        if (!response.body) {
          this.loading = false;
          this.router.navigate(['/products']);
          return;
        }

        const detailedProduct = response.body;
        const normalizedAttributes = this.normalizeAttributes((detailedProduct as any).attributes);
        const processedMainImage = this.processImageUrl(detailedProduct.mainImageUrl);
        const processedImages = (detailedProduct.images || []).map((image: ProductImage) => ({
          ...image,
          imageUrl: this.processImageUrl(image.imageUrl) || '/imagen_not_found_sorry.png'
        }));

        this.product = {
          ...detailedProduct,
          attributes: normalizedAttributes,
          mainImageUrl: processedMainImage,
          images: processedImages
        };
        this.productAttributes = normalizedAttributes;

        const imagesFromGallery = (this.product.images || []).map(image => image.imageUrl);
        this.galleryImages = imagesFromGallery.length > 0
          ? imagesFromGallery
          : [processedMainImage || '/imagen_not_found_sorry.png'];

        this.currentImageIndex = 0;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo obtener el detalle del producto'
        });
        this.router.navigate(['/products']);
      }
    });
  }

  previousImage(): void {
    if (this.galleryImages.length <= 1) {
      return;
    }

    this.currentImageIndex =
      this.currentImageIndex === 0 ? this.galleryImages.length - 1 : this.currentImageIndex - 1;
  }

  nextImage(): void {
    if (this.galleryImages.length <= 1) {
      return;
    }

    this.currentImageIndex =
      this.currentImageIndex === this.galleryImages.length - 1 ? 0 : this.currentImageIndex + 1;
  }

  selectImage(index: number): void {
    this.currentImageIndex = index;
  }

  addToCart(): void {
    if (!this.product) {
      return;
    }

    this.cartService.addToCart(this.product, 1);
    this.messageService.add({
      severity: 'success',
      summary: 'Producto agregado',
      detail: `${this.product.name} agregado al carrito`
    });
  }

  buyNow(): void {
    if (!this.product) {
      return;
    }

    this.cartService.addToCart(this.product, 1);
    this.router.navigate(['/cart']);
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    target.src = '/imagen_not_found_sorry.png';
  }

  private normalizeAttributes(attributes: unknown): ProductAttribute[] {
    if (!Array.isArray(attributes)) {
      return [];
    }

    return attributes.map((attribute: any) => ({
      attributeId: Number(attribute?.attributeId ?? attribute?.id ?? 0),
      name: String(attribute?.name ?? attribute?.attributeName ?? 'Sin nombre'),
      value: String(attribute?.value ?? attribute?.attributeValue ?? '-')
    }));
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
