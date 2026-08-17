import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { TabViewModule } from 'primeng/tabview';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { CheckboxModule } from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { GalleriaModule } from 'primeng/galleria';
import { ToastModule } from 'primeng/toast';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AdminService } from '../../../services/admin.service';
import { CategoryService } from '../../../services/category.service';
import { BrandService } from '../../../services/brand.service';
import { GenericResponse, Product, Attribute, ProductAttribute, ProductImage } from '../../../models/ecommerce.models';

interface ImageUpload {
  name: string;
  size: number;
  base64?: string;
  file?: File;
}

interface ExistingProductImageView extends ProductImage {
  previewUrl: string;
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
    SelectModule,
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
  originalMainImageUrl: string | null = null;
  existingProductImages: ExistingProductImageView[] = [];

  isLoadingAttributes = false;
  isLoadingData = false;
  isSubmitting = false;
  isEditMode = false;
  currentProductId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    this.initializeForm();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.currentProductId = idParam ? Number(idParam) : null;
    this.isEditMode = !!this.currentProductId;

    await this.loadInitialData();
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

  private async loadInitialData(): Promise<void> {
    this.isLoadingData = true;
    this.isLoadingAttributes = true;

    try {
      const [categoriesResponse, brandsResponse, attributesResponse] =
        await Promise.all([
          firstValueFrom(this.categoryService.getAllCategories()),
          firstValueFrom(this.brandService.getAllBrands()),
          firstValueFrom(this.adminService.getAttributes())
        ]);

      this.categories = this.flattenCategoryOptions(categoriesResponse.body || []);
      this.brands = brandsResponse.body || [];
      this.attributes = attributesResponse.body || [];

      if (this.isEditMode && this.currentProductId) {
        await this.loadProductForEdit(this.currentProductId);
      }
    } catch (error: any) {
      console.error('Error loading initial product data:', error);
      this.showError(error?.message || 'No se pudo cargar la información inicial');
    } finally {
      this.isLoadingAttributes = false;
      this.isLoadingData = false;
    }
  }

  private async loadProductForEdit(productId: number): Promise<void> {
    const response = await firstValueFrom(this.adminService.getProductById(productId));

    if (!response?.body) {
      throw new Error('No se pudo obtener el producto a editar');
    }

    const product = response.body;
    const selectedCategory = this.categories.find(
      (category) =>
        category.rawDescription === product.categoryName ||
        category.description === product.categoryName
    );
    const selectedBrand = this.brands.find(
      (brand) => brand.description === product.brandName
    );

    this.productForm.patchValue({
      barCode: product.barCode,
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: selectedCategory?.id || null,
      brandId: selectedBrand?.id || null,
      isActive: product.isActive ?? true,
      isRecommended: product.isRecommended ?? false
    });

    this.originalMainImageUrl = product.mainImageUrl || null;
    this.mainImagePreview = this.normalizeImageUrl(product.mainImageUrl) || null;
    this.mainImageFile = null;
    this.attributesTable = (product.attributes || []).map((attribute) => ({
      attributeId: attribute.attributeId,
      name: attribute.name,
      value: attribute.value
    }));
    this.existingProductImages = (product.images || []).map((image) => ({
      ...image,
      previewUrl: this.normalizeImageUrl(image.imageUrl)
    }));
  }

  private flattenCategoryOptions(categories: any[], ancestors: string[] = []): any[] {
    const options: any[] = [];

    for (const category of categories) {
      const fullPath = [...ancestors, category.description].join(' > ');

      options.push({
        id: category.id,
        description: category.description,
        fullPath,
        rawDescription: category.description
      });

      if (category.subCategories?.length) {
        options.push(...this.flattenCategoryOptions(category.subCategories, [...ancestors, category.description]));
      }
    }

    return options;
  }

