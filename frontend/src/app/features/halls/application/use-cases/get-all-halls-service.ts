import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HallApiService } from '../../infrastructure/adapters/hall-api-service';
import { HallMapper } from '../../infrastructure/adapters/hall-mapper.mapper';
import { HallModel } from '../../domain/models/hall.model';

@Injectable({
  providedIn: 'root'
})
export class GetAllHallsService {
  private api = inject(HallApiService);

  execute(): Observable<HallModel[]> {
    return this.api.getAllHalls().pipe(
      map(dtos => dtos.map(dto => HallMapper.fromDto(dto)))
    );
  }
}
