import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReservationModel } from '../../../domain/models/reservation.model';

@Component({
  selector: 'app-reservation-card',
  templateUrl: './reservation-card.html',
  styleUrls: ['./reservation-card.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ReservationCardComponent {
  @Input() reservation!: ReservationModel;
}
