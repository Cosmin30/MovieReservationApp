import { Injectable } from '@angular/core';
import { AuthApiService } from '../../infrastructure/adapters/auth-api-service';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private api: AuthApiService) {}

  execute(dto: any) {
    return this.api.register(dto);
  }
}
