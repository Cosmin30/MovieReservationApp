import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MoviesListPage } from './presentation/pages/movies-list-page/movies-list-page';
import { MovieDetailsPage } from './presentation/pages/movie-details-page/movie-details-page';

const routes: Routes = [
  { path: '', component: MoviesListPage },
  { path: ':id', component: MovieDetailsPage }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MoviesRoutingModule {}
