import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';
import { MovieMapper } from '../../infrastructure/adapters/movie-mapper.mapper';
import { MovieModel } from '../../domain/models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class SearchMoviesService {
  private api = inject(MovieApiService);

  execute(query: string): Observable<MovieModel[]> {
    return this.api.searchMovies(query).pipe(
      map(dtos => dtos.map(dto => MovieMapper.fromDto(dto)))
    );
  }
}
