import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RegisterFormComponent } from '../../components/register-form/register-form';
import { RegisterService } from '../../../application/use-cases/register-service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.html',
  standalone: true,
  imports: [CommonModule, RegisterFormComponent]
})
export class RegisterPage implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  constructor(private registerService: RegisterService) {}

  ngOnInit() {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  register(form: any) {
    this.registerService.execute(form)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          alert("Cont creat cu succes!");
          window.location.href = '/login';
        },
        error: () => {
          alert("A apărut o eroare la înregistrare.");
        }
      });
  }
}
