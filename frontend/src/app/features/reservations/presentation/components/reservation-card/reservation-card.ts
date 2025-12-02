import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-reservation-card',
  templateUrl: './reservation-card.html',
  styleUrls: ['./reservation-card.css']
})
export class ReservationCardComponent {
  @Input() reservation: any;
}
