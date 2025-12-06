import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';
import { LoggerService } from '../services/logger.service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const logger = inject(LoggerService);

  logger.debug('GuestGuard - isAuthenticated:', authService.isAuthenticated());
  
  if (!authService.isAuthenticated()) {
    return true;
  }

  logger.debug('GuestGuard - Redirecting to /movies');
  return router.parseUrl('/movies');
};