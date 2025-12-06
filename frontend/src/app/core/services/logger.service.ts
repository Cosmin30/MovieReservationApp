import { Injectable, inject } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  private isProduction = environment.production;
  private enableLogging = environment.enableLogging;

  log(message: string, ...args: any[]): void {
    if (!this.isProduction && this.enableLogging) {
      console.log(message, ...args);
    }
  }

  error(message: string, ...args: any[]): void {
    // Errors should always be logged, even in production
    console.error(message, ...args);
  }

  warn(message: string, ...args: any[]): void {
    if (!this.isProduction && this.enableLogging) {
      console.warn(message, ...args);
    }
  }

  debug(message: string, ...args: any[]): void {
    if (!this.isProduction && this.enableLogging) {
      console.debug(message, ...args);
    }
  }
}

