import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Product, GenericResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private baseEndpoint = '/api/products';

  constructor(private apiService: ApiService) {}

  getAllProducts(): Observable<GenericResponse<Product[]>> {
    return this.apiService.get<Product[]>(this.baseEndpoint);
  }

  getActiveProducts(): Observable<GenericResponse<Product[]>> {
    return this.apiService.get<Product[]>(`${this.baseEndpoint}/active`);
  }

  getProductById(id: number): Observable<GenericResponse<Product>> {
    return this.apiService.get<Product>(`${this.baseEndpoint}/${id}`);
  }

  createProduct(product: Product): Observable<GenericResponse<Product>> {
    return this.apiService.post<Product>(this.baseEndpoint, product);
  }

  updateProduct(id: number, product: Product): Observable<GenericResponse<Product>> {
    return this.apiService.put<Product>(`${this.baseEndpoint}/${id}`, product);
  }

  deleteProduct(id: number): Observable<GenericResponse<Product>> {
    return this.apiService.delete<Product>(`${this.baseEndpoint}/${id}`);
  }
}
