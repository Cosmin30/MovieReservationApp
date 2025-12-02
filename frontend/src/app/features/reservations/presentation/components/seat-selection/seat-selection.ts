import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-seat-selection',
  templateUrl: './seat-selection.html',
  styleUrls: ['./seat-selection.css']
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
