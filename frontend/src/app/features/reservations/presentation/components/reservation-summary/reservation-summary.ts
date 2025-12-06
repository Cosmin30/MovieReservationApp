import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reservation-summary',
   standalone: true,
  imports: [CommonModule],
  templateUrl: './reservation-summary.html',
  styleUrls: ['./reservation-summary.css']
})
export class ReservationSummaryComponent {
  @Input() reservation: any;
}
