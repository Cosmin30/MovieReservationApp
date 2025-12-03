import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const role = authService.getRole();
  
  if (authService.isAuthenticated() && role === 'ADMIN') {
    return true;
  }
  
  // Redirect to home if not admin
  return router.parseUrl('/home');
};

