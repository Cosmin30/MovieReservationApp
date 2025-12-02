import { HallDTO } from '../dtos/hall.dto';
import { HallModel } from '../../domain/models/hall.model';

export class HallMapper {
  static fromDto(dto: HallDTO): HallModel {
    return {
      id: dto.id!,
      name: dto.name!,
      number: dto.number!,
      capacity: dto.capacity!
    };
  }
}
