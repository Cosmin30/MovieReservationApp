import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { CacheService } from '../../../../core/services/cache-service';

@Injectable({
  providedIn: 'root'
})
export class MovieApiService {

  private baseUrl = 'http://localhost:8080/api/movies';

  constructor(
    private http: HttpClient,
    private cache: CacheService
  ) {}

  getAllMovies() {
    return this.cache.getOrFetch(
      'all_movies',
      () => this.http.get<any[]>(this.baseUrl)
    );
  }

  getMovieById(id: string) {
    return this.cache.getOrFetch(
      `movie_${id}`,
      () => this.http.get<any>(`${this.baseUrl}/${id}`)
    );
  }

  createMovie(dto: any) {
    return this.http.post(this.baseUrl, dto).pipe(
      tap(() => {
        // Clear cache after create to force reload
        this.cache.clear('all_movies');
      })
    );
  }

  updateMovie(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        // Clear cache after update to force reload
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  patchMovie(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto).pipe(
      tap(() => {
        // Clear cache after patch to force reload
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  deleteMovie(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        // Clear cache after delete to force reload
        this.cache.clear('all_movies');
        this.cache.clear(`movie_${id}`);
      })
    );
  }

  searchMovies(query: string) {
    return this.http.get<any[]>(`${this.baseUrl}?search=${query}`);
  }

  clearCache(): void {
    this.cache.clear('all_movies');
  }
}
