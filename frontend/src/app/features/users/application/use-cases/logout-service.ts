import { Injectable } from '@angular/core';
import { AuthApiService } from '../../infrastructure/adapters/auth-api-service';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private api: AuthApiService) {}

  execute() {
    return this.api.logout();
  }
}
