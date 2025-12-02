import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const GuestGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) return true;

  return router.parseUrl('/');
};
