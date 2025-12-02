import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TicketApiService {

  private baseUrl = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  getAllTickets() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getTicketById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getTicketsByReservation(reservationId: string) {
    return this.http.get<any[]>(`${this.baseUrl}/reservation/${reservationId}`);
  }

  createTicket(dto: any) {
    return this.http.post(this.baseUrl, dto);
  }

  updateTicket(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchTicket(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deleteTicket(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  downloadTicket(id: string) {
    return this.http.get(`${this.baseUrl}/${id}/download`, {
      responseType: 'blob'
    });
  }
}
