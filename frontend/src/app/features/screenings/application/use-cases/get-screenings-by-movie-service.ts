import { Injectable } from '@angular/core';
import { ScreeningApiService } from '../../infrastructure/adapters/screening-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetScreeningsByMovieService {

  constructor(private api: ScreeningApiService) {}

  execute(movieId: string) {
    return this.api.getScreeningsByMovie(movieId);
  }
}
