import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {

  private baseUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  createReservation(userId: string, screeningId: string, seatIds: string[], pricePerSeat: number) {
    return this.http.post(this.baseUrl, null, {
      params: {
        userId,
        screeningId,
        seatIds: seatIds.join(','),
        pricePerSeat
      }
    });
  }

  getAll() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getReservationById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getReservationsByUser(userId: string) {
    return this.http.get<any[]>(`${this.baseUrl}/user/${userId}`);
  }

  updateReservation(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchReservation(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deleteReservation(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAvailableSeats(screeningId: string) {
    return this.http.get(`${this.baseUrl}/available-seats`, {
      params: { screeningId }
    });
  }
}
