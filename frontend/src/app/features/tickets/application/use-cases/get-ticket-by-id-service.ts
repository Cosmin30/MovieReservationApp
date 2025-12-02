import { Injectable } from '@angular/core';
import { TicketApiService } from '../../infrastructure/adapters/ticket-api-service';

@Injectable({
  providedIn: 'root'
})
export class GetTicketByIdService {

  constructor(private api: TicketApiService) {}

  execute(id: string) {
    return this.api.getTicketById(id);
  }
}
