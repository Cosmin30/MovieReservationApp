import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-available-seats-display',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './available-seats-display.html',
  styleUrls: ['./available-seats-display.css']
})
export class AvailableSeatsDisplayComponent {
  @Input() seats: any[] = [];

  get availableCount(): number {
    return this.seats.filter(s => s.status === 'AVAILABLE').length;
  }

  get reservedCount(): number {
    return this.seats.filter(s => s.status === 'RESERVED').length;
  }

  get unavailableCount(): number {
    return this.seats.filter(s => s.status === 'UNAVAILABLE').length;
  }

  get totalSeats(): number {
    return this.seats.length;
  }
}
