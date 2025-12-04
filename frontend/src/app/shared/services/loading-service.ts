import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  show() {
    this.loadingSubject.next(true);
  }

  hide() {
    this.loadingSubject.next(false);
  }

  /**
   * Force hide loading after timeout (safety measure)
   * Use this in specific long-running operations
   */
  forceHideAfter(milliseconds: number = 30000) {
    setTimeout(() => {
      if (this.loadingSubject.value) {
        console.warn(`⚠️ Force hiding loading spinner after ${milliseconds}ms`);
        this.hide();
      }
    }, milliseconds);
  }
}
