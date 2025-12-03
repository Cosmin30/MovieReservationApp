import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DatePipe } from '@angular/common'; // add this import

@Component({
  selector: 'app-screening-time-selector',
  templateUrl: './screening-time-selector.html',
  styleUrls: ['./screening-time-selector.css'],
  standalone: true, 
  imports: [DatePipe] 
})
export class ScreeningTimeSelectorComponent {
  @Input() screenings: any[] = [];
  @Output() onSelected = new EventEmitter<any>();

  select(s: any) {
    this.onSelected.emit(s);
  }
}