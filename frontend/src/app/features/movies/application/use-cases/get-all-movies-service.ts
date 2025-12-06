import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';
import { MoviesState } from '../state/movies-state.state';
import { MovieMapper } from '../../infrastructure/adapters/movie-mapper.mapper';
import { MovieModel } from '../../domain/models/movie.model';
import { LoggerService } from '../../../../core/services/logger.service';

@Injectable({
  providedIn: 'root'
})
export class GetAllMoviesService {
  private api = inject(MovieApiService);
  private state = inject(MoviesState);
  private logger = inject(LoggerService);

  execute(): Observable<MovieModel[]> {
    return this.api.getAllMovies().pipe(
      map(dtos => dtos.map(dto => MovieMapper.fromDto(dto))),
      tap(movies => {
        this.state.setMovies(movies);
      }),
      catchError(error => {
        this.logger.error('Error loading movies:', error);
        throw error;
      })
    );
  }
}
