import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Brand, GenericResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class BrandService {
  private baseEndpoint = '/maintenance/api/brands';

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
}
