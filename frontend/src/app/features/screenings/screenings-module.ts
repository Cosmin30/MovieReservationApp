import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { ScreeningCardComponent } from './presentation/components/screening-card/screening-card';
import { ScreeningListComponent } from './presentation/components/screening-list/screening-list';
import { ScreeningTimeSelectorComponent } from './presentation/components/screening-time-selector/screening-time-selector';

@NgModule({
  declarations: [
    ScreeningCardComponent,
    ScreeningListComponent,
    ScreeningTimeSelectorComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class ScreeningsModule {}
