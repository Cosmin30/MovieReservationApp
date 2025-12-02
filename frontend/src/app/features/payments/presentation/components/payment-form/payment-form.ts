import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.html',
  styleUrls: ['./payment-form.css']
})
export class PaymentFormComponent {

  @Output() onPay = new EventEmitter<any>();

  form = {
    amount: 0,
    reservationId: ''
  };

  submit() {
    this.onPay.emit(this.form);
  }
}
