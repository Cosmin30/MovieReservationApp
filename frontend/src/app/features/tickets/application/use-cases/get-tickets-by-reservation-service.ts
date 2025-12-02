import { Injectable } from '@angular/core';
import { TicketApiService } from '../../infrastructure/adapters/ticket-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetTicketsByReservationService {

  constructor(private api: TicketApiService) {}

  execute(reservationId: string) {
    return this.api.getTicketsByReservation(reservationId);
  }
}
