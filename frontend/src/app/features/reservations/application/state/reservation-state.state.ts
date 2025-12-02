import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ReservationModel } from '../../domain/models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationState {

  private reservationsSubject = new BehaviorSubject<ReservationModel[]>([]);
  reservations$ = this.reservationsSubject.asObservable();

  setReservations(list: ReservationModel[]) {
    this.reservationsSubject.next(list);
  }
}
