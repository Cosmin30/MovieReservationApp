import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ScreeningApiService {

  private baseUrl = 'http://localhost:8080/api/screenings';

  constructor(private http: HttpClient) {}

  getAllScreenings() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getScreeningById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getScreeningsByMovie(movieId: string) {
    return this.http.get<any[]>(`${this.baseUrl}?movieId=${movieId}`);
  }

  createScreening(dto: any) {
    return this.http.post(this.baseUrl, dto);
  }

  updateScreening(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchScreening(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deleteScreening(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
