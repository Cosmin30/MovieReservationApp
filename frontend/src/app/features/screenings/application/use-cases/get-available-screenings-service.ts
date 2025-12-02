import { Injectable } from '@angular/core';
import { ScreeningApiService } from '../../infrastructure/adapters/screening-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetAvailableScreeningsService {

  constructor(private api: ScreeningApiService) {}

  execute() {
    return this.api.getAllScreenings();
  }
}
