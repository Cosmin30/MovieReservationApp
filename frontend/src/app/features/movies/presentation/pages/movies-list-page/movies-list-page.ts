import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';

import { MoviesState } from '../../../application/state/movies-state.state';
import { GetAllMoviesService } from '../../../application/use-cases/get-all-movies-service';
import { FilterMoviesService } from '../../../application/use-cases/filter-movies-service';
import { MovieApiService } from '../../../infrastructure/adapters/movie-api-service';
import { AuthService } from '../../../../../core/auth/auth-service';
import { MovieModel } from '../../../domain/models/movie.model';
import { MovieDTO } from '../../../infrastructure/dtos/movie.dto';
import { LoggerService } from '../../../../../core/services/logger.service';
import { NotificationService } from '../../../../../shared/services/notification.service';

import { MovieGridComponent } from '../../components/movie-grid/movie-grid';
import { MovieSearchComponent } from '../../components/movie-search/movie-search';
import { MovieFiltersComponent } from '../../components/movie-filters/movie-filters';
import { MovieFormComponent } from '../../components/movie-form/movie-form';
import { CacheService } from '../../../../../core/services/cache-service';

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

  movies: MovieModel[] = []; // Filtered movies (for display)
  allMovies: MovieModel[] = []; // All movies from database (for genre extraction)
  error: string | null = null;
  success: string | null = null;
  showMovieForm = false;
  editingMovie: MovieModel | null = null;
  private destroy$ = new Subject<void>();
  authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private cacheService = inject(CacheService);
  private logger = inject(LoggerService);
  private notificationService = inject(NotificationService);

  constructor(
    private state: MoviesState,
    private getAllMovies: GetAllMoviesService,
    private filterService: FilterMoviesService,
    private movieApi: MovieApiService
  ) {}

  ngOnInit() {
    // Clear cache first to ensure fresh data
    this.movieApi.clearCache();
    this.state.clearCache();
    
    this.state.movies$
      .pipe(takeUntil(this.destroy$))
      .subscribe(movies => {
        // Movies are already normalized by mapper, no need for manual normalization
        this.movies = movies;
        
        // Set allMovies from filterService originalMovies (all movies from database)
        // This ensures genres are always extracted from all movies, not filtered ones
        const originalMovies = this.filterService.getOriginalMovies();
        if (originalMovies.length > 0) {
          this.allMovies = originalMovies;
        } else {
          // First load - set allMovies to current movies and store in filterService
          this.allMovies = [...movies];
          this.filterService.setOriginalMovies(movies);
        }
        this.cdr.detectChanges();
      });
    
    // Load data on initial component creation
    this.getAllMovies.execute()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // Data loaded successfully
        },
        error: (err) => {
          this.logger.error('Error loading movies:', err);
          this.error = 'Nu am putut încărca filmele. Te rugăm să reîncerci.';
          this.notificationService.error('Nu am putut încărca filmele. Te rugăm să reîncerci.');
        }
      });

    // Reload data whenever navigating to this route (but skip the first one since ngOnInit already loaded)
    let isFirstNavigation = true;
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        filter((event: NavigationEnd) => event.url === '/movies' || (event.url.includes('/movies') && !event.url.includes('/movies/'))),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (isFirstNavigation) {
          isFirstNavigation = false;
          return;
        }
        // Clear cache and reload when navigating to this route
        this.movieApi.clearCache();
        this.state.clearCache();
        this.getAllMovies.execute()
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Data reloaded successfully
            },
            error: (err) => {
              this.logger.error('Error reloading movies:', err);
              this.error = 'Nu am putut reîncărca filmele. Te rugăm să reîncerci.';
              this.notificationService.error('Nu am putut reîncărca filmele. Te rugăm să reîncerci.');
            }
          });
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  search(query: string): void {
    this.filterService.execute(query);
  }

  filter(query: string): void {
    this.filterService.execute(query);
  }

  filterGenre(genres: string[]): void {
    this.filterService.filterByGenres(genres);
  }

  onDeleteMovie(movieId: string): void {
    if (confirm('Ești sigur că vrei să ștergi acest film?')) {
      this.movieApi.deleteMovie(movieId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            // Use setTimeout to defer change detection
            setTimeout(() => {
              this.success = 'Filmul a fost șters cu succes!';
              this.cdr.detectChanges();
              
              // Clear after 3 seconds
              setTimeout(() => {
                this.success = null;
                this.cdr.detectChanges();
              }, 3000);
            }, 0);
            
            // Clear all related caches (movies and screenings)
            this.movieApi.clearCache();
            this.cacheService.clear('all_screenings'); // Clear screenings cache too
            this.state.clearCache();
            
            // Remove movie from local array immediately for better UX
            this.movies = this.movies.filter(m => m.id !== movieId);
            this.filterService.setOriginalMovies(this.movies);
            
            // Then reload movies to ensure consistency
            this.getAllMovies.execute()
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: () => {
                  // Force change detection after reload
                  this.cdr.detectChanges();
                },
                error: (err) => {
                  this.logger.error('Error reloading movies after delete:', err);
                }
              });
            
            this.notificationService.success('Filmul a fost șters cu succes!');
          },
          error: (err) => {
            this.logger.error('Error deleting movie:', err);
            this.error = 'Nu am putut șterge filmul. Te rugăm să încerci din nou.';
            this.notificationService.error('Nu am putut șterge filmul. Te rugăm să încerci din nou.');
            setTimeout(() => this.error = null, 3000);
          }
        });
    }
  }

  onAddMovie(): void {
    this.editingMovie = null;
    this.showMovieForm = true;
  }

  onEditMovie(movieId: string): void {
    const movie = this.movies.find(m => m.id === movieId);
    if (movie) {
      this.editingMovie = movie;
      this.showMovieForm = true;
    }
  }

  onMovieFormSubmit(movieData: MovieDTO): void {
    const operation = this.editingMovie ? 
      this.movieApi.updateMovie(this.editingMovie.id, movieData) :
      this.movieApi.createMovie(movieData);

    operation
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const message = this.editingMovie ? 
            'Filmul a fost actualizat cu succes!' : 
            'Filmul a fost adăugat cu succes!';
          
          // Use setTimeout to defer change detection
          setTimeout(() => {
            this.success = message;
            this.cdr.detectChanges();
            
            // Clear after 3 seconds
            setTimeout(() => {
              this.success = null;
              this.cdr.detectChanges();
            }, 3000);
          }, 0);
          
          this.notificationService.success(message);
          this.showMovieForm = false;
          this.editingMovie = null;
          // Clear cache and reload movies
          this.state.clearCache();
          this.getAllMovies.execute()
            .pipe(takeUntil(this.destroy$))
            .subscribe();
        },
        error: (err) => {
          this.logger.error('Error saving movie:', err);
          const errorMessage = this.editingMovie ? 
            'Nu am putut actualiza filmul. Te rugăm să încerci din nou.' :
            'Nu am putut adăuga filmul. Te rugăm să încerci din nou.';
          this.error = errorMessage;
          this.notificationService.error(errorMessage);
          setTimeout(() => this.error = null, 3000);
        }
      });
  }

  onMovieFormCancel(): void {
    this.showMovieForm = false;
    this.editingMovie = null;
  }
}
