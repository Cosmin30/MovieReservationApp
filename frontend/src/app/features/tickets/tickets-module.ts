import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { TicketCardComponent } from './presentation/components/ticket-card/ticket-card';
import { TicketDetailsComponent } from './presentation/components/ticket-details/ticket-details';

@NgModule({
  declarations: [
    TicketCardComponent,
    TicketDetailsComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class TicketsModule {}
