import { Component, EventEmitter, Output, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-movie-filters',
  templateUrl: './movie-filters.html',
  styleUrls: ['./movie-filters.css'],
  standalone:true, 
  imports: [
    CommonModule   
  ]
})
export class MovieFiltersComponent implements OnInit, OnChanges {

  @Input() movies: any[] = []; // Filtered movies (for display)
  @Input() allMovies: any[] = []; // All movies from database (for genre extraction)
  genres: string[] = [];
  selectedGenres: Set<string> = new Set<string>();

  @Output() onFilter = new EventEmitter<string>();
  @Output() onGenreFilter = new EventEmitter<string[]>();

  ngOnInit() {
    this.updateGenres();
  }

  ngOnChanges(changes: SimpleChanges) {
    // Only update genres when allMovies changes, not when filtered movies change
    if (changes['allMovies']) {
      this.updateGenres();
    }
  }

  private updateGenres() {
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
    
    console.log('🎬 [MOVIE FILTERS] Extracted genres from database:', this.genres);
    console.log('🎬 [MOVIE FILTERS] Total movies:', moviesToUse?.length || 0);
  }

  filter(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.onFilter.emit(value);
  }

  toggleGenre(genre: string, event: Event) {
    const isChecked = (event.target as HTMLInputElement).checked;
    
    if (isChecked) {
      this.selectedGenres.add(genre);
    } else {
      this.selectedGenres.delete(genre);
    }
    
    // Emit array of selected genres
    const selectedGenresArray = Array.from(this.selectedGenres);
    this.onGenreFilter.emit(selectedGenresArray);
    
    console.log('🎬 [MOVIE FILTERS] Selected genres:', selectedGenresArray);
  }

  clearGenreFilters() {
    this.selectedGenres.clear();
    this.onGenreFilter.emit([]);
    console.log('🎬 [MOVIE FILTERS] Cleared genre filters');
  }

}
