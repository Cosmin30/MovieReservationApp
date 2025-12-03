import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { GetHallByIdService } from '../../../application/use-cases/get-hall-by-id-service';

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

  constructor(
    private route: ActivatedRoute,
    private getHallByIdService: GetHallByIdService
  ) {}

  ngOnInit(): void {
    // Get hall ID from route parameters
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      if (params['id']) {
        this.hallId = params['id'];
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
        },
        error: (err: any) => {
          console.error('Error loading hall:', err);
          this.error = 'Nu am putut încărca detaliile salii. Vă rugăm încercați din nou.';
          this.isLoading = false;
        }
      });
  }

  goBack() {
    window.history.back();
  }
}
