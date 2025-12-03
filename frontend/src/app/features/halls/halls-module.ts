import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Importă componentele standalone
import { HallLayoutComponent } from './presentation/components/hall-layout/hall-layout';
import { SeatComponent } from './presentation/components/seat/seat';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    HallLayoutComponent, 
    SeatComponent       
  ],
  exports: [
    HallLayoutComponent,
    SeatComponent
  ]
})
export class HallsModule {}
