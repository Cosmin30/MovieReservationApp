import { Injectable, inject } from '@angular/core';
import { AuthService } from '../../../../core/auth/auth-service';
import { LoginFormData } from '../../presentation/components/login-form/login-form';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private authService = inject(AuthService);

  execute(form: LoginFormData) {
    return this.authService.login(form.email, form.password);
  }
}