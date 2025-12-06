import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { PaymentDTO } from '../dtos/payment.dto';
import { CreatePaymentDTO } from '../dtos/create-payment.dto';
import { PaymentStatusDTO } from '../dtos/payment-status.dto';

@Injectable({
  providedIn: 'root'
})
export class PaymentApiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/payments`;

  getAllPayments(): Observable<PaymentDTO[]> {
    return this.http.get<PaymentDTO[]>(this.baseUrl);
  }

  getPaymentById(id: string): Observable<PaymentDTO> {
    return this.http.get<PaymentDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`);
  }

  getPaymentByReservation(reservationId: string): Observable<PaymentDTO> {
    return this.http.get<PaymentDTO>(`${this.baseUrl}?reservationId=${encodeURIComponent(reservationId)}`);
  }

  createPayment(dto: CreatePaymentDTO): Observable<PaymentDTO> {
    return this.http.post<PaymentDTO>(this.baseUrl, dto);
  }

  updatePayment(id: string, dto: PaymentDTO): Observable<PaymentDTO> {
    return this.http.put<PaymentDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`, dto);
  }

  patchPayment(id: string, dto: Partial<PaymentDTO>): Observable<PaymentDTO> {
    return this.http.patch<PaymentDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`, dto);
  }

  deletePayment(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${encodeURIComponent(id)}`);
  }

  getPaymentStatus(id: string): Observable<PaymentStatusDTO> {
    return this.http.get<PaymentStatusDTO>(`${this.baseUrl}/${encodeURIComponent(id)}/status`);
  }

  processPayment(id: string): Observable<PaymentDTO> {
    return this.http.post<PaymentDTO>(`${this.baseUrl}/${encodeURIComponent(id)}/process`, {});
  }
}
