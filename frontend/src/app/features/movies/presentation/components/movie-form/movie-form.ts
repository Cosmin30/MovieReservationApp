import { Component, Input, Output, EventEmitter, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MovieDTO } from '../../../infrastructure/dtos/movie.dto';

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

  ngOnInit() {
    // Format releaseDate for date input (YYYY-MM-DD)
    // Handle both releaseDate (camelCase) and release_date (snake_case from backend)
    let releaseDateValue = '';
    const movieData = this.movie as any; // Cast to any to access release_date if present
    
    // Debug: log what we receive
    console.log('🔍 [MOVIE FORM] Movie data:', movieData);
    console.log('🔍 [MOVIE FORM] releaseDate:', movieData?.releaseDate);
    console.log('🔍 [MOVIE FORM] release_date:', movieData?.release_date);
    
    // Prioritize release_date (from backend), fallback to releaseDate
    const releaseDate = movieData?.release_date || movieData?.releaseDate;
    console.log('🔍 [MOVIE FORM] Final releaseDate value:', releaseDate);
    console.log('🔍 [MOVIE FORM] releaseDate type:', typeof releaseDate);
    
    if (releaseDate) {
      // If it's already in YYYY-MM-DD format, use it directly
      if (typeof releaseDate === 'string' && releaseDate.match(/^\d{4}-\d{2}-\d{2}$/)) {
        releaseDateValue = releaseDate;
        console.log('✅ [MOVIE FORM] Using direct YYYY-MM-DD format:', releaseDateValue);
      } else {
        // Try parsing as Date object or ISO string
        const date = new Date(releaseDate);
        if (!isNaN(date.getTime())) {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          releaseDateValue = `${year}-${month}-${day}`;
          console.log('✅ [MOVIE FORM] Parsed date:', releaseDateValue);
        } else {
          // Try removing time part if present
          const dateStr = String(releaseDate).split('T')[0];
          if (dateStr.match(/^\d{4}-\d{2}-\d{2}$/)) {
            releaseDateValue = dateStr;
            console.log('✅ [MOVIE FORM] Extracted date from string:', releaseDateValue);
          } else {
            console.warn('⚠️ [MOVIE FORM] Could not parse releaseDate:', releaseDate);
          }
        }
      }
    } else {
      console.warn('⚠️ [MOVIE FORM] No releaseDate found in movie data');
      console.warn('⚠️ [MOVIE FORM] Movie object keys:', movieData ? Object.keys(movieData) : 'null');
    }
    
    console.log('🔍 [MOVIE FORM] Final releaseDateValue for form:', releaseDateValue);

    // Normalize genre - ensure it matches exactly with form options
    const genreValue = this.movie?.genre ? this.movie.genre.trim() : '';
    console.log('🔍 [MOVIE FORM] Genre value:', genreValue);
    
    this.movieForm = this.fb.group({
      title: [this.movie?.title || '', [Validators.required, Validators.minLength(2)]],
      description: [this.movie?.description || '', [Validators.required, Validators.minLength(10)]],
      genre: [genreValue, [Validators.required]],
      duration: [this.movie?.duration || '', [Validators.required, Validators.min(1)]],
      releaseDate: [releaseDateValue, [Validators.required]]
    });
    
    // Log form values after initialization
    console.log('🔍 [MOVIE FORM] Form genre value:', this.movieForm.get('genre')?.value);
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

