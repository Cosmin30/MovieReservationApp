import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScreeningCardComponent } from '../screening-card/screening-card';

@Component({
  selector: 'app-screening-list',
  templateUrl: './screening-list.html',
  styleUrls: ['./screening-list.css'],
  standalone: true,
  imports: [CommonModule, ScreeningCardComponent]  
})
export class ScreeningListComponent {
  @Input() screenings: any[] = [];
}
