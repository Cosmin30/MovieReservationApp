import { ScreeningDTO } from '../dtos/screening.dto';
import { ScreeningModel } from '../../domain/models/screening.model';

export class ScreeningMapper {
  static fromDto(dto: ScreeningDTO): ScreeningModel {
    return {
      id: dto.id!,
      movie: dto.movie!,
      hall: dto.hall!,
      startTime: dto.startTime!,
      roomNumber: dto.roomNumber!,
      capacity: dto.capacity!,
      seats: dto.seats || []
    };
  }
}
