import { HallDTO } from '../dtos/hall.dto';
import { HallModel } from '../../domain/models/hall.model';

export class HallMapper {
  static fromDto(dto: HallDTO): HallModel {
    if (!dto.id || !dto.name || dto.number === undefined || dto.capacity === undefined) {
      throw new Error('Invalid HallDTO: missing required fields');
    }

    return {
      id: dto.id,
      name: dto.name,
      number: dto.number,
      capacity: dto.capacity
    };
  }

  static toDto(model: HallModel): HallDTO {
    return {
      id: model.id,
      name: model.name,
      number: model.number,
      capacity: model.capacity
    };
  }
}
