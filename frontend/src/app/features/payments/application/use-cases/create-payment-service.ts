import { Injectable } from '@angular/core';
import { PaymentApiService } from '../../infrastructure/adapters/payment-api-service';

@Injectable({
  providedIn: 'root'
})
export class CreatePaymentService {

  constructor(private api: PaymentApiService) {}

  execute(dto: any) {
    return this.api.createPayment(dto);
  }
}
