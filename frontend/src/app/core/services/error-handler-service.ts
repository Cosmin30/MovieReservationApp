import { Injectable, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from '../../shared/services/notification.service';
import { LoggerService } from './logger.service';

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {
  private notificationService: NotificationService = inject(NotificationService);
  private logger: LoggerService = inject(LoggerService);

  handle(error: HttpErrorResponse | Error): void {
    this.logger.error('API Error:', error);

    if (error instanceof HttpErrorResponse) {
      this.handleHttpError(error);
    } else {
      this.handleGenericError(error);
    }
  }

  private handleHttpError(error: HttpErrorResponse): void {
    let message = 'A apărut o eroare!';

    if (error.error?.message) {
      message = error.error.message;
    } else if (error.message) {
      message = error.message;
    } else {
      switch (error.status) {
        case 400:
          message = 'Cerere invalidă. Verifică datele introduse.';
          break;
        case 401:
          message = 'Nu ești autentificat. Te rugăm să te loghezi.';
          break;
        case 403:
          message = 'Nu ai permisiunea de a accesa această resursă.';
          break;
        case 404:
          message = 'Resursa solicitată nu a fost găsită.';
          break;
        case 500:
          message = 'Eroare de server. Te rugăm să încerci mai târziu.';
          break;
        case 0:
          message = 'Nu s-a putut conecta la server. Verifică conexiunea la internet.';
          break;
        default:
          message = `Eroare ${error.status}: ${error.statusText || 'Eroare necunoscută'}`;
      }
    }

    this.notificationService.error(message);
  }

  private handleGenericError(error: Error): void {
    this.notificationService.error(error.message || 'A apărut o eroare neașteptată!');
  }
}
