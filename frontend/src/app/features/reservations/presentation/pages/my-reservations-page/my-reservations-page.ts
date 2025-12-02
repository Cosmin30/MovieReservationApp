import { Component, OnInit } from '@angular/core';
import { ReservationState } from '../../../application/state/reservation-state.state';
import { GetUserReservationsService } from '../../../application/use-cases/get-user-reservations-service';

@Component({
  selector: 'app-my-reservations-page',
  templateUrl: './my-reservations-page.html',
  styleUrls: ['./my-reservations-page.css']
})
export class MyReservationsPage implements OnInit {

  reservations: any[] = [];

  constructor(
    private state: ReservationState,
    private getUserReservations: GetUserReservationsService
  ) {}

  ngOnInit() {
    this.state.reservations$.subscribe(res => this.reservations = res);

    // HARD-CODED userId (în realitate vine din login)
    const userId = '11111111-1111-1111-1111-111111111111';
    this.getUserReservations.execute(userId);
  }
}
