import { Component } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [NgIf],
  templateUrl: './loading-spinner.html',
  styleUrls: ['./loading-spinner.css']
})
export class LoadingSpinnerComponent {
  isLoading = false; // poate fi legat la un LoadingService
}
