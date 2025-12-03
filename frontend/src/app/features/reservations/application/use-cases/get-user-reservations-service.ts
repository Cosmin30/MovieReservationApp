import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
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

  execute(userId: string): Observable<any[]> {
    return this.api.getReservationsByUser(userId).pipe(
      tap(res => {
        this.state.setReservations(res);
      })
    );
  }
}
