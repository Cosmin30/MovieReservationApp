import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { MovieCardComponent } from './presentation/components/movie-card/movie-card';
import { MovieGridComponent } from './presentation/components/movie-grid/movie-grid';
import { MovieFiltersComponent } from './presentation/components/movie-filters/movie-filters';
import { MovieSearchComponent } from './presentation/components/movie-search/movie-search';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MovieCardComponent,    
    MovieGridComponent,    
    MovieFiltersComponent, 
    MovieSearchComponent   
  ],
  exports: [
    MovieCardComponent,
    MovieGridComponent,
    MovieFiltersComponent,
    MovieSearchComponent
  ]
})
export class MoviesModule {}
