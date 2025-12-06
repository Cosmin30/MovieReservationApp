import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MovieApiService } from '../../infrastructure/adapters/movie-api-service';
import { MovieMapper } from '../../infrastructure/adapters/movie-mapper.mapper';
import { MovieModel } from '../../domain/models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class GetMovieByIdService {
  private api = inject(MovieApiService);

  execute(id: string): Observable<MovieModel> {
    return this.api.getMovieById(id).pipe(
      map(dto => MovieMapper.fromDto(dto))
    );
  }
}
