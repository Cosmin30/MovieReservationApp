import { Component, OnInit, OnDestroy, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService, NotificationMessage } from '../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrls: ['./notification.css']
})
export class NotificationComponent implements OnInit, OnDestroy, AfterViewInit {
  messages: NotificationMessage[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Subscribe to notifications but defer updates to next tick
    this.notificationService.notifications$
      .pipe(takeUntil(this.destroy$))
      .subscribe(notifications => {
        // Use setTimeout to defer change detection to next tick
        setTimeout(() => {
          this.messages = notifications;
          this.cdr.detectChanges();
        }, 0);
      });
  }

  ngAfterViewInit(): void {
    // Initial load after view is initialized
    this.messages = this.notificationService.notificationsSubject.value;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  remove(id: string): void {
    this.notificationService.remove(id);
  }
}
