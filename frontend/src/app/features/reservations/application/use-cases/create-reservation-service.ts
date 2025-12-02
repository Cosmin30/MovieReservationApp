import { Injectable } from '@angular/core';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';

@Injectable({
  providedIn: 'root'
})
export class CreateReservationService {

  constructor(private api: ReservationApiService) {}

  execute(userId: string, screeningId: string, seatIds: string[], pricePerSeat: number) {
    return this.api.createReservation(userId, screeningId, seatIds, pricePerSeat);
  }
}
