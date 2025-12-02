import { Injectable } from '@angular/core';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';

@Injectable({
  providedIn: 'root'
})
export class SearchMoviesService {

  constructor(private api: MovieApiService) {}

  execute(query: string) {
    return this.api.searchMovies(query);
  }
}
