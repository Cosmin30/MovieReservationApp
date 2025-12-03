import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { PaymentFormComponent } from './presentation/components/payment-form/payment-form';
import { PaymentSummaryComponent } from './presentation/components/payment-summary/payment-summary';
import { PaymentPage } from './presentation/pages/payment-page/payment-page';

@NgModule({
  declarations: [], 
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    PaymentFormComponent,       
    PaymentSummaryComponent,
    PaymentPage
  ]
})
export class PaymentsModule {}
