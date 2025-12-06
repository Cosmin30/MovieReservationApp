import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CacheService } from '../../../../core/services/cache-service';
import { LoggerService } from '../../../../core/services/logger.service';
import { environment } from '../../../../../environments/environment';
import { ReservationDTO } from '../dtos/reservation.dto';
import { CreateReservationDTO } from '../dtos/create-reservation.dto';
import { UpdateReservationDTO } from '../dtos/update-reservation.dto';
import { AvailableSeatsResponseDTO } from '../dtos/available-seats-response.dto';
import { SeatDTO } from '../../../halls/infrastructure/dtos/seat.dto';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {
  private http = inject(HttpClient);
  private cache = inject(CacheService);
  private logger = inject(LoggerService);
  private baseUrl = `${environment.apiUrl}/reservations`;

  createReservation(userId: string, screeningId: string, seatIds: string[], pricePerSeat: number): Observable<ReservationDTO> {
    return this.http.post<ReservationDTO>(this.baseUrl, null, {
      params: {
        userId,
        screeningId,
        seatIds: seatIds.join(','),
        pricePerSeat: pricePerSeat.toString()
      }
    });
  }

  getAll(): Observable<ReservationDTO[]> {
    return this.cache.getOrFetch(
      'all_reservations',
      () => this.http.get<ReservationDTO[]>(this.baseUrl)
    );
  }

  getReservationById(id: string): Observable<ReservationDTO> {
    return this.cache.getOrFetch(
      `reservation_${id}`,
      () => this.http.get<ReservationDTO>(`${this.baseUrl}/${id}`)
    );
  }

  clearReservationCache(id: string): void {
    this.cache.clear(`reservation_${id}`);
  }

  getReservationsByUser(userId: string): Observable<ReservationDTO[]> {
    return this.cache.getOrFetch(
      `reservations_user_${userId}`,
      () => this.http.get<ReservationDTO[]>(`${this.baseUrl}/user/${encodeURIComponent(userId)}`)
    );
  }

  updateReservation(id: string, dto: UpdateReservationDTO): Observable<ReservationDTO> {
    return this.http.put<ReservationDTO>(`${this.baseUrl}/${id}`, dto);
  }

  patchReservation(id: string, dto: Partial<UpdateReservationDTO>): Observable<ReservationDTO> {
    return this.http.patch<ReservationDTO>(`${this.baseUrl}/${id}`, dto);
  }

  deleteReservation(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getAvailableSeats(screeningId: string): Observable<SeatDTO[]> {
    this.logger.debug(`Fetching all seats for screening ID: ${screeningId}`);
    // Use /screening/{id} to get ALL seats, not just available ones
    // This way we can count available vs reserved
    return this.http.get<SeatDTO[]>(`${environment.apiUrl}/seats/screening/${encodeURIComponent(screeningId)}`);
  }
}
