import { Component, OnInit } from '@angular/core';
import { MoviesState } from '../../../application/state/movies-state.state';
import { GetAllMoviesService } from '../../../application/use-cases/get-all-movies-service';
import { FilterMoviesService } from '../../../application/use-cases/filter-movies-service';

@Component({
  selector: 'app-movies-list-page',
  templateUrl: './movies-list-page.html',
  styleUrls: ['./movies-list-page.css']
})
export class MoviesListPage implements OnInit {

  movies: any[] = [];

  constructor(
    private state: MoviesState,
    private getAllMovies: GetAllMoviesService,
    private filterService: FilterMoviesService
  ) {}

  ngOnInit() {
    this.state.movies$.subscribe(m => this.movies = m);
    this.getAllMovies.execute();
  }

  search(query: string) {
    this.filterService.execute(query);
  }

  filter(query: string) {
    this.filterService.execute(query);
  }

  filterGenre(genre: string) {
    this.filterService.execute(genre);
  }
}
