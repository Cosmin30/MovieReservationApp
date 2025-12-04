import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { MoviesState } from '../../../application/state/movies-state.state';
import { GetAllMoviesService } from '../../../application/use-cases/get-all-movies-service';
import { FilterMoviesService } from '../../../application/use-cases/filter-movies-service';
import { MovieApiService } from '../../../infrastructure/adapters/movie-api-service';
import { AuthService } from '../../../../../core/auth/auth-service';

import { MovieGridComponent } from '../../components/movie-grid/movie-grid';
import { MovieSearchComponent } from '../../components/movie-search/movie-search';
import { MovieFiltersComponent } from '../../components/movie-filters/movie-filters';
import { MovieFormComponent } from '../../components/movie-form/movie-form';

@Component({
  selector: 'app-movies-list-page',
  standalone: true,
  imports: [
    CommonModule,
    MovieGridComponent,
    MovieSearchComponent,
    MovieFiltersComponent,
    MovieFormComponent
  ],
  templateUrl: './movies-list-page.html',
  styleUrls: ['./movies-list-page.css']
})
export class MoviesListPage implements OnInit, OnDestroy {

  movies: any[] = [];
  error: string | null = null;
  success: string | null = null;
  showMovieForm = false;
  editingMovie: any = null;
  private destroy$ = new Subject<void>();
  authService = inject(AuthService);
  private router = inject(Router);

  constructor(
    private state: MoviesState,
    private getAllMovies: GetAllMoviesService,
    private filterService: FilterMoviesService,
    private movieApi: MovieApiService
  ) {}

  ngOnInit() {
    this.state.movies$
      .pipe(takeUntil(this.destroy$))
      .subscribe(m => {
        this.movies = m;
        this.filterService.setOriginalMovies(m);
      });
    
    this.getAllMovies.execute()
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  search(query: string) {
    this.filterService.execute(query);
  }

  filter(query: string) {
    this.filterService.execute(query);
  }

  filterGenre(genre: string) {
    this.filterService.execute(genre);
  }

  onDeleteMovie(movieId: string) {
    if (confirm('Ești sigur că vrei să ștergi acest film?')) {
      this.movieApi.deleteMovie(movieId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.success = 'Filmul a fost șters cu succes!';
            // Clear cache and reload movies
            this.state.clearCache();
            this.getAllMovies.execute()
              .pipe(takeUntil(this.destroy$))
              .subscribe();
            setTimeout(() => this.success = null, 3000);
          },
          error: (err) => {
            this.error = 'Nu am putut șterge filmul. Te rugăm să încerci din nou.';
            setTimeout(() => this.error = null, 3000);
          }
        });
    }
  }

  onAddMovie() {
    this.editingMovie = null;
    this.showMovieForm = true;
  }

  onEditMovie(movieId: string) {
    const movie = this.movies.find(m => m.id === movieId);
    if (movie) {
      this.editingMovie = movie;
      this.showMovieForm = true;
    }
  }

  onMovieFormSubmit(movieData: any) {
    const operation = this.editingMovie ? 
      this.movieApi.updateMovie(this.editingMovie.id, movieData) :
      this.movieApi.createMovie(movieData);

    operation
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.success = this.editingMovie ? 
            'Filmul a fost actualizat cu succes!' : 
            'Filmul a fost adăugat cu succes!';
          this.showMovieForm = false;
          this.editingMovie = null;
          // Clear cache and reload movies
          this.state.clearCache();
          this.getAllMovies.execute()
            .pipe(takeUntil(this.destroy$))
            .subscribe();
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.error = this.editingMovie ? 
            'Nu am putut actualiza filmul. Te rugăm să încerci din nou.' :
            'Nu am putut adăuga filmul. Te rugăm să încerci din nou.';
          setTimeout(() => this.error = null, 3000);
        }
      });
  }

  onMovieFormCancel() {
    this.showMovieForm = false;
    this.editingMovie = null;
  }
}
