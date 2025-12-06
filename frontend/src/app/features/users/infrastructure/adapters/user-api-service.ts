import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { UserDTO } from '../dtos/user.dto';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/users`;

  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/me`);
  }

  updateCurrentUser(dto: Partial<UserDTO>): Observable<UserDTO> {
    return this.http.patch<UserDTO>(`${this.baseUrl}/me`, dto);
  }

  getUserById(id: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/${id}`);
  }
}
