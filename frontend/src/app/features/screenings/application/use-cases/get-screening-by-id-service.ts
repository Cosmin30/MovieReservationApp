import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ScreeningApiService } from '../../infrastructure/adapters/screening-api-service';
import { ScreeningMapper } from '../../infrastructure/adapters/screening-mapper.mapper';
import { ScreeningModel } from '../../domain/models/screening.model';

@Injectable({
  providedIn: 'root'
})
export class GetScreeningByIdService {
  private api = inject(ScreeningApiService);

  execute(id: string): Observable<ScreeningModel> {
    return this.api.getScreeningById(id).pipe(
      map(dto => ScreeningMapper.fromDto(dto))
    );
  }
}
