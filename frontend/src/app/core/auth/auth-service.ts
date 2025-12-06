import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, tap, timeout, catchError, of, Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { UserModel } from '../../features/users/domain/models/user.model';
import { environment } from '../../../environments/environment';
import { LoggerService } from '../services/logger.service';
import { UserDTO } from '../../features/users/infrastructure/dtos/user.dto';
import { UserMapper } from '../../features/users/infrastructure/adapters/user-mapper.mapper';

interface LoginResponse {
  token: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private logger = inject(LoggerService);
  private apiUrl = `${environment.apiUrl}/auth`;

  private currentUserSubject = new BehaviorSubject<UserModel | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  private userProfileSubscription: Subscription | null = null;

  constructor() {
    if (this.isBrowser()) {
      this.loadUserFromStorage();
    }
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      timeout(10000),
      tap(response => {
        if (this.isBrowser()) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('role', response.role);
        }
        this.loadUserProfile();
        this.router.navigate(['/home']);
      }),
      catchError(error => {
        this.logger.error('Login failed:', error);
        throw error;
      })
    );
  }

  private loadUserFromStorage(): void {
    if (!this.isBrowser()) return;

    const token = localStorage.getItem('token');
    if (token) {
      this.logger.debug('Token found, loading user profile...');
      this.loadUserProfile();
    } else {
      this.logger.debug('No token found');
    }
  }

  loadUserProfile(): void {
    const token = this.getToken();
    if (!token) {
      this.logger.debug('No token available');
      return;
    }

    // Unsubscribe previous subscription if exists
    if (this.userProfileSubscription) {
      this.userProfileSubscription.unsubscribe();
    }

    this.logger.debug('Fetching user profile...');
    
    // Use http directly to avoid circular dependency with UserApiService
    this.userProfileSubscription = this.http
      .get<UserDTO>(`${environment.apiUrl}/users/me`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .pipe(
        timeout(8000),
        catchError(error => {
          this.logger.error('Failed to load user profile:', error);
          return of(null);
        })
      )
      .subscribe({
        next: (response) => {
          if (!response) {
            this.logger.debug('No user data received');
            return;
          }

          try {
            const user = UserMapper.fromDto(response);
            this.logger.debug('User loaded:', user);
            this.currentUserSubject.next(user);
            if (this.isBrowser()) {
              localStorage.setItem('user', JSON.stringify(user));
            }
          } catch (error) {
            this.logger.error('Error mapping user:', error);
          }
        },
        error: (err) => {
          this.logger.error('Subscription error:', err);
        }
      });
  }

  ngOnDestroy(): void {
    if (this.userProfileSubscription) {
      this.userProfileSubscription.unsubscribe();
    }
  }

  logout() {
    if (this.isBrowser()) {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('user');
    }
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.isBrowser() ? localStorage.getItem('token') : null;
  }

  getRole(): string | null {
    return this.isBrowser() ? localStorage.getItem('role') : null;
  }

  getCurrentUser(): UserModel | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    const hasToken = !!this.getToken();
    this.logger.debug('isAuthenticated:', hasToken);
    return hasToken;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}