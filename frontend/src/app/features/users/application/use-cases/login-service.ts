import { Injectable } from '@angular/core';
import { AuthService } from '../../../../core/auth/auth-service';

@Injectable({
  providedIn: 'root'
})
@Injectable({ providedIn: 'root' })
export class LoginService {
  constructor(private authService: AuthService) {}

  execute(form: any) {
    return this.authService.login(form.email, form.password);
  }
}