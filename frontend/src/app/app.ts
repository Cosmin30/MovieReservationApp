import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovieService, Movie } from './core/service/movie.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  movies: Movie[] = [];
  isLoading = true;
  errorMessage = '';

  // Lista cu imagini fixe
  movieImages = [
    'https://m.media-amazon.com/images/I/51CP55uF0lL._AC_UF1000,1000_QL80_.jpg',
    'https://berkleyspectator.com/wp-content/uploads/2022/01/vgPj2F128qtShMaT9DNa8ODtWUFhqqrFPEUWfTRo-e1642785179405-683x900.jpeg',
    'https://user-assets.unbounce.com/6ba6383f-f04f-4308-a404-a233e12be903/693c4ab8-05cd-4dd1-8933-982c73e76ad0/movie-amcn-ftp145857-night-of-the-reaper-img-poster-2x3.original.jpg' 
  ];

  constructor(private movieService: MovieService) {}

  ngOnInit(): void {
    this.movieService.getMovies().subscribe({
      next: (data) => {
        // primele 3 filme
        this.movies = data.slice(0, 3);
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'A apărut o eroare la încărcarea filmelor.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  getMovieImage(index: number): string {
    // returnează imaginea corespunzătoare
    return this.movieImages[index] || this.movieImages[0];
  }
}
