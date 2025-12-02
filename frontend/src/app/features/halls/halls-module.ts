import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { HallLayout } from './presentation/components/hall-layout/hall-layout';
import { Seat } from './presentation/components/seat/seat';

@NgModule({
  declarations: [
    HallLayout,
    Seat
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class HallsModule {}
