import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';
import { SeatDTO } from '../../../halls/infrastructure/dtos/seat.dto';

@Injectable({
  providedIn: 'root'
})
export class GetAvailableSeatsService {
  private api = inject(ReservationApiService);

  execute(screeningId: string): Observable<SeatDTO[]> {
    return this.api.getAvailableSeats(screeningId);
  }
}
