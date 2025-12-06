import { ScreeningDTO } from '../dtos/screening.dto';
import { ScreeningModel } from '../../domain/models/screening.model';

export class ScreeningMapper {
  static fromDto(dto: ScreeningDTO): ScreeningModel {
    if (!dto.id || !dto.movie || !dto.hall) {
      throw new Error('Invalid ScreeningDTO: missing required fields');
    }

    // Backend sends snake_case, prioritize it
    const startTime = dto.start_time || dto.startTime;
    const roomNumber = dto.room_number || dto.roomNumber;

    if (!startTime || roomNumber === undefined) {
      throw new Error('Invalid ScreeningDTO: missing startTime or roomNumber');
    }

    // Normalize seats - handle both SeatDTO and plain objects
    const seats = (dto.seats || []).map((seat: any) => {
      // If seat already has status, use it
      if (seat.status) {
        return seat;
      }
      // Otherwise, determine status from is_available or isAvailable
      const isAvailable = seat.is_available !== undefined ? seat.is_available 
                        : seat.isAvailable !== undefined ? seat.isAvailable
                        : seat.available !== undefined ? seat.available
                        : true;
      
      return {
        ...seat,
        status: isAvailable ? 'AVAILABLE' : 'RESERVED',
        row: typeof seat.row === 'string' ? parseInt(seat.row) || 0 : seat.row,
        isSelected: false
      };
    });

    return {
      id: dto.id,
      movie: dto.movie,
      hall: dto.hall,
      startTime: startTime,
      roomNumber: roomNumber,
      capacity: dto.capacity || 0,
      seats: seats
    };
  }

  static toDto(model: ScreeningModel): ScreeningDTO {
    return {
      id: model.id,
      movie: model.movie,
      hall: model.hall,
      startTime: model.startTime,
      roomNumber: model.roomNumber,
      capacity: model.capacity,
      seats: model.seats
    };
  }
}
