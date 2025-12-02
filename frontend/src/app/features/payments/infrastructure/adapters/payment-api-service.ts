import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PaymentApiService {

  private baseUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  getAllPayments() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getPaymentById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getPaymentByReservation(reservationId: string) {
    return this.http.get<any>(`${this.baseUrl}?reservationId=${reservationId}`);
  }

  createPayment(dto: any) {
    return this.http.post(this.baseUrl, dto);
  }

  updatePayment(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchPayment(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deletePayment(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getPaymentStatus(id: string) {
    return this.http.get(`${this.baseUrl}/${id}/status`);
  }

  processPayment(id: string) {
    return this.http.post(`${this.baseUrl}/${id}/process`, {});
  }
}
