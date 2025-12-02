import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// SHARED COMPONENTS
import { HeaderComponent } from './shared/components/header/header';
import { FooterComponent } from './shared/components/footer/footer';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner';
import { NotificationComponent } from './shared/components/notification/notification';
import { ErrorMessageComponent } from './shared/components/error-message/error-message';
import { ConfirmationDialogComponent } from './shared/components/confirmation-dialog/confirmation-dialog';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.html',

  // ❗ AICI trebuie importate TOATE componentele folosite în app.html
  imports: [
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    LoadingSpinnerComponent,
    NotificationComponent,
    ErrorMessageComponent,
    ConfirmationDialogComponent
  ]
})
export class App {}
