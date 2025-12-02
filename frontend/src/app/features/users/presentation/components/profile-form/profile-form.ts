import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-profile-form',
  templateUrl: './profile-form.html',
  styleUrls: ['./profile-form.css']
})
export class ProfileFormComponent {

  @Input() form: any = {};
  @Output() onUpdate = new EventEmitter<any>();

  submit() {
    this.onUpdate.emit(this.form);
  }
}
