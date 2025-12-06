import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';
import { ReservationState } from '../state/reservation-state.state';
import { ReservationMapper } from '../../infrastructure/adapters/reservation-mapper.mapper';
import { ReservationModel } from '../../domain/models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class GetUserReservationsService {
  private api = inject(ReservationApiService);
  private state = inject(ReservationState);

  execute(userId: string, forceRefresh: boolean = false): Observable<ReservationModel[]> {
    return this.api.getReservationsByUser(userId, forceRefresh).pipe(
      map(dtos => dtos.map(dto => ReservationMapper.fromDto(dto))),
      tap(reservations => {
        this.state.setReservations(reservations);
      })
    );
  }
}
