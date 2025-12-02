import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-screening-card',
  templateUrl: './screening-card.html',
  styleUrls: ['./screening-card.css']
})
export class ScreeningCardComponent {
  @Input() screening: any;
}
