import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-reservation-summary',
   standalone: true,
  templateUrl: './reservation-summary.html',
  styleUrls: ['./reservation-summary.css']
})
export class ReservationSummaryComponent {
  @Input() reservation: any;
}
