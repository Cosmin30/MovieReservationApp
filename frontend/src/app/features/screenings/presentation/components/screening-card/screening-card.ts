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
import { NotificationService } from '../../../../../shared/services/notification.service';
import { LoggerService } from '../../../../../core/services/logger.service';
import { ScreeningModel } from '../../../domain/models/screening.model';
import { SeatModel } from '../../../../halls/domain/models/seat.model';
import { ScreeningDTO } from '../../../infrastructure/dtos/screening.dto';
import { ScreeningMapper } from '../../../infrastructure/adapters/screening-mapper.mapper';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-screening-card',
  standalone: true,
  imports: [CommonModule, RouterModule, SeatGridComponent, ScreeningFormComponent],
  templateUrl: './screening-card.html',
  styleUrls: ['./screening-card.css']
})
export class ScreeningCardComponent implements OnInit, OnDestroy {
  @Input() screening: ScreeningModel | null = null;
  @Output() deleteScreening = new EventEmitter<string>();
  @Output() editScreening = new EventEmitter<string>();
  isLoading = false; // Only true if we're loading detail page
  error: string | null = null;
  isDetailPage = false;
  authService = inject(AuthService);
  showScreeningForm = false;
  editingScreening: ScreeningModel | null = null;
  success: string | null = null;
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private notificationService = inject(NotificationService);
  private logger = inject(LoggerService);
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
          next: (screening: ScreeningModel) => {
            // Screening is already normalized by mapper
            this.screening = screening;
            this.isLoading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.logger.error('Failed to load screening details:', err);
            this.error = 'Failed to load screening details';
            this.notificationService.error('Nu am putut încărca detaliile proiecției.');
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

  selectedSeats: SeatModel[] = [];

  toggleSeat(seat: SeatModel): void {
    if (seat?.status !== 'AVAILABLE') return;
    seat.isSelected = !seat.isSelected;
    this.updateSelectedSeats();
  }

  updateSelectedSeats(): void {
    this.selectedSeats = this.screening?.seats?.filter((s: SeatModel) => s.isSelected) || [];
  }

  getAvailableSeatsCount(): number {
    if (!this.screening?.seats || this.screening.seats.length === 0) return 0;
    // Check status or isAvailable/is_available flags
    return this.screening.seats.filter((s: any) => {
      return s.status === 'AVAILABLE' || 
             s.isAvailable === true || 
             s.is_available === true ||
             (s.status !== 'RESERVED' && s.status !== 'UNAVAILABLE' && (s.isAvailable !== false && s.is_available !== false));
    }).length;
  }

  getReservedSeatsCount(): number {
    if (!this.screening?.seats || this.screening.seats.length === 0) return 0;
    // Check status or isAvailable/is_available flags
    return this.screening.seats.filter((s: any) => {
      return s.status === 'RESERVED' || 
             s.isAvailable === false || 
             s.is_available === false;
    }).length;
  }

  goToReservation(): void {
    if (this.selectedSeats.length === 0) {
      this.notificationService.warning('Te rugăm să selectezi cel puțin un loc înainte de a continua.');
      return;
    }
    if (!this.screening?.id) return;
    
    // Navigate to reservation page with selected seats
    const seatIds = this.selectedSeats.map(s => s.id).join(',');
    this.router.navigate(['/reservations/new'], {
      queryParams: {
        screeningId: this.screening.id,
        seatIds: seatIds
      }
    });
  }

  onEditScreening(): void {
    if (this.isDetailPage) {
      // Show form directly on detail page
      this.editingScreening = this.screening;
      this.showScreeningForm = true;
      this.cdr.detectChanges();
    } else {
      if (this.screening?.id) {
        this.editScreening.emit(this.screening.id);
      }
    }
  }

  onDeleteScreening(): void {
    if (this.isDetailPage) {
      // Delete directly from detail page
      if (confirm('Ești sigur că vrei să ștergi această proiecție?')) {
        if (!this.screening?.id) return;
        
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
      if (this.screening?.id) {
        this.deleteScreening.emit(this.screening.id);
      }
    }
  }

  onScreeningFormSubmit(screeningData: ScreeningDTO): void {
    if (!this.editingScreening?.id && !screeningData.id) {
      this.logger.error('Cannot submit screening form: missing ID');
      return;
    }
    
    const operation = this.editingScreening?.id ? 
      this.screeningApi.updateScreening(this.editingScreening.id, screeningData) :
      this.screeningApi.createScreening(screeningData);

    operation
      .pipe(
        map((dto: ScreeningDTO) => ScreeningMapper.fromDto(dto)),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (screening: ScreeningModel) => {
          const successMessage = this.editingScreening ? 
            'Proiecția a fost actualizată cu succes!' : 
            'Proiecția a fost adăugată cu succes!';
          this.success = successMessage;
          this.notificationService.success(successMessage);
          this.showScreeningForm = false;
          this.editingScreening = null;
          
          // Update screening with response data (already normalized by mapper)
          if (this.isDetailPage && screening) {
            this.screening = screening;
            
            // Clear cache for this screening to ensure fresh data on next load
            if (this.screening?.id) {
              this.screeningApi.clearScreeningCache(this.screening.id);
            }
            
            this.cdr.detectChanges();
          } else if (this.isDetailPage && this.screening?.id) {
            // Fallback: reload screening details after a short delay if response doesn't have all data
            const screeningId = this.screening.id;
            this.screeningApi.clearScreeningCache(screeningId);
            
            setTimeout(() => {
              this.getScreeningById.execute(screeningId)
                .pipe(takeUntil(this.destroy$))
                .subscribe({
                  next: (reloadedScreening: ScreeningModel) => {
                    this.screening = reloadedScreening;
                    this.cdr.detectChanges();
                  },
                  error: (err) => {
                    this.logger.error('Error reloading screening after update:', err);
                    this.error = 'Proiecția a fost actualizată, dar nu am putut reîncărca detaliile. Te rugăm să reîmprospătezi pagina.';
                    this.notificationService.warning('Proiecția a fost actualizată, dar nu am putut reîncărca detaliile. Te rugăm să reîmprospătezi pagina.');
                    setTimeout(() => this.error = null, 5000);
                    this.cdr.detectChanges();
                  }
                });
            }, 500); // Delay to ensure backend has processed the update
          }
          
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.logger.error('Error saving screening:', err);
          const errorMessage = this.editingScreening ? 
            'Nu am putut actualiza proiecția. Te rugăm să încerci din nou.' :
            'Nu am putut adăuga proiecția. Te rugăm să încerci din nou.';
          this.error = errorMessage;
          this.notificationService.error(errorMessage);
          setTimeout(() => this.error = null, 3000);
        }
      });
  }

  onScreeningFormCancel(): void {
    this.showScreeningForm = false;
    this.editingScreening = null;
  }
}
