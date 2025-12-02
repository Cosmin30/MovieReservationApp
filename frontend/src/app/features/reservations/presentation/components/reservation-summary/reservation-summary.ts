import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-reservation-summary',
  templateUrl: './reservation-summary.html',
  styleUrls: ['./reservation-summary.css']
})
export class ReservationSummaryComponent {
  @Input() reservation: any;
}
