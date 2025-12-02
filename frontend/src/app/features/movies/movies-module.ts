import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { MovieCardComponent } from './presentation/components/movie-card/movie-card';
import { MovieGridComponent } from './presentation/components/movie-grid/movie-grid';
import { MovieFiltersComponent } from './presentation/components/movie-filters/movie-filters';
import { MovieSearchComponent } from './presentation/components/movie-search/movie-search';

import { MoviesListPage } from './presentation/pages/movies-list-page/movies-list-page';
import { MovieDetailsPage } from './presentation/pages/movie-details-page/movie-details-page';

@NgModule({
  declarations: [
    MovieCardComponent,
    MovieGridComponent,
    MovieFiltersComponent,
    MovieSearchComponent,
    MoviesListPage,
    MovieDetailsPage
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class MoviesModule {}
