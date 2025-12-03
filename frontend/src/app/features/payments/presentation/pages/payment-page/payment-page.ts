import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
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
export class PaymentPage implements OnInit, OnDestroy {
  payment: any = null;
  private destroy$ = new Subject<void>();

  constructor(private createPayment: CreatePaymentService) {}

  ngOnInit() {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  pay(form: any) {
    this.createPayment.execute(form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: p => {
          this.payment = p;
        }
      });
  }
}
