import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {

  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getCurrentUser() {
    return this.http.get(`${this.baseUrl}/me`);
  }

  updateCurrentUser(dto: any) {
    return this.http.patch(`${this.baseUrl}/me`, dto);
  }

  getUserById(id: string) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }
}
