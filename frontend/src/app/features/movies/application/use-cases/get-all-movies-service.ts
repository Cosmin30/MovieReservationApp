import { Injectable } from '@angular/core';
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

  execute() {
    this.api.getAllMovies().subscribe(movies => {
      this.state.setMovies(movies);
    });
  }
}
