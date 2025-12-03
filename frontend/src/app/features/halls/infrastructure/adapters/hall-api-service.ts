import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CacheService } from '../../../../core/services/cache-service';

@Injectable({
  providedIn: 'root'
})
export class HallApiService {

  private baseUrl = 'http://localhost:8080/api/halls';

  constructor(
    private http: HttpClient,
    private cache: CacheService
  ) {}

  getAllHalls() {
    return this.cache.getOrFetch(
      'all_halls',
      () => this.http.get<any[]>(this.baseUrl)
    );
  }

  getHallById(id: string) {
    return this.cache.getOrFetch(
      `hall_${id}`,
      () => this.http.get<any>(`${this.baseUrl}/${id}`)
    );
  }

  createHall(dto: any) {
    return this.http.post(this.baseUrl, dto);
  }

  updateHall(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchHall(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deleteHall(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getHallLayout(id: string) {
    // Don't cache layout - it may require authentication and can change
    return this.http.get(`${this.baseUrl}/${id}/layout`);
  }
}
