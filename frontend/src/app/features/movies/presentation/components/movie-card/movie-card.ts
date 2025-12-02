import { Component, Input } from '@angular/core';
import { MovieModel } from '../../../domain/models/movie.model';

@Component({
  selector: 'app-movie-card',
  templateUrl: './movie-card.html',
  styleUrls: ['./movie-card.css']
})
export class MovieCardComponent {
  @Input() movie!: MovieModel;
}
