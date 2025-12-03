import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-reservation-card',
  templateUrl: './reservation-card.html',
  styleUrls: ['./reservation-card.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ReservationCardComponent {
  @Input() reservation: any;
}
