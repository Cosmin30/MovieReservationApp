import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreatePaymentService } from '../../../application/use-cases/create-payment-service';
import { PaymentFormComponent } from '../../components/payment-form/payment-form';
import { PaymentSummaryComponent } from '../../components/payment-summary/payment-summary';

@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.html',
  styleUrls: ['./payment-page.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PaymentFormComponent,
    PaymentSummaryComponent
  ]
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
