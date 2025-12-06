import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MovieDTO } from '../../../infrastructure/dtos/movie.dto';
import { LoggerService } from '../../../../../core/services/logger.service';

@Component({
  selector: 'app-movie-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './movie-form.html',
  styleUrls: ['./movie-form.css']
})
export class MovieFormComponent implements OnInit {
  @Input() movie: MovieDTO | null = null; // For edit mode
  @Input() isEditMode: boolean = false;
  @Output() submitForm = new EventEmitter<MovieDTO>();
  @Output() cancel = new EventEmitter<void>();

  movieForm!: FormGroup;
  private fb = inject(FormBuilder);
  private logger = inject(LoggerService);

  ngOnInit() {
    // Format releaseDate for date input (YYYY-MM-DD)
    // Handle both releaseDate (camelCase) and release_date (snake_case from backend)
    let releaseDateValue = '';
    // Access release_date from DTO if present (DTO may have both formats)
    const releaseDate = (this.movie as any)?.release_date || this.movie?.releaseDate;
    
    if (releaseDate) {
      // If it's already in YYYY-MM-DD format, use it directly
      if (typeof releaseDate === 'string' && releaseDate.match(/^\d{4}-\d{2}-\d{2}$/)) {
        releaseDateValue = releaseDate;
      } else {
        // Try parsing as Date object or ISO string
        const date = new Date(releaseDate);
        if (!isNaN(date.getTime())) {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          releaseDateValue = `${year}-${month}-${day}`;
        } else {
          // Try removing time part if present
          const dateStr = String(releaseDate).split('T')[0];
          if (dateStr.match(/^\d{4}-\d{2}-\d{2}$/)) {
            releaseDateValue = dateStr;
          } else {
            this.logger.warn('Could not parse releaseDate:', releaseDate);
          }
        }
      }
    } else {
      this.logger.warn('No releaseDate found in movie data');
    }

    // Normalize genre - ensure it matches exactly with form options
    const genreValue = this.movie?.genre ? this.movie.genre.trim() : '';
    
    this.movieForm = this.fb.group({
      title: [this.movie?.title || '', [Validators.required, Validators.minLength(2)]],
      description: [this.movie?.description || '', [Validators.required, Validators.minLength(10)]],
      genre: [genreValue, [Validators.required]],
      duration: [this.movie?.duration || '', [Validators.required, Validators.min(1)]],
      releaseDate: [releaseDateValue, [Validators.required]]
    });
  }

  onSubmit() {
    if (this.movieForm.valid) {
      const formValue = this.movieForm.value;
      const movieData: MovieDTO = {
        ...formValue,
        id: this.isEditMode ? this.movie?.id : undefined
      };
      this.submitForm.emit(movieData);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.movieForm.controls).forEach(key => {
        this.movieForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel() {
    this.cancel.emit();
  }

  get title() { return this.movieForm.get('title'); }
  get description() { return this.movieForm.get('description'); }
  get genre() { return this.movieForm.get('genre'); }
  get duration() { return this.movieForm.get('duration'); }
  get releaseDate() { return this.movieForm.get('releaseDate'); }
}

