import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { GenericResponse, RegisterRequestDto, CustomerResponseDto } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private readonly authEndpoint = '/api/auth';

  constructor(private apiService: ApiService) { }

  register(payload: RegisterRequestDto): Observable<GenericResponse<CustomerResponseDto>> {
    return this.apiService.post<CustomerResponseDto>(`${this.authEndpoint}/register`, payload);
  }
}
