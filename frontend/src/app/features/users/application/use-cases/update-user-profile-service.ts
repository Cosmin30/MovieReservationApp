import { Injectable } from '@angular/core';
import { UserApiService } from '../../infrastructure/adapters/user-api-service';

@Injectable({
  providedIn: 'root'
})
export class UpdateUserProfileService {

  constructor(private api: UserApiService) {}

  execute(dto: any) {
    return this.api.updateCurrentUser(dto);
  }
}
