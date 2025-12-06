import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ReservationState } from '../../../application/state/reservation-state.state';
import { GetUserReservationsService } from '../../../application/use-cases/get-user-reservations-service';
import { ReservationCardComponent } from '../../components/reservation-card/reservation-card';
import { AuthService } from '../../../../../core/auth/auth-service';
import { ReservationModel } from '../../../domain/models/reservation.model';
import { LoggerService } from '../../../../../core/services/logger.service';
import { NotificationService } from '../../../../../shared/services/notification.service';

@Component({
  selector: 'app-my-reservations-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReservationCardComponent  
  ],
  templateUrl: './my-reservations-page.html',
  styleUrls: ['./my-reservations-page.css']
})
export class MyReservationsPage implements OnInit, OnDestroy {
  reservations: ReservationModel[] = [];
  isLoading = true;
  error: string | null = null;
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private logger = inject(LoggerService);
  private notificationService = inject(NotificationService);

  constructor(
    private state: ReservationState,
    private getUserReservations: GetUserReservationsService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.state.reservations$
      .pipe(takeUntil(this.destroy$))
      .subscribe(res => {
        this.reservations = res || [];
        this.isLoading = false;
        // Force change detection
        this.cdr.detectChanges();
      });

    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user && user.id) {
          this.loadReservations(user.id);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadReservations(userId: string) {
    this.isLoading = true;
    this.error = null;
    
    this.getUserReservations.execute(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isLoading = false;
          // Force change detection
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.logger.error('Error loading reservations:', err);
          this.error = 'Nu am putut încărca rezervările.';
          this.notificationService.error('Nu am putut încărca rezervările.');
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }
}

