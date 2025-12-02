import { Injectable } from '@angular/core';
import { TicketApiService } from '../../infrastructure/adapters/ticket-api-service';

@Injectable({
  providedIn: 'root'
})
export class DownloadTicketService {

  constructor(private api: TicketApiService) {}

  execute(id: string) {
    return this.api.downloadTicket(id);
  }
}
