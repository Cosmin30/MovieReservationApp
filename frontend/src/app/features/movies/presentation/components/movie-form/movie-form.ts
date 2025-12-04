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
    this.movieForm = this.fb.group({
      title: [this.movie?.title || '', [Validators.required, Validators.minLength(2)]],
      description: [this.movie?.description || '', [Validators.required, Validators.minLength(10)]],
      genre: [this.movie?.genre || '', [Validators.required]],
      duration: [this.movie?.duration || '', [Validators.required, Validators.min(1)]],
      releaseDate: [this.movie?.releaseDate || '', [Validators.required]]
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

