import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { TabViewModule } from 'primeng/tabview';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { GalleriaModule } from 'primeng/galleria';
import { ToastModule } from 'primeng/toast';
import { Router } from '@angular/router';

import { AdminService } from '../../../services/admin.service';
import { CategoryService } from '../../../services/category.service';
import { BrandService } from '../../../services/brand.service';
import { GenericResponse, Product, Attribute, ProductAttribute } from '../../../models/ecommerce.models';

interface ImageUpload {
  name: string;
  size: number;
  base64?: string;
  file?: File;
}

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TabViewModule,
    InputTextModule,
    InputNumberModule,
    DropdownModule,
    CheckboxModule,
    TableModule,
    ButtonModule,
    FileUploadModule,
    GalleriaModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.scss'
})
export class AddProductComponent implements OnInit {
  productForm!: FormGroup;
  categories: any[] = [];
  brands: any[] = [];
  attributes: Attribute[] = [];
  selectedAttribute: any = null;
  selectedAttributeValue: string = '';
  attributesTable: ProductAttribute[] = [];
  productImages: ImageUpload[] = [];

  mainImagePreview: string | null = null;
  mainImageFile: File | null = null;

  isLoadingAttributes = false;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private messageService: MessageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadCategories();
    this.loadBrands();
    this.loadAttributes();
  }

  private initializeForm(): void {
    this.productForm = this.fb.group({
      barCode: ['', Validators.required],
      name: ['', Validators.required],
      description: [''],
      price: [0, [Validators.required, Validators.min(0)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required],
      brandId: ['', Validators.required],
      isActive: [true],
      isRecommended: [false]
    });
  }

  private loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (response: GenericResponse<any[]>) => {
        this.categories = response.body || [];
      },
      error: (err: any) => {
        console.error('Error loading categories:', err);
        this.showError('No se pudieron cargar las categorías');
      }
    });
  }

  private loadBrands(): void {
    this.brandService.getAllBrands().subscribe({
      next: (response: GenericResponse<any[]>) => {
        this.brands = response.body || [];
      },
      error: (err: any) => {
        console.error('Error loading brands:', err);
        this.showError('No se pudieron cargar las marcas');
      }
    });
  }

  private loadAttributes(): void {
    this.isLoadingAttributes = true;
    this.adminService.getAttributes().subscribe({
      next: (response: GenericResponse<Attribute[]>) => {
        this.attributes = response.body || [];
        this.isLoadingAttributes = false;
      },
      error: (err: any) => {
        console.error('Error loading attributes:', err);
        this.showError('No se pudieron cargar los atributos');
        this.isLoadingAttributes = false;
      }
    });
  }

  // ============= TAB 1: MAIN IMAGE UPLOAD =============

  onMainImageSelect(event: any): void {
    const file = event.files[0];
    if (!file) return;

    // Validar tipo de archivo
    const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
    if (!validTypes.includes(file.type)) {
      this.showWarning('Solo se permiten imágenes JPEG, PNG o WebP');
      return;
    }

    // Validar tamaño (max 5MB)
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      this.showWarning('La imagen no debe exceder 5MB');
      return;
    }

    this.mainImageFile = file;

    // Mostrar preview
    const reader = new FileReader();
    reader.onload = (e) => {
      this.mainImagePreview = e.target?.result as string;
    };
    reader.readAsDataURL(file);

    this.showSuccess('Imagen principal cargada');
  }

  clearMainImage(): void {
    this.mainImageFile = null;
    this.mainImagePreview = null;
  }

  // ============= TAB 2: ATTRIBUTES =============

  addAttribute(selectedAttr: any): void {
    if (!selectedAttr) {
      this.showWarning('Selecciona un atributo');
      return;
    }

    const value = this.selectedAttributeValue.trim();
    if (!value) {
      this.showWarning('Ingresa un valor para el atributo');
      return;
    }

    // Verificar si ya existe
    const exists = this.attributesTable.some((a) => a.attributeId === selectedAttr.id);
    if (exists) {
      this.showWarning('Este atributo ya ha sido agregado');
      return;
    }

    this.attributesTable.push({
      attributeId: selectedAttr.id,
      name: selectedAttr.name,
      value: value
    });

    this.selectedAttribute = null;
    this.selectedAttributeValue = '';
    this.showSuccess('Atributo agregado');
  }

  removeAttribute(index: number): void {
    this.attributesTable.splice(index, 1);
    this.messageService.add({
      severity: 'info',
      summary: 'Atributo removido',
      life: 1500
    });
  }

  // ============= TAB 3: PRODUCT IMAGES =============

  onImagesSelect(event: any): void {
    const files = event.files;
    if (!files) return;

    const totalImages = this.productImages.length + files.length;
    if (totalImages > 4) {
      this.showError('No se pueden subir más de 4 imágenes');
      return;
    }

    for (const file of files) {
      const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
      if (!validTypes.includes(file.type)) {
        this.showWarning(`${file.name} no es un tipo de imagen válido`);
        continue;
      }

      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        this.showWarning(`${file.name} excede el tamaño máximo de 5MB`);
        continue;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        this.productImages.push({
          name: file.name,
          size: file.size,
          base64: e.target?.result as string,
          file: file
        });
      };
      reader.readAsDataURL(file);
    }

    this.showSuccess(`Imágenes agregadas (${this.productImages.length}/4)`);
  }

  removeProductImage(index: number): void {
    this.productImages.splice(index, 1);
    this.messageService.add({
      severity: 'info',
      summary: 'Imagen removida',
      life: 1500
    });
  }

  // ============= SUBMIT =============

  async onSubmit(): Promise<void> {
    if (!this.productForm.valid) {
      this.showError('Completa todos los campos requeridos');
      return;
    }

    if (!this.mainImageFile) {
      this.showError('Debes subir una imagen principal');
      return;
    }

    this.isSubmitting = true;

    try {
      // Step 1: Subir imagen principal
      this.messageService.add({
        severity: 'info',
        summary: 'Procesando',
        detail: 'Subiendo imagen principal...',
        life: 3000
      });

      const imageResponse = await this.adminService
        .uploadMainImage(this.mainImageFile)
        .toPromise();

      if (!imageResponse || imageResponse.rpta !== 1) {
        throw new Error(imageResponse?.message || 'Error subiendo imagen');
      }

      const mainImageUrl = imageResponse.body.customUrl;

      // Step 2: Crear producto
      this.messageService.add({
        severity: 'info',
        summary: 'Procesando',
        detail: 'Creando producto...',
        life: 3000
      });

      const formValue = this.productForm.value;
      const productData: any = {
        barCode: formValue.barCode,
        name: formValue.name,
        description: formValue.description,
        price: formValue.price,
        stock: formValue.stock,
        category: {
          id: formValue.categoryId
        },
        brand: {
          id: formValue.brandId
        },
        isActive: formValue.isActive,
        isRecommended: formValue.isRecommended,
        mainImageUrl: mainImageUrl,
        attributes: this.attributesTable
      };

      const productResponse = await this.adminService
        .createProduct(productData)
        .toPromise();

      if (!productResponse || productResponse.rpta !== 1) {
        throw new Error(productResponse?.message || 'Error creando producto');
      }

      const productId = productResponse.body.id;

      // Step 3: Subir imágenes del producto (si existen)
      if (this.productImages.length > 0) {
        this.messageService.add({
          severity: 'info',
          summary: 'Procesando',
          detail: 'Subiendo imágenes del producto...',
          life: 3000
        });

        const files = this.productImages.map((img) => img.file!);
        const imgResponse = await this.adminService
          .uploadProductImages(productId, files)
          .toPromise();

        if (!imgResponse || imgResponse.rpta !== 1) {
          console.warn('Advertencia subiendo imágenes:', imgResponse?.message);
        }
      }

      this.showSuccess(
        `✅ Producto "${productData.name}" registrado exitosamente`
      );
      this.isSubmitting = false;

      // Redirigir a productos
      setTimeout(() => {
        this.router.navigate(['/products']);
      }, 1500);
    } catch (error: any) {
      this.isSubmitting = false;
      console.error('❌ Error:', error);
      this.showError(error.message || 'Error procesando solicitud');
    }
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }

  // ============= MESSAGES =============

  private showSuccess(message: string): void {
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: message,
      life: 3000
    });
  }

  private showWarning(message: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: 'Advertencia',
      detail: message,
      life: 3000
    });
  }

  private showError(message: string): void {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 3000
    });
  }
}
