import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';
import { ReservationMapper } from '../../infrastructure/adapters/reservation-mapper.mapper';
import { ReservationModel } from '../../domain/models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class CreateReservationService {
  private api = inject(ReservationApiService);

  execute(userId: string, screeningId: string, seatIds: string[], pricePerSeat: number): Observable<ReservationModel> {
    return this.api.createReservation(userId, screeningId, seatIds, pricePerSeat).pipe(
      map(dto => ReservationMapper.fromDto(dto))
    );
  }
}
