import { Injectable } from '@angular/core';
import { AuthService } from '../../../../core/auth/auth-service';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private authService: AuthService) {}

  execute() {
    this.authService.logout();
  }
}