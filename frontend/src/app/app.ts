import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// SHARED COMPONENTS
import { HeaderComponent } from './shared/components/header/header';
import { FooterComponent } from './shared/components/footer/footer';
import { NotificationComponent } from './shared/components/notification/notification';
import { ErrorMessageComponent } from './shared/components/error-message/error-message';
import { ConfirmationDialogComponent } from './shared/components/confirmation-dialog/confirmation-dialog';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
  imports: [
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    NotificationComponent,
    ErrorMessageComponent,
    ConfirmationDialogComponent
  ]
})
export class App {}
