import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Nu le mai declarăm aici dacă sunt standalone
@NgModule({
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class TicketsModule {}
