import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GenericResponse, CustomerResponseDto, CustomerRequest } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly API_BASE = environment.apiUrl + '/api/maintenance/customers';

  constructor(private http: HttpClient) {}

  /**
   * Obtener los datos del cliente por su ID
   */
  getCustomerById(id: number): Observable<GenericResponse<CustomerResponseDto>> {
    return this.http.get<GenericResponse<CustomerResponseDto>>(`${this.API_BASE}/${id}`);
  }

  /**
   * Actualizar los datos personales y de dirección del cliente
   */
  updateCustomer(id: number, customer: CustomerRequest): Observable<GenericResponse<CustomerResponseDto>> {
    return this.http.put<GenericResponse<CustomerResponseDto>>(`${this.API_BASE}/${id}`, customer);
  }
}
