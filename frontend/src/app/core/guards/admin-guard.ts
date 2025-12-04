// admin.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  console.log('🛡️ AdminGuard - Checking...', {
    authenticated: authService.isAuthenticated(),
    role: authService.getRole()
  });
  
  if (!authService.isAuthenticated()) {
    console.log('❌ AdminGuard - Not authenticated');
    return router.createUrlTree(['/login']);
  }
  
  const role = authService.getRole();
  
  if (role === 'ADMIN') {
    console.log('✅ AdminGuard - Admin access granted');
    return true;
  }
  
  console.log('❌ AdminGuard - Not admin, redirecting');
  return router.createUrlTree(['/movies']); // ✅ Schimbat de la /home la /movies
};