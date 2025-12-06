import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CreatePaymentService } from '../../../application/use-cases/create-payment-service';
import { PaymentFormComponent } from '../../components/payment-form/payment-form';
import { PaymentSummaryComponent } from '../../components/payment-summary/payment-summary';
import { PaymentDTO } from '../../../infrastructure/dtos/payment.dto';
import { CreatePaymentDTO } from '../../../infrastructure/dtos/create-payment.dto';
import { LoggerService } from '../../../../../core/services/logger.service';
import { NotificationService } from '../../../../../shared/services/notification.service';

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
  payment: PaymentDTO | null = null;
  private destroy$ = new Subject<void>();
  private createPayment = inject(CreatePaymentService);
  private logger = inject(LoggerService);
  private notificationService = inject(NotificationService);

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  pay(form: CreatePaymentDTO): void {
    this.createPayment.execute(form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payment: PaymentDTO) => {
          this.payment = payment;
          this.notificationService.success('Plata a fost procesată cu succes!');
        },
        error: (err) => {
          this.logger.error('Error processing payment:', err);
          this.notificationService.error('A apărut o eroare la procesarea plății. Te rugăm să încerci din nou.');
        }
      });
  }
}
