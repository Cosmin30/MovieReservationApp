import { Injectable } from '@angular/core';
import { ReservationApiService } from '../../infrastructure/adapters/reservation-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetReservationByIdService {

  constructor(private api: ReservationApiService) {}

  execute(id: string) {
    return this.api.getReservationById(id);
  }
}
