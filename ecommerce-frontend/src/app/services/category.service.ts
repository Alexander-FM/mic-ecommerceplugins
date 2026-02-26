import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Category, GenericResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseEndpoint = '/api/maintenance/categories';

  constructor(private apiService: ApiService) {}

  getAllCategories(): Observable<GenericResponse<Category[]>> {
    return this.apiService.get<Category[]>(this.baseEndpoint);
  }

  getActiveCategories(): Observable<GenericResponse<Category[]>> {
    return this.apiService.get<Category[]>(`${this.baseEndpoint}/active`);
  }

  getCategoryById(id: number): Observable<GenericResponse<Category>> {
    return this.apiService.get<Category>(`${this.baseEndpoint}/${id}`);
  }

  createCategory(category: Partial<Category>): Observable<GenericResponse<Category>> {
    return this.apiService.post<Category>(this.baseEndpoint, category);
  }

  updateCategory(id: number, category: Partial<Category>): Observable<GenericResponse<Category>> {
    return this.apiService.put<Category>(`${this.baseEndpoint}/${id}`, category);
  }

  updateCategoryStatus(id: number, isActive: boolean): Observable<GenericResponse<Category>> {
    return this.apiService.patch<Category>(`${this.baseEndpoint}/${id}/status?isActive=${isActive}`);
  }

  deleteCategory(id: number): Observable<GenericResponse<Category>> {
    return this.apiService.delete<Category>(`${this.baseEndpoint}/${id}`);
  }
}
