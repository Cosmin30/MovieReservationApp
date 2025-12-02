import { ScreeningDTO } from './screening.dto';

export interface ScreeningsListResponseDTO {
  items: ScreeningDTO[];
  total: number;
}
