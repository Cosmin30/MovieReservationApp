import { Component } from '@angular/core';
import { LoginService } from '../../../application/use-cases/login-service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css']
})
export class LoginPage {

  constructor(private loginService: LoginService) {}

  login(form: any) {
    this.loginService.execute(form).subscribe();
  }
}
