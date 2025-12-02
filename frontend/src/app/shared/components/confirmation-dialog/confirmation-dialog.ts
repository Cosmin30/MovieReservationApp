import { Component } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [NgIf],
  templateUrl: './confirmation-dialog.html',
  styleUrls: ['./confirmation-dialog.css']
})
export class ConfirmationDialogComponent {
  visible = false;
  title = '';
  message = '';

  confirmCallback: (() => void) | null = null;

  open(title: string, message: string, onConfirm: () => void) {
    this.title = title;
    this.message = message;
    this.visible = true;
    this.confirmCallback = onConfirm;
  }

  confirm() {
    if (this.confirmCallback) this.confirmCallback();
    this.visible = false;
  }

  cancel() {
    this.visible = false;
  }
}

