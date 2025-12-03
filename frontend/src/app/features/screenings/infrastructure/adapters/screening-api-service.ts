import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CacheService } from '../../../../core/services/cache-service';

@Injectable({
  providedIn: 'root'
})
export class ScreeningApiService {

  private baseUrl = 'http://localhost:8080/api/screenings';

  constructor(
    private http: HttpClient,
    private cache: CacheService
  ) {}

  getAllScreenings() {
    return this.cache.getOrFetch(
      'all_screenings',
      () => this.http.get<any[]>(this.baseUrl)
    );
  }

  getScreeningById(id: string) {
    return this.cache.getOrFetch(
      `screening_${id}`,
      () => this.http.get<any>(`${this.baseUrl}/${id}`)
    );
  }

  getScreeningsByMovie(movieId: string) {
    return this.cache.getOrFetch(
      `screenings_movie_${movieId}`,
      () => this.http.get<any[]>(`${this.baseUrl}?movieId=${movieId}`)
    );
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
