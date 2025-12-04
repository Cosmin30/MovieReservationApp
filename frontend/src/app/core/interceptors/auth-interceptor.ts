import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError, timeout } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  
  // Only add auth in browser context
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    console.log('🔍 [AUTH INTERCEPTOR] Request:', req.method, req.url);
    console.log('🔍 [AUTH INTERCEPTOR] Token exists:', !!token);
    console.log('🔍 [AUTH INTERCEPTOR] Role:', role);

    if (token && token.trim()) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.trim()}`
        }
      });
      console.log('✅ [AUTH INTERCEPTOR] Authorization header added');
    } else {
      console.log('⚠️ [AUTH INTERCEPTOR] No token found, request will be unauthenticated');
    }
  }

  return next(req).pipe(
    timeout(10000), // ✅ 10 secunde timeout global
    catchError((error: HttpErrorResponse) => {
      console.log('❌ [AUTH INTERCEPTOR] Error:', error.status, error.statusText, error.url);
      
      // ✅ Dacă e 401 Unauthorized, șterge token-ul invalid
      if (error.status === 401 && isPlatformBrowser(platformId)) {
        console.log('🔒 [AUTH INTERCEPTOR] 401 Unauthorized - clearing auth data');
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user');
        // Nu face redirect aici - lasă guard-ul să se ocupe
      }
      
      // ✅ Dacă e 403 Forbidden, logăm pentru debug
      if (error.status === 403) {
        console.log('🚫 [AUTH INTERCEPTOR] 403 Forbidden - User may not have required role');
        console.log('🚫 [AUTH INTERCEPTOR] Current role:', localStorage.getItem('role'));
        console.log('🚫 [AUTH INTERCEPTOR] Request URL:', error.url);
      }
      
      return throwError(() => error);
    })
  );
};
