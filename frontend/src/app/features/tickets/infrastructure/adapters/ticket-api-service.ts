import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { TicketDTO } from '../dtos/ticket.dto';

@Injectable({
  providedIn: 'root'
})
export class TicketApiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/tickets`;

  getAllTickets(): Observable<TicketDTO[]> {
    return this.http.get<TicketDTO[]>(this.baseUrl);
  }

  getTicketById(id: string): Observable<TicketDTO> {
    return this.http.get<TicketDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`);
  }

  getTicketsByReservation(reservationId: string): Observable<TicketDTO[]> {
    return this.http.get<TicketDTO[]>(`${this.baseUrl}/reservation/${encodeURIComponent(reservationId)}`);
  }

  createTicket(dto: TicketDTO): Observable<TicketDTO> {
    return this.http.post<TicketDTO>(this.baseUrl, dto);
  }

  updateTicket(id: string, dto: TicketDTO): Observable<TicketDTO> {
    return this.http.put<TicketDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`, dto);
  }

  patchTicket(id: string, dto: Partial<TicketDTO>): Observable<TicketDTO> {
    return this.http.patch<TicketDTO>(`${this.baseUrl}/${encodeURIComponent(id)}`, dto);
  }

  deleteTicket(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${encodeURIComponent(id)}`);
  }

  downloadTicket(id: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${encodeURIComponent(id)}/download`, {
      responseType: 'blob'
    });
  }
}
