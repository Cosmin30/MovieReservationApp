import { Component, Input } from '@angular/core';
import { MovieModel } from '../../../domain/models/movie.model';

@Component({
  selector: 'app-movie-grid',
  templateUrl: './movie-grid.html',
  styleUrls: ['./movie-grid.css']
})
export class MovieGridComponent {
  @Input() movies: MovieModel[] = [];
}
