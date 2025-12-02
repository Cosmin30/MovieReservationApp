import { Component } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [NgFor],
  templateUrl: './notification.html',
  styleUrls: ['./notification.css']
})
export class NotificationComponent {

  messages: { type: string; text: string }[] = [
    // exemplu de test:
    // { type: 'success', text: 'Test notificare!' }
  ];

}
