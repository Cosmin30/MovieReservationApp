import { Component, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RegisterFormComponent, RegisterFormData } from '../../components/register-form/register-form';
import { RegisterService } from '../../../application/use-cases/register-service';
import { NotificationService } from '../../../../../shared/services/notification.service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.html',
  standalone: true,
  imports: [CommonModule, RegisterFormComponent]
})
export class RegisterPage implements OnDestroy {
  private registerService = inject(RegisterService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private destroy$ = new Subject<void>();

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  register(form: RegisterFormData): void {
    this.registerService.execute(form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.notificationService.success('Cont creat cu succes!');
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        },
        error: () => {
          this.notificationService.error('A apărut o eroare la înregistrare. Te rugăm să încerci din nou.');
        }
      });
  }
}
