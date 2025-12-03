import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-ticket-card',
  templateUrl: './ticket-card.html',
  styleUrls: ['./ticket-card.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class TicketCardComponent {
  @Input() ticket: any;
}
