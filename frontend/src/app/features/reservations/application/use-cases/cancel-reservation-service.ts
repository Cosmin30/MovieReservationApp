import { Injectable } from '@angular/core';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';

@Injectable({
  providedIn: 'root'
})
export class CancelReservationService {

  constructor(private api: ReservationApiService) {}

  execute(id: string) {
    return this.api.updateReservation(id, { status: 'CANCELLED' });
  }
}
