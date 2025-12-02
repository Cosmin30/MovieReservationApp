import { Component } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-error-message',
  standalone: true,
  imports: [NgIf],
  templateUrl: './error-message.html',
  styleUrls: ['./error-message.css']
})
export class ErrorMessageComponent {
  errorMessage: string | null = null;
}
