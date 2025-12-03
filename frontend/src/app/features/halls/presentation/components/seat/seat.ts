import { Component, Input } from '@angular/core';
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

  selectSeat() {
    console.log('Seat selected', this.seat);
  }
}
