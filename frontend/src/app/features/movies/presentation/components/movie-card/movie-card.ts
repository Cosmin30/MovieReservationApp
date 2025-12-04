import { Component, Input, OnInit, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MovieModel } from '../../../domain/models/movie.model';
import { MovieImageService } from '../../../infrastructure/adapters/movie-image-service';
import { AuthService } from '../../../../../core/auth/auth-service';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-card.html',
  styleUrls: ['./movie-card.css']
})
export class MovieCardComponent implements OnInit {
  @Input() movie!: MovieModel;
  @Output() deleteMovie = new EventEmitter<string>();
  @Output() editMovie = new EventEmitter<string>();
  imageUrl: string = 'assets/default-movie.jpg';
  isLoadingImage = true;
  authService = inject(AuthService);

  constructor(private movieImageService: MovieImageService) {}

  ngOnInit() {
    if ((this.movie as any).imageUrl) {
      this.imageUrl = (this.movie as any).imageUrl;
      this.isLoadingImage = false;
    } else {
      // Fetch image based on movie title
      this.movieImageService.getMovieImage(this.movie.title).subscribe({
        next: (url: string) => {
          this.imageUrl = url;
          this.isLoadingImage = false;
        },
        error: () => {
          this.imageUrl = 'assets/default-movie.jpg';
          this.isLoadingImage = false;
        }
      });
    }
  }

  get genre(): string {
    return (this.movie as any).genre || 'Gen necunoscut';
  }

  onImageError() {
    this.imageUrl = 'assets/default-movie.jpg';
  }
}
