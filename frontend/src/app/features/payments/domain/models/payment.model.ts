import { PaymentStatus } from './payment-status.enum';

export interface PaymentModel {
  id: string;
  amount: number;
  paidAt: string | null;
  status: PaymentStatus;
  reservationId: string;
}
