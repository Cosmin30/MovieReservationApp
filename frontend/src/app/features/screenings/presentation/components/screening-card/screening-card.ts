import { Component, Input } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-screening-card',
  templateUrl: './screening-card.html',
  styleUrls: ['./screening-card.css'],
  standalone: true,
  imports: [CommonModule, RouterModule] 
})
export class ScreeningCardComponent {
  @Input() screening: any;
}
