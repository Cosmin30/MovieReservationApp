import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ScreeningCardComponent } from '../screening-card/screening-card';
import { ScreeningApiService } from '../../../infrastructure/adapters/screening-api-service';
import { AuthService } from '../../../../../core/auth/auth-service';

@Component({
  selector: 'app-screening-list',
  standalone: true,
  imports: [CommonModule, ScreeningCardComponent],
  templateUrl: './screening-list.html',
  styleUrls: ['./screening-list.css']
})
export class ScreeningListComponent implements OnInit, OnDestroy {
  screenings: any[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
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
    // TODO: Navigate to add screening page or open modal
    alert('Funcționalitatea de adăugare proiecție va fi implementată!');
  }

  onEditScreening(screeningId: string) {
    // TODO: Navigate to edit screening page or open modal
    alert(`Funcționalitatea de editare proiecție (ID: ${screeningId}) va fi implementată!`);
  }
}

