import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';
import { MoviesState } from '../state/movies-state.state';

@Injectable({
  providedIn: 'root'
})
export class GetAllMoviesService {

  constructor(
    private api: MovieApiService,
    private state: MoviesState
  ) {}

  execute(): Observable<any[]> {
    return this.api.getAllMovies().pipe(
      tap(movies => {
        this.state.setMovies(movies);
      })
    );
  }
}
