import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import QRCode from 'qrcode';

import { SeatSelectionComponent } from '../../components/seat-selection/seat-selection';
import { GetAvailableSeatsService } from '../../../application/use-cases/get-available-seats-service';
import { CreateReservationService } from '../../../application/use-cases/create-reservation-service';
import { AuthService } from '../../../../../core/auth/auth-service';
import { GetScreeningByIdService } from '../../../../screenings/application/use-cases/get-screening-by-id-service';
import { GetReservationByIdService } from '../../../application/use-cases/get-reservation-by-id-service';
import { ReservationApiService } from '../../../infrastructure/adapters/reservation-api-service';
import { ScreeningModel } from '../../../../screenings/domain/models/screening.model';
import { ReservationModel } from '../../../domain/models/reservation.model';
import { SeatModel } from '../../../../halls/domain/models/seat.model';
import { LoggerService } from '../../../../../core/services/logger.service';
import { NotificationService } from '../../../../../shared/services/notification.service';
import { SeatMapper } from '../../../../halls/infrastructure/adapters/seat-mapper.mapper';
import { AvailableSeatsResponseDTO } from '../../../infrastructure/dtos/available-seats-response.dto';
import { SeatDTO } from '../../../../halls/infrastructure/dtos/seat.dto';

@Component({
  selector: 'app-reservation-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    SeatSelectionComponent
  ],
  templateUrl: './reservation-page.html',
  styleUrls: ['./reservation-page.css']
})
export class ReservationPage implements OnInit, OnDestroy {

  screeningId!: string;
  screening: ScreeningModel | null = null;
  availableSeats: SeatModel[] = [];
  selectedSeats: SeatModel[] = [];
  reservation: ReservationModel | null = null;
  pricePerSeat = 50; // Lei
  preselectedSeatIds: string[] = []; // Store seat IDs from query params
  
  // Payment
  showPaymentForm = false;
  paymentData = {
    cardNumber: '',
    cardHolder: '',
    expiry: '',
    cvv: ''
  };
  
  // UI State
  isLoadingSeats = true;
  isLoadingScreening = true;
  isCreatingReservation = false;
  isLoadingReservation = false;
  error: string | null = null;
  success: string | null = null;
  isExistingReservation = false;
  qrCodeDataUrl: string | null = null;
  private destroy$ = new Subject<void>();
  private lastScreeningId: string | null = null;
  private lastReservationId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private getSeats: GetAvailableSeatsService,
    private createReservation: CreateReservationService,
    private authService: AuthService,
    private getScreeningById: GetScreeningByIdService,
    private getReservationById: GetReservationByIdService,
    private reservationApi: ReservationApiService,
    private cdr: ChangeDetectorRef,
    private logger: LoggerService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    // Check if we're viewing an existing reservation (route param :id) or creating a new one (query param screeningId)
    const reservationId = this.route.snapshot.paramMap.get('id');
    
