import { Injectable } from '@angular/core';
import { MoviesState } from '../state/movies-state.state';

@Injectable({
  providedIn: 'root'
})
export class FilterMoviesService {

  constructor(private state: MoviesState) {}

  execute(filter: string) {
    this.state.movies$.subscribe(movies => {
      const filtered = movies.filter(m => 
        m.title.toLowerCase().includes(filter.toLowerCase()) ||
        m.genre.toLowerCase().includes(filter.toLowerCase())
      );
      this.state.setMovies(filtered);
    });
  }
}
