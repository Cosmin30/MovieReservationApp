import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ScreeningListComponent } from './presentation/components/screening-list/screening-list';
import { ScreeningCardComponent } from './presentation/components/screening-card/screening-card';

const routes: Routes = [
  { path: '', component: ScreeningListComponent },
  { path: ':id', component: ScreeningCardComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ScreeningsRoutingModule {}
