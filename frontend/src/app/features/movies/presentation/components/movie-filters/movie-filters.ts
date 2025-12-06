import { Component, EventEmitter, Output, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovieModel } from '../../../domain/models/movie.model';

@Component({
  selector: 'app-movie-filters',
  templateUrl: './movie-filters.html',
  styleUrls: ['./movie-filters.css'],
  standalone: true, 
  imports: [
    CommonModule   
  ]
})
export class MovieFiltersComponent implements OnInit, OnChanges {
  @Input() movies: MovieModel[] = []; // Filtered movies (for display)
  @Input() allMovies: MovieModel[] = []; // All movies from database (for genre extraction)
  genres: string[] = [];
  selectedGenres: Set<string> = new Set<string>();

  @Output() onFilter = new EventEmitter<string>();
  @Output() onGenreFilter = new EventEmitter<string[]>();

  ngOnInit(): void {
    this.updateGenres();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Only update genres when allMovies changes, not when filtered movies change
    if (changes['allMovies']) {
      this.updateGenres();
    }
  }

  private updateGenres(): void {
    // Extract unique genres from ALL movies in database (not filtered ones)
    // This ensures all genres remain available even after filtering
    const uniqueGenres = new Set<string>();
    
    const moviesToUse = this.allMovies && this.allMovies.length > 0 
      ? this.allMovies 
      : this.movies; // Fallback to filtered movies if allMovies not provided
    
    if (moviesToUse && moviesToUse.length > 0) {
      moviesToUse.forEach(movie => {
        if (movie.genre && movie.genre.trim()) {
          // Normalize genre (trim whitespace)
          uniqueGenres.add(movie.genre.trim());
        }
      });
    }
    
    // Sort genres alphabetically
    this.genres = Array.from(uniqueGenres).sort();
  }

  filter(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.onFilter.emit(value);
  }

  toggleGenre(genre: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    
    if (isChecked) {
      this.selectedGenres.add(genre);
    } else {
      this.selectedGenres.delete(genre);
    }
    
    // Emit array of selected genres
    const selectedGenresArray = Array.from(this.selectedGenres);
    this.onGenreFilter.emit(selectedGenresArray);
  }

  clearGenreFilters(): void {
    this.selectedGenres.clear();
    this.onGenreFilter.emit([]);
  }
}
