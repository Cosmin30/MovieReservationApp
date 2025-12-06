import { Injectable } from '@angular/core';
import { TicketApiService } from '../../infrastructure/adapters/ticket-api-service';

@Injectable({
  providedIn: 'root'
})
export class BuyTicketService {

  constructor(private api: TicketApiService) {}

  /**
   * Buy a single ticket for a reservation and seat
   */
  execute(reservationId: string, seatId: string, price: number) {
    const dto = {
      reservationId,
      seatId,
      price
    };
    return this.api.createTicket(dto);
  }

  /**
   * Buy multiple tickets for a reservation with multiple seats
   */
  executeBatch(reservationId: string, seatIds: string[], pricePerSeat: number) {
    const tickets = seatIds.map(seatId => ({
      reservationId,
      seatId,
      price: pricePerSeat
    }));
    
    return Promise.all(
      tickets.map(ticket => this.api.createTicket(ticket).toPromise())
    );
  }
}
