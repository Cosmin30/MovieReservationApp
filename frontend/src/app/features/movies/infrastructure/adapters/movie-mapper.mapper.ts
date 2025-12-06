import { MovieDTO } from '../dtos/movie.dto';
import { MovieModel } from '../../domain/models/movie.model';

export class MovieMapper {
  static fromDto(dto: MovieDTO): MovieModel {
    if (!dto.id || !dto.title || !dto.description || !dto.genre || !dto.duration) {
      throw new Error('Invalid MovieDTO: missing required fields');
    }

    // Backend sends release_date (snake_case), prioritize it
    const releaseDate = dto.release_date || dto.releaseDate;
    if (!releaseDate) {
      throw new Error('Invalid MovieDTO: missing releaseDate');
    }

    return {
      id: dto.id,
      title: dto.title,
      description: dto.description,
      genre: dto.genre,
      duration: dto.duration,
      releaseDate: releaseDate
    };
  }

  static toDto(model: MovieModel): MovieDTO {
    return {
      id: model.id,
      title: model.title,
      description: model.description,
      genre: model.genre,
      duration: model.duration,
      releaseDate: model.releaseDate
    };
  }
}
