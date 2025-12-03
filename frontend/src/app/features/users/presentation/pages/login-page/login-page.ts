import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginFormComponent } from '../../components/login-form/login-form';
import { LoginService } from '../../../application/use-cases/login-service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  standalone: true,
  imports: [CommonModule, LoginFormComponent]
})
export class LoginPage {

  constructor(private loginService: LoginService) {}

  login(form: any) {
    this.loginService.execute(form).subscribe({
      next: () => {},  
      error: () => alert("Email sau parolă greșită")
    });
  }
}
