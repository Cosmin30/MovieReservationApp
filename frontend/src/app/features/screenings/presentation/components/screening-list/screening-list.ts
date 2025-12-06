import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
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
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  constructor(private api: ScreeningApiService) {}

  ngOnInit(): void {
    // Check for query params
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        const shouldRefresh = params['refresh'] === 'true';
        
        if (shouldRefresh) {
          // Clear cache and reload when refresh flag is present
          this.api.clearCache();
          this.loadScreenings();
          // Clear query param
          this.router.navigate(['/screenings'], { replaceUrl: true, queryParams: {} });
        } else if (params['edit']) {
          this.onEditScreening(params['edit']);
          // Clear query param
          this.router.navigate(['/screenings'], { replaceUrl: true });
        } else {
          // Normal load - clear cache first to ensure fresh data
          this.api.clearCache();
          this.loadScreenings();
        }
      });
    
    // Reload data whenever navigating to this route (but skip if refresh query param is handled above)
    let isFirstNavigation = true;
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        filter((event: any) => {
          const url = event.url || '';
          return (url === '/screenings' || url.startsWith('/screenings?')) && !url.includes('/screenings/');
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (isFirstNavigation) {
          isFirstNavigation = false;
          return;
        }
        // Only reload if no refresh query param (to avoid double load)
        const hasRefresh = this.route.snapshot.queryParams['refresh'] === 'true';
        if (!hasRefresh) {
          this.api.clearCache();
          this.loadScreenings();
        }
      });
  }

  private loadScreenings(): void {
    // Use setTimeout to defer change detection
    setTimeout(() => {
      this.loading = true;
      this.error = null;
      this.cdr.detectChanges();
    }, 0);
    
    this.api.getAllScreenings()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: any) => {
          if (data && Array.isArray(data)) {
            // map snake_case -> camelCase dacă e nevoie
            const mappedScreenings = data.map((s: any) => ({
              ...s,
              startTime: s.startTime || s.start_time,
              roomNumber: s.roomNumber || s.room_number
            }));
            this.screenings = mappedScreenings;
          } else {
            this.screenings = [];
          }
          
          // Use setTimeout to defer change detection
          setTimeout(() => {
            this.loading = false;
            this.cdr.detectChanges();
          }, 0);
        },
        error: (err) => {
          // Error is already handled by errorInterceptor and ErrorHandlerService
          this.error = 'Nu am putut încărca proiecțiile. Te rugăm să reîncerci.';
          this.screenings = [];
          
          // Use setTimeout to defer change detection
          setTimeout(() => {
            this.loading = false;
            this.cdr.detectChanges();
          }, 0);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onDeleteScreening(screeningId: string) {
    if (confirm('Ești sigur că vrei să ștergi această proiecție?')) {
      // Remove from local array immediately for better UX
      const originalScreenings = [...this.screenings];
      this.screenings = this.screenings.filter(s => s.id !== screeningId);
      this.cdr.detectChanges();
      
      this.api.deleteScreening(screeningId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            // Clear cache immediately
            this.api.clearCache();
            
            // Show success message
            this.success = 'Proiecția a fost ștearsă cu succes!';
            this.cdr.detectChanges();
            
            // Reload screenings to ensure consistency
            this.loadScreenings();
            
            // Clear success message after 3 seconds
            setTimeout(() => {
              this.success = null;
              this.cdr.detectChanges();
            }, 3000);
          },
          error: (err) => {
            // Restore original screenings on error
            this.screenings = originalScreenings;
            this.cdr.detectChanges();
            
            this.error = 'Nu am putut șterge proiecția. Te rugăm să încerci din nou.';
            setTimeout(() => {
              this.error = null;
              this.cdr.detectChanges();
            }, 3000);
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
    // Store editing state before making the request
    const isEditing = !!this.editingScreening;
    // Use ID from editingScreening or from screeningData (form might send it)
    const screeningId = this.editingScreening?.id || screeningData?.id;
    
    const operation = isEditing && screeningId ? 
      this.api.updateScreening(screeningId, screeningData) :
      this.api.createScreening(screeningData);

    operation
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.success = isEditing ? 
            'Proiecția a fost actualizată cu succes!' : 
            'Proiecția a fost adăugată cu succes!';
          this.showScreeningForm = false;
          
          // Clear cache before reloading to ensure fresh data
          this.api.clearCache();
          
          // Reload screenings
          this.loading = true;
          this.loadScreenings();
          
          // Reset editing state after successful operation
          this.editingScreening = null;
          
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.error = isEditing ? 
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

  trackByScreeningId(index: number, screening: any): string {
    return screening?.id || index;
  }
}

