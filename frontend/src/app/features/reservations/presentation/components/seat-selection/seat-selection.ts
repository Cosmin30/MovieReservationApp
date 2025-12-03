// seat-selection.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
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
  @Output() selectedSeatsChanged = new EventEmitter<any[]>();

  get selectedSeats(): any[] {
    return this.seats.filter(seat => seat.isSelected);
  }

  selectSeat(seat: any) {
    // Emit the updated list of selected seats
    this.selectedSeatsChanged.emit(this.selectedSeats);
  }

  deselectAll() {
    this.seats.forEach(seat => seat.isSelected = false);
    this.selectedSeatsChanged.emit(this.selectedSeats);
  }
}
