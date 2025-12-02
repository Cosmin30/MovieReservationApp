import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GetTicketByIdService } from '../../../application/use-cases/get-ticket-by-id-service';
import { DownloadTicketService } from '../../../application/use-cases/download-ticket-service';

@Component({
  selector: 'app-ticket-details',
  templateUrl: './ticket-details.html',
  styleUrls: ['./ticket-details.css']
})
export class TicketDetailsComponent implements OnInit {

  ticket: any;

  constructor(
    private route: ActivatedRoute,
    private getTicket: GetTicketByIdService,
    private downloadService: DownloadTicketService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.getTicket.execute(id).subscribe(t => this.ticket = t);
  }

  goBack() {
    history.back();
  }

  downloadTicket() {
    this.downloadService.execute(this.ticket.id).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ticket-${this.ticket.id}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
