import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();
  private requestCount = 0;

  show() {
    this.requestCount++;
    if (this.requestCount > 0) {
      this.loadingSubject.next(true);
    }
  }

  hide() {
    this.requestCount--;
    if (this.requestCount <= 0) {
      this.requestCount = 0; // Ensure it doesn't go negative
      this.loadingSubject.next(false);
    }
  }
}
