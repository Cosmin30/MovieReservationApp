import { Injectable } from '@angular/core';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetMovieByIdService {

  constructor(private api: MovieApiService) {}

  execute(id: string) {
    return this.api.getMovieById(id);
  }
}
