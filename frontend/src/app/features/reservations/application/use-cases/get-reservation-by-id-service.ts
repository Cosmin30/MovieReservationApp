import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';
import { ReservationMapper } from '../../infrastructure/adapters/reservation-mapper.mapper';
import { ReservationModel } from '../../domain/models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class GetReservationByIdService {
  private api = inject(ReservationApiService);

  execute(id: string): Observable<ReservationModel> {
    return this.api.getReservationById(id).pipe(
      map(dto => ReservationMapper.fromDto(dto))
    );
  }
}
