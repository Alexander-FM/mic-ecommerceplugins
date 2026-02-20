import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { GenericResponse, Role, UserRequest, CustomerRequest, UserResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private readonly baseEndpoint = '/api/maintenance';

  constructor(private apiService: ApiService) {}

  getRoles(): Observable<GenericResponse<Role[]>> {
    return this.apiService.get<Role[]>(`${this.baseEndpoint}/roles`);
  }

  createUser(payload: UserRequest): Observable<GenericResponse<UserResponse>> {
    return this.apiService.post<UserResponse>(`${this.baseEndpoint}/users`, payload);
  }

  createCustomer(payload: CustomerRequest): Observable<GenericResponse<CustomerRequest>> {
    return this.apiService.post<CustomerRequest>(`${this.baseEndpoint}/customers`, payload);
  }
}
