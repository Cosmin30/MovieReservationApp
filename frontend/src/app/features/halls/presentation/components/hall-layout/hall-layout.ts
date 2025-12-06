import { Component, Input, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, distinctUntilChanged } from 'rxjs/operators';
import { GetHallByIdService } from '../../../application/use-cases/get-hall-by-id-service';
import { HallApiService } from '../../../infrastructure/adapters/hall-api-service';

@Component({
  selector: 'app-hall-layout',
  standalone: true,
  imports: [CommonModule, RouterModule], 
  templateUrl: './hall-layout.html',
  styleUrls: ['./hall-layout.css']
})
export class HallLayoutComponent implements OnInit, OnDestroy {
  @Input() hall: any;
  
  isLoading = false;
  error: string | null = null;
  hallId: string | null = null;
  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);
  private lastHallId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private getHallByIdService: GetHallByIdService,
    private hallApi: HallApiService
  ) {}

  ngOnInit(): void {
    // Get hall ID from route parameters
    this.route.params
      .pipe(
        distinctUntilChanged((prev, curr) => prev['id'] === curr['id']),
        takeUntil(this.destroy$)
      )
      .subscribe(params => {
        const id = params['id'];
        if (id && id !== this.lastHallId) {
          this.lastHallId = id;
          this.hallId = id;
          // Clear cache for this specific hall
          this.hallApi.clearHallCache(id);
          this.loadHall();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadHall(): void {
    if (!this.hallId) return;

    this.isLoading = true;
    this.error = null;

    // Load hall details only
    this.getHallByIdService.execute(this.hallId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (hall: any) => {
          this.hall = hall;
          this.isLoading = false;
          // Force change detection
          this.cdr.detectChanges();
        },
        error: (err) => {
          // Error is already handled by errorInterceptor and ErrorHandlerService
          this.error = 'Nu am putut încărca detaliile salii. Vă rugăm încercați din nou.';
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  goBack() {
    window.history.back();
  }
}
