import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TicketDetailsComponent } from './presentation/components/ticket-details/ticket-details';

const routes: Routes = [
  { path: ':id', component: TicketDetailsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TicketsRoutingModule {}
