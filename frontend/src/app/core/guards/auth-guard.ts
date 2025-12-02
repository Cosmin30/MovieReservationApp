import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { User } from '../../features/users/domain/models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor() {
    this.loadUserFromBackend();
  }

  login(email: string, password: string) {
    return this.http.post<User>(`${this.apiUrl}/auth/login`, { email, password })
      .subscribe(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      });
  }

  private loadUserFromBackend() {
    const saved = localStorage.getItem('user');
    if (saved) {
      this.currentUserSubject.next(JSON.parse(saved));
    }

    this.http.get<User>(`${this.apiUrl}/users/me`).subscribe({
      next: user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      },
      error: () => {}
    });
  }

  logout() {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }
}
