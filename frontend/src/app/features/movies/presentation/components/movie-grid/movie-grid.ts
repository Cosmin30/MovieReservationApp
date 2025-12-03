import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MovieModel } from '../../../domain/models/movie.model';
import { CommonModule } from '@angular/common';
import { MovieCardComponent } from '../movie-card/movie-card';
@Component({
  selector: 'app-movie-grid',
  standalone: true,
  imports: [CommonModule, MovieCardComponent],
  templateUrl: './movie-grid.html',
  styleUrls: ['./movie-grid.css']
})
export class MovieGridComponent {
  @Input() movies: MovieModel[] = [];
  @Output() deleteMovie = new EventEmitter<string>();
}