  private normalizeImageUrl(url?: string | null): string {
    if (!url) {
      return '';
    }

    if (!url.includes('drive.google.com')) {
      return url;
    }

    const drivePathMatch = url.match(/\/file\/d\/([a-zA-Z0-9-_]+)/);
    const driveOpenMatch = url.match(/\/open\?id=([a-zA-Z0-9-_]+)/);
    const driveQueryMatch = url.match(/[?&]id=([a-zA-Z0-9-_]+)/);
    const fileId = drivePathMatch?.[1] || driveOpenMatch?.[1] || driveQueryMatch?.[1];

    if (fileId) {
      const previewUrl = `https://drive.google.com/uc?id=${fileId}&export=view`;
      return `https://images.weserv.nl/?url=${encodeURIComponent(previewUrl)}&w=800`;
    }

    return url;
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
    this.originalMainImageUrl = null;
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

    const totalImages =
      this.existingProductImages.length + this.productImages.length + files.length;
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

  removeExistingProductImage(index: number): void {
    this.existingProductImages.splice(index, 1);
    this.messageService.add({
      severity: 'info',
      summary: 'Imagen actual removida',
      life: 1500
    });
  }

  // ============= SUBMIT =============

  async onSubmit(): Promise<void> {
    if (!this.productForm.valid) {
      this.productForm.markAllAsTouched();
      this.showError('Completa todos los campos requeridos');
      return;
    }

    if (!this.mainImageFile && !this.mainImagePreview) {
      this.showError('Debes subir o conservar una imagen principal');
      return;
    }

    this.isSubmitting = true;

    try {
      let mainImageUrl = this.originalMainImageUrl || '';

      // Step 1: Subir imagen principal (solo si fue cambiada)
      if (this.mainImageFile) {
        this.messageService.add({
          severity: 'info',
          summary: 'Procesando',
          detail: 'Subiendo imagen principal...',
          life: 3000
        });

        const imageResponse = await firstValueFrom(
          this.adminService.uploadMainImage(this.mainImageFile)
        );

        if (!imageResponse || imageResponse.rpta !== 1) {
          throw new Error(imageResponse?.message || 'Error subiendo imagen');
        }

        mainImageUrl = imageResponse.body.customUrl;
        this.originalMainImageUrl = mainImageUrl;
      }

      if (!mainImageUrl) {
        throw new Error('La imagen principal es obligatoria');
      }

      // Step 2: Crear/Actualizar producto
      this.messageService.add({
        severity: 'info',
        summary: 'Procesando',
        detail: this.isEditMode ? 'Actualizando producto...' : 'Creando producto...',
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
        attributes: this.attributesTable.map((attribute) => ({
          attributeId: attribute.attributeId,
          value: attribute.value
        })),
        images: this.existingProductImages.map((image) => ({
          id: image.id,
          imageUrl: image.imageUrl,
          productId: image.productId
        }))
      };

      let productId = this.currentProductId;

      if (this.isEditMode && this.currentProductId) {
        const updateResponse = await firstValueFrom(
          this.adminService.updateProduct(this.currentProductId, productData)
        );

        if (!updateResponse || updateResponse.rpta !== 1) {
          throw new Error(updateResponse?.message || 'Error actualizando producto');
        }

        productId = this.currentProductId;
      } else {
        const productResponse = await firstValueFrom(
          this.adminService.createProduct(productData)
        );

        if (!productResponse || productResponse.rpta !== 1) {
          throw new Error(productResponse?.message || 'Error creando producto');
        }

        productId = productResponse.body.id;
      }

      // Step 3: Subir imágenes del producto (si existen)
      const filesToUpload = this.productImages
        .filter((img) => !!img.file)
        .map((img) => img.file!);

      if (filesToUpload.length > 0 && productId) {
        this.messageService.add({
          severity: 'info',
          summary: 'Procesando',
          detail: 'Subiendo imágenes del producto...',
          life: 3000
        });

        const imgResponse = await firstValueFrom(
          this.adminService.uploadProductImages(productId, filesToUpload)
        );

        if (!imgResponse || imgResponse.rpta !== 1) {
          console.warn('Advertencia subiendo imágenes:', imgResponse?.message);
        }
      }

      this.showSuccess(
        this.isEditMode
          ? `✅ Producto "${productData.name}" actualizado exitosamente`
          : `✅ Producto "${productData.name}" registrado exitosamente`
      );
      this.isSubmitting = false;
      await this.router.navigate(['/admin/maintenance/products']);
    } catch (error: any) {
      this.isSubmitting = false;
      console.error('❌ Error:', error);
      this.showError(error.message || 'Error procesando solicitud');
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/maintenance/products']);
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