    if (reservationId) {
      // Existing reservation - load reservation details (exactly like MovieDetailsPage)
      this.isExistingReservation = true;
      this.isLoadingReservation = true;
      // Clear cache for this specific reservation
      this.reservationApi.clearReservationCache(reservationId);
      
      this.getReservationById.execute(reservationId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (reservation: any) => {
            // Normalize reservation data
            this.reservation = {
              ...reservation,
              totalPrice: reservation.totalPrice || reservation.total_price || 0,
              tickets: (reservation.tickets || []).map((ticket: any) => ({
                ...ticket,
                seat: ticket.seat ? {
                  row: ticket.seat.row || ticket.seat.rowNumber || ticket.seat.row_number || 'N/A',
                  number: ticket.seat.number || ticket.seat.seatNumber || ticket.seat.seat_number || 'N/A'
                } : null
              }))
            };
            this.isLoadingReservation = false;
            // Load screening from reservation
            if (reservation.screening) {
              this.screening = reservation.screening;
              this.screeningId = reservation.screening.id;
            }
            // Generate QR code
            this.generateQRCode();
            // Force change detection
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.logger.error('Error loading reservation:', err);
            this.error = 'Nu am putut încărca detaliile rezervării. Te rugăm să reîmprospătezi pagina.';
            this.notificationService.error('Nu am putut încărca detaliile rezervării. Te rugăm să reîmprospătezi pagina.');
            this.isLoadingReservation = false;
            this.cdr.detectChanges();
          }
        });
    } else {
      // New reservation - check query params for screeningId and seatIds
      this.isExistingReservation = false;
      const screeningId = this.route.snapshot.queryParamMap.get('screeningId');
      const seatIdsParam = this.route.snapshot.queryParamMap.get('seatIds');
      
      // Parse preselected seat IDs from query params
      if (seatIdsParam) {
        this.preselectedSeatIds = seatIdsParam.split(',').filter(id => id.trim() !== '');
      }
      
      if (screeningId) {
        this.screeningId = screeningId;
        this.error = null;
        // Load screening details first, seats will be loaded after screening is loaded
        this.loadScreeningDetails();
      } else {
        this.error = 'Nu a fost selectată nicio proiecție';
        this.cdr.detectChanges();
      }
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadScreeningDetails(): void {
    this.isLoadingScreening = true;
    this.error = null;

    this.getScreeningById.execute(this.screeningId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (screening: ScreeningModel) => {
          this.screening = screening;
          this.isLoadingScreening = false;
          this.cdr.detectChanges();
          
          // Load seats after screening is loaded
          this.loadAvailableSeats();
        },
        error: (err) => {
          this.logger.error('Error loading screening details:', err);
          this.error = 'Nu am putut încărca detaliile proiecției. Te rugăm să reîmprospătezi pagina.';
          this.notificationService.error('Nu am putut încărca detaliile proiecției. Te rugăm să reîmprospătezi pagina.');
          this.isLoadingScreening = false;
          this.cdr.detectChanges();
        }
      });
  }

  getAvailableSeatsCount(): number {
    // Use screening.seats which has all seats with their status
    if (!this.screening?.seats || this.screening.seats.length === 0) {
      return 0;
    }
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
    return this.screening.seats.filter((s: SeatModel) => s.status === 'RESERVED').length;
  }

  loadAvailableSeats() {
    this.isLoadingSeats = true;
    this.error = null;

    // First try to use seats from screening if already loaded
    if (this.screening?.seats && this.screening.seats.length > 0) {
      // Seats are already SeatModel from mapper
      this.availableSeats = this.screening.seats.map((seat: SeatModel) => {
        const isPreselected = this.preselectedSeatIds.includes(seat.id);
        return {
          ...seat,
          isSelected: isPreselected
        };
      });
      
      // Update selectedSeats with preselected seats
      this.selectedSeats = this.availableSeats.filter(seat => seat.isSelected);
      
      // Emit event to update seat selection component
      if (this.selectedSeats.length > 0) {
        this.onSeatsSelected(this.selectedSeats);
      }
      
      this.isLoadingSeats = false;
      this.cdr.detectChanges();
      return;
    }

    // Otherwise fetch from API
    this.getSeats.execute(this.screeningId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (seatsDto: SeatDTO[]) => {
          // Backend returns List<SeatDTO> directly
          // Transform backend data to frontend format using SeatMapper
          this.availableSeats = seatsDto.map(dto => {
            const seat = SeatMapper.fromDto(dto);
            const isPreselected = this.preselectedSeatIds.includes(seat.id);
            return {
              ...seat,
              isSelected: isPreselected
            };
          });
          
          // Update selectedSeats with preselected seats
          this.selectedSeats = this.availableSeats.filter(seat => seat.isSelected);
          
          // Emit event to update seat selection component
          if (this.selectedSeats.length > 0) {
            this.onSeatsSelected(this.selectedSeats);
          }
          
          this.isLoadingSeats = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.logger.error('Error loading seats:', err);
          // If we have screening seats, use those
          if (this.screening?.seats && this.screening.seats.length > 0) {
            this.availableSeats = this.screening.seats.map((seat: SeatModel) => {
              const isPreselected = this.preselectedSeatIds.includes(seat.id);
              return {
                ...seat,
                isSelected: isPreselected
              };
            });
            
            this.selectedSeats = this.availableSeats.filter(seat => seat.isSelected);
            
            if (this.selectedSeats.length > 0) {
              this.onSeatsSelected(this.selectedSeats);
            }
            
            this.isLoadingSeats = false;
          } else {
            this.error = 'Nu am putut încărca locurile disponibile. Te rugăm să reîmprospătezi pagina.';
            this.notificationService.error('Nu am putut încărca locurile disponibile. Te rugăm să reîmprospătezi pagina.');
            this.isLoadingSeats = false;
          }
          this.cdr.detectChanges();
        }
      });
  }


  onSeatsSelected(seats: SeatModel[]): void {
    this.selectedSeats = seats;
  }

  getTotalPrice(): number {
    return this.selectedSeats.length * this.pricePerSeat;
  }

  processPayment() {
    if (this.selectedSeats.length === 0) {
      this.error = 'Te rugăm să selectezi cel puțin un loc';
      return;
    }

    // Validate payment data
    if (!this.paymentData.cardNumber || !this.paymentData.cardHolder || 
        !this.paymentData.expiry || !this.paymentData.cvv) {
      this.error = 'Te rugăm să completezi toate câmpurile de plată';
      return;
    }

    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (!user || !user.id) {
          this.error = 'Utilizatorul nu este autentificat';
          return;
        }

        // Simulate payment processing (fictive)
        this.isCreatingReservation = true;
        this.error = null;
        this.success = null;

        // Simulate payment delay
        setTimeout(() => {
          this.createReservationAndBuyTickets(user.id);
        }, 1000);
      });
  }

  createReservationAndBuyTickets(userId: string) {
    const seatIds = this.selectedSeats.map(s => s.id);

    // Backend creates tickets automatically when creating reservation
    this.createReservation.execute(userId, this.screeningId, seatIds, this.pricePerSeat)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: any) => {
          // Backend now creates reservation with CONFIRMED status (payment is simulated)
          this.reservation = res;
          
          // Use setTimeout to avoid ExpressionChangedAfterItHasBeenCheckedError
          setTimeout(() => {
            this.isCreatingReservation = false;
            this.success = 'Rezervarea a fost creată cu succes! Plata a fost procesată. Biletele au fost generate automat.';
            
            // Clear cache for user reservations to force refresh on next load
            this.reservationApi.clearUserReservationsCache(userId);
            
            // Don't clear selected seats yet - keep them visible in summary until redirect
            // They will be cleared when navigating away
            this.showPaymentForm = false;
            this.paymentData = {
              cardNumber: '',
              cardHolder: '',
              expiry: '',
              cvv: ''
            };
            
            // Force change detection
            this.cdr.detectChanges();
            
            // Redirect to reservations page after 3 seconds with refresh flag
            setTimeout(() => {
              // Clear selected seats only when redirecting
              this.selectedSeats = [];
              this.router.navigate(['/reservations'], { queryParams: { refresh: 'true' } });
            }, 3000);
          }, 0);
        },
        error: (err) => {
          this.logger.error('Error creating reservation:', err);
          const errorMessage = err.error?.message || 'Nu am putut crea rezervarea. Te rugăm să încerci din nou.';
          this.error = errorMessage;
          this.notificationService.error(errorMessage);
          this.isCreatingReservation = false;
        }
      });
  }

  cancelReservation() {
    this.selectedSeats = [];
    this.reservation = null;
    this.showPaymentForm = false;
    this.paymentData = {
      cardNumber: '',
      cardHolder: '',
      expiry: '',
      cvv: ''
    };
    this.error = null;
    this.success = null;
  }

  getReservationStatusText(): string {
    if (!this.reservation?.status) return 'Necunoscut';
    
    const status = this.reservation.status.toUpperCase();
    switch (status) {
      case 'CONFIRMED':
      case 'CREATED': // Treat CREATED as paid since payment is simulated
        return 'Paid';
      case 'PENDING':
        return 'Pending';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return 'Paid'; // Default to Paid for any other status
    }
  }

  getReservationStatusClass(): string {
    if (!this.reservation?.status) return 'bg-success';
    
    const status = this.reservation.status.toUpperCase();
    switch (status) {
      case 'CONFIRMED':
      case 'CREATED': // Treat CREATED as paid (green)
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return 'bg-success'; // Default to green (paid) for any other status
    }
  }

  generateQRCode() {
    if (!this.reservation) return;

    // Create QR code data with reservation information
    const user = this.reservation.user;
    const screening = this.reservation.screening as ScreeningModel | null;
    const userId = user && typeof user === 'object' && 'id' in user ? (user as any).id : null;
    const screeningId = screening && typeof screening === 'object' && 'id' in screening ? screening.id : null;
    
    const qrData = JSON.stringify({
      reservationId: this.reservation.id,
      userId: userId || 'N/A',
      screeningId: screeningId || 'N/A',
      movie: screening?.movie && typeof screening.movie === 'object' && 'title' in screening.movie 
        ? (screening.movie as any).title 
        : 'N/A',
      hall: screening?.hall && typeof screening.hall === 'object' && 'name' in screening.hall
        ? (screening.hall as any).name
        : 'N/A',
      date: screening?.startTime || 'N/A',
      totalPrice: this.reservation.totalPrice || 0,
      status: this.reservation.status || 'N/A',
      tickets: this.reservation.tickets?.map((ticket) => ({
        seat: ticket.seat && typeof ticket.seat === 'object'
          ? `${(ticket.seat as any).row || 'N/A'}-${(ticket.seat as any).number || 'N/A'}`
          : 'N/A'
      })) || []
    });

    // Generate QR code as data URL
    QRCode.toDataURL(qrData, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    })
      .then((url: string) => {
        this.qrCodeDataUrl = url;
        this.cdr.detectChanges();
      })
      .catch((err) => {
        this.logger.error('Error generating QR code:', err);
      });
  }
}
