import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-seat',
  standalone: true,    // must stay standalone
  imports: [CommonModule], // pentru *ngIf, *ngFor, [ngClass]
  templateUrl: './seat.html',
  styleUrls: ['./seat.css']
})
export class SeatComponent {
  @Input() seat: any;
  @Output() seatToggled = new EventEmitter<any>();

  selectSeat() {
    if (this.seat?.status !== 'AVAILABLE') {
      return;
    }
    this.seat.isSelected = !this.seat.isSelected;
    this.seatToggled.emit(this.seat);
  }
}
