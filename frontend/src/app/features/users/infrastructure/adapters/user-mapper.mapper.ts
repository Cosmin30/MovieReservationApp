import { UserDTO } from '../dtos/user.dto';
import { UserModel } from '../../domain/models/user.model';

export class UserMapper {
  static fromDto(dto: UserDTO): UserModel {
    if (!dto.id || !dto.email) {
      throw new Error('Invalid UserDTO: missing required fields');
    }

    // Backend sends snake_case, prioritize it
    const fullName = dto.full_name || dto.fullName;
    const createdAt = dto.created_at || dto.createdAt || null;

    if (!fullName) {
      throw new Error('Invalid UserDTO: missing fullName');
    }

    return {
      id: dto.id,
      email: dto.email,
      fullName: fullName,
      createdAt: createdAt,
      reservations: dto.reservations || null
    };
  }

  static toDto(model: UserModel): UserDTO {
    return {
      id: model.id,
      email: model.email,
      fullName: model.fullName,
      createdAt: model.createdAt || undefined,
      reservations: model.reservations || undefined
    };
  }
}
