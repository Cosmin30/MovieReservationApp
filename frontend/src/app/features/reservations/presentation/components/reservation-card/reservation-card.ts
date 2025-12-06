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

  getStatusText(): string {
    if (!this.reservation?.status) return 'Necunoscut';
    
    const status = this.reservation.status.toUpperCase();
    switch (status) {
      case 'CONFIRMED':
      case 'CREATED': // Treat CREATED as paid since payment is simulated
        return 'Paid';
      case 'PENDING':
        return 'Pending';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return 'Paid'; // Default to Paid for any other status
    }
  }

  getStatusClass(): string {
    if (!this.reservation?.status) return 'bg-success';
    
    const status = this.reservation.status.toUpperCase();
    switch (status) {
      case 'CONFIRMED':
      case 'CREATED': // Treat CREATED as paid (green)
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return 'bg-success'; // Default to green (paid) for any other status
    }
  }
}
