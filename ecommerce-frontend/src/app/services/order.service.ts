import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GenericResponse, OrderRequest, OrderResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://127.0.0.1:9089/api/orders';

  constructor(private http: HttpClient) {}

  createOrder(order: OrderRequest): Observable<GenericResponse<OrderResponse>> {
    return this.http.post<GenericResponse<OrderResponse>>(this.apiUrl, order);
  }
}
