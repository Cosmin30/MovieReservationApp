import { Injectable } from '@angular/core';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetAvailableSeatsService {

  constructor(private api: ReservationApiService) {}

  execute(screeningId: string) {
    return this.api.getAvailableSeats(screeningId);
  }
}
