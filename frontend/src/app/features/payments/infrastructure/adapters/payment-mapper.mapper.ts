import { PaymentDTO } from '../dtos/payment.dto';
import { PaymentModel } from '../../domain/models/payment.model';

export class PaymentMapper {
  static fromDto(dto: PaymentDTO): PaymentModel {
    return {
      id: dto.id!,
      amount: dto.amount!,
      paidAt: dto.paidAt || null,
      status: dto.status as any,
      reservationId: dto.reservationId!
    };
  }
}
