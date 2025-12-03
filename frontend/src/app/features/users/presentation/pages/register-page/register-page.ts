import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegisterFormComponent } from '../../components/register-form/register-form';
import { RegisterService } from '../../../application/use-cases/register-service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.html',
  standalone: true,
  imports: [CommonModule, RegisterFormComponent]
})
export class RegisterPage {

  constructor(private registerService: RegisterService) {}

 register(form: any) {
  this.registerService.execute(form).subscribe({
    next: () => {
      alert("Cont creat cu succes!");
      // Redirect după înregistrare
      window.location.href = '/login';
    },
    error: () => {
      alert("A apărut o eroare la înregistrare.");
    }
  });
}

}
