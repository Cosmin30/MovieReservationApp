import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';
import { LoggerService } from '../services/logger.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const logger = inject(LoggerService);
  
  logger.debug('AdminGuard - Checking...', {
    authenticated: authService.isAuthenticated(),
    role: authService.getRole()
  });
  
  if (!authService.isAuthenticated()) {
    logger.debug('AdminGuard - Not authenticated');
    return router.createUrlTree(['/login']);
  }
  
  const role = authService.getRole();
  
  if (role === 'ADMIN') {
    logger.debug('AdminGuard - Admin access granted');
    return true;
  }
  
  logger.debug('AdminGuard - Not admin, redirecting');
  return router.createUrlTree(['/movies']);
};