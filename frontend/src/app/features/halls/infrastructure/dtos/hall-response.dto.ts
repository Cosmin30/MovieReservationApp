import { HallDTO } from './hall.dto';
import { SeatDTO } from './seat.dto';

export interface HallResponseDTO {
  hall: HallDTO;
  seats: SeatDTO[];
}
