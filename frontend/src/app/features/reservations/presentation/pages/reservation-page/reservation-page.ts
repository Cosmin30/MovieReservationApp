import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { SeatSelectionComponent } from '../../components/seat-selection/seat-selection';
import { GetAvailableSeatsService } from '../../../application/use-cases/get-available-seats-service';
import { CreateReservationService } from '../../../application/use-cases/create-reservation-service';
import { AuthService } from '../../../../../core/auth/auth-service';
import { GetScreeningByIdService } from '../../../../screenings/application/use-cases/get-screening-by-id-service';

@Component({
  selector: 'app-reservation-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    SeatSelectionComponent
  ],
  templateUrl: './reservation-page.html',
  styleUrls: ['./reservation-page.css']
})
export class ReservationPage implements OnInit, OnDestroy {

  screeningId!: string;
  screening: any = null;
  availableSeats: any[] = [];
  selectedSeats: any[] = [];
  reservation: any;
  pricePerSeat = 50; // Lei
  
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
  error: string | null = null;
  success: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private getSeats: GetAvailableSeatsService,
    private createReservation: CreateReservationService,
    private authService: AuthService,
    private getScreeningById: GetScreeningByIdService
  ) {}

  ngOnInit() {
    this.screeningId = this.route.snapshot.queryParamMap.get('screeningId')!;

    if (!this.screeningId) {
      this.error = 'Nu a fost selectată nicio proiecție';
      return;
    }

    // Load screening details first, seats will be loaded after screening is loaded
    this.loadScreeningDetails();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadScreeningDetails() {
    this.isLoadingScreening = true;
    this.error = null;

    this.getScreeningById.execute(this.screeningId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: any) => {
          console.log('Raw screening response:', res);
          
          // Normalize snake_case to camelCase if needed
          this.screening = {
            ...res,
            startTime: res.startTime || res.start_time,
            roomNumber: res.roomNumber || res.room_number,
            capacity: res.capacity || 0,
            // Normalize seats if they exist
            seats: res.seats ? res.seats.map((seat: any) => ({
              ...seat,
              isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true),
              status: this.getSeatStatus(seat),
              row: seat.row || String(seat.rowNumber) || '0',
              number: seat.number || seat.seatNumber || 0,
              isSelected: false
            })) : (res.seats || []),
            // Ensure movie and hall are preserved
            movie: res.movie || null,
            hall: res.hall || null
          };
          
          console.log('Normalized screening:', this.screening);
          console.log('Screening seats count:', this.screening.seats?.length || 0);
          console.log('Available seats:', this.getAvailableSeatsCount());
          console.log('Reserved seats:', this.getReservedSeatsCount());
          
          this.isLoadingScreening = false;
          
          // Load seats after screening is loaded
          this.loadAvailableSeats();
        },
        error: (err: any) => {
          console.error('Error loading screening details:', err);
          this.error = 'Nu am putut încărca detaliile proiecției. Te rugăm să reîmprospătezi pagina.';
          this.isLoadingScreening = false;
        }
      });
  }

  getAvailableSeatsCount(): number {
    if (!this.screening?.seats || this.screening.seats.length === 0) return 0;
    return this.screening.seats.filter((s: any) => {
      const isAvailable = s.isAvailable !== undefined ? s.isAvailable : (s.is_available !== undefined ? s.is_available : false);
      return isAvailable === true || isAvailable === 'true';
    }).length;
  }

  getReservedSeatsCount(): number {
    if (!this.screening?.seats || this.screening.seats.length === 0) return 0;
    return this.screening.seats.filter((s: any) => {
      const isAvailable = s.isAvailable !== undefined ? s.isAvailable : (s.is_available !== undefined ? s.is_available : true);
      return isAvailable === false || isAvailable === 'false';
    }).length;
  }

  loadAvailableSeats() {
    this.isLoadingSeats = true;
    this.error = null;

    // First try to use seats from screening if already loaded
    if (this.screening?.seats && this.screening.seats.length > 0) {
      this.availableSeats = this.screening.seats.map((seat: any) => ({
        id: seat.id,
        row: seat.row || String(seat.rowNumber) || '0',
        number: seat.number || seat.seatNumber || 0,
        status: this.getSeatStatus(seat),
        isSelected: false,
        isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true)
      }));
      this.isLoadingSeats = false;
      return;
    }

    // Otherwise fetch from API
    this.getSeats.execute(this.screeningId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: any) => {
          // Backend returns array of seats directly
          const seats = Array.isArray(res) ? res : (res.seats || []);
          
          // Transform backend data to frontend format
          this.availableSeats = seats.map((seat: any) => ({
            id: seat.id,
            row: seat.row || String(seat.rowNumber) || '0',
            number: seat.number || seat.seatNumber || 0,
            status: this.getSeatStatus(seat),
            isSelected: false,
            isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true)
          }));
          
          this.isLoadingSeats = false;
        },
        error: (err: any) => {
          console.error('Error loading seats:', err);
          // If we have screening seats, use those
          if (this.screening?.seats && this.screening.seats.length > 0) {
            this.availableSeats = this.screening.seats.map((seat: any) => ({
              id: seat.id,
              row: seat.row || String(seat.rowNumber) || '0',
              number: seat.number || seat.seatNumber || 0,
              status: this.getSeatStatus(seat),
              isSelected: false,
              isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : (seat.is_available !== undefined ? seat.is_available : true)
            }));
            this.isLoadingSeats = false;
          } else {
            this.error = 'Nu am putut încărca locurile disponibile. Te rugăm să reîmprospătezi pagina.';
            this.isLoadingSeats = false;
          }
        }
      });
  }

  getSeatStatus(seat: any): string {
    // Backend uses isAvailable (boolean), transform to status
    if (seat.status) {
      return seat.status;
    }
    if (seat.isAvailable === true || seat.isAvailable === 'true') {
      return 'AVAILABLE';
    }
    if (seat.isAvailable === false || seat.isAvailable === 'false') {
      return 'RESERVED';
    }
    return 'UNAVAILABLE';
  }

  onSeatsSelected(seats: any[]) {
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
          this.reservation = res;
          this.isCreatingReservation = false;
          this.success = 'Rezervarea a fost creată cu succes! Plata a fost procesată. Biletele au fost generate automat.';
          
          // Clear selected seats
          this.selectedSeats = [];
          this.showPaymentForm = false;
          this.paymentData = {
            cardNumber: '',
            cardHolder: '',
            expiry: '',
            cvv: ''
          };
          
          // Redirect to reservations page after 3 seconds (don't reload seats)
          setTimeout(() => {
            this.router.navigate(['/reservations']);
          }, 3000);
        },
        error: (err: any) => {
          console.error('Error creating reservation:', err);
          this.error = err.error?.message || 'Nu am putut crea rezervarea. Te rugăm să încerci din nou.';
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
}
