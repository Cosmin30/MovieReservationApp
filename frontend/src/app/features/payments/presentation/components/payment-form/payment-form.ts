import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.html',
  styleUrls: ['./payment-form.css'],
  standalone: true,
  imports: [CommonModule, FormsModule] 
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
