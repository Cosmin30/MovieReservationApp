import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  console.log('AuthGuard - isAuthenticated:', authService.isAuthenticated());
  console.log('AuthGuard - token:', authService.getToken());
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  console.log('AuthGuard - Redirecting to /login');
  return router.parseUrl('/login');
};