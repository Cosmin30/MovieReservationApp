import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  
  // Only add auth in browser context
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');

    if (token && token.trim()) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.trim()}`
        }
      });
    }
  }

  return next(req);
};