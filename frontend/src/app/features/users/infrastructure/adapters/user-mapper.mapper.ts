import { UserDTO } from '../dtos/user.dto';
import { UserModel } from '../../domain/models/user.model';

export class UserMapper {
  static fromDto(dto: UserDTO): UserModel {
    return {
      id: dto.id!,
      email: dto.email!,
      fullName: dto.fullName!,
      createdAt: dto.createdAt!,
      reservations: dto.reservations || []
    };
  }
}
