import { Injectable } from '@angular/core';
import { PaymentApiService } from '../../infrastructure/adapters/payment-api-service';

@Injectable({
  providedIn: 'root'
})
export class ProcessPaymentService {

  constructor(private api: PaymentApiService) {}

  execute(id: string) {
    return this.api.processPayment(id);
  }
}
