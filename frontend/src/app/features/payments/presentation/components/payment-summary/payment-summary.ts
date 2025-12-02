import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-payment-summary',
  templateUrl: './payment-summary.html',
  styleUrls: ['./payment-summary.css']
})
export class PaymentSummaryComponent {
  @Input() payment: any;

  goBack() {
    history.back();
  }
}
