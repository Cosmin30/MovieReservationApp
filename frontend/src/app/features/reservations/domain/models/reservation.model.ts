import { ReservationStatus } from './reservation-status.enum';

export interface ReservationModel {
  id: string;
  user: any;
  screening: any;
  createdAt: string;
  status: ReservationStatus;
  totalPrice: number;
  tickets: any[];
}
