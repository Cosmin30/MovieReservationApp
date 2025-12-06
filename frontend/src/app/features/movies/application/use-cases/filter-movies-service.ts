import { Injectable, inject } from '@angular/core';
import { MoviesState } from '../state/movies-state.state';
import { MovieModel } from '../../domain/models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class FilterMoviesService {
  private state = inject(MoviesState);
  private originalMovies: MovieModel[] = [];
  private isFiltering = false;

  setOriginalMovies(movies: MovieModel[]): void {
    if (!this.isFiltering) {
      this.originalMovies = [...movies];
    }
  }

  getIsFiltering(): boolean {
    return this.isFiltering;
  }

  getOriginalMovies(): MovieModel[] {
    return [...this.originalMovies];
  }

  execute(filter: string) {
    if (!filter || filter.trim() === '') {
      this.isFiltering = false;
      this.state.setMovies(this.originalMovies);
      return;
    }

    this.isFiltering = true;
    const filtered = this.originalMovies.filter(m => 
      m.title.toLowerCase().includes(filter.toLowerCase()) ||
      m.genre.toLowerCase().includes(filter.toLowerCase())
    );
    this.state.setMovies(filtered);
  }

  filterByGenre(genre: string) {
    if (!genre || genre.trim() === '') {
      this.isFiltering = false;
      this.state.setMovies(this.originalMovies);
      return;
    }

    this.isFiltering = true;
    const filtered = this.originalMovies.filter(m => 
      m.genre === genre
    );
    this.state.setMovies(filtered);
  }

  filterByGenres(genres: string[]) {
    if (!genres || genres.length === 0) {
      this.isFiltering = false;
      this.state.setMovies(this.originalMovies);
      return;
    }

    this.isFiltering = true;
    // Filter movies that match ANY of the selected genres
    const filtered = this.originalMovies.filter(m => 
      genres.includes(m.genre)
    );
    this.state.setMovies(filtered);
  }
}
