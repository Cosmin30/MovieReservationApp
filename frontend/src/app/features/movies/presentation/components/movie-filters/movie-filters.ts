import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-movie-filters',
  templateUrl: './movie-filters.html',
  styleUrls: ['./movie-filters.css']
})
export class MovieFiltersComponent {

  genres = ['Action', 'Comedy', 'Drama', 'Horror', 'Sci-Fi'];

  @Output() onFilter = new EventEmitter<string>();
  @Output() onGenreFilter = new EventEmitter<string>();

  filter(value: string) {
    this.onFilter.emit(value);
  }

  filterGenre(value: string) {
    this.onGenreFilter.emit(value);
  }
}
