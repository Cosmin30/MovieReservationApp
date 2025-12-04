import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ScreeningCardComponent } from '../screening-card/screening-card';
import { ScreeningFormComponent } from '../screening-form/screening-form';
import { ScreeningApiService } from '../../../infrastructure/adapters/screening-api-service';
import { AuthService } from '../../../../../core/auth/auth-service';

@Component({
  selector: 'app-screening-list',
  standalone: true,
  imports: [CommonModule, ScreeningCardComponent, ScreeningFormComponent],
  templateUrl: './screening-list.html',
  styleUrls: ['./screening-list.css']
})
export class ScreeningListComponent implements OnInit, OnDestroy {
  screenings: any[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
  showScreeningForm = false;
  editingScreening: any = null;
  private destroy$ = new Subject<void>();
  authService = inject(AuthService);

  constructor(private api: ScreeningApiService) {}

  ngOnInit(): void {
    this.loading = true;
    this.api.getAllScreenings()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: any) => {
          // map snake_case -> camelCase dacă e nevoie
          this.screenings = data.map((s: any) => ({
            ...s,
            startTime: s.startTime || s.start_time,
            roomNumber: s.roomNumber || s.room_number
          }));
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Eroare la încărcarea proiecțiilor', err);
          this.error = 'Nu am putut încărca proiecțiile';
          this.loading = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onDeleteScreening(screeningId: string) {
    if (confirm('Ești sigur că vrei să ștergi această proiecție?')) {
      this.api.deleteScreening(screeningId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.success = 'Proiecția a fost ștearsă cu succes!';
            // Remove from list
            this.screenings = this.screenings.filter(s => s.id !== screeningId);
            setTimeout(() => this.success = null, 3000);
          },
          error: (err) => {
            this.error = 'Nu am putut șterge proiecția. Te rugăm să încerci din nou.';
            setTimeout(() => this.error = null, 3000);
          }
        });
    }
  }

  onAddScreening() {
    this.editingScreening = null;
    this.showScreeningForm = true;
  }

  onEditScreening(screeningId: string) {
    const screening = this.screenings.find(s => s.id === screeningId);
    if (screening) {
      this.editingScreening = screening;
      this.showScreeningForm = true;
    }
  }

  onScreeningFormSubmit(screeningData: any) {
    const operation = this.editingScreening ? 
      this.api.updateScreening(this.editingScreening.id, screeningData) :
      this.api.createScreening(screeningData);

    operation
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.success = this.editingScreening ? 
            'Proiecția a fost actualizată cu succes!' : 
            'Proiecția a fost adăugată cu succes!';
          this.showScreeningForm = false;
          this.editingScreening = null;
          // Reload screenings
          this.loading = true;
          this.api.getAllScreenings()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: (data: any) => {
                this.screenings = data.map((s: any) => ({
                  ...s,
                  startTime: s.startTime || s.start_time,
                  roomNumber: s.roomNumber || s.room_number
                }));
                this.loading = false;
              },
              error: () => {
                this.loading = false;
              }
            });
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.error = this.editingScreening ? 
            'Nu am putut actualiza proiecția. Te rugăm să încerci din nou.' :
            'Nu am putut adăuga proiecția. Te rugăm să încerci din nou.';
          setTimeout(() => this.error = null, 3000);
        }
      });
  }

  onScreeningFormCancel() {
    this.showScreeningForm = false;
    this.editingScreening = null;
  }
}

