import { TicketDTO } from './ticket.dto';

export interface TicketsListResponseDTO {
  items: TicketDTO[];
  total: number;
}
