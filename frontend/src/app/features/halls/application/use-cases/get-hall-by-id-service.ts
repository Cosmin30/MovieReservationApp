import { Injectable } from '@angular/core';
import { HallApiService } from '../../infrastructure/adapters/hall-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetHallByIdService {

  constructor(private api: HallApiService) {}

  execute(id: string) {
    return this.api.getHallById(id);
  }
}
