import { Injectable } from '@angular/core';
import { ScreeningApiService } from '../../infrastructure/adapters/screening-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetScreeningByIdService {

  constructor(private api: ScreeningApiService) {}

  execute(id: string) {
    return this.api.getScreeningById(id);
  }
}
