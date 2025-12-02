import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HallLayout } from './presentation/components/hall-layout/hall-layout';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: ':id/layout', component: HallLayout }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HallsRoutingModule {}
