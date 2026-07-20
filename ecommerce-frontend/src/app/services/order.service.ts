import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { GenericResponse, OrderRequest, OrderResponse, OrderDetail } from '../models/ecommerce.models';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.apiUrl + '/api/orders';
  private ordersUpdatedSubject = new Subject<void>();

  // Observable para notificar cuando se crea una orden exitosamente
  public ordersUpdated$ = this.ordersUpdatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  createOrder(order: OrderRequest): Observable<GenericResponse<OrderResponse>> {
    return this.http.post<GenericResponse<OrderResponse>>(this.apiUrl, order);
  }

  getOrdersByCustomer(customerId: number): Observable<GenericResponse<OrderResponse[]>> {
    return this.http.get<GenericResponse<OrderResponse[]>>(`${this.apiUrl}/customer/${customerId}`);
  }

  getOrderDetails(orderId: number): Observable<GenericResponse<OrderDetail[]>> {
    return this.http.get<GenericResponse<OrderDetail[]>>(`${this.apiUrl}/details/${orderId}`);
  }

  notifyOrderCreated(): void {
    this.ordersUpdatedSubject.next();
  }
}
