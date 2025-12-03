import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatComponent } from '../seat/seat';

@Component({
  selector: 'app-hall-layout',
  standalone: true,
  imports: [CommonModule, SeatComponent], 
  templateUrl: './hall-layout.html',
  styleUrls: ['./hall-layout.css']
})
export class HallLayoutComponent {
  @Input() hall: any;
  @Input() seats: any[] = [];

  goBack() {
    window.history.back();
  }
}
