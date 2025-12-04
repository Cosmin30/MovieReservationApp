import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-privacy-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './privacy-page.html',
  styleUrls: ['./privacy-page.css']
})
export class PrivacyPage {}

