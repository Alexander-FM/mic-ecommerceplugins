import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { GenericResponse, OrderRequest, OrderResponse } from '../models/ecommerce.models';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://127.0.0.1:9089/api/orders';
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

  notifyOrderCreated(): void {
    this.ordersUpdatedSubject.next();
  }
}
