import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GenericResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  get<T>(endpoint: string): Observable<GenericResponse<T>> {
    return this.http.get<GenericResponse<T>>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders()
    });
  }

  post<T>(endpoint: string, data: any): Observable<GenericResponse<T>> {
    return this.http.post<GenericResponse<T>>(`${this.apiUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    });
  }

  put<T>(endpoint: string, data: any): Observable<GenericResponse<T>> {
    return this.http.put<GenericResponse<T>>(`${this.apiUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    });
  }

  patch<T>(endpoint: string, data?: any): Observable<GenericResponse<T>> {
    return this.http.patch<GenericResponse<T>>(`${this.apiUrl}${endpoint}`, data, {
      headers: this.getHeaders()
    });
  }

  delete<T>(endpoint: string): Observable<GenericResponse<T>> {
    return this.http.delete<GenericResponse<T>>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders()
    });
  }
}
