import { MovieDTO } from './movie.dto';

export interface MoviesListResponseDTO {
  items: MovieDTO[];
  total: number;
}
