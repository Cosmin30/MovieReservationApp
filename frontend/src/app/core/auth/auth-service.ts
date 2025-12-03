import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { UserModel } from '../../features/users/domain/models/user.model';

interface LoginResponse {
  token: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/api';

  private currentUserSubject = new BehaviorSubject<UserModel | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor() {
    if (this.isBrowser()) {
      this.loadUserFromStorage();
    }
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, { email, password }).pipe(
      tap(response => {
        if (this.isBrowser()) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('role', response.role);
        }
        this.loadUserProfile();
        this.router.navigate(['/movies']);
      })
    );
  }

  private loadUserFromStorage() {
    if (!this.isBrowser()) return;

    const token = localStorage.getItem('token');
    if (token) {
      this.loadUserProfile();
    }
  }

  loadUserProfile() {
    const token = this.getToken();
    if (!token) return;

    this.http
      .get<UserModel>(`${this.apiUrl}/users/me`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: user => {
          this.currentUserSubject.next(user);
          if (this.isBrowser()) {
            localStorage.setItem('user', JSON.stringify(user));
          }
        },
        error: () => {
          this.logout();
        }
      });
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
    return !!this.getToken();
  }
}
