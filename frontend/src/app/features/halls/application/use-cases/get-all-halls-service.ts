import { Injectable } from '@angular/core';
import { HallApiService } from '../../infrastructure/adapters/hall-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetAllHallsService {

  constructor(private api: HallApiService) {}

  execute() {
    return this.api.getAllHalls();
  }
}
