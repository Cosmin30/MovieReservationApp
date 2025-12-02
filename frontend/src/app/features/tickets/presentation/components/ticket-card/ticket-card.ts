import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ticket-card',
  templateUrl: './ticket-card.html',
  styleUrls: ['./ticket-card.css']
})
export class TicketCardComponent {
  @Input() ticket: any;
}
