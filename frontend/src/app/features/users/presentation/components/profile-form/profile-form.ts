import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile-form',
  templateUrl: './profile-form.html',
  styleUrls: ['./profile-form.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]  
})
export class ProfileFormComponent {
  @Input() form: any = {};
  @Output() onUpdate = new EventEmitter<any>();

  submit() {
    this.onUpdate.emit(this.form);
  }
}
