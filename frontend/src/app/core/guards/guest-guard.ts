import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('GuestGuard - isAuthenticated:', authService.isAuthenticated());
  
  if (!authService.isAuthenticated()) {
    return true;
  }

  console.log('GuestGuard - Redirecting to /movies');
  return router.parseUrl('/movies');
};