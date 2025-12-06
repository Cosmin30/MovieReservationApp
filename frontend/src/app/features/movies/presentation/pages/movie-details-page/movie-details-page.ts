import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil, map } from 'rxjs/operators';
import { GetMovieByIdService } from '../../../application/use-cases/get-movie-by-id-service';
import { MovieApiService } from '../../../infrastructure/adapters/movie-api-service';
import { ScreeningApiService } from '../../../../screenings/infrastructure/adapters/screening-api-service';
import { MovieModel } from '../../../domain/models/movie.model';
import { ScreeningModel } from '../../../../screenings/domain/models/screening.model';
import { ScreeningMapper } from '../../../../screenings/infrastructure/adapters/screening-mapper.mapper';
import { LoggerService } from '../../../../../core/services/logger.service';
import { NotificationService } from '../../../../../shared/services/notification.service';

@Component({
  selector: 'app-movie-details-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-details-page.html',
  styleUrls: ['./movie-details-page.css']
})
export class MovieDetailsPage implements OnInit, OnDestroy {
  movie: MovieModel | null = null;
  screenings: ScreeningModel[] = [];
  isLoading = true;
  isLoadingScreenings = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private logger = inject(LoggerService);
  private notificationService = inject(NotificationService);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private getMovie: GetMovieByIdService,
    private movieApi: MovieApiService,
    private screeningApi: ScreeningApiService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    
    if (!id) {
      this.error = 'ID film lipsă';
      this.isLoading = false;
      this.cdr.detectChanges();
      return;
    }

    // Clear cache for this specific movie
    this.movieApi.clearCache();
    
    this.getMovie.execute(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (movie: MovieModel) => {
          this.movie = movie;
          this.isLoading = false;
          this.cdr.detectChanges();
          
          // Load screenings for this movie (after movie is loaded)
          this.loadScreenings(id);
        },
        error: (err) => {
          this.logger.error('Error loading movie:', err);
          this.error = 'Nu am putut încărca detaliile filmului. Te rugăm să reîncerci.';
          this.notificationService.error('Nu am putut încărca detaliile filmului. Te rugăm să reîncerci.');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  loadScreenings(movieId: string) {
    if (!movieId) return;
    
    this.isLoadingScreenings = true;
    // Clear cache for this specific movie's screenings
    this.screeningApi.clearScreeningsByMovieCache(movieId);
    
    this.screeningApi.getScreeningsByMovie(movieId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (screenings: any[]) => {
          // Filter to ensure only screenings for this movie are shown
          // Also normalize the data
          this.screenings = (screenings || [])
            .filter((s: any) => {
              // Filter by movie ID to ensure we only show screenings for this movie
              // Check multiple possible field names
              const screeningMovieId = s.movie?.id || s.movieId || s.movie_id;
              // Also check if movie object has id property
              const movieIdFromObject = s.movie ? (s.movie.id || s.movie.id) : null;
              const finalMovieId = screeningMovieId || movieIdFromObject;
              
              // Convert both to strings for comparison
              return String(finalMovieId) === String(movieId);
            })
            .map((s: any) => ({
              ...s,
              startTime: s.startTime || s.start_time,
              roomNumber: s.roomNumber || s.room_number
            }));
          
          this.isLoadingScreenings = false;
          // Force change detection
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.logger.error('Error loading screenings:', err);
          this.screenings = [];
          this.isLoadingScreenings = false;
          this.cdr.detectChanges();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  goBack() {
    this.router.navigate(['/movies']);
  }

  formatDuration(minutes: number): string {
    if (!minutes) return 'N/A';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
      return `${hours}h ${mins}m`;
    }
    return `${mins}m`;
  }

  formatDate(date: string | Date | null | undefined): string {
    if (!date) {
      return 'N/A';
    }
    
    try {
      // Handle different date formats
      let dateObj: Date;
      
      // If it's already a Date object
      if (date instanceof Date) {
        dateObj = date;
      } 
      // If it's a string in format "yyyy-MM-dd" or ISO
      else if (typeof date === 'string') {
        // Try parsing as ISO date first
        dateObj = new Date(date);
        
        // If invalid, try parsing as "yyyy-MM-dd"
        if (isNaN(dateObj.getTime())) {
          const parts = date.split('-');
          if (parts.length === 3) {
            dateObj = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
          } else {
            return date; // Return as-is if can't parse
          }
        }
      } else {
        return String(date);
      }
      
      // Check if date is valid
      if (isNaN(dateObj.getTime())) {
        return String(date);
      }
      
      return dateObj.toLocaleDateString('ro-RO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      this.logger.error('formatDate error:', error, date);
      return String(date);
    }
  }
}
