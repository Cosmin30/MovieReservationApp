import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-seat-grid',
  templateUrl: './seat-grid.html',
  styleUrls: ['./seat-grid.css']
})
export class SeatGridComponent {
  @Input() seats: any[] = [];
  @Output() seatSelected = new EventEmitter<any>();

  toggleSeat(seat: any) {
    if (!seat.available) return;
    this.seatSelected.emit(seat);
  }
}
