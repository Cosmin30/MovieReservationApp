import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CacheService } from '../../../../core/services/cache-service';
import { environment } from '../../../../../environments/environment';
import { ScreeningDTO } from '../dtos/screening.dto';

@Injectable({
  providedIn: 'root'
})
export class ScreeningApiService {
  private http = inject(HttpClient);
  private cache = inject(CacheService);
  private baseUrl = `${environment.apiUrl}/screenings`;

  getAllScreenings(): Observable<ScreeningDTO[]> {
    return this.cache.getOrFetch(
      'all_screenings',
      () => this.http.get<ScreeningDTO[]>(this.baseUrl)
    );
  }

  getScreeningById(id: string): Observable<ScreeningDTO> {
    return this.cache.getOrFetch(
      `screening_${id}`,
      () => this.http.get<ScreeningDTO>(`${this.baseUrl}/${id}`)
    );
  }

  clearScreeningCache(id: string): void {
    this.cache.clear(`screening_${id}`);
  }

  getScreeningsByMovie(movieId: string): Observable<ScreeningDTO[]> {
    return this.cache.getOrFetch(
      `screenings_movie_${movieId}`,
      () => this.http.get<ScreeningDTO[]>(`${this.baseUrl}?movieId=${encodeURIComponent(movieId)}`)
    );
  }

  clearScreeningsByMovieCache(movieId: string): void {
    this.cache.clear(`screenings_movie_${movieId}`);
  }

  createScreening(dto: ScreeningDTO): Observable<ScreeningDTO> {
    return this.http.post<ScreeningDTO>(this.baseUrl, dto).pipe(
      tap(() => {
        this.cache.clear('all_screenings');
      })
    );
  }

  updateScreening(id: string, dto: ScreeningDTO): Observable<ScreeningDTO> {
    return this.http.put<ScreeningDTO>(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  patchScreening(id: string, dto: Partial<ScreeningDTO>): Observable<ScreeningDTO> {
    return this.http.patch<ScreeningDTO>(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  deleteScreening(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        this.cache.clear('all_screenings');
        this.cache.clear(`screening_${id}`);
      })
    );
  }

  clearCache(): void {
    this.cache.clear('all_screenings');
  }
}
