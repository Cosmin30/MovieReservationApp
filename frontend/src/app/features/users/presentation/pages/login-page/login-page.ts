import { Component, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LoginFormComponent, LoginFormData } from '../../components/login-form/login-form';
import { LoginService } from '../../../application/use-cases/login-service';
import { NotificationService } from '../../../../../shared/services/notification.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  standalone: true,
  imports: [CommonModule, LoginFormComponent]
})
export class LoginPage implements OnDestroy {
  private loginService = inject(LoginService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private destroy$ = new Subject<void>();

  login(form: LoginFormData): void {
    this.loginService.execute(form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.success('Autentificare reușită!');
        },
        error: () => {
          this.notificationService.error('Email sau parolă greșită');
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
