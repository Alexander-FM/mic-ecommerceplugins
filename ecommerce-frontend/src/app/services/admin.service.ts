import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GenericResponse, Product, Attribute } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly API_BASE = 'http://127.0.0.1:9089/api/maintenance';

  constructor(private http: HttpClient) {}

  /**
   * Crear un nuevo producto
   */
  createProduct(product: Product): Observable<GenericResponse<{ id: number }>> {
    return this.http.post<GenericResponse<{ id: number }>>(
      `${this.API_BASE}/products`,
      product
    );
  }

  /**
   * Obtener lista de atributos disponibles
   */
  getAttributes(): Observable<GenericResponse<Attribute[]>> {
    return this.http.get<GenericResponse<Attribute[]>>(
      `${this.API_BASE}/products/attributes`
    );
  }

  /**
   * Guardar atributos de un producto
   */
  saveProductAttributes(
    productId: number,
    attributes: any[]
  ): Observable<GenericResponse<any>> {
    return this.http.post<GenericResponse<any>>(
      `${this.API_BASE}/products/${productId}/attributes`,
      { attributes }
    );
  }

  /**
   * Subir imagen principal a Google Drive
   */
  uploadMainImage(file: File): Observable<GenericResponse<{ customUrl: string }>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<GenericResponse<{ customUrl: string }>>(
      `${this.API_BASE}/google-drive/images`,
      formData
    );
  }

  /**
   * Subir múltiples imágenes de producto (max 4)
   */
  uploadProductImages(
    productId: number,
    files: File[]
  ): Observable<GenericResponse<any>> {
    const formData = new FormData();
    formData.append('productId', productId.toString());
    files.forEach((file, index) => {
      formData.append(`files`, file);
    });
    return this.http.post<GenericResponse<any>>(
      `${this.API_BASE}/products/images/bulk-upload`,
      formData
    );
  }
}
