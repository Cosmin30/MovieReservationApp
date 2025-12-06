// auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isAuthenticated()) {
    return true;
  }
  
  // ✅ Salvează URL-ul unde voiam să mergem pentru redirect după login
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};