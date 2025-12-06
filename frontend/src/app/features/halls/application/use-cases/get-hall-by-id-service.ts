import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HallApiService } from '../../infrastructure/adapters/hall-api-service';
import { HallMapper } from '../../infrastructure/adapters/hall-mapper.mapper';
import { HallModel } from '../../domain/models/hall.model';

@Injectable({
  providedIn: 'root'
})
export class GetHallByIdService {
  private api = inject(HallApiService);

  execute(id: string): Observable<HallModel> {
    return this.api.getHallById(id).pipe(
      map(dto => HallMapper.fromDto(dto))
    );
  }
}
