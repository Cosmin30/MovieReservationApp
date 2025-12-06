import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError, timeout } from 'rxjs';
import { LoggerService } from '../services/logger.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  const logger = inject(LoggerService);
  
  // Only add auth in browser context
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    logger.debug('Auth Interceptor - Request:', req.method, req.url);
    logger.debug('Auth Interceptor - Token exists:', !!token);
    logger.debug('Auth Interceptor - Role:', role);

    if (token && token.trim()) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token.trim()}`
        }
      });
      logger.debug('Auth Interceptor - Authorization header added');
    } else {
      logger.debug('Auth Interceptor - No token found, request will be unauthenticated');
    }
  }

  return next(req).pipe(
    timeout(10000),
    catchError((error: HttpErrorResponse) => {
      logger.error('Auth Interceptor - Error:', error.status, error.statusText, error.url);
      
      // If 401 Unauthorized, clear invalid token
      if (error.status === 401 && isPlatformBrowser(platformId)) {
        logger.debug('Auth Interceptor - 401 Unauthorized - clearing auth data');
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('user');
      }
      
      // If 403 Forbidden, log for debug
      if (error.status === 403) {
        logger.debug('Auth Interceptor - 403 Forbidden - User may not have required role');
        logger.debug('Auth Interceptor - Current role:', localStorage.getItem('role'));
        logger.debug('Auth Interceptor - Request URL:', error.url);
      }
      
      return throwError(() => error);
    })
  );
};
