import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {

  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(dto: any) {
    return this.http.post(`${this.baseUrl}/login`, dto);
  }

  logout() {
    return this.http.post(`${this.baseUrl}/logout`, {});
  }

  register(dto: any) {
    return this.http.post(`${this.baseUrl}/register`, dto);
  }
}
