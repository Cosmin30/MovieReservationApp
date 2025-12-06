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
}
