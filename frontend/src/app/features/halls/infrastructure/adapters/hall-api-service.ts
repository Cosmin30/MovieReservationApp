import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CacheService } from '../../../../core/services/cache-service';
import { environment } from '../../../../../environments/environment';
import { HallDTO } from '../dtos/hall.dto';
import { HallResponseDTO } from '../dtos/hall-response.dto';

@Injectable({
  providedIn: 'root'
})
export class HallApiService {
  private http = inject(HttpClient);
  private cache = inject(CacheService);
  private baseUrl = `${environment.apiUrl}/halls`;

  getAllHalls(): Observable<HallDTO[]> {
    return this.cache.getOrFetch(
      'all_halls',
      () => this.http.get<HallDTO[]>(this.baseUrl)
    );
  }

  getHallById(id: string): Observable<HallDTO> {
    return this.cache.getOrFetch(
      `hall_${id}`,
      () => this.http.get<HallDTO>(`${this.baseUrl}/${id}`)
    );
  }

  clearHallCache(id: string): void {
    this.cache.clear(`hall_${id}`);
  }

  createHall(dto: HallDTO): Observable<HallDTO> {
    return this.http.post<HallDTO>(this.baseUrl, dto);
  }

  updateHall(id: string, dto: HallDTO): Observable<HallDTO> {
    return this.http.put<HallDTO>(`${this.baseUrl}/${id}`, dto);
  }

  patchHall(id: string, dto: Partial<HallDTO>): Observable<HallDTO> {
    return this.http.patch<HallDTO>(`${this.baseUrl}/${id}`, dto);
  }

  deleteHall(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getHallLayout(id: string): Observable<HallResponseDTO> {
    // Don't cache layout - it may require authentication and can change
    return this.http.get<HallResponseDTO>(`${this.baseUrl}/${encodeURIComponent(id)}/layout`);
  }

  clearCache(): void {
    this.cache.clear('all_halls');
  }
}
