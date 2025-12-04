import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
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

  clearScreeningCache(id: string): void {
    this.cache.clear(`screening_${id}`);
  }

  getScreeningsByMovie(movieId: string) {
    return this.cache.getOrFetch(
      `screenings_movie_${movieId}`,
      () => this.http.get<any[]>(`${this.baseUrl}?movieId=${movieId}`)
    );
  }

  clearScreeningsByMovieCache(movieId: string): void {
    this.cache.clear(`screenings_movie_${movieId}`);
  }

  createScreening(dto: any) {
    return this.http.post(this.baseUrl, dto).pipe(
      tap(() => {
        // Clear cache after create to force reload
        this.cache.clear('all_screenings');
      })
    );
  }

  updateScreening(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        // Clear cache after update to force reload
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  patchScreening(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        // Clear cache after patch to force reload
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  deleteScreening(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        // Clear cache after delete to force reload
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  clearCache(): void {
    this.cache.clear('all_screenings');
  }
}
