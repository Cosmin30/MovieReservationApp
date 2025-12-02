import { Injectable } from '@angular/core';
import { AuthApiService } from '../../infrastructure/adapters/auth-api-service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private api: AuthApiService) {}

  execute(credentials: any) {
    return this.api.login(credentials);
  }
}
