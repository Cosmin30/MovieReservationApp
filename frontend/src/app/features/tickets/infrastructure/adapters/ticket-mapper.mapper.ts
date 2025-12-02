import { TicketDTO } from '../dtos/ticket.dto';
import { TicketModel } from '../../domain/models/ticket.model';

export class TicketMapper {
  static fromDto(dto: TicketDTO): TicketModel {
    return {
      id: dto.id!,
      price: dto.price!,
      reservationId: dto.reservationId!,
      seatId: dto.seatId!
    };
  }
}
