// auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  console.log('🛡️ AuthGuard - Route:', state.url);
  console.log('🛡️ AuthGuard - isAuthenticated:', authService.isAuthenticated());
  
  if (authService.isAuthenticated()) {
    console.log('✅ AuthGuard - Access granted');
    return true;
  }
  
  console.log('❌ AuthGuard - Redirecting to /login');
  // ✅ Salvează URL-ul unde voiam să mergem pentru redirect după login
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};