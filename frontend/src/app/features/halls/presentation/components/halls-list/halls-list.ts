import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { GetAllHallsService } from '../../../application/use-cases/get-all-halls-service';

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

  constructor(private getAllHallsService: GetAllHallsService) {}

  ngOnInit(): void {
    this.loadHalls();
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
          this.halls = data;
          this.isLoading = false;
        },
        error: (err: any) => {
          console.error('Error loading halls:', err);
          this.error = 'Nu am putut încărca salile. Vă rugăm încercați din nou.';
          this.isLoading = false;
        }
      });
  }
}
