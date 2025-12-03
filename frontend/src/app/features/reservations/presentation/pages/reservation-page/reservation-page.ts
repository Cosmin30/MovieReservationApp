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
  isCreatingReservation = false;
  error: string | null = null;
  success: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private getSeats: GetAvailableSeatsService,
    private createReservation: CreateReservationService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.screeningId = this.route.snapshot.queryParamMap.get('screeningId')!;

    if (!this.screeningId) {
      this.error = 'Nu a fost selectată nicio proiecție';
      return;
    }

    this.loadAvailableSeats();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAvailableSeats() {
    this.isLoadingSeats = true;
    this.error = null;

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
            isAvailable: seat.isAvailable !== undefined ? seat.isAvailable : true
          }));
          
          this.isLoadingSeats = false;
        },
        error: (err: any) => {
          console.error('Error loading seats:', err);
          this.error = 'Nu am putut încărca locurile disponibile. Te rugăm să reîmprospătezi pagina.';
          this.isLoadingSeats = false;
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
          
          // Reload available seats to reflect changes
          this.loadAvailableSeats();
          
          // Redirect to reservations page after 3 seconds
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
