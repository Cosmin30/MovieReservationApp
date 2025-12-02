import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GetAvailableSeatsService } from '../../../application/use-cases/get-available-seats-service';
import { GetReservationByIdService } from '../../../application/use-cases/get-reservation-by-id-service';

@Component({
  selector: 'app-reservation-page',
  templateUrl: './reservation-page.html',
  styleUrls: ['./reservation-page.css']
})
export class ReservationPage implements OnInit {

  screeningId!: string;
  availableSeats: any[] = [];
  reservation: any;

  constructor(
    private route: ActivatedRoute,
    private getSeats: GetAvailableSeatsService,
    private getReservation: GetReservationByIdService
  ) {}

  ngOnInit() {
    this.screeningId = this.route.snapshot.queryParamMap.get('screeningId')!;

    this.getSeats.execute(this.screeningId).subscribe((res: any) => {
      this.availableSeats = res.seats;
    });
  }
}
