import { Component, Input, OnInit, OnDestroy, Output, EventEmitter, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, distinctUntilChanged } from 'rxjs/operators';
import { GetScreeningByIdService } from '../../../application/use-cases/get-screening-by-id-service';
import { AuthService } from '../../../../../core/auth/auth-service';
import { SeatGridComponent } from '../../../../reservations/presentation/components/seat-grid/seat-grid';
import { ScreeningApiService } from '../../../infrastructure/adapters/screening-api-service';
import { ScreeningFormComponent } from '../screening-form/screening-form';

@Component({
  selector: 'app-screening-card',
  standalone: true,
  imports: [CommonModule, RouterModule, SeatGridComponent, ScreeningFormComponent],
  templateUrl: './screening-card.html',
  styleUrls: ['./screening-card.css']
})
export class ScreeningCardComponent implements OnInit, OnDestroy {
  @Input() screening: any;
  @Output() deleteScreening = new EventEmitter<string>();
  @Output() editScreening = new EventEmitter<string>();
  isLoading = false; // Only true if we're loading detail page
  error: string | null = null;
  isDetailPage = false;
  authService = inject(AuthService);
  showScreeningForm = false;
  editingScreening: any = null;
  success: string | null = null;
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private lastScreeningId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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

  onEditScreening() {
    if (this.isDetailPage) {
      // Show form directly on detail page
      this.editingScreening = this.screening;
      this.showScreeningForm = true;
      this.cdr.detectChanges();
    } else {
      this.editScreening.emit(this.screening.id);
    }
  }

  onDeleteScreening() {
    if (this.isDetailPage) {
      // Delete directly from detail page
      if (confirm('Ești sigur că vrei să ștergi această proiecție?')) {
        this.screeningApi.deleteScreening(this.screening.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.success = 'Proiecția a fost ștearsă cu succes!';
              // Navigate back to screenings list after deletion
              setTimeout(() => {
                this.router.navigate(['/screenings']);
              }, 1500);
            },
            error: (err) => {
              this.error = 'Nu am putut șterge proiecția. Te rugăm să încerci din nou.';
              setTimeout(() => this.error = null, 3000);
            }
          });
      }
    } else {
      this.deleteScreening.emit(this.screening.id);
    }
  }

  onScreeningFormSubmit(screeningData: any) {
    const operation = this.editingScreening ? 
      this.screeningApi.updateScreening(this.editingScreening.id, screeningData) :
      this.screeningApi.createScreening(screeningData);

    operation
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: any) => {
          this.success = this.editingScreening ? 
            'Proiecția a fost actualizată cu succes!' : 
            'Proiecția a fost adăugată cu succes!';
          this.showScreeningForm = false;
          this.editingScreening = null;
          
          // Update screening with response data directly (faster and avoids 403 issues)
          if (this.isDetailPage && response) {
            // Use the response data directly to update the screening
            this.screening = {
              ...response,
              startTime: response.startTime || response.start_time,
              roomNumber: response.roomNumber || response.room_number,
              seats: response.seats ? response.seats.map((seat: any) => ({
                ...seat,
                isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true),
                status: this.getSeatStatus(seat),
                isSelected: false,
                row: seat.row || String(seat.rowNumber) || '0',
                number: seat.number || seat.seatNumber || 0
              })) : (this.screening?.seats || [])
            };
            
            // Clear cache for this screening to ensure fresh data on next load
            if (this.screening?.id) {
              this.screeningApi.clearScreeningCache(this.screening.id);
            }
            
            this.cdr.detectChanges();
          } else if (this.isDetailPage && this.screening?.id) {
            // Fallback: reload screening details after a short delay if response doesn't have all data
            this.screeningApi.clearScreeningCache(this.screening.id);
            
            setTimeout(() => {
              this.getScreeningById.execute(this.screening.id)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: (res: any) => {
                    this.screening = {
                      ...res,
                      startTime: res.startTime || res.start_time,
                      roomNumber: res.roomNumber || res.room_number,
                      seats: res.seats ? res.seats.map((seat: any) => ({
                        ...seat,
                        isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true),
                        status: this.getSeatStatus(seat),
                        isSelected: false,
                        row: seat.row || String(seat.rowNumber) || '0',
                        number: seat.number || seat.seatNumber || 0
                      })) : []
                    };
                    this.cdr.detectChanges();
                  },
                  error: (err) => {
                    console.error('Error reloading screening after update:', err);
                    // If reload fails, just show error but don't navigate away
                    this.error = 'Proiecția a fost actualizată, dar nu am putut reîncărca detaliile. Te rugăm să reîmprospătezi pagina.';
                    setTimeout(() => this.error = null, 5000);
                    this.cdr.detectChanges();
                  }
                });
            }, 500); // Delay to ensure backend has processed the update
          }
          
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
