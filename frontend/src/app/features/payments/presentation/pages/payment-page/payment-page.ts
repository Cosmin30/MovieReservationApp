import { Component } from '@angular/core';
import { CreatePaymentService } from '../../../application/use-cases/create-payment-service';

@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.html',
  styleUrls: ['./payment-page.css']
})
export class PaymentPage {

  payment: any = null;

  constructor(private createPayment: CreatePaymentService) {}

  pay(form: any) {
    this.createPayment.execute(form).subscribe(p => {
      this.payment = p;
    });
  }
}
