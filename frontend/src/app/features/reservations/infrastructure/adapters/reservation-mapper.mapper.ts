import { ReservationDTO } from '../dtos/reservation.dto';
import { ReservationModel } from '../../domain/models/reservation.model';
import { ReservationStatus } from '../../domain/models/reservation-status.enum';

export class ReservationMapper {
  static fromDto(dto: ReservationDTO): ReservationModel {
    // Handle snake_case from backend
    const id = dto.id;
    const user = dto.user;
    const screening = dto.screening;
    const statusStr = dto.status || 'PENDING';
    const totalPrice = dto.totalPrice ?? (dto as any).total_price ?? 0;
    const createdAt = dto.createdAt || (dto as any).created_at || new Date().toISOString();
    const tickets = dto.tickets || [];

    // If missing critical fields, use defaults or throw
    if (!id) {
      throw new Error('Invalid ReservationDTO: missing id');
    }

    // Map status string to enum
    let status: ReservationStatus;
    switch (statusStr.toUpperCase()) {
      case 'PENDING':
      case 'CREATED': // Backend uses CREATED
        status = ReservationStatus.PENDING;
        break;
      case 'CONFIRMED':
        status = ReservationStatus.CONFIRMED;
        break;
      case 'CANCELLED':
        status = ReservationStatus.CANCELLED;
        break;
      default:
        status = ReservationStatus.PENDING;
    }

    return {
      id: id,
      user: user || {} as any, // Allow missing user for now
      screening: screening || {} as any, // Allow missing screening for now
      createdAt: createdAt,
      status: status,
      totalPrice: totalPrice,
      tickets: tickets
    };
  }

  static toDto(model: ReservationModel): ReservationDTO {
    return {
      id: model.id,
      user: model.user,
      screening: model.screening,
      createdAt: model.createdAt,
      status: model.status,
      totalPrice: model.totalPrice,
      tickets: model.tickets
    };
  }
}
