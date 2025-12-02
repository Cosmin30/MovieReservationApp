import { Injectable } from '@angular/core';
import { UserApiService } from '../../infrastructure/adapters/user-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetCurrentUserService {

  constructor(private api: UserApiService) {}

  execute() {
    return this.api.getCurrentUser();
  }
}
