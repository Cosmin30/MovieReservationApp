import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class HallApiService {

  private baseUrl = 'http://localhost:8080/api/halls';

  constructor(private http: HttpClient) {}

  getAllHalls() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getHallById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
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
    return this.http.get(`${this.baseUrl}/${id}/layout`);
  }
}
