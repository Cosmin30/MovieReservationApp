import { Component, Input, Output, EventEmitter, OnInit, AfterViewInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ScreeningDTO } from '../../../infrastructure/dtos/screening.dto';
import { MovieApiService } from '../../../../movies/infrastructure/adapters/movie-api-service';
import { HallApiService } from '../../../../halls/infrastructure/adapters/hall-api-service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-screening-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './screening-form.html',
  styleUrls: ['./screening-form.css']
})
export class ScreeningFormComponent implements OnInit, AfterViewInit {
  @Input() screening: ScreeningDTO | null = null;
  @Input() isEditMode: boolean = false;
  @Output() submitForm = new EventEmitter<ScreeningDTO>();
  @Output() cancel = new EventEmitter<void>();

  screeningForm: FormGroup;
  movies: any[] = [];
  halls: any[] = [];
  loading = false;
  private destroy$ = new Subject<void>();
  private fb = inject(FormBuilder);
  private movieApi = inject(MovieApiService);
  private hallApi = inject(HallApiService);
  private cdr = inject(ChangeDetectorRef);

  constructor() {
    // Initialize form immediately to avoid template errors
    this.screeningForm = this.fb.group({
      movieId: ['', [Validators.required]],
      hallId: ['', [Validators.required]],
      startTime: ['', [Validators.required]],
      roomNumber: ['', [Validators.required, Validators.min(1)]],
      capacity: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    // Load data immediately when component is initialized
    this.loadMoviesAndHalls();
    
    // Format startTime for datetime-local input
    let startTimeValue = '';
    if (this.screening?.startTime) {
      const date = new Date(this.screening.startTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      startTimeValue = `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    // Update form with initial values
    this.screeningForm.patchValue({
      movieId: this.screening?.movie?.id || '',
      hallId: this.screening?.hall?.id || '',
      startTime: startTimeValue,
      roomNumber: this.screening?.roomNumber || '',
      capacity: this.screening?.capacity || ''
    });

    // Listen to hall selection changes to auto-fill roomNumber and capacity
    this.screeningForm.get('hallId')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(hallId => {
        const selectedHall = this.halls.find(h => h.id === hallId);
        if (selectedHall) {
          // Auto-fill roomNumber and capacity
          this.screeningForm.patchValue({
            roomNumber: selectedHall.number || '',
            capacity: selectedHall.capacity || ''
          }, { emitEvent: false });
          
          // Make roomNumber and capacity readonly when hall is selected
          if (hallId) {
            this.screeningForm.get('roomNumber')?.disable();
            this.screeningForm.get('capacity')?.disable();
          }
        } else {
          // Enable editing when no hall is selected
          this.screeningForm.get('roomNumber')?.enable();
          this.screeningForm.get('capacity')?.enable();
        }
      });
    
    // Initially disable if hall is already selected
    if (this.screening?.hall?.id) {
      this.screeningForm.get('roomNumber')?.disable();
      this.screeningForm.get('capacity')?.disable();
    }
  }

  ngAfterViewInit() {
    // Force change detection after view is initialized to ensure data is displayed
    // This is especially important when component is created via *ngIf
    if (this.movies.length === 0 || this.halls.length === 0) {
      // If data hasn't loaded yet, wait a bit and check again
      setTimeout(() => {
        this.cdr.detectChanges();
      }, 100);
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadMoviesAndHalls() {
    this.loading = true;
    
    this.movieApi.getAllMovies()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (movies) => {
          this.movies = movies;
          this.loading = false;
          // Force change detection after data is loaded
          this.cdr.detectChanges();
        },
        error: () => {
          this.loading = false;
          this.cdr.detectChanges();
        }
      });

    this.hallApi.getAllHalls()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (halls) => {
          this.halls = halls;
          // Force change detection after data is loaded
          this.cdr.detectChanges();
        },
        error: () => {
          this.cdr.detectChanges();
        }
      });
  }

  onSubmit() {
    if (this.screeningForm.valid) {
      // Get form value including disabled fields
      const formValue = this.screeningForm.getRawValue();
      
      // Convert datetime-local to ISO string
      const startTimeDate = new Date(formValue.startTime);
      const startTimeISO = startTimeDate.toISOString();

      const screeningData: ScreeningDTO = {
        id: this.isEditMode ? this.screening?.id : undefined,
        movie: { id: formValue.movieId },
        hall: { id: formValue.hallId },
        startTime: startTimeISO,
        roomNumber: parseInt(formValue.roomNumber),
        capacity: parseInt(formValue.capacity)
      };
      
      this.submitForm.emit(screeningData);
    } else {
      Object.keys(this.screeningForm.controls).forEach(key => {
        this.screeningForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel() {
    this.cancel.emit();
  }

  get movieId() { return this.screeningForm.get('movieId'); }
  get hallId() { return this.screeningForm.get('hallId'); }
  get startTime() { return this.screeningForm.get('startTime'); }
  get roomNumber() { return this.screeningForm.get('roomNumber'); }
  get capacity() { return this.screeningForm.get('capacity'); }
}

