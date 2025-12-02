import { Injectable } from '@angular/core';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';
import { ReservationState } from '../state/reservation-state.state';

@Injectable({
  providedIn: 'root'
})
export class GetUserReservationsService {

  constructor(
    private api: ReservationApiService,
    private state: ReservationState
  ) {}

  execute(userId: string) {
    this.api.getReservationsByUser(userId).subscribe(res => {
      this.state.setReservations(res);
    });
  }
}
