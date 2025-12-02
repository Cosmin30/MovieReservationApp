import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MovieModel } from '../../domain/models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class MoviesState {

  private moviesSubject = new BehaviorSubject<MovieModel[]>([]);
  movies$ = this.moviesSubject.asObservable();

  setMovies(movies: MovieModel[]) {
    this.moviesSubject.next(movies);
  }
}
