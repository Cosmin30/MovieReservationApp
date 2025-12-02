import { MovieDTO } from '../dtos/movie.dto';
import { MovieModel } from '../../domain/models/movie.model';

export class MovieMapper {

  static fromDto(dto: MovieDTO): MovieModel {
    return {
      id: dto.id!,
      title: dto.title!,
      description: dto.description!,
      genre: dto.genre!,
      duration: dto.duration!,
      releaseDate: dto.releaseDate!
    };
  }
}
