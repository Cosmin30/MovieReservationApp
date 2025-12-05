import { Injectable } from '@angular/core';
import { MoviesState } from '../state/movies-state.state';

@Injectable({
  providedIn: 'root'
})
export class FilterMoviesService {
  private originalMovies: any[] = [];
  private isFiltering = false;

  constructor(private state: MoviesState) {}

  setOriginalMovies(movies: any[]) {
    if (!this.isFiltering) {
      this.originalMovies = [...movies];
    }
  }

  getIsFiltering(): boolean {
    return this.isFiltering;
  }

  getOriginalMovies(): any[] {
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
