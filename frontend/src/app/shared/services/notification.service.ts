import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface NotificationMessage {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number; // Duration in milliseconds, undefined = no auto-dismiss
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private _notificationsSubject = new BehaviorSubject<NotificationMessage[]>([]);
  notifications$: Observable<NotificationMessage[]> = this._notificationsSubject.asObservable();
  
  // Expose subject for direct access if needed
  get notificationsSubject(): BehaviorSubject<NotificationMessage[]> {
    return this._notificationsSubject;
  }

  private generateId(): string {
    return `notification-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  show(message: string, type: NotificationMessage['type'] = 'info', duration: number = 5000): void {
    const notification: NotificationMessage = {
      id: this.generateId(),
      type,
      message,
      duration
    };

    const current = this._notificationsSubject.value;
    this._notificationsSubject.next([...current, notification]);

    // Auto-dismiss if duration is set
    if (duration > 0) {
      setTimeout(() => {
        this.remove(notification.id);
      }, duration);
    }
  }

  success(message: string, duration: number = 5000): void {
    this.show(message, 'success', duration);
  }

  error(message: string, duration: number = 7000): void {
    this.show(message, 'error', duration);
  }

  warning(message: string, duration: number = 5000): void {
    this.show(message, 'warning', duration);
  }

  info(message: string, duration: number = 5000): void {
    this.show(message, 'info', duration);
  }

  remove(id: string): void {
    const current = this._notificationsSubject.value;
    this._notificationsSubject.next(current.filter(n => n.id !== id));
  }

  clear(): void {
    this._notificationsSubject.next([]);
  }
}

