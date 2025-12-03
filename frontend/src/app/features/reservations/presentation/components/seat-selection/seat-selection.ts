// seat-selection.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatGridComponent } from '../seat-grid/seat-grid';

@Component({
  selector: 'app-seat-selection',
  templateUrl: './seat-selection.html',
  styleUrls: ['./seat-selection.css'],
  standalone: true,
  imports: [CommonModule, SeatGridComponent] 
})
export class SeatSelectionComponent {
  @Input() seats: any[] = [];
  selectedSeats: any[] = [];

  selectSeat(seat: any) {
    if (!this.selectedSeats.includes(seat)) {
      this.selectedSeats.push(seat);
    }
  }
}
