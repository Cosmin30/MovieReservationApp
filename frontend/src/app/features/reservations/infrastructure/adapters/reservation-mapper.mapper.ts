import { ReservationDTO } from '../dtos/reservation.dto';
import { ReservationModel } from '../../domain/models/reservation.model';

export class ReservationMapper {
  static fromDto(dto: ReservationDTO): ReservationModel {
    return {
      id: dto.id!,
      user: dto.user!,
      screening: dto.screening!,
      createdAt: dto.createdAt!,
      status: dto.status! as any,
      totalPrice: dto.totalPrice!,
      tickets: dto.tickets || []
    };
  }
}
