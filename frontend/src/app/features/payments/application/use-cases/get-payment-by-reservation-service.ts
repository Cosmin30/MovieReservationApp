import { Injectable } from '@angular/core';
import { PaymentApiService } from '../../infrastructure/adapters/payment-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetPaymentByReservationService {

  constructor(private api: PaymentApiService) {}

  execute(reservationId: string) {
    return this.api.getPaymentByReservation(reservationId);
  }
}
