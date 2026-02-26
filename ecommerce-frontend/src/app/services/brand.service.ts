import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Brand, GenericResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private baseEndpoint = '/api/maintenance/brands';

  constructor(private apiService: ApiService) {}

  getAllBrands(): Observable<GenericResponse<Brand[]>> {
    return this.apiService.get<Brand[]>(this.baseEndpoint);
  }

  getActiveBrands(): Observable<GenericResponse<Brand[]>> {
    return this.apiService.get<Brand[]>(`${this.baseEndpoint}/active`);
  }

  getBrandById(id: number): Observable<GenericResponse<Brand>> {
    return this.apiService.get<Brand>(`${this.baseEndpoint}/${id}`);
  }

  createBrand(brand: Partial<Brand>): Observable<GenericResponse<Brand>> {
    return this.apiService.post<Brand>(this.baseEndpoint, brand);
  }

  updateBrand(id: number, brand: Partial<Brand>): Observable<GenericResponse<Brand>> {
    return this.apiService.put<Brand>(`${this.baseEndpoint}/${id}`, brand);
  }

  updateBrandStatus(id: number, isActive: boolean): Observable<GenericResponse<Brand>> {
    return this.apiService.patch<Brand>(`${this.baseEndpoint}/${id}/status?isActive=${isActive}`);
  }

  deleteBrand(id: number): Observable<GenericResponse<Brand>> {
    return this.apiService.delete<Brand>(`${this.baseEndpoint}/${id}`);
  }
}
