import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MovieModel } from '../../../domain/models/movie.model';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-card.html',
  styleUrls: ['./movie-card.css']
})
export class MovieCardComponent {
  @Input() movie!: MovieModel;

  get imageUrl(): string {
    return (this.movie as any).imageUrl || 'assets/default-movie.jpg';
  }

  get genre(): string {
    return (this.movie as any).genre || 'Gen necunoscut';
  }
}
