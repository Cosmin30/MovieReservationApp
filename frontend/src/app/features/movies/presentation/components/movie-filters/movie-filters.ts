import { Component, EventEmitter, Output } from '@angular/core';
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
export class MovieFiltersComponent {

  genres = ['Action', 'Comedy', 'Drama', 'Horror', 'Sci-Fi'];

  @Output() onFilter = new EventEmitter<string>();
  @Output() onGenreFilter = new EventEmitter<string>();

filter(event: Event) {
  const value = (event.target as HTMLInputElement).value;
  this.onFilter.emit(value);
}

filterGenre(event: Event) {
  const value = (event.target as HTMLSelectElement).value;
  this.onGenreFilter.emit(value);
}

}
