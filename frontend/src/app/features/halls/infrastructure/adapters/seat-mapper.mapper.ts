import { SeatDTO } from '../dtos/seat.dto';
import { SeatModel } from '../../domain/models/seat.model';

export class SeatMapper {
  static fromDto(dto: SeatDTO): SeatModel {
    if (!dto.id || dto.row === undefined || dto.number === undefined) {
      throw new Error(`Invalid SeatDTO: missing required fields. DTO: ${JSON.stringify(dto)}`);
    }

    // Map status - check multiple sources: status string, is_available, isAvailable, available
    let status: 'AVAILABLE' | 'RESERVED' | 'UNAVAILABLE' = 'AVAILABLE'; // Default to AVAILABLE
    
    // First check explicit status string
    if (dto.status) {
      switch (dto.status.toUpperCase()) {
        case 'AVAILABLE':
          status = 'AVAILABLE';
          break;
        case 'RESERVED':
          status = 'RESERVED';
          break;
        case 'UNAVAILABLE':
          status = 'UNAVAILABLE';
          break;
        default:
          // Fall through to boolean check
          break;
      }
    }
    
    // If no status string, check boolean flags (backend sends is_available)
    if (!dto.status) {
      const isAvailable = dto.is_available ?? dto.isAvailable ?? dto.available;
      
      // Only override if we have a boolean value
      if (isAvailable !== undefined) {
        status = isAvailable ? 'AVAILABLE' : 'RESERVED';
      }
      // If no boolean flag and no status, default to AVAILABLE (since getAvailableSeatsByScreening only returns available seats)
    }

    const result = {
      id: dto.id,
      row: typeof dto.row === 'string' ? parseInt(dto.row) || 0 : dto.row,
      number: dto.number,
      status: status,
      isSelected: false
    };
    
    // Debug logging
    console.debug('SeatMapper.fromDto:', { dto, result });
    
    return result;
  }

  static fromDtoArray(dtos: SeatDTO[]): SeatModel[] {
    return dtos.map(dto => this.fromDto(dto));
  }

  static toDto(model: SeatModel): SeatDTO {
    return {
      id: model.id,
      row: model.row,
      number: model.number,
      status: model.status
    };
  }

  /**
   * Maps from AvailableSeatsResponseDTO format (from backend)
   * Backend may send: { id: string, row: number, number: number, is_available: boolean }
   */
  static fromAvailableSeatsResponse(seat: { id: string; row: number | string; number: number; is_available?: boolean; isAvailable?: boolean; available?: boolean }): SeatModel {
    const isAvailable = seat.is_available ?? seat.isAvailable ?? seat.available ?? true;
    return {
      id: seat.id,
      row: typeof seat.row === 'string' ? parseInt(seat.row) || 0 : seat.row,
      number: seat.number,
      status: isAvailable ? 'AVAILABLE' : 'RESERVED',
      isSelected: false
    };
  }

  static fromAvailableSeatsResponseArray(seats: { id: string; row: number | string; number: number; is_available?: boolean; isAvailable?: boolean; available?: boolean }[]): SeatModel[] {
    return seats.map(seat => this.fromAvailableSeatsResponse(seat));
  }
}

