import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { GetAllHallsService } from '../../../application/use-cases/get-all-halls-service';
import { HallApiService } from '../../../infrastructure/adapters/hall-api-service';

@Component({
  selector: 'app-halls-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './halls-list.html',
  styleUrls: ['./halls-list.css']
})
export class HallsListComponent implements OnInit, OnDestroy {
  halls: any[] = [];
  isLoading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(
    private getAllHallsService: GetAllHallsService,
    private hallApi: HallApiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Clear cache first to ensure fresh data
    this.hallApi.clearCache();
    
    // Load halls on initial component creation
    this.loadHalls();

    // Reload data whenever navigating to this route (but skip the first one since ngOnInit already loaded)
    let isFirstNavigation = true;
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        filter((event: any) => event.url === '/halls' || (event.url.includes('/halls') && !event.url.includes('/halls/'))),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (isFirstNavigation) {
          isFirstNavigation = false;
          return;
        }
        // Clear cache and reload when navigating to this route
        this.hallApi.clearCache();
        this.loadHalls();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadHalls(): void {
    this.isLoading = true;
    this.error = null;
    
    this.getAllHallsService.execute()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: any) => {
          this.halls = data || [];
          this.isLoading = false;
          // Force change detection
          this.cdr.detectChanges();
        },
        error: (err: any) => {
          console.error('Error loading halls:', err);
          this.error = 'Nu am putut încărca salile. Vă rugăm încercați din nou.';
          this.halls = [];
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  trackByHallId(index: number, hall: any): string {
    return hall?.id || index.toString();
  }
}
