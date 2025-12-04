import { Component, Input, OnInit, OnDestroy, Output, EventEmitter, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, distinctUntilChanged } from 'rxjs/operators';
import { GetScreeningByIdService } from '../../../application/use-cases/get-screening-by-id-service';
import { AuthService } from '../../../../../core/auth/auth-service';
import { SeatGridComponent } from '../../../../reservations/presentation/components/seat-grid/seat-grid';
import { ScreeningApiService } from '../../../infrastructure/adapters/screening-api-service';

@Component({
  selector: 'app-screening-card',
  standalone: true,
  imports: [CommonModule, RouterModule, SeatGridComponent],
  templateUrl: './screening-card.html'
})
export class ScreeningCardComponent implements OnInit, OnDestroy {
  @Input() screening: any;
  @Output() deleteScreening = new EventEmitter<string>();
  @Output() editScreening = new EventEmitter<string>();
  isLoading = false; // Only true if we're loading detail page
  error: string | null = null;
  isDetailPage = false;
  authService = inject(AuthService);
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private lastScreeningId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private getScreeningById: GetScreeningByIdService,
    private screeningApi: ScreeningApiService
  ) {}

  ngOnInit() {
    // Check if we're on the detail page by checking if we have a route parameter
    const screeningId = this.route.snapshot.paramMap.get('id');
    if (screeningId && !this.screening) {
      this.isDetailPage = true;
      
      // Clear cache for this specific screening
      if (screeningId) {
        this.screeningApi.clearScreeningCache(screeningId);
      }
      
      // Subscribe to route parameter changes to reload when screeningId changes
      this.route.paramMap
        .pipe(
          switchMap(params => {
            const id = params.get('id');
            // Skip if same ID as last time
            if (id && id !== this.lastScreeningId) {
              this.lastScreeningId = id;
              this.isLoading = true;
              this.error = null;
              return this.getScreeningById.execute(id);
            }
            return [];
          }),
          distinctUntilChanged(),
          takeUntil(this.destroy$)
        )
        .subscribe({
          next: (res: any) => {
            // Normalize snake_case to camelCase if needed
            this.screening = {
              ...res,
              startTime: res.startTime || res.start_time,
              roomNumber: res.roomNumber || res.room_number,
              // Normalize seats if they exist
              seats: res.seats ? res.seats.map((seat: any) => ({
                ...seat,
                isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true),
                status: this.getSeatStatus(seat),
                isSelected: false,
                row: seat.row || String(seat.rowNumber) || '0',
                number: seat.number || seat.seatNumber || 0
              })) : []
            };
            this.isLoading = false;
            // Force change detection
            this.cdr.detectChanges();
          },
          error: (err: any) => {
            this.error = 'Failed to load screening details';
            this.isLoading = false;
            this.cdr.detectChanges();
          }
        });
    } else if (this.screening) {
      this.isDetailPage = false;
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  selectedSeats: any[] = [];

  getSeatStatus(seat: any): string {
    if (seat.status) {
      return seat.status;
    }
    const isAvailable = seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : false);
    if (isAvailable === true || isAvailable === 'true') {
      return 'AVAILABLE';
    }
    return 'RESERVED';
  }

  toggleSeat(seat: any) {
    if (seat?.status !== 'AVAILABLE') return;
    seat.isSelected = !seat.isSelected;
    this.updateSelectedSeats();
  }

  updateSelectedSeats() {
    this.selectedSeats = this.screening?.seats?.filter((s: any) => s.isSelected) || [];
  }

  getAvailableSeatsCount(): number {
    if (!this.screening?.seats) return 0;
    return this.screening.seats.filter((s: any) => {
      const isAvailable = s.isAvailable !== undefined ? s.isAvailable : (s.is_available !== undefined ? s.is_available : false);
      return isAvailable === true || isAvailable === 'true';
    }).length;
  }

  getReservedSeatsCount(): number {
    if (!this.screening?.seats) return 0;
    return this.screening.seats.filter((s: any) => {
      const isAvailable = s.isAvailable !== undefined ? s.isAvailable : (s.is_available !== undefined ? s.is_available : true);
      return isAvailable === false || isAvailable === 'false';
    }).length;
  }

  goToReservation() {
    if (this.selectedSeats.length === 0) {
      alert('Te rugăm să selectezi cel puțin un loc înainte de a continua.');
      return;
    }
    // Navigate to reservation page with selected seats
    const seatIds = this.selectedSeats.map(s => s.id).join(',');
    window.location.href = `/reservations/new?screeningId=${this.screening.id}&seatIds=${seatIds}`;
  }
}
