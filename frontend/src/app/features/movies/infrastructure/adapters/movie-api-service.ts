import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { CacheService } from '../../../../core/services/cache-service';
import { environment } from '../../../../../environments/environment';
import { MovieDTO } from '../dtos/movie.dto';
import { MovieMapper } from './movie-mapper.mapper';
import { MovieModel } from '../../domain/models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class MovieApiService {
  private http = inject(HttpClient);
  private cache = inject(CacheService);
  private baseUrl = `${environment.apiUrl}/movies`;

  getAllMovies(): Observable<MovieDTO[]> {
    return this.cache.getOrFetch(
      'all_movies',
      () => this.http.get<MovieDTO[]>(this.baseUrl)
    );
  }

  getMovieById(id: string): Observable<MovieDTO> {
    return this.cache.getOrFetch(
      `movie_${id}`,
      () => this.http.get<MovieDTO>(`${this.baseUrl}/${id}`)
    );
  }

  createMovie(dto: MovieDTO): Observable<MovieDTO> {
    return this.http.post<MovieDTO>(this.baseUrl, dto).pipe(
      tap(() => {
        this.cache.clear('all_movies');
      })
    );
  }

  updateMovie(id: string, dto: MovieDTO): Observable<MovieDTO> {
    return this.http.put<MovieDTO>(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  patchMovie(id: string, dto: Partial<MovieDTO>): Observable<MovieDTO> {
    return this.http.patch<MovieDTO>(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  deleteMovie(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  searchMovies(query: string): Observable<MovieDTO[]> {
    return this.http.get<MovieDTO[]>(`${this.baseUrl}?search=${encodeURIComponent(query)}`);
  }

  clearCache(): void {
    this.cache.clear('all_movies');
  }
}
