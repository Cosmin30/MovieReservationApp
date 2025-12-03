import { Component, Input, OnInit, OnDestroy, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { GetScreeningByIdService } from '../../../application/use-cases/get-screening-by-id-service';
import { AuthService } from '../../../../../core/auth/auth-service';

@Component({
  selector: 'app-screening-card',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './screening-card.html'
})
export class ScreeningCardComponent implements OnInit, OnDestroy {
  @Input() screening: any;
  @Output() deleteScreening = new EventEmitter<string>();
  isLoading = true;
  error: string | null = null;
  isDetailPage = false;
  authService = inject(AuthService);
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private getScreeningById: GetScreeningByIdService
  ) {}

  ngOnInit() {
    // Check if we're on the detail page by checking if we have a route parameter
    const screeningId = this.route.snapshot.paramMap.get('id');
    if (screeningId && !this.screening) {
      this.isDetailPage = true;
      this.loadScreening(screeningId);
    } else if (this.screening) {
      this.isDetailPage = false;
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadScreening(id: string) {
    this.isLoading = true;
    this.error = null;

    this.getScreeningById.execute(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: any) => {
          this.screening = res;
          this.isLoading = false;
        },
        error: (err: any) => {
          this.error = 'Failed to load screening details';
          this.isLoading = false;
        }
      });
  }

  getAvailableSeatsCount(): number {
    if (!this.screening?.seats) return 0;
    return this.screening.seats.filter((s: any) => s.status === 'AVAILABLE').length;
  }

  getReservedSeatsCount(): number {
    if (!this.screening?.seats) return 0;
    return this.screening.seats.filter((s: any) => s.status === 'RESERVED').length;
  }
}
