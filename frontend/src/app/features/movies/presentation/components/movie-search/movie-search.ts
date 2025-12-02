import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-movie-search',
  templateUrl: './movie-search.html',
  styleUrls: ['./movie-search.css']
})
export class MovieSearchComponent {
  @Output() searchEvent = new EventEmitter<string>();

  onSearch(value: string) {
    this.searchEvent.emit(value);
  }
}
