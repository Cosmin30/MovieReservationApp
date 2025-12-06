import { MovieDTO } from '../../../movies/infrastructure/dtos/movie.dto';
import { HallDTO } from '../../../halls/infrastructure/dtos/hall.dto';
import { SeatDTO } from '../../../halls/infrastructure/dtos/seat.dto';

export interface ScreeningDTO {
  id?: string;
  movie?: MovieDTO;
  hall?: HallDTO;
  startTime?: string;
  start_time?: string; // Backend sends snake_case
  roomNumber?: number;
  room_number?: number; // Backend sends snake_case
  capacity?: number;
  seats?: SeatDTO[];
}
