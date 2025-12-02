import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MovieApiService {

  private baseUrl = 'http://localhost:8080/api/movies';

  constructor(private http: HttpClient) {}

  getAllMovies() {
    return this.http.get<any[]>(this.baseUrl);
  }

  getMovieById(id: string) {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  createMovie(dto: any) {
    return this.http.post(this.baseUrl, dto);
  }

  updateMovie(id: string, dto: any) {
    return this.http.put(`${this.baseUrl}/${id}`, dto);
  }

  patchMovie(id: string, dto: any) {
    return this.http.patch(`${this.baseUrl}/${id}`, dto);
  }

  deleteMovie(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  searchMovies(query: string) {
    return this.http.get<any[]>(`${this.baseUrl}?search=${query}`);
  }
}
