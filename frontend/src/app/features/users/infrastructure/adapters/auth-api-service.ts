import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { LoginRequestDTO } from '../dtos/login-request.dto';
import { LoginResponseDTO } from '../dtos/login-response.dto';
import { RegisterRequestDTO } from '../dtos/register-request.dto';
import { RegisterResponseDTO } from '../dtos/register-response.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/auth`;

  login(dto: LoginRequestDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.baseUrl}/login`, dto);
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/logout`, {});
  }

  register(dto: RegisterRequestDTO): Observable<RegisterResponseDTO> {
    return this.http.post<RegisterResponseDTO>(`${this.baseUrl}/register`, dto);
  }
}
