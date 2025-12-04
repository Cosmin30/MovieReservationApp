import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError, timeout } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  
  console.log('🌐 HTTP Request:', req.method, req.url);
  
  // Only add auth in browser context
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');

    if (token && token.trim()) {
      console.log('🔑 Adding auth token to request');
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.trim()}`
        }
      });
    }
  }

  return next(req).pipe(
    timeout(10000), // ✅ 10 secunde timeout global
    catchError((error: HttpErrorResponse) => {
      console.error('❌ HTTP Error:', {
        status: error.status,
        message: error.message,
        url: req.url
      });
      
      // ✅ Dacă e 401 Unauthorized, șterge token-ul invalid
      if (error.status === 401 && isPlatformBrowser(platformId)) {
        console.log('🔓 Token invalid, clearing...');
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user');
        // Nu face redirect aici - lasă guard-ul să se ocupe
      }
      
      return throwError(() => error);
    })
  );
};