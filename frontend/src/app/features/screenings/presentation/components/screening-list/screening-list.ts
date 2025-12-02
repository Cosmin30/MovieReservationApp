import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-screening-list',
  templateUrl: './screening-list.html',
  styleUrls: ['./screening-list.css']
})
export class ScreeningListComponent {
  @Input() screenings: any[] = [];
}
