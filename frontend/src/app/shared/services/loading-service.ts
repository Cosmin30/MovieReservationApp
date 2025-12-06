import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { LoggerService } from '../../core/services/logger.service';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();
  private logger = inject(LoggerService);

  show(): void {
    this.loadingSubject.next(true);
  }

  hide(): void {
    this.loadingSubject.next(false);
  }

  /**
   * Force hide loading after timeout (safety measure)
   * Use this in specific long-running operations
   */
  forceHideAfter(milliseconds: number = 30000): void {
    setTimeout(() => {
      if (this.loadingSubject.value) {
        this.logger.warn(`Force hiding loading spinner after ${milliseconds}ms`);
        this.hide();
      }
    }, milliseconds);
  }
}